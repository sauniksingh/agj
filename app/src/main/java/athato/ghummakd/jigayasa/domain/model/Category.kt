package athato.ghummakd.jigayasa.domain.model

/**
 * Event category with keywords used for auto-detection from title.
 * Order matters: earlier entries take precedence when multiple keywords match.
 */
enum class Category(val displayName: String, val keywords: List<String>) {
    AIR_TRAVEL(
        "Air Travel",
        listOf("flight", "airline", "airport", "del-", "ahm-", "blr-", "bom-", "ccu-", "hyd-", "maa-", "6e-", "ai-", "uk-", "g8-", "indigo", "vistara", "spicejet")
    ),
    BAJAJ("Bajaj Finance", listOf("bajaj")),
    LIC("LIC", listOf("lic")),
    HDFC("HDFC", listOf("hdfc")),
    ICICI("ICICI", listOf("icici")),
    IDFC("IDFC", listOf("idfc")),
    AXIS("Axis Bank", listOf("axis")),
    PNB("PNB", listOf("pnb", "punjab national")),
    AIRTEL("Airtel", listOf("airtel")),
    RENTOMOJO("Rentomojo", listOf("rentomojo")),
    EKADASHI("Fasting", listOf("ekadashi", "vrat", "fasting")),
    IGL("Gas / IGL", listOf("igl", "png gas", "gas pipeline")),
    VACCINATION("Vaccination", listOf("vaccin")),
    PASSPORT("Passport", listOf("passport")),
    AADHAR("Aadhaar", listOf("aadhar", "aadhaar")),
    ACCESS_SUZUKI("Access (Suzuki)", listOf("access", "suzuki")),
    INSURANCE("Insurance", listOf("insurance", "policy")),
    CAR("Car", listOf("harrier", "tata car", "car ", " car", "service")),
    SCOOTER("Scooter", listOf("scooty", "scooter")),
    DRIVING_LICENSE("Driving Licence", listOf("driving", "licence", "license", "form 6", " dl ", " dl")),
    MAINTENANCE("Maintenance", listOf("maintenance", "ace city", "society")),
    TRAIN_TRAVEL("Train Travel", listOf("train", "irctc", "railway")),
    BUS_TRAVEL("Bus Travel", listOf("bus", "redbus")),
    TRAVEL("Travel", listOf("trip", "vacation", "hotel", "tour")),
    GENERAL("General", emptyList());

    companion object {
        fun fromTitle(title: String): Category {
            if (title.isBlank()) return GENERAL
            val padded = " ${title.lowercase()} "
            return entries.firstOrNull { cat ->
                cat != GENERAL && cat.keywords.any { kw -> padded.contains(kw.lowercase()) }
            } ?: GENERAL
        }

        fun fromName(name: String?): Category =
            name?.let { stored -> entries.firstOrNull { it.name == stored } } ?: GENERAL

        fun resolve(stored: String?, title: String): Category =
            if (stored == null) fromTitle(title) else fromName(stored)
    }
}
