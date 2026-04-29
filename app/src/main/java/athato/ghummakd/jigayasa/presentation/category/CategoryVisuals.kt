package athato.ghummakd.jigayasa.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import athato.ghummakd.jigayasa.domain.model.Category

sealed class CategoryBadgeStyle {
    data class IconStyle(val icon: ImageVector) : CategoryBadgeStyle()
    data class LetterStyle(val text: String) : CategoryBadgeStyle()
}

data class CategoryVisual(
    val accent: Color,
    val badge: CategoryBadgeStyle,
    val miniIcon: ImageVector
)

fun Category.visual(): CategoryVisual = when (this) {
    Category.AIR_TRAVEL -> CategoryVisual(
        accent = Color(0xFF1976D2),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.Flight),
        miniIcon = Icons.Filled.Flight
    )
    Category.TRAIN_TRAVEL -> CategoryVisual(
        accent = Color(0xFF388E3C),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.Train),
        miniIcon = Icons.Filled.Train
    )
    Category.BUS_TRAVEL -> CategoryVisual(
        accent = Color(0xFFEF6C00),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.DirectionsBus),
        miniIcon = Icons.Filled.DirectionsBus
    )
    Category.TRAVEL -> CategoryVisual(
        accent = Color(0xFF00897B),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.CardTravel),
        miniIcon = Icons.Filled.CardTravel
    )
    Category.AIRTEL -> CategoryVisual(
        accent = Color(0xFFE40000),
        badge = CategoryBadgeStyle.LetterStyle("AIR"),
        miniIcon = Icons.Filled.Smartphone
    )
    Category.RENTOMOJO -> CategoryVisual(
        accent = Color(0xFF6D4C41),
        badge = CategoryBadgeStyle.LetterStyle("RM"),
        miniIcon = Icons.Filled.Chair
    )
    Category.MAINTENANCE -> CategoryVisual(
        accent = Color(0xFF455A64),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.Apartment),
        miniIcon = Icons.Filled.Apartment
    )
    Category.HDFC -> CategoryVisual(
        accent = Color(0xFF004C8F),
        badge = CategoryBadgeStyle.LetterStyle("HDFC"),
        miniIcon = Icons.Filled.AccountBalance
    )
    Category.IDFC -> CategoryVisual(
        accent = Color(0xFF7A1F2A),
        badge = CategoryBadgeStyle.LetterStyle("IDFC"),
        miniIcon = Icons.Filled.AccountBalance
    )
    Category.ICICI -> CategoryVisual(
        accent = Color(0xFFF37920),
        badge = CategoryBadgeStyle.LetterStyle("ICI"),
        miniIcon = Icons.Filled.AccountBalance
    )
    Category.AXIS -> CategoryVisual(
        accent = Color(0xFF97144D),
        badge = CategoryBadgeStyle.LetterStyle("AXIS"),
        miniIcon = Icons.Filled.AccountBalance
    )
    Category.PNB -> CategoryVisual(
        accent = Color(0xFFB6862C),
        badge = CategoryBadgeStyle.LetterStyle("PNB"),
        miniIcon = Icons.Filled.AccountBalance
    )
    Category.EKADASHI -> CategoryVisual(
        accent = Color(0xFFFF8F00),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.SelfImprovement),
        miniIcon = Icons.Filled.SelfImprovement
    )
    Category.IGL -> CategoryVisual(
        accent = Color(0xFFE65100),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.LocalFireDepartment),
        miniIcon = Icons.Filled.LocalFireDepartment
    )
    Category.VACCINATION -> CategoryVisual(
        accent = Color(0xFF00897B),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.Vaccines),
        miniIcon = Icons.Filled.Vaccines
    )
    Category.PASSPORT -> CategoryVisual(
        accent = Color(0xFF1565C0),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.Public),
        miniIcon = Icons.Filled.Public
    )
    Category.AADHAR -> CategoryVisual(
        accent = Color(0xFF6A1B9A),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.Fingerprint),
        miniIcon = Icons.Filled.Fingerprint
    )
    Category.ACCESS_SUZUKI -> CategoryVisual(
        accent = Color(0xFF1A237E),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.TwoWheeler),
        miniIcon = Icons.Filled.TwoWheeler
    )
    Category.INSURANCE -> CategoryVisual(
        accent = Color(0xFF2E7D32),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.HealthAndSafety),
        miniIcon = Icons.Filled.HealthAndSafety
    )
    Category.LIC -> CategoryVisual(
        accent = Color(0xFFC8102E),
        badge = CategoryBadgeStyle.LetterStyle("LIC"),
        miniIcon = Icons.Filled.Shield
    )
    Category.BAJAJ -> CategoryVisual(
        accent = Color(0xFF002F6C),
        badge = CategoryBadgeStyle.LetterStyle("BAJ"),
        miniIcon = Icons.Filled.AccountBalanceWallet
    )
    Category.CAR -> CategoryVisual(
        accent = Color(0xFF424242),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.DirectionsCar),
        miniIcon = Icons.Filled.DirectionsCar
    )
    Category.SCOOTER -> CategoryVisual(
        accent = Color(0xFF455A64),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.TwoWheeler),
        miniIcon = Icons.Filled.TwoWheeler
    )
    Category.DRIVING_LICENSE -> CategoryVisual(
        accent = Color(0xFF6D4C41),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.CreditCard),
        miniIcon = Icons.Filled.Badge
    )
    Category.GENERAL -> CategoryVisual(
        accent = Color(0xFF1976D2),
        badge = CategoryBadgeStyle.IconStyle(Icons.Filled.Event),
        miniIcon = Icons.Filled.Event
    )
}

/**
 * Visual badge for a category. Renders either an icon or a short brand-letter on a
 * tinted background. When [highlightGradient] is non-null, the badge background uses
 * that gradient (used for the next-upcoming card).
 */
@Composable
fun CategoryBadge(
    category: Category,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    highlightGradient: Brush? = null
) {
    val visual = category.visual()
    val backgroundBrush = when {
        highlightGradient != null -> highlightGradient
        visual.badge is CategoryBadgeStyle.LetterStyle ->
            Brush.linearGradient(listOf(visual.accent, visual.accent))
        else ->
            Brush.linearGradient(
                listOf(visual.accent.copy(alpha = 0.18f), visual.accent.copy(alpha = 0.10f))
            )
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3.2f))
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        when (val b = visual.badge) {
            is CategoryBadgeStyle.IconStyle -> Icon(
                imageVector = b.icon,
                contentDescription = category.displayName,
                tint = if (highlightGradient != null) Color.White else visual.accent,
                modifier = Modifier.size(size * 0.55f)
            )
            is CategoryBadgeStyle.LetterStyle -> Text(
                text = b.text,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = letterFontSizeFor(size, b.text.length),
                maxLines = 1
            )
        }
    }
}

@Composable
fun CategoryMiniIcon(
    category: Category,
    modifier: Modifier = Modifier
) {
    val visual = category.visual()
    Icon(
        imageVector = visual.miniIcon,
        contentDescription = category.displayName,
        tint = visual.accent,
        modifier = modifier
    )
}

private fun letterFontSizeFor(size: Dp, length: Int): androidx.compose.ui.unit.TextUnit {
    val baseSp = when (length) {
        1 -> size.value * 0.55f
        2 -> size.value * 0.40f
        3 -> size.value * 0.30f
        4 -> size.value * 0.25f
        else -> size.value * 0.22f
    }
    return baseSp.sp
}
