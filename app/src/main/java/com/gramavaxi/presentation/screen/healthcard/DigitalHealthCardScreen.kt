package com.gramavaxi.presentation.screen.healthcard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.gramavaxi.presentation.theme.GreenMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalHealthCardScreen(animalId: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧾 Digital Health Card", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMedium)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔲", style = MaterialTheme.typography.displayLarge)
                Text("QR Health Card", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Animal ID: $animalId", style = MaterialTheme.typography.bodyMedium)
                Text("QR code generation with ZXing and PDF export coming soon.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
