package athato.ghummakd.jigayasa.presentation.add

import androidx.lifecycle.viewModelScope
import athato.ghummakd.jigayasa.domain.model.Category
import athato.ghummakd.jigayasa.domain.usecase.AddEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.GetEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.UpdateEventUseCase
import athato.ghummakd.jigayasa.presentation.mvi.MviViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class AddEventViewModel(
    private val addEvent: AddEventUseCase,
    private val updateEvent: UpdateEventUseCase,
    private val getEvent: GetEventUseCase,
    private val editingId: Int?
) : MviViewModel<AddEventIntent, AddEventState, AddEventEffect>(AddEventState(editingId = editingId)) {

    init {
        if (editingId != null) {
            viewModelScope.launch {
                getEvent(editingId)?.let { event ->
                    val local = Calendar.getInstance().apply { timeInMillis = event.timestamp }
                    val utcMidnight = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        clear()
                        set(
                            local.get(Calendar.YEAR),
                            local.get(Calendar.MONTH),
                            local.get(Calendar.DAY_OF_MONTH),
                            0,
                            0,
                            0
                        )
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    setState {
                        copy(
                            title = event.title,
                            message = event.message,
                            pickedDateMillis = utcMidnight,
                            pickedHour = local.get(Calendar.HOUR_OF_DAY),
                            pickedMinute = local.get(Calendar.MINUTE),
                            category = Category.resolve(event.category, event.title),
                            categoryManuallyPicked = true
                        )
                    }
                }
            }
        }
    }

    override suspend fun handle(intent: AddEventIntent) {
        when (intent) {
            is AddEventIntent.TitleChanged -> setState {
                val newCategory = if (categoryManuallyPicked) category else Category.fromTitle(intent.value)
                copy(title = intent.value, category = newCategory, error = null)
            }
            is AddEventIntent.MessageChanged -> setState { copy(message = intent.value) }
            is AddEventIntent.DatePicked -> setState { copy(pickedDateMillis = intent.millis, error = null) }
            is AddEventIntent.TimePicked -> setState {
                copy(pickedHour = intent.hour, pickedMinute = intent.minute, error = null)
            }
            is AddEventIntent.CategoryPicked -> setState {
                copy(category = intent.category, categoryManuallyPicked = true)
            }
            AddEventIntent.DismissError -> setState { copy(error = null) }
            AddEventIntent.Submit -> submit()
        }
    }

    private suspend fun submit() {
        val current = state.value
        val ts = current.combinedTimestamp
        if (current.title.isBlank()) {
            setState { copy(error = "Title is required") }
            return
        }
        if (ts == null) {
            setState { copy(error = "Pick a date and time") }
            return
        }
        if (ts <= System.currentTimeMillis()) {
            setState { copy(error = "Pick a future date and time") }
            return
        }
        setState { copy(isSubmitting = true, error = null) }
        runCatching {
            if (current.editingId == null) {
                addEvent(current.title, current.message, ts, current.category)
            } else {
                updateEvent(current.editingId, current.title, current.message, ts, current.category)
            }
        }.onSuccess {
            setState { copy(isSubmitting = false) }
            emitEffect(AddEventEffect.Saved)
        }.onFailure { e ->
            setState { copy(isSubmitting = false, error = e.message ?: "Failed to save") }
        }
    }
}
