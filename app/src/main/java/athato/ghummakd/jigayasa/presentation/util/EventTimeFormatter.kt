package athato.ghummakd.jigayasa.presentation.util

import athato.ghummakd.jigayasa.domain.model.Event
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object EventTimeFormatter {

    private val DISPLAY = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
    private val SHORT = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    fun formatLong(timestamp: Long): String = DISPLAY.format(Date(timestamp))
    fun formatShort(timestamp: Long): String = SHORT.format(Date(timestamp))

    data class Segment(val value: Long, val label: String)

    data class Countdown(
        val years: Long,
        val months: Long,
        val daysOfMonth: Long,
        val hoursOfDay: Long,
        val minutesOfHour: Long,
        val secondsOfMinute: Long,
        val totalDays: Long,
        val isPast: Boolean
    ) {
        /** Top three non-zero segments, in order of magnitude. Skips zero units. */
        val displaySegments: List<Segment>
            get() {
                if (isPast) return emptyList()
                val nonZero = listOf(
                    Segment(years, "Y"),
                    Segment(months, "M"),
                    Segment(daysOfMonth, "D"),
                    Segment(hoursOfDay, "H"),
                    Segment(minutesOfHour, "min"),
                    Segment(secondsOfMinute, "s")
                ).filter { it.value > 0 }.take(3)
                return nonZero.ifEmpty { listOf(Segment(0, "s")) }
            }
    }

    fun countdown(event: Event, now: Long = System.currentTimeMillis()): Countdown {
        val zone = ZoneId.systemDefault()
        val nowZdt = Instant.ofEpochMilli(now).atZone(zone)
        val targetZdt = Instant.ofEpochMilli(event.timestamp).atZone(zone)

        if (!targetZdt.isAfter(nowZdt)) {
            return Countdown(0, 0, 0, 0, 0, 0, 0, isPast = true)
        }

        var cursor = nowZdt
        val years = ChronoUnit.YEARS.between(cursor, targetZdt)
        cursor = cursor.plusYears(years)
        val months = ChronoUnit.MONTHS.between(cursor, targetZdt)
        cursor = cursor.plusMonths(months)
        val days = ChronoUnit.DAYS.between(cursor, targetZdt)
        cursor = cursor.plusDays(days)
        val hours = ChronoUnit.HOURS.between(cursor, targetZdt)
        cursor = cursor.plusHours(hours)
        val minutes = ChronoUnit.MINUTES.between(cursor, targetZdt)
        cursor = cursor.plusMinutes(minutes)
        val seconds = ChronoUnit.SECONDS.between(cursor, targetZdt)

        val totalDays = (event.timestamp - now) / (24L * 60 * 60 * 1000)

        return Countdown(
            years = years,
            months = months,
            daysOfMonth = days,
            hoursOfDay = hours,
            minutesOfHour = minutes,
            secondsOfMinute = seconds,
            totalDays = totalDays,
            isPast = false
        )
    }
}
