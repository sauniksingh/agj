package athato.ghummakd.jigayasa.presentation.list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import athato.ghummakd.jigayasa.domain.model.Category
import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.model.SupportedCurrencies
import athato.ghummakd.jigayasa.presentation.category.CategoryBadge
import athato.ghummakd.jigayasa.presentation.category.visual
import athato.ghummakd.jigayasa.presentation.theme.GradientEnd
import athato.ghummakd.jigayasa.presentation.theme.GradientMid
import athato.ghummakd.jigayasa.presentation.theme.GradientStart
import athato.ghummakd.jigayasa.presentation.util.AmountFormatter
import athato.ghummakd.jigayasa.presentation.util.EventTimeFormatter

@Composable
fun EventCard(
    event: Event,
    countdown: EventTimeFormatter.Countdown,
    isNextUpcoming: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val category = remember(event.id, event.category, event.title) {
        Category.resolve(event.category, event.title)
    }
    val accent = remember(category, isNextUpcoming) {
        if (isNextUpcoming) GradientStart else category.visual().accent
    }
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.92f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val cardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .border(
                width = if (isNextUpcoming) 1.5.dp else 0.dp,
                brush = Brush.linearGradient(listOf(GradientStart, GradientMid, GradientEnd)),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = cardColors,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNextUpcoming) 8.dp else 2.dp)
    ) {
        Box(
            modifier = if (isNextUpcoming) {
                Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = pulse * 0.18f),
                            GradientMid.copy(alpha = pulse * 0.14f),
                            GradientEnd.copy(alpha = pulse * 0.18f)
                        )
                    )
                )
            } else Modifier
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(category = category, size = 46.dp)
                Spacer(Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    AnimatedVisibility(visible = isNextUpcoming, enter = fadeIn(), exit = fadeOut()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = GradientEnd,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.size(4.dp))
                            Text(
                                text = "NEXT UP",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GradientStart
                                )
                            )
                        }
                    }
                    Text(
                        text = event.title.ifBlank { "Untitled" },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = EventTimeFormatter.formatLong(event.timestamp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    val amountLine = remember(event.amount, event.currencyCode) {
                        event.amount?.takeIf { it > 0.0 }?.let { amt ->
                            val symbol = SupportedCurrencies.find(event.currencyCode).symbol
                            "$symbol${AmountFormatter.groupIndian(amt)}"
                        }
                    }
                    val secondary = amountLine ?: event.message.takeIf { it.isNotBlank() }
                    if (secondary != null) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = secondary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (amountLine != null) FontWeight.SemiBold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    CountdownDisplay(countdown = countdown, accent = accent)
                }
            }
        }
    }
}
