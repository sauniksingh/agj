package athato.ghummakd.jigayasa.presentation.view

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.presentation.mvi.UiEffect
import athato.ghummakd.jigayasa.presentation.mvi.UiIntent
import athato.ghummakd.jigayasa.presentation.mvi.UiState

data class ViewEventState(
    val event: Event? = null,
    val isLoading: Boolean = true,
    val notFound: Boolean = false
) : UiState

sealed interface ViewEventIntent : UiIntent {
    data object EditRequested : ViewEventIntent
    data object DeleteRequested : ViewEventIntent
}

sealed interface ViewEventEffect : UiEffect {
    data class NavigateToEdit(val id: Int) : ViewEventEffect
    data object DeletedAndClose : ViewEventEffect
}
