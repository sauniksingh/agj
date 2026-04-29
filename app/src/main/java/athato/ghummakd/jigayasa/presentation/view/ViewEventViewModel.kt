package athato.ghummakd.jigayasa.presentation.view

import androidx.lifecycle.viewModelScope
import athato.ghummakd.jigayasa.domain.usecase.DeleteEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.ObserveEventsUseCase
import athato.ghummakd.jigayasa.presentation.mvi.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ViewEventViewModel(
    observeEvents: ObserveEventsUseCase,
    private val deleteEvent: DeleteEventUseCase,
    private val eventId: Int
) : MviViewModel<ViewEventIntent, ViewEventState, ViewEventEffect>(ViewEventState()) {

    init {
        observeEvents()
            .onEach { events ->
                val event = events.firstOrNull { it.id == eventId }
                setState {
                    if (event != null) copy(event = event, isLoading = false, notFound = false)
                    else copy(event = null, isLoading = false, notFound = true)
                }
            }
            .launchIn(viewModelScope)
    }

    override suspend fun handle(intent: ViewEventIntent) {
        when (intent) {
            ViewEventIntent.EditRequested -> emitEffect(ViewEventEffect.NavigateToEdit(eventId))
            ViewEventIntent.DeleteRequested -> {
                deleteEvent(eventId)
                emitEffect(ViewEventEffect.DeletedAndClose)
            }
        }
    }
}
