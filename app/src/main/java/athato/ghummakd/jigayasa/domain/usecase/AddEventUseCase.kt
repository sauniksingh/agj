package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.model.Category
import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository

class AddEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(
        title: String,
        message: String,
        timestamp: Long,
        category: Category = Category.GENERAL,
        amount: Double? = null,
        currencyCode: String? = null
    ): Event {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(timestamp > System.currentTimeMillis()) { "Pick a future date and time" }
        val nextId = (repository.snapshot().maxOfOrNull { it.id } ?: -1) + 1
        val safeAmount = amount?.takeIf { it > 0.0 }
        val event = Event(
            id = nextId,
            title = title.trim(),
            message = message.trim(),
            timestamp = timestamp,
            category = category.name,
            amount = safeAmount,
            currencyCode = if (safeAmount != null) currencyCode else null
        )
        repository.add(event)
        return event
    }
}
