package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.model.Category
import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository

class UpdateEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(
        id: Int,
        title: String,
        message: String,
        timestamp: Long,
        category: Category = Category.GENERAL,
        amount: Long? = null,
        currencyCode: String? = null
    ): Event {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(timestamp > System.currentTimeMillis()) { "Pick a future date and time" }
        val event = Event(
            id = id,
            title = title.trim(),
            message = message.trim(),
            timestamp = timestamp,
            category = category.name,
            amount = amount?.takeIf { it > 0 },
            currencyCode = if (amount != null && amount > 0) currencyCode else null
        )
        repository.update(event)
        return event
    }
}
