package athato.ghummakd.jigayasa.domain.model

data class CurrencyOption(
    val code: String,
    val symbol: String,
    val displayName: String
)

object SupportedCurrencies {
    val ALL: List<CurrencyOption> = listOf(
        CurrencyOption("INR", "₹", "Indian Rupee"),
        CurrencyOption("USD", "$", "US Dollar"),
        CurrencyOption("EUR", "€", "Euro"),
        CurrencyOption("GBP", "£", "British Pound"),
        CurrencyOption("AED", "د.إ", "UAE Dirham"),
        CurrencyOption("SGD", "S$", "Singapore Dollar"),
        CurrencyOption("AUD", "A$", "Australian Dollar"),
        CurrencyOption("CAD", "C$", "Canadian Dollar"),
        CurrencyOption("JPY", "¥", "Japanese Yen"),
    )

    val DEFAULT: CurrencyOption = ALL.first()

    fun find(code: String?): CurrencyOption =
        code?.let { wanted -> ALL.firstOrNull { it.code == wanted } } ?: DEFAULT
}
