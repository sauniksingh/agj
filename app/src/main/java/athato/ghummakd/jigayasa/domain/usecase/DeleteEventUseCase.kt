package athato.ghummakd.jigayasa.domain.usecase

import athato.ghummakd.jigayasa.domain.repository.EventRepository

class DeleteEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: Int) = repository.delete(id)
}
