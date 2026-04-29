package athato.ghummakd.jigayasa.presentation.list

import androidx.lifecycle.viewModelScope
import athato.ghummakd.jigayasa.domain.usecase.DeleteEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.ObserveEventsUseCase
import athato.ghummakd.jigayasa.presentation.mvi.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class EventListViewModel(
    private val observeEvents: ObserveEventsUseCase,
    private val deleteEvent: DeleteEventUseCase
) : MviViewModel<EventListIntent, EventListState, EventListEffect>(EventListState()) {

    init {
        observeEvents()
            .onEach { events ->
                val now = System.currentTimeMillis()
                val nextId = events.filter { it.timestamp > now }.minByOrNull { it.timestamp }?.id
                setState { copy(events = events, isLoading = false, nextUpcomingId = nextId) }
            }
            .launchIn(viewModelScope)
    }

    override suspend fun handle(intent: EventListIntent) {
        when (intent) {
            is EventListIntent.Delete -> {
                val title = state.value.events.firstOrNull { it.id == intent.id }?.title.orEmpty()
                deleteEvent(intent.id)
                emitEffect(EventListEffect.ShowDeleted(title))
            }
            is EventListIntent.Edit -> emitEffect(EventListEffect.NavigateToEdit(intent.id))
            EventListIntent.AddRequested -> emitEffect(EventListEffect.NavigateToAdd)
            is EventListIntent.Tick -> {
                val nextId = state.value.events
                    .filter { it.timestamp > intent.now }
                    .minByOrNull { it.timestamp }?.id
                if (nextId != state.value.nextUpcomingId) {
                    setState { copy(nextUpcomingId = nextId) }
                }
            }
        }
    }
}
