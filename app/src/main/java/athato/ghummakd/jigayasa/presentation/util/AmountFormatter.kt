package athato.ghummakd.jigayasa.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object AmountFormatter {

    /** Indian grouping: last three digits, then groups of two (1,00,000). */
    fun groupIndian(digits: String): String {
        val clean = digits.filter { it.isDigit() }
        if (clean.length <= 3) return clean
        val last3 = clean.takeLast(3)
        val rest = clean.dropLast(3)
        val groups = ArrayDeque<String>()
        var s = rest
        while (s.length > 2) {
            groups.addFirst(s.takeLast(2))
            s = s.dropLast(2)
        }
        if (s.isNotEmpty()) groups.addFirst(s)
        return groups.joinToString(",") + "," + last3
    }

    fun groupIndian(amount: Long): String = groupIndian(amount.toString())

    /** Parse a free-form text and extract a rupee amount, e.g. "₹14,003" or "₹2,357.64". */
    private val AMOUNT_REGEX = Regex("""[₹$€£¥]\s*([\d,]+(?:\.\d+)?)""")

    fun parseAmountFromText(text: String): Long? {
        val match = AMOUNT_REGEX.find(text) ?: return null
        val raw = match.groupValues[1].replace(",", "")
        return raw.toBigDecimalOrNull()?.toLong()
    }

    fun stripAmountFromText(text: String): String =
        text.replace(AMOUNT_REGEX, "").trim().trim(',', '/', '·', '-').trim()
}

/**
 * Visual transformation that displays digits in Indian grouping style while keeping the
 * underlying value digits-only. Cursor offsets are mapped between digit-space and grouped-space.
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
                var digits = 0
                while (pos < formatted.length && digits < safe) {
                    if (formatted[pos].isDigit()) digits++
                    pos++
                }
                return pos
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safe = offset.coerceIn(0, formatted.length)
                var digits = 0
                for (i in 0 until safe) {
                    if (formatted[i].isDigit()) digits++
                }
                return digits
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
