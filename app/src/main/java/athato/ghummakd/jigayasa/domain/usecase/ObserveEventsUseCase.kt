package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveEventsUseCase(private val repository: EventRepository) {
    operator fun invoke(): Flow<List<Event>> =
        repository.observeEvents().map { list -> list.sortedBy { it.timestamp } }
}
