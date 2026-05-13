package athato.ghummakd.jigayasa.presentation.list

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import athato.ghummakd.jigayasa.di.ServiceLocator
import athato.ghummakd.jigayasa.presentation.list.components.EventCard
import athato.ghummakd.jigayasa.presentation.list.components.SwipeToRevealActions
import athato.ghummakd.jigayasa.presentation.theme.GradientEnd
import athato.ghummakd.jigayasa.presentation.theme.GradientMid
import athato.ghummakd.jigayasa.presentation.theme.GradientStart
import athato.ghummakd.jigayasa.presentation.util.EventExporter
import athato.ghummakd.jigayasa.presentation.util.EventTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EventListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToView: (Int) -> Unit
) {
    val context = LocalContext.current
    val viewModel: EventListViewModel = viewModel(factory = ServiceLocator.listViewModelFactory(context))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
            viewModel.send(EventListIntent.Tick(now))
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val events = state.events
        coroutineScope.launch {
            val result = runCatching {
                val json = EventExporter.toJson(events)
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(json.toByteArray(Charsets.UTF_8))
                    } ?: error("Could not open file for writing")
                }
            }
            val message = if (result.isSuccess) {
                "Exported ${events.size} event${if (events.size == 1) "" else "s"}"
            } else {
                "Export failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                EventListEffect.NavigateToAdd -> onNavigateToAdd()
                is EventListEffect.NavigateToEdit -> onNavigateToEdit(effect.id)
                is EventListEffect.ShowDeleted -> {
                    val title = effect.title.ifBlank { "Event" }
                    snackbarHostState.showSnackbar("$title deleted")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Header(
                eventCount = state.events.size,
                exportEnabled = state.events.isNotEmpty(),
                onExportClick = { exportLauncher.launch(EventExporter.defaultFileName()) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.send(EventListIntent.AddRequested) },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Add Event") },
                containerColor = GradientStart,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GradientStart)
                    }
                }
                state.events.isEmpty() -> EmptyState()
                else -> EventList(
                    state = state,
                    now = now,
                    onDelete = { viewModel.send(EventListIntent.Delete(it.id)) },
                    onEdit = { viewModel.send(EventListIntent.Edit(it.id)) },
                    onCardClick = { onNavigateToView(it.id) }
                )
            }
        }
    }
}

@Composable
private fun Header(
    eventCount: Int,
    exportEnabled: Boolean,
    onExportClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(GradientStart, GradientMid, GradientEnd))
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Upcoming",
                    style = MaterialTheme.typography.headlineLarge,
                    color = androidx.compose.ui.graphics.Color.White
                )
                Text(
                    text = if (eventCount == 0) "No events yet" else "$eventCount event${if (eventCount == 1) "" else "s"} on your timeline",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f)
                )
            }
            IconButton(
                onClick = onExportClick,
                enabled = exportEnabled
            ) {
                Icon(
                    imageVector = Icons.Filled.FileDownload,
                    contentDescription = "Export events as JSON",
                    tint = androidx.compose.ui.graphics.Color.White.copy(
                        alpha = if (exportEnabled) 1f else 0.4f
                    )
                )
            }
        }
    }
}

@Composable
private fun EventList(
    state: EventListState,
    now: Long,
    onDelete: (athato.ghummakd.jigayasa.domain.model.Event) -> Unit,
    onEdit: (athato.ghummakd.jigayasa.domain.model.Event) -> Unit,
    onCardClick: (athato.ghummakd.jigayasa.domain.model.Event) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = state.events, key = { it.id }) { event ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) +
                    slideInVertically(animationSpec = tween(300)) { it / 4 }
            ) {
                SwipeToRevealActions(
                    item = event,
                    onEdit = onEdit,
                    onDelete = onDelete
                ) { item ->
                    EventCard(
                        event = item,
                        countdown = EventTimeFormatter.countdown(item, now),
                        isNextUpcoming = item.id == state.nextUpcomingId,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onCardClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.SwipeLeft,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No events yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Tap the button below to add one. Swipe a card left to reveal Edit and Delete.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
