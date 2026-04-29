package athato.ghummakd.jigayasa.presentation.list

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.presentation.mvi.UiEffect
import athato.ghummakd.jigayasa.presentation.mvi.UiIntent
import athato.ghummakd.jigayasa.presentation.mvi.UiState

data class EventListState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = true,
    val nextUpcomingId: Int? = null
) : UiState

sealed interface EventListIntent : UiIntent {
    data class Delete(val id: Int) : EventListIntent
    data class Edit(val id: Int) : EventListIntent
    data object AddRequested : EventListIntent
    data class Tick(val now: Long) : EventListIntent
}

sealed interface EventListEffect : UiEffect {
    data object NavigateToAdd : EventListEffect
    data class NavigateToEdit(val id: Int) : EventListEffect
    data class ShowDeleted(val title: String) : EventListEffect
}
