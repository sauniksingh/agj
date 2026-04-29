package athato.ghummakd.jigayasa.presentation.list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import athato.ghummakd.jigayasa.presentation.util.EventTimeFormatter

@Composable
fun CountdownDisplay(
    countdown: EventTimeFormatter.Countdown,
    accent: Color,
    modifier: Modifier = Modifier
) {
    if (countdown.isPast) {
        Text(
            text = "Past",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelLarge,
            modifier = modifier
        )
        return
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (countdown.days > 0) UnitChip(value = countdown.days, label = "d", accent = accent)
        UnitChip(value = countdown.hours, label = "h", accent = accent)
        UnitChip(value = countdown.minutes, label = "m", accent = accent)
        if (countdown.days == 0L) UnitChip(value = countdown.seconds, label = "s", accent = accent)
    }
}

@Composable
private fun UnitChip(value: Long, label: String, accent: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                    } else {
                        slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                label = "countdown_value"
            ) { v ->
                Text(
                    text = "%02d".format(v),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                )
            }
            Spacer(Modifier.width(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = accent.copy(alpha = 0.8f)
            )
        }
    }
}
