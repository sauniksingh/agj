package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository

class UpdateEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: Int, title: String, message: String, timestamp: Long): Event {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(timestamp > System.currentTimeMillis()) { "Pick a future date and time" }
        val event = Event(id = id, title = title.trim(), message = message.trim(), timestamp = timestamp)
        repository.update(event)
        return event
    }
}
