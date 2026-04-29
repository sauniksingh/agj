package athato.ghummakd.jigayasa.presentation.list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import athato.ghummakd.jigayasa.presentation.theme.DangerRed
import athato.ghummakd.jigayasa.presentation.theme.Indigo40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class RevealValue { Resting, Revealed }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SwipeToRevealActions(
    item: T,
    onEdit: (T) -> Unit,
    onDelete: (T) -> Unit,
    deleteAnimationDurationMillis: Int = 300,
    content: @Composable (T) -> Unit
) {
    val density = LocalDensity.current
    val actionsWidthDp = 152.dp
    val actionsWidthPx = with(density) { actionsWidthDp.toPx() }
    val decay = rememberSplineBasedDecay<Float>()
    val scope = rememberCoroutineScope()
    var isRemoved by remember { mutableStateOf(false) }

    val state = remember(actionsWidthPx) {
        AnchoredDraggableState(
            initialValue = RevealValue.Resting,
            anchors = DraggableAnchors {
                RevealValue.Resting at 0f
                RevealValue.Revealed at -actionsWidthPx
            },
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { actionsWidthPx },
            snapAnimationSpec = tween(220),
            decayAnimationSpec = decay
        )
    }

    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            delay(deleteAnimationDurationMillis.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(animationSpec = tween(deleteAnimationDurationMillis)) +
            fadeOut(animationSpec = tween(deleteAnimationDurationMillis))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.matchParentSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionTile(
                    icon = Icons.Filled.Edit,
                    label = "Edit",
                    background = Indigo40,
                    onClick = {
                        scope.launch { state.animateTo(RevealValue.Resting) }
                        onEdit(item)
                    }
                )
                Spacer(Modifier.width(8.dp))
                ActionTile(
                    icon = Icons.Filled.Delete,
                    label = "Delete",
                    background = DangerRed,
                    onClick = { isRemoved = true }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(state.requireOffset().toInt(), 0) }
                    .anchoredDraggable(state, Orientation.Horizontal)
            ) {
                content(item)
            }
        }
    }
}

@Composable
private fun ActionTile(
    icon: ImageVector,
    label: String,
    background: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(72.dp)
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
