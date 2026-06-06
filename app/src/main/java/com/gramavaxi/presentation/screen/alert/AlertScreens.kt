package com.gramavaxi.presentation.screen.alert

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gramavaxi.domain.model.AlertSeverity
import com.gramavaxi.domain.model.OutbreakAlert
import com.gramavaxi.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// ALERTS SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertViewModel = hiltViewModel(),
    onReportOutbreak: () -> Unit,
    onBack: () -> Unit
) {
    val alerts by viewModel.alerts.collectAsStateWithLifecycle(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Area Alerts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(Icons.Default.Checklist, "Read all", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLowest),
                modifier = Modifier.shadow(2.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReportOutbreak,
                icon = { Icon(Icons.Default.Campaign, null) },
                text = { Text("Report Issue") },
                containerColor = Error,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        containerColor = Background
    ) { padding ->
        if (alerts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(Primary.copy(0.05f)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(48.dp), tint = Primary) }
                    Text("Your area is safe", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                    Text("No disease outbreaks reported nearby.", color = OnSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(alerts) { alert ->
                    AlertCard(alert = alert, onRead = { viewModel.markAsRead(alert.alertId) })
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: OutbreakAlert, onRead: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val isCritical = alert.severity == AlertSeverity.CRITICAL

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (!alert.isRead) 6.dp else 1.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!alert.isRead) SurfaceContainerLowest else SurfaceContainerLow
        ),
        border = if (!alert.isRead) BorderStroke(1.dp, Error.copy(0.2f)) else null
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = when (alert.severity) {
                        AlertSeverity.CRITICAL -> Error
                        AlertSeverity.HIGH -> Color(0xFFF44336)
                        AlertSeverity.MEDIUM -> Color(0xFFFF9800)
                        AlertSeverity.LOW -> Color(0xFF4CAF50)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        alert.severity.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    alert.disease,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
                if (!alert.isRead) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Error))
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Text(alert.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnSurface)
            Text(alert.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, lineHeight = 18.sp)
            
            if (!alert.actionRequired.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainer.copy(0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp), tint = Primary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Action: ${alert.actionRequired}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Reported by ${alert.reportedBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(0.7f)
                )
                Text(
                    dateFormat.format(Date(alert.reportedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(0.7f)
                )
            }
            
            if (!alert.isRead) {
                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = onRead,
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Dismiss", color = Primary, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REPORT OUTBREAK SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportOutbreakScreen(onReported: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Foot and Mouth Disease (FMD)", "Lumpy Skin Disease", "Mastitis", "Brucellosis", "Other")
    var selectedOption by remember { mutableStateOf(options[0]) }
    var customSickness by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Issue", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLowest),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Error.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Warning, null, modifier = Modifier.size(40.dp), tint = Error) }
            Spacer(Modifier.height(16.dp))
            Text("Community Protection", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(Modifier.height(8.dp))
            Text(
                "Help protect your fellow farmers. Report symptoms or unusual livestock sickness.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(32.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Sickness/Issue") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedOption = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (selectedOption == "Other") {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = customSickness,
                    onValueChange = { customSickness = it },
                    label = { Text("Specify Sickness") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Additional Notes / Symptoms") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { 
                    Toast.makeText(context, "Issue reported successfully", Toast.LENGTH_SHORT).show()
                    onReported() 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Error)
            ) {
                Text("Submit Report", fontWeight = FontWeight.Bold)
            }
        }
    }
}
