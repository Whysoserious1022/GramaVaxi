package com.gramavaxi.presentation.screen.vaccine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.domain.model.VaccineStatus
import com.gramavaxi.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationCalendarScreen(
    animalId: String,
    viewModel: VaccinationViewModel = hiltViewModel(),
    onLogVaccination: (String) -> Unit,
    onBack: () -> Unit
) {
    val schedules by viewModel.getSchedulesForAnimal(animalId).collectAsStateWithLifecycle(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccination Calendar", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMedium)
            )
        }
    ) { padding ->
        if (schedules.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No vaccination schedules found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val grouped = schedules.groupBy { it.status }
                listOf(VaccineStatus.OVERDUE, VaccineStatus.PENDING, VaccineStatus.COMPLETED).forEach { status ->
                    val group = grouped[status] ?: emptyList()
                    if (group.isNotEmpty()) {
                        item {
                            Text(
                                when (status) {
                                    VaccineStatus.OVERDUE -> "🚨 Overdue"
                                    VaccineStatus.PENDING -> "⏰ Upcoming"
                                    VaccineStatus.COMPLETED -> "✅ Completed"
                                    VaccineStatus.SKIPPED -> "⏭️ Skipped"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (status) {
                                    VaccineStatus.OVERDUE -> ErrorColor
                                    VaccineStatus.PENDING -> AmberMedium
                                    VaccineStatus.COMPLETED -> SuccessColor
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                        items(group) { schedule ->
                            ScheduleCard(schedule = schedule, onLogClick = { onLogVaccination(schedule.scheduleId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(schedule: VaccinationSchedule, onLogClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val bgColor = when (schedule.status) {
        VaccineStatus.COMPLETED -> Color(0xFFE8F5E9)
        VaccineStatus.OVERDUE -> Color(0xFFFFEBEE)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(schedule.vaccineName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Dose ${schedule.doseNumber}/${schedule.totalDoses}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    if (schedule.status == VaccineStatus.COMPLETED && schedule.completedDate != null)
                        "Done: ${dateFormat.format(Date(schedule.completedDate))}"
                    else "Due: ${dateFormat.format(Date(schedule.dueDate))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (schedule.status == VaccineStatus.PENDING || schedule.status == VaccineStatus.OVERDUE) {
                Button(
                    onClick = onLogClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenMedium),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Log", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

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
                title = { Text("Log Vaccination", color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMedium)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("📋 Log this vaccination", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = batchNumber, onValueChange = { batchNumber = it }, label = { Text("Batch Number (optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(
                onClick = { viewModel.logVaccination(scheduleId, batchNumber, notes) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenMedium),
                enabled = uiState !is LogVaccineUiState.Loading
            ) {
                if (uiState is LogVaccineUiState.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("✅ Mark as Vaccinated", fontWeight = FontWeight.Bold)
            }
        }
    }
}
