package athato.ghummakd.jigayasa.domain.repository

import athato.ghummakd.jigayasa.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun observeEvents(): Flow<List<Event>>
    fun snapshot(): List<Event>
    suspend fun add(event: Event)
    suspend fun update(event: Event)
    suspend fun delete(id: Int)
}
