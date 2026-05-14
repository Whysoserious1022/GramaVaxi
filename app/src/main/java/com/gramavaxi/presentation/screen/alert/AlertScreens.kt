package com.gramavaxi.presentation.screen.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                title = { Text("🚨 Disease Alerts", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } },
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(Icons.Default.DoneAll, "Mark all read", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ErrorColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReportOutbreak,
                icon = { Icon(Icons.Default.ReportProblem, null) },
                text = { Text("Report Outbreak") },
                containerColor = ErrorColor,
                contentColor = Color.White
            )
        }
    ) { padding ->
        if (alerts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 56.sp)
                    Text("No alerts in your area", style = MaterialTheme.typography.titleMedium)
                    Text("Your area is safe!", style = MaterialTheme.typography.bodyMedium, color = SuccessColor)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alerts) { alert ->
                    AlertCard(alert = alert, onRead = { viewModel.markAsRead(alert.alertId) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: OutbreakAlert, onRead: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val severityColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> SeverityCritical
        AlertSeverity.HIGH -> SeverityHigh
        AlertSeverity.MEDIUM -> SeverityMedium
        AlertSeverity.LOW -> SeverityLow
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!alert.isRead) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (!alert.isRead) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = severityColor, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        alert.severity.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    alert.disease,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (!alert.isRead) {
                    IconButton(onClick = onRead, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, "Mark read", modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(alert.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(alert.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (alert.actionRequired != null) {
                Spacer(Modifier.height(8.dp))
                Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "Action: ${alert.actionRequired}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = GreenDark
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Reported by ${alert.reportedBy} • ${dateFormat.format(Date(alert.reportedAt))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportOutbreakScreen(onReported: () -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Outbreak", color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ErrorColor)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Report Outbreak Form - Coming in next sprint", style = MaterialTheme.typography.titleMedium)
        }
    }
}
