package com.gramavaxi.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gramavaxi.presentation.screen.dashboard.DashboardViewModel
import com.gramavaxi.presentation.theme.*
import com.gramavaxi.presentation.util.ImageHelper
import com.gramavaxi.util.LanguageManager

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onNavigateToAlerts: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentLocale by LanguageManager.currentLocale.collectAsState()

    var showSignOutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val savedPath = ImageHelper.saveImageLocally(context, it)
                if (savedPath != null) viewModel.updateProfilePhoto(savedPath)
            }
        }
    )

    // ── Dialogs ───────────────────────────────────────────────────────────────

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            icon = { Icon(Icons.Default.Logout, null, tint = Error) },
            title = { Text("Sign Out?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out of Grama-Vaxi?") },
            confirmButton = {
                Button(
                    onClick = onSignOut,
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) { Text("Sign Out") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSignOutDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = { Icon(Icons.Default.Agriculture, null, tint = Primary, modifier = Modifier.size(40.dp)) },
            title = {
                Text(
                    "Grama-Vaxi",
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary,
                    fontSize = 22.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "🐄 Livestock Health Management Platform",
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        "Grama-Vaxi helps Karnataka farmers digitally track animal health, " +
                        "vaccination schedules, disease alerts, and connect with nearby " +
                        "government veterinary services.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    InfoRow("Version", "1.0.0")
                    InfoRow("Platform", "Android")
                    InfoRow("Language Support", "English, ಕನ್ನಡ")
                    InfoRow("Maps", "Google Maps SDK")
                    InfoRow("AI", "Gemini AI")
                    InfoRow("Backend", "Firebase Firestore")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) { Text("Close") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            icon = { Icon(Icons.Default.Language, null, tint = Primary) },
            title = { Text("Select Language / ಭಾಷೆ ಆಯ್ಕೆ ಮಾಡಿ", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LanguageOption(
                        label = "English",
                        subtitle = "Switch to English",
                        flag = "🇬🇧",
                        isSelected = currentLocale == "en",
                        onClick = {
                            LanguageManager.setLocale(context, "en")
                            showLanguageDialog = false
                        }
                    )
                    HorizontalDivider()
                    LanguageOption(
                        label = "ಕನ್ನಡ",
                        subtitle = "Kannada ಗೆ ಬದಲಾಯಿಸಿ",
                        flag = "🇮🇳",
                        isSelected = currentLocale == "kn",
                        onClick = {
                            LanguageManager.setLocale(context, "kn")
                            showLanguageDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            icon = { Icon(Icons.Default.Notifications, null, tint = Primary) },
            title = { Text("Notifications", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Notification preferences for Grama-Vaxi:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val prefs = listOf(
                        "🔔 Vaccination reminders – 1 day before due",
                        "🚨 Overdue vaccination alerts – Daily until done",
                        "📢 Disease outbreak alerts – Immediate",
                        "🩺 AI health diagnosis results"
                    )
                    prefs.forEach { pref ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(pref, style = MaterialTheme.typography.bodySmall, color = OnSurface)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = {
                            showNotificationsDialog = false
                            onNavigateToAlerts()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = Primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("View All Alerts", color = Primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) { Text("Got it") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ── Main Content ──────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Primary, PrimaryContainer)))
                .padding(vertical = 40.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.15f))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.userPhotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = uiState.userPhotoUrl,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("👨‍🌾", style = MaterialTheme.typography.displaySmall)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Tap to change photo",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.6f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    uiState.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${uiState.totalAnimals} animals registered",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.75f)
                )
                Spacer(Modifier.height(8.dp))
                // Current language badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(0.2f)
                ) {
                    Text(
                        if (currentLocale == "en") "🌐 English" else "🌐 ಕನ್ನಡ",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileStat(Modifier.weight(1f), "${uiState.totalAnimals}", "Animals")
            ProfileStat(Modifier.weight(1f), "${uiState.fullyVaccinated}", "Vaccinated")
            ProfileStat(Modifier.weight(1f), "${uiState.animalsNeedingAttention}", "Need Care")
        }

        // Menu card
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // ✅ NOTIFICATIONS – shows notification prefs dialog + link to AlertsScreen
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    label = "Notifications",
                    subtitle = "Vaccination reminders & alerts",
                    iconTint = Primary
                ) { showNotificationsDialog = true }

                HorizontalDivider(color = OutlineVariant.copy(0.5f))

                // ✅ LANGUAGE – shows language picker dialog
                ProfileMenuItem(
                    icon = Icons.Default.Language,
                    label = "Language / ಭಾಷೆ",
                    subtitle = if (currentLocale == "en") "Current: English" else "ಪ್ರಸ್ತುತ: ಕನ್ನಡ",
                    iconTint = Primary
                ) { showLanguageDialog = true }

                HorizontalDivider(color = OutlineVariant.copy(0.5f))

                // ✅ ABOUT – shows app info dialog
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    label = "About Grama-Vaxi",
                    subtitle = "Version 1.0 • Karnataka Livestock App",
                    iconTint = Secondary
                ) { showAboutDialog = true }

                HorizontalDivider(color = OutlineVariant.copy(0.5f))

                // Sign Out
                ProfileMenuItem(
                    icon = Icons.Default.Logout,
                    label = "Sign Out",
                    subtitle = "Log out from your account",
                    iconTint = Error
                ) { showSignOutDialog = true }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ─── Helper composables ───────────────────────────────────────────────────────

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
    }
}

@Composable
private fun LanguageOption(
    label: String,
    subtitle: String,
    flag: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryContainer.copy(0.15f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(flag, fontSize = 28.sp)
        Column(Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, color = OnSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, null, tint = Primary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun ProfileStat(modifier: Modifier = Modifier, value: String, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = Primary, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String = "",
    iconTint: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}
