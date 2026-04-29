package athato.ghummakd.jigayasa.data.repository

import athato.ghummakd.jigayasa.data.local.EventLocalDataSource
import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class EventRepositoryImpl(
    private val local: EventLocalDataSource,
    private val onChange: () -> Unit = {}
) : EventRepository {

    override fun observeEvents(): Flow<List<Event>> = local.events

    override suspend fun add(event: Event) {
        local.add(event)
        onChange()
    }

    override suspend fun update(event: Event) {
        local.update(event)
        onChange()
    }

    override suspend fun delete(id: Int) {
        local.delete(id)
        onChange()
    }

    override fun snapshot(): List<Event> = local.snapshot()
}
