package com.gramavaxi.presentation.screen.map

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
fun NearbyVetScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📍 Find Nearby Vets", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMedium)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🗺️", style = MaterialTheme.typography.displayLarge)
                Text("Vet Finder Map", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Google Maps integration with nearby veterinary clinics", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("(Implement after adding Maps API Key)", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
