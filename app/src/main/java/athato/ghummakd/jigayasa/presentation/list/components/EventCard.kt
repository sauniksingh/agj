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
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.presentation.theme.GradientEnd
import athato.ghummakd.jigayasa.presentation.theme.GradientMid
import athato.ghummakd.jigayasa.presentation.theme.GradientStart
import athato.ghummakd.jigayasa.presentation.util.EventTimeFormatter

@Composable
fun EventCard(
    event: Event,
    countdown: EventTimeFormatter.Countdown,
    isNextUpcoming: Boolean,
    modifier: Modifier = Modifier
) {
    val accent = remember(event.id, isNextUpcoming) {
        if (isNextUpcoming) GradientStart else accentForId(event.id)
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
    val cardColors = if (isNextUpcoming) {
        CardDefaults.cardColors(containerColor = Color.Transparent)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
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
                IconBadge(accent = accent, isNext = isNextUpcoming)
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
                    if (event.message.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = event.message,
                            style = MaterialTheme.typography.bodyMedium,
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

@Composable
private fun IconBadge(accent: Color, isNext: Boolean) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isNext)
                    Brush.linearGradient(listOf(GradientStart, GradientMid, GradientEnd))
                else
                    Brush.linearGradient(listOf(accent.copy(alpha = 0.18f), accent.copy(alpha = 0.10f)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Event,
            contentDescription = null,
            tint = if (isNext) Color.White else accent
        )
    }
}

private fun accentForId(id: Int): Color {
    val palette = listOf(
        Color(0xFF3F51B5),
        Color(0xFF00897B),
        Color(0xFFD81B60),
        Color(0xFF8E24AA),
        Color(0xFFFB8C00),
        Color(0xFF1E88E5)
    )
    return palette[(id % palette.size + palette.size) % palette.size]
}
