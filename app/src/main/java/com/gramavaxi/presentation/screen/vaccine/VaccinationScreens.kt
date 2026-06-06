package com.gramavaxi.presentation.screen.vaccine

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.domain.model.VaccineStatus
import com.gramavaxi.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// VACCINATION CALENDAR SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationCalendarScreen(
    animalId: String,
    viewModel: VaccinationViewModel = hiltViewModel(),
    animalViewModel: com.gramavaxi.presentation.screen.animal.AnimalViewModel = hiltViewModel(),
    onLogVaccination: (String) -> Unit,
    onBack: () -> Unit
) {
    val schedules by viewModel.getSchedulesForAnimal(animalId).collectAsStateWithLifecycle(emptyList())
    val animals by animalViewModel.animals.collectAsStateWithLifecycle()
    val animal = animals.find { it.animalId == animalId }
    
    var displayMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }

    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayFormat = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()) }
    val displayFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val calendar = displayMonth.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-based for Sunday

    val daysList = mutableListOf<Int?>()
    for (i in 0 until firstDayOfWeek) daysList.add(null)
    for (i in 1..maxDays) daysList.add(i)

    fun getSchedulesForDay(day: Int): List<VaccinationSchedule> {
        val checkDate = (displayMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
        val checkStr = dayFormat.format(checkDate.time)
        return schedules.filter {
            val dueStr = dayFormat.format(Date(it.dueDate))
            dueStr == checkStr
        }
    }

    var showAddEventDialog by remember { mutableStateOf(false) }

    if (showAddEventDialog) {
        var eventName by remember { mutableStateOf("") }
        var eventNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = { Text("Add Health Event", fontWeight = FontWeight.Bold, color = Primary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text("Event Name (e.g., Checkup)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventNotes,
                        onValueChange = { eventNotes = it },
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dateToSave = selectedDate?.timeInMillis ?: System.currentTimeMillis()
                        viewModel.addCustomEvent(
                            animalId = animalId,
                            animalName = animal?.name ?: "Unknown",
                            eventName = eventName.ifBlank { "Health Check" },
                            date = dateToSave,
                            notes = eventNotes
                        ) {
                            showAddEventDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEventDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccination Calendar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest,
                    titleContentColor = Primary,
                    navigationIconContentColor = Primary
                ),
                modifier = Modifier.shadow(2.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Event")
            }
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    displayMonth = (displayMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    selectedDate = null
                }) {
                    Icon(Icons.Default.ChevronLeft, "Previous Month")
                }
                Text(monthFormat.format(displayMonth.time), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
                IconButton(onClick = { 
                    displayMonth = (displayMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                    selectedDate = null
                }) {
                    Icon(Icons.Default.ChevronRight, "Next Month")
                }
            }

            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                daysOfWeek.forEach { day ->
                    Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 400.dp).padding(horizontal = 16.dp),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysList) { day ->
                    if (day == null) {
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val daySchedules = getSchedulesForDay(day)
                        val isSelected = selectedDate?.get(Calendar.DAY_OF_MONTH) == day && selectedDate?.get(Calendar.MONTH) == displayMonth.get(Calendar.MONTH)
                        
                        val today = Calendar.getInstance()
                        val isToday = today.get(Calendar.DAY_OF_MONTH) == day && 
                                      today.get(Calendar.MONTH) == displayMonth.get(Calendar.MONTH) && 
                                      today.get(Calendar.YEAR) == displayMonth.get(Calendar.YEAR)
                        
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryContainer else if (daySchedules.isNotEmpty()) SecondaryContainer else if (isToday) Secondary.copy(alpha = 0.1f) else SurfaceContainerLowest)
                                .clickable { 
                                    selectedDate = (displayMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
                                }
                                .border(
                                    width = if (isSelected || isToday) 2.dp else 1.dp, 
                                    color = if (isSelected) Primary else if (isToday) Secondary else OutlineVariant.copy(0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(day.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                if (daySchedules.isNotEmpty()) {
                                    Spacer(Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier.size(24.dp).clip(CircleShape).background(PrimaryContainer.copy(0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (animal?.photoUri != null) {
                                            AsyncImage(model = animal.photoUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                        } else {
                                            Text(if (animal?.species == "Cow") "🐄" else if (animal?.species == "Goat") "🐐" else "🐾", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = OutlineVariant.copy(0.3f))
            Spacer(Modifier.height(16.dp))

            val schedulesToShow = if (selectedDate != null) {
                getSchedulesForDay(selectedDate!!.get(Calendar.DAY_OF_MONTH))
            } else {
                schedules.filter { it.status == VaccineStatus.PENDING || it.status == VaccineStatus.OVERDUE }
            }

            Text(
                text = if (selectedDate != null) "Tasks for ${displayFormat.format(selectedDate!!.time)}" else "Upcoming & Overdue Tasks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(horizontal = 20.dp).align(Alignment.Start)
            )
            Spacer(Modifier.height(12.dp))

            if (schedulesToShow.isEmpty()) {
                Text("No tasks scheduled.", color = OnSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp).align(Alignment.Start))
            } else {
                Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    schedulesToShow.forEach { schedule ->
                        ScheduleCard(schedule = schedule, onLogClick = { onLogVaccination(schedule.scheduleId) })
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ScheduleCard(schedule: VaccinationSchedule, onLogClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val isCompleted = schedule.status == VaccineStatus.COMPLETED
    val isOverdue = schedule.status == VaccineStatus.OVERDUE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) ErrorContainer.copy(0.3f) 
                           else if (isCompleted) PrimaryContainer.copy(0.05f) 
                           else SurfaceContainerLowest
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isOverdue) Error.copy(0.2f) 
                    else if (isCompleted) Primary.copy(0.1f) 
                    else OutlineVariant.copy(0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isOverdue) Error.copy(0.15f) 
                        else if (isCompleted) Primary.copy(0.15f) 
                        else Secondary.copy(0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle 
                                 else if (isOverdue) Icons.Default.PriorityHigh 
                                 else Icons.Default.Vaccines,
                    contentDescription = null,
                    tint = if (isOverdue) Error 
                           else if (isCompleted) Primary 
                           else Secondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(Modifier.weight(1f)) {
                Text(
                    schedule.vaccineName, 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold,
                    color = if (isOverdue) OnErrorContainer else Primary
                )
                Text(
                    "Dose ${schedule.doseNumber} of ${schedule.totalDoses}", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = OnSurfaceVariant
                )
                Text(
                    text = if (isCompleted && schedule.completedDate != null)
                        "Completed: ${dateFormat.format(Date(schedule.completedDate))}"
                    else "Due Date: ${dateFormat.format(Date(schedule.dueDate))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOverdue) Error else OnSurfaceVariant,
                    fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (!isCompleted) {
                Button(
                    onClick = onLogClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOverdue) Error else Primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Log", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LOG VACCINATION SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogVaccinationScreen(
    scheduleId: String,
    viewModel: VaccinationViewModel = hiltViewModel(),
    onLogged: () -> Unit,
    onBack: () -> Unit
) {
    var batchNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val uiState by viewModel.logUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is LogVaccineUiState.Success) onLogged()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Vaccination", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest,
                    titleContentColor = Primary,
                    navigationIconContentColor = Primary
                ),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryContainer.copy(0.1f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FactCheck, null, modifier = Modifier.size(48.dp), tint = Primary)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Verify and log dose completion", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            OutlinedTextField(
                value = batchNumber, 
                onValueChange = { batchNumber = it }, 
                label = { Text("Batch / Vial Number") }, 
                placeholder = { Text("e.g. B-1022-X") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                ),
                leadingIcon = { Icon(Icons.Default.QrCode, null, tint = Outline) }
            )

            OutlinedTextField(
                value = notes, 
                onValueChange = { notes = it }, 
                label = { Text("Observation Notes") }, 
                placeholder = { Text("Any side effects or health status...") },
                modifier = Modifier.fillMaxWidth(), 
                minLines = 4,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.logVaccination(scheduleId, batchNumber, notes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(18.dp),
                enabled = uiState !is LogVaccineUiState.Loading
            ) {
                if (uiState is LogVaccineUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Confirm Vaccination", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            if (uiState is LogVaccineUiState.Error) {
                Text(
                    text = (uiState as LogVaccineUiState.Error).message,
                    color = Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
