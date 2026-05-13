package athato.ghummakd.jigayasa.presentation.util

import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.model.SupportedCurrencies
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Serialises events into the same shape used by `res/raw/todo.json` so an export can be
 * re-imported or hand-edited without surprises:
 *
 * [
 *   { "title": "...", "timeStamp": "yyyy-MM-dd HH:mm", "key": 1, "greetingMsg": "..." }
 * ]
 *
 * `greetingMsg` is the amount string (`₹14,003`) plus the note when both are present, and is
 * omitted entirely when neither exists.
 */
object EventExporter {

    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    fun toJson(events: List<Event>): String {
        val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val payload = events
            .sortedBy { it.timestamp }
            .map { event ->
                val map = linkedMapOf<String, Any?>()
                map["title"] = event.title
                map["timeStamp"] = timestampFormat.format(Date(event.timestamp))
                map["key"] = event.id
                buildGreetingMsg(event)?.let { map["greetingMsg"] = it }
                map
            }
        return gson.toJson(payload)
    }

    private fun buildGreetingMsg(event: Event): String? {
        val amountText = event.amount?.takeIf { it > 0.0 }?.let { amt ->
            val symbol = SupportedCurrencies.find(event.currencyCode).symbol
            "$symbol${AmountFormatter.groupIndian(amt)}"
        }
        val note = event.message.takeIf { it.isNotBlank() }
        return when {
            amountText != null && note != null -> "$amountText $note"
            amountText != null -> amountText
            note != null -> note
            else -> null
        }
    }

    fun defaultFileName(): String {
        val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
        return "agj-events-$stamp.json"
    }
}
