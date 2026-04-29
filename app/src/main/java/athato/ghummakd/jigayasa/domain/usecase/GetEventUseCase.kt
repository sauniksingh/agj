package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository

class GetEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: Int): Event? = repository.snapshot().firstOrNull { it.id == id }
}
