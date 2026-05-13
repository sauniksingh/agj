package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository

class GetNextUpcomingEventUseCase(private val repository: EventRepository) {
    /**
     * Returns all upcoming events sharing the earliest future timestamp. If two events
     * share the same minute (and the user picked seconds=0 via the time picker), they are
     * grouped here so callers can render them as a single merged entry.
     */
    operator fun invoke(now: Long = System.currentTimeMillis()): List<Event> {
        val upcoming = repository.snapshot().filter { it.timestamp > now }
        val earliest = upcoming.minByOrNull { it.timestamp }?.timestamp ?: return emptyList()
        return upcoming.filter { it.timestamp == earliest }
    }
}
