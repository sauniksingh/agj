package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository

class GetNextUpcomingEventUseCase(private val repository: EventRepository) {
    operator fun invoke(now: Long = System.currentTimeMillis()): Event? =
        repository.snapshot()
            .filter { it.timestamp > now }
            .minByOrNull { it.timestamp }
}
