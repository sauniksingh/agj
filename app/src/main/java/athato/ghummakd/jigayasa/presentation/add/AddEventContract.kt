package athato.ghummakd.jigayasa.presentation.add

import athato.ghummakd.jigayasa.presentation.mvi.UiEffect
import athato.ghummakd.jigayasa.presentation.mvi.UiIntent
import athato.ghummakd.jigayasa.presentation.mvi.UiState

data class AddEventState(
    val editingId: Int? = null,
    val title: String = "",
    val message: String = "",
    val pickedDateMillis: Long? = null,
    val pickedHour: Int? = null,
    val pickedMinute: Int? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null
) : UiState {
    val isEditing: Boolean get() = editingId != null

    val combinedTimestamp: Long?
        get() {
            val date = pickedDateMillis ?: return null
            val h = pickedHour ?: return null
            val m = pickedMinute ?: return null
            val cal = java.util.Calendar.getInstance().apply {
                timeInMillis = date
                set(java.util.Calendar.HOUR_OF_DAY, h)
                set(java.util.Calendar.MINUTE, m)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

    val isValid: Boolean
        get() = title.isNotBlank() && (combinedTimestamp ?: 0L) > System.currentTimeMillis()
}

sealed interface AddEventIntent : UiIntent {
    data class TitleChanged(val value: String) : AddEventIntent
    data class MessageChanged(val value: String) : AddEventIntent
    data class DatePicked(val millis: Long) : AddEventIntent
    data class TimePicked(val hour: Int, val minute: Int) : AddEventIntent
    data object Submit : AddEventIntent
    data object DismissError : AddEventIntent
}

sealed interface AddEventEffect : UiEffect {
    data object Saved : AddEventEffect
}
