package athato.ghummakd.jigayasa.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.roundToLong

object AmountFormatter {

    /**
     * Indian grouping with optional fractional part. Last three integer digits, then groups
     * of two (1,00,000). Anything after a single '.' is kept as the decimal part (digits only).
     */
    fun groupIndian(input: String): String {
        val dotIndex = input.indexOf('.')
        val intRaw = if (dotIndex >= 0) input.substring(0, dotIndex) else input
        val fracRaw = if (dotIndex >= 0) input.substring(dotIndex + 1) else null
        val intDigits = intRaw.filter { it.isDigit() }
        val fracDigits = fracRaw?.filter { it.isDigit() }
        val groupedInt = if (intDigits.length <= 3) intDigits else {
            val last3 = intDigits.takeLast(3)
            val rest = intDigits.dropLast(3)
            val groups = ArrayDeque<String>()
            var s = rest
            while (s.length > 2) {
                groups.addFirst(s.takeLast(2))
                s = s.dropLast(2)
            }
            if (s.isNotEmpty()) groups.addFirst(s)
            groups.joinToString(",") + "," + last3
        }
        return if (fracDigits == null) groupedInt else "$groupedInt.$fracDigits"
    }

    fun groupIndian(amount: Long): String = groupIndian(amount.toString())

    /** Indian-grouped display for a Double, keeping up to 2 decimal places (trailing zeros trimmed). */
    fun groupIndian(amount: Double): String {
        val cents = (amount * 100.0).roundToLong()
        val sign = if (cents < 0) "-" else ""
        val abs = if (cents < 0) -cents else cents
        val intPart = abs / 100
        val frac = abs % 100
        val intGrouped = groupIndian(intPart.toString())
        val fracStr = when {
            frac == 0L -> ""
            frac % 10 == 0L -> ".${frac / 10}"
            else -> ".%02d".format(frac)
        }
        return sign + intGrouped + fracStr
    }

    /** Parse a free-form text and extract a rupee amount, e.g. "₹14,003" or "₹2,357.64". */
    private val AMOUNT_REGEX = Regex("""[₹$€£¥]\s*([\d,]+(?:\.\d+)?)""")

    fun parseAmountFromText(text: String): Double? {
        val match = AMOUNT_REGEX.find(text) ?: return null
        val raw = match.groupValues[1].replace(",", "")
        return raw.toDoubleOrNull()
    }

    fun stripAmountFromText(text: String): String =
        text.replace(AMOUNT_REGEX, "").trim().trim(',', '/', '·', '-').trim()
}

/**
 * Visual transformation that displays digits in Indian grouping style while keeping the
 * underlying value intact. The raw text can contain digits and at most one '.'; only ','
 * separators are added by formatting, so offset mapping counts non-comma characters.
 */
class IndianAmountVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val formatted = AmountFormatter.groupIndian(raw)
        if (formatted == raw) return TransformedText(text, OffsetMapping.Identity)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safe = offset.coerceIn(0, raw.length)
                if (safe == 0) return 0
                var pos = 0
                var preserved = 0
                while (pos < formatted.length && preserved < safe) {
                    if (formatted[pos] != ',') preserved++
                    pos++
                }
                return pos
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safe = offset.coerceIn(0, formatted.length)
                var preserved = 0
                for (i in 0 until safe) {
                    if (formatted[i] != ',') preserved++
                }
                return preserved
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
