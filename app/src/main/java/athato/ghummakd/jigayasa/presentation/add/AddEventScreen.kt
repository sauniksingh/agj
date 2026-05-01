package athato.ghummakd.jigayasa.presentation.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import athato.ghummakd.jigayasa.di.ServiceLocator
import athato.ghummakd.jigayasa.domain.model.Category
import athato.ghummakd.jigayasa.domain.model.SupportedCurrencies
import athato.ghummakd.jigayasa.presentation.category.CategoryMiniIcon
import athato.ghummakd.jigayasa.presentation.category.visual
import athato.ghummakd.jigayasa.presentation.theme.GradientEnd
import athato.ghummakd.jigayasa.presentation.theme.GradientMid
import athato.ghummakd.jigayasa.presentation.theme.GradientStart
import athato.ghummakd.jigayasa.presentation.util.IndianAmountVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(onClose: () -> Unit, editingId: Int? = null) {
    val context = LocalContext.current
    val viewModel: AddEventViewModel = viewModel(
        key = "add_event_${editingId ?: "new"}",
        factory = ServiceLocator.addViewModelFactory(context, editingId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                AddEventEffect.Saved -> onClose()
            }
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.send(AddEventIntent.DismissError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Event" else "New Event") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Banner(isEditing = state.isEditing)
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.send(AddEventIntent.TitleChanged(it)) },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            AmountRow(
                amountInput = state.amountInput,
                currencyCode = state.currencyCode,
                onAmountChange = { viewModel.send(AddEventIntent.AmountChanged(it)) },
                onCurrencyChange = { viewModel.send(AddEventIntent.CurrencyPicked(it)) }
            )
            OutlinedTextField(
                value = state.message,
                onValueChange = { viewModel.send(AddEventIntent.MessageChanged(it)) },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            CategorySelector(
                selected = state.category,
                onPick = { viewModel.send(AddEventIntent.CategoryPicked(it)) }
            )
            PickerRow(
                icon = Icons.Filled.DateRange,
                label = "Date",
                value = state.pickedDateMillis?.let { formatPickedDate(it) } ?: "Pick a date",
                onClick = { showDatePicker = true }
            )
            PickerRow(
                icon = Icons.Filled.Schedule,
                label = "Time",
                value = if (state.pickedHour != null && state.pickedMinute != null) {
                    "%02d:%02d".format(state.pickedHour, state.pickedMinute)
                } else "Pick a time",
                onClick = { showTimePicker = true }
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.send(AddEventIntent.Submit) },
                enabled = state.isValid && !state.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart,
                    disabledContainerColor = GradientStart.copy(alpha = 0.4f),
                    contentColor = Color.White
                )
            ) {
                AnimatedVisibility(
                    visible = state.isSubmitting,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                }
                AnimatedVisibility(
                    visible = !state.isSubmitting,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { -it } + fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = if (state.isEditing) "Update Event" else "Save Event",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        FutureDatePickerDialog(
            initialMillis = state.pickedDateMillis ?: defaultUtcMidnightForToday(),
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                viewModel.send(AddEventIntent.DatePicked(millis))
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        FutureTimePickerDialog(
            pickedDateMillis = state.pickedDateMillis,
            initialHour = state.pickedHour,
            initialMinute = state.pickedMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                viewModel.send(AddEventIntent.TimePicked(hour, minute))
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AmountRow(
    amountInput: String,
    currencyCode: String,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit
) {
    val current = SupportedCurrencies.find(currencyCode)
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.width(96.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { menuOpen = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = current.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = current.code,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false }
            ) {
                SupportedCurrencies.ALL.forEach { c ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(c.symbol, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.size(8.dp))
                                Text("${c.code} · ${c.displayName}")
                            }
                        },
                        onClick = {
                            onCurrencyChange(c.code)
                            menuOpen = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(
            value = amountInput,
            onValueChange = onAmountChange,
            label = { Text("Amount (optional)") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = remember { IndianAmountVisualTransformation() },
            colors = OutlinedTextFieldDefaults.colors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    selected: Category,
    onPick: (Category) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(items = Category.entries.toTypedArray(), key = { it.name }) { cat ->
                val isSelected = cat == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onPick(cat) },
                    label = { Text(cat.displayName) },
                    leadingIcon = {
                        CategoryMiniIcon(
                            category = cat,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = cat.visual().accent.copy(alpha = 0.18f),
                        selectedLabelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = cat.visual().accent,
                        selectedBorderWidth = 1.5.dp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FutureDatePickerDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val todayUtcMillis = remember {
        LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    }
    val currentYear = remember { LocalDate.now().year }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayUtcMillis
            override fun isSelectableYear(year: Int): Boolean = year >= currentYear
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                pickerState.selectedDateMillis?.let(onConfirm)
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = pickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FutureTimePickerDialog(
    pickedDateMillis: Long?,
    initialHour: Int?,
    initialMinute: Int?,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val now = remember { Calendar.getInstance() }
    val isToday = remember(pickedDateMillis) {
        if (pickedDateMillis == null) false else {
            val pickedDate = Instant_atZone_UTC_LocalDate(pickedDateMillis)
            pickedDate == LocalDate.now()
        }
    }
    val defaultHour = initialHour ?: now.get(Calendar.HOUR_OF_DAY)
    val defaultMinute = initialMinute ?: now.get(Calendar.MINUTE)
    val timeState = rememberTimePickerState(
        initialHour = defaultHour,
        initialMinute = defaultMinute,
        is24Hour = false
    )
    var error by remember { mutableStateOf<String?>(null) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Pick time", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                TimePicker(state = timeState)
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        if (isToday) {
                            val candidate = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, timeState.hour)
                                set(Calendar.MINUTE, timeState.minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis
                            if (candidate <= System.currentTimeMillis()) {
                                error = "Pick a future time"
                                return@TextButton
                            }
                        }
                        onConfirm(timeState.hour, timeState.minute)
                    }) { Text("OK") }
                }
            }
        }
    }
}

@Composable
private fun Banner(isEditing: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.linearGradient(listOf(GradientStart, GradientMid, GradientEnd)),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                if (isEditing) "Update your event" else "Track what's next",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                if (isEditing) "Change the details below" else "Add an event to your timeline",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PickerRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = GradientStart)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun formatPickedDate(utcMidnightMillis: Long): String {
    // Format the UTC-midnight value in the UTC zone so the displayed date matches what the picker shows.
    val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    fmt.timeZone = TimeZone.getTimeZone("UTC")
    return fmt.format(Date(utcMidnightMillis))
}

private fun defaultUtcMidnightForToday(): Long =
    LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()

private fun Instant_atZone_UTC_LocalDate(utcMidnightMillis: Long): LocalDate =
    java.time.Instant.ofEpochMilli(utcMidnightMillis).atZone(ZoneId.of("UTC")).toLocalDate()
