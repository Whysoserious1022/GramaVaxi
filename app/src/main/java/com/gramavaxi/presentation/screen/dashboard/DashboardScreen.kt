package com.gramavaxi.presentation.screen.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.presentation.theme.*
import com.gramavaxi.util.LanguageManager
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToRegisterAnimal: () -> Unit,
    onNavigateToAnimalList: () -> Unit,
    onNavigateToAIChat: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToNearbyVet: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAnimalProfile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentLocale by LanguageManager.currentLocale.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = uiState.firstName,
                alertCount = uiState.unreadAlertCount,
                onAlertsClick = onNavigateToAlerts,
                currentLocale = currentLocale,
                onLanguageToggle = { LanguageManager.toggle(context) }
            )
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── Greeting ──────────────────────────────────────────────
                item {
                    GreetingSection(firstName = uiState.firstName)
                }

                // ── Health Snapshot Bento Grid ─────────────────────────
                item {
                    HealthSnapshotGrid(uiState = uiState)
                }

                // ── Quick Actions ──────────────────────────────────────
                item {
                    SectionTitle("Quick Actions")
                }
                item {
                    QuickActionsGrid(
                        onRegisterAnimal    = onNavigateToRegisterAnimal,
                        onReportSickness    = { onNavigateToAlerts() },
                        onFindVet           = onNavigateToNearbyVet,
                        onAnalytics         = onNavigateToAnalytics
                    )
                }

                // ── Upcoming Vaccinations ──────────────────────────────
                if (uiState.upcomingVaccinations.isNotEmpty()) {
                    item { SectionTitle("📅 Due This Week") }
                    items(uiState.upcomingVaccinations.take(5)) { schedule ->
                        VaccinationCard(
                            schedule = schedule,
                            onMarkDone = { viewModel.markVaccinationDone(schedule.scheduleId) }
                        )
                    }
                }

                // ── Overdue ────────────────────────────────────────────
                if (uiState.overdueVaccinations.isNotEmpty()) {
                    item {
                        SectionTitle(
                            "🚨 Immediate Action Needed",
                            titleColor = Error
                        )
                    }
                    items(uiState.overdueVaccinations.take(3)) { schedule ->
                        VaccinationCard(
                            schedule = schedule,
                            isOverdue = true,
                            onReportIssue = onNavigateToAlerts,
                            onMarkDone = { viewModel.markVaccinationDone(schedule.scheduleId) }
                        )
                    }
                }

                // ── Empty state ────────────────────────────────────────
                if (uiState.animals.isEmpty()) {
                    item { EmptyStateCard(onRegister = onNavigateToRegisterAnimal) }
                }

                // ── Educational Tip ────────────────────────────────────
                item { TipCard() }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    userName: String,
    alertCount: Int,
    onAlertsClick: () -> Unit,
    currentLocale: String = "en",
    onLanguageToggle: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Agriculture,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Grama-Vaxi",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        actions = {
            // Language toggle chip
            AssistChip(
                onClick = onLanguageToggle,
                label = {
                    Text(
                        if (currentLocale == "en") "EN → ಕನ್ನಡ" else "KN → English",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = PrimaryContainer.copy(0.25f)
                ),
                border = AssistChipDefaults.assistChipBorder(
                    borderColor = PrimaryContainer.copy(0.5f),
                    enabled = true
                ),
                modifier = Modifier.height(32.dp)
            )
            Spacer(Modifier.width(4.dp))
            // Notification bell
            BadgedBox(
                badge = {
                    if (alertCount > 0) {
                        Badge(
                            containerColor = Error,
                            contentColor = OnError
                        ) { Text("$alertCount") }
                    }
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                IconButton(onClick = onAlertsClick) {
                    Icon(Icons.Default.Notifications, "Alerts", tint = OnSurfaceVariant)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceContainerLowest
        ),
        modifier = Modifier.shadow(
            elevation = 2.dp,
            ambientColor = Primary.copy(0.08f)
        )
    )
}

@Composable
private fun GreetingSection(firstName: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "Namaste, $firstName! 🙏",
            style = MaterialTheme.typography.headlineMedium,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Your livestock health status is updated.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun HealthSnapshotGrid(uiState: DashboardUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Full-width Total Animals hero card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(Primary, PrimaryContainer, Color(0xFF2E6B4F))),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                // Background icon
                Icon(
                    Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color.White.copy(0.08f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = 20.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            androidx.compose.ui.res.stringResource(com.gramavaxi.R.string.total_animals),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(0.8f)
                        )
                        Text(
                            "${uiState.totalAnimals}",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Two half-width cards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Fully vaccinated
            BentoStatCard(
                modifier = Modifier.weight(1f),
                label = "Vaccinated",
                value = "${uiState.fullyVaccinated}",
                borderColor = PrimaryContainer,
                valueColor = Primary
            )
            // Upcoming
            BentoStatCard(
                modifier = Modifier.weight(1f),
                label = "Upcoming",
                value = "${uiState.upcomingVaccinations.size}",
                borderColor = Secondary,
                valueColor = Secondary
            )
        }

        // Urgent action card (only if overdue > 0)
        if (uiState.animalsNeedingAttention > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorContainer),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            "Urgent Action Required",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnErrorContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${uiState.animalsNeedingAttention} animal(s) need immediate attention",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnErrorContainer.copy(0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BentoStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    borderColor: Color,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
        ) {
            // Left colored border accent
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .background(borderColor)
            )
            Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class QuickAction(val label: String, val icon: ImageVector, val tint: Color, val onClick: () -> Unit)

@Composable
private fun QuickActionsGrid(
    onRegisterAnimal: () -> Unit,
    onReportSickness: () -> Unit,
    onFindVet: () -> Unit,
    onAnalytics: () -> Unit
) {
    val actions = listOf(
        QuickAction(androidx.compose.ui.res.stringResource(com.gramavaxi.R.string.register_animal),   Icons.Default.AddBox,        Primary,    onRegisterAnimal),
        QuickAction(androidx.compose.ui.res.stringResource(com.gramavaxi.R.string.report_outbreak),   Icons.Default.ReportProblem, Error,      onReportSickness),
        QuickAction(androidx.compose.ui.res.stringResource(com.gramavaxi.R.string.find_vet),     Icons.Default.LocalHospital,  Secondary,  onFindVet),
        QuickAction(androidx.compose.ui.res.stringResource(com.gramavaxi.R.string.analytics),Icons.Default.BarChart,       Tertiary,   onAnalytics)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        actions.forEach { action ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { action.onClick() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                border = BorderStroke(1.dp, OutlineVariant),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        action.icon,
                        contentDescription = action.label,
                        tint = action.tint,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        action.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun VaccinationCard(
    schedule: VaccinationSchedule,
    isOverdue: Boolean = false,
    onReportIssue: (() -> Unit)? = null,
    onMarkDone: (() -> Unit)? = null
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var showMarkDoneDialog by remember { mutableStateOf(false) }

    // Confirmation dialog
    if (showMarkDoneDialog) {
        AlertDialog(
            onDismissRequest = { showMarkDoneDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Primary) },
            title = { Text("Mark as Done?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Mark ${schedule.vaccineName} for ${schedule.animalName} as completed today?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMarkDoneDialog = false
                        onMarkDone?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Mark Done ✓") }
            },
            dismissButton = {
                TextButton(onClick = { showMarkDoneDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) ErrorContainer.copy(0.5f) else SurfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isOverdue) Error else PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Vaccines,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        schedule.animalName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOverdue) OnErrorContainer else OnSurface
                    )
                    Text(
                        schedule.vaccineName,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        dateFormat.format(Date(schedule.dueDate)),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOverdue) Error else OnSurfaceVariant
                    )
                    if (isOverdue) {
                        Text(
                            "OVERDUE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Error,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text("DUE SOON", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = SecondaryContainer.copy(0.7f)
                            ),
                            border = null,
                            modifier = Modifier.height(22.dp)
                        )
                    }
                }
            }
            // Action row
            if (onMarkDone != null || onReportIssue != null) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (onMarkDone != null) {
                        Button(
                            onClick = { showMarkDoneDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Mark Done", style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
                        }
                    }
                    if (onReportIssue != null && isOverdue) {
                        OutlinedButton(
                            onClick = onReportIssue,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Error)
                        ) {
                            Text("Report Issue", style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, color = Error)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyStateCard(onRegister: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🐄", fontSize = 64.sp)
            Text(
                "No animals registered yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
            Text(
                "Register your first animal to start tracking health & vaccinations.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRegister,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Register Animal", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Monsoon Care Tips 🌧️",
                    style = MaterialTheme.typography.titleSmall,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Protect your cattle from foot rot during the rains. Check hooves weekly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Icon(
                Icons.Default.WaterDrop,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, titleColor: Color = Primary) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        color = titleColor,
        fontWeight = FontWeight.SemiBold
    )
}
