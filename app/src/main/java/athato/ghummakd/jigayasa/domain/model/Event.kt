package athato.ghummakd.jigayasa.domain.model

data class Event(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long,
    val category: String? = null,
    val amount: Long? = null,
    val currencyCode: String? = null
)
