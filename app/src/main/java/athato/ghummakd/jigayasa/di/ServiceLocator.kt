package athato.ghummakd.jigayasa.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import athato.ghummakd.jigayasa.data.local.EventLocalDataSource
import athato.ghummakd.jigayasa.data.repository.EventRepositoryImpl
import athato.ghummakd.jigayasa.domain.repository.EventRepository
import athato.ghummakd.jigayasa.domain.usecase.AddEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.DeleteEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.GetEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.GetNextUpcomingEventUseCase
import athato.ghummakd.jigayasa.domain.usecase.ObserveEventsUseCase
import athato.ghummakd.jigayasa.domain.usecase.UpdateEventUseCase
import athato.ghummakd.jigayasa.presentation.add.AddEventViewModel
import athato.ghummakd.jigayasa.presentation.list.EventListViewModel
import athato.ghummakd.jigayasa.presentation.view.ViewEventViewModel
import athato.ghummakd.jigayasa.widget.TripWidgetUpdater

object ServiceLocator {

    @Volatile private var dataSource: EventLocalDataSource? = null
    @Volatile private var repository: EventRepository? = null

    fun repository(context: Context): EventRepository {
        return repository ?: synchronized(this) {
            repository ?: build(context.applicationContext).also { repository = it }
        }
    }

    private fun build(appContext: Context): EventRepository {
        val ds = dataSource ?: EventLocalDataSource(appContext).also { dataSource = it }
        return EventRepositoryImpl(
            local = ds,
            onChange = { TripWidgetUpdater.requestUpdate(appContext) }
        )
    }

    fun observeEventsUseCase(context: Context) = ObserveEventsUseCase(repository(context))
    fun addEventUseCase(context: Context) = AddEventUseCase(repository(context))
    fun updateEventUseCase(context: Context) = UpdateEventUseCase(repository(context))
    fun deleteEventUseCase(context: Context) = DeleteEventUseCase(repository(context))
    fun getEventUseCase(context: Context) = GetEventUseCase(repository(context))
    fun nextUpcomingUseCase(context: Context) = GetNextUpcomingEventUseCase(repository(context))

    fun listViewModelFactory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                EventListViewModel(
                    observeEvents = observeEventsUseCase(context),
                    deleteEvent = deleteEventUseCase(context)
                ) as T
        }

    fun addViewModelFactory(context: Context, editingId: Int? = null): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AddEventViewModel(
                    addEvent = addEventUseCase(context),
                    updateEvent = updateEventUseCase(context),
                    getEvent = getEventUseCase(context),
                    editingId = editingId
                ) as T
        }

    fun viewViewModelFactory(context: Context, eventId: Int): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ViewEventViewModel(
                    observeEvents = observeEventsUseCase(context),
                    deleteEvent = deleteEventUseCase(context),
                    eventId = eventId
                ) as T
        }
}
