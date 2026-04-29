package athato.ghummakd.jigayasa.presentation.add

import athato.ghummakd.jigayasa.domain.model.Category
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
    val category: Category = Category.GENERAL,
    val categoryManuallyPicked: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null
) : UiState {
    val isEditing: Boolean get() = editingId != null

    val combinedTimestamp: Long?
        get() {
            val date = pickedDateMillis ?: return null
            val h = pickedHour ?: return null
            val m = pickedMinute ?: return null
            // pickedDateMillis is UTC midnight (DatePicker convention). Pull
            // year/month/day from UTC, then build a LOCAL timestamp at h:m.
            val utc = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = date
            }
            val local = java.util.Calendar.getInstance().apply {
                clear()
                set(
                    utc.get(java.util.Calendar.YEAR),
                    utc.get(java.util.Calendar.MONTH),
                    utc.get(java.util.Calendar.DAY_OF_MONTH),
                    h,
                    m,
                    0
                )
                set(java.util.Calendar.MILLISECOND, 0)
            }
            return local.timeInMillis
        }

    val isValid: Boolean
        get() = title.isNotBlank() && (combinedTimestamp ?: 0L) > System.currentTimeMillis()
}

sealed interface AddEventIntent : UiIntent {
    data class TitleChanged(val value: String) : AddEventIntent
    data class MessageChanged(val value: String) : AddEventIntent
    data class DatePicked(val millis: Long) : AddEventIntent
    data class TimePicked(val hour: Int, val minute: Int) : AddEventIntent
    data class CategoryPicked(val category: Category) : AddEventIntent
    data object Submit : AddEventIntent
    data object DismissError : AddEventIntent
}

sealed interface AddEventEffect : UiEffect {
    data object Saved : AddEventEffect
}
