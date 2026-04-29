package athato.ghummakd.jigayasa.presentation.util

import athato.ghummakd.jigayasa.domain.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EventTimeFormatter {

    private val DISPLAY = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
    private val SHORT = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    fun formatLong(timestamp: Long): String = DISPLAY.format(Date(timestamp))
    fun formatShort(timestamp: Long): String = SHORT.format(Date(timestamp))

    data class Countdown(val days: Long, val hours: Long, val minutes: Long, val seconds: Long, val isPast: Boolean)

    fun countdown(event: Event, now: Long = System.currentTimeMillis()): Countdown {
        val diff = event.timestamp - now
        if (diff <= 0) return Countdown(0, 0, 0, 0, isPast = true)
        val days = diff / (24 * 60 * 60 * 1000)
        val hours = (diff / (60 * 60 * 1000)) % 24
        val minutes = (diff / (60 * 1000)) % 60
        val seconds = (diff / 1000) % 60
        return Countdown(days, hours, minutes, seconds, isPast = false)
    }
}
