package athato.ghummakd.jigayasa.data.local

import android.content.Context
import android.content.SharedPreferences
import athato.ghummakd.jigayasa.R
import athato.ghummakd.jigayasa.domain.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

class EventLocalDataSource(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _events = MutableStateFlow(loadInitial())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    fun add(event: Event) {
        val updated = (_events.value + event).distinctBy { it.id }
        persist(updated)
    }

    fun update(event: Event) {
        val current = _events.value
        val index = current.indexOfFirst { it.id == event.id }
        val updated = if (index >= 0) {
            current.toMutableList().also { it[index] = event }
        } else {
            current + event
        }
        persist(updated)
    }

    fun delete(id: Int) {
        val updated = _events.value.filterNot { it.id == id }
        persist(updated)
    }

    fun snapshot(): List<Event> = _events.value

    private fun persist(events: List<Event>) {
        _events.value = events
        prefs.edit()
            .putString(KEY_EVENTS, gson.toJson(events))
            .putBoolean(KEY_SEEDED, true)
            .apply()
    }

    private fun loadInitial(): List<Event> {
        if (prefs.getBoolean(KEY_SEEDED, false)) {
            val json = prefs.getString(KEY_EVENTS, null) ?: return emptyList()
            val type = object : TypeToken<List<Event>>() {}.type
            return runCatching { gson.fromJson<List<Event>>(json, type) }.getOrNull().orEmpty()
        }
        val seeded = readSeedFromRaw()
        prefs.edit()
            .putString(KEY_EVENTS, gson.toJson(seeded))
            .putBoolean(KEY_SEEDED, true)
            .apply()
        return seeded
    }

    private fun readSeedFromRaw(): List<Event> = runCatching {
        val raw = context.resources.openRawResource(R.raw.todo).use { input ->
            String(input.readBytes(), StandardCharsets.UTF_8)
        }
        val type = object : TypeToken<List<SeedDto>>() {}.type
        val seeds: List<SeedDto> = gson.fromJson(raw, type)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        seeds.mapIndexedNotNull { index, dto ->
            val ts = dto.timeStamp ?: return@mapIndexedNotNull null
            val parsed = runCatching { format.parse(ts)?.time }.getOrNull() ?: return@mapIndexedNotNull null
            Event(
                id = index,
                title = dto.title.orEmpty(),
                message = dto.greetingMsg.orEmpty(),
                timestamp = parsed
            )
        }
    }.getOrElse { emptyList() }

    private data class SeedDto(
        val title: String? = null,
        val greetingMsg: String? = null,
        val timeStamp: String? = null,
        val key: Int = 0
    )

    companion object {
        private const val PREFS_NAME = "agj_events"
        private const val KEY_EVENTS = "events_json"
        private const val KEY_SEEDED = "seeded"
    }
}
