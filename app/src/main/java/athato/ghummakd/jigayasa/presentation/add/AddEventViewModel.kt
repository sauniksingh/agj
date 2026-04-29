package athato.ghummakd.jigayasa.presentation.add

import androidx.lifecycle.viewModelScope
import athato.ghummakd.jigayasa.domain.usecase.AddEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.GetEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.UpdateEventUseCase
import athato.ghummakd.jigayasa.presentation.mvi.MviViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

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
                    val cal = Calendar.getInstance().apply { timeInMillis = event.timestamp }
                    val dayOnly = Calendar.getInstance().apply {
                        timeInMillis = event.timestamp
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    setState {
                        copy(
                            title = event.title,
                            message = event.message,
                            pickedDateMillis = dayOnly.timeInMillis,
                            pickedHour = cal.get(Calendar.HOUR_OF_DAY),
                            pickedMinute = cal.get(Calendar.MINUTE)
                        )
                    }
                }
            }
        }
    }

    override suspend fun handle(intent: AddEventIntent) {
        when (intent) {
            is AddEventIntent.TitleChanged -> setState { copy(title = intent.value, error = null) }
            is AddEventIntent.MessageChanged -> setState { copy(message = intent.value) }
            is AddEventIntent.DatePicked -> setState { copy(pickedDateMillis = intent.millis, error = null) }
            is AddEventIntent.TimePicked -> setState {
                copy(pickedHour = intent.hour, pickedMinute = intent.minute, error = null)
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
                addEvent(current.title, current.message, ts)
            } else {
                updateEvent(current.editingId, current.title, current.message, ts)
            }
        }.onSuccess {
            setState { copy(isSubmitting = false) }
            emitEffect(AddEventEffect.Saved)
        }.onFailure { e ->
            setState { copy(isSubmitting = false, error = e.message ?: "Failed to save") }
        }
    }
}
