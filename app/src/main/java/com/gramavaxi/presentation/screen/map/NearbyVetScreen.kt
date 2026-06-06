package com.gramavaxi.presentation.screen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.gramavaxi.presentation.screen.animal.AnimalViewModel
import com.gramavaxi.presentation.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─── Karnataka Government Veterinary Hospitals (real GPS) ─────────────────────
private data class VetClinic(val name: String, val district: String, val lat: Double, val lng: Double)

private val KARNATAKA_VET_CLINICS = listOf(
    VetClinic("District Veterinary Hospital, Tumkur",       "Tumkur",         13.3422, 77.1012),
    VetClinic("Govt Veterinary Dispensary, Kunigal",        "Tumkur",         13.0284, 77.0259),
    VetClinic("Veterinary Hospital, Pavagada",              "Tumkur",         14.0939, 77.2711),
    VetClinic("District Animal Husbandry Office, Bengaluru","Bengaluru Rural", 13.0012, 77.5800),
    VetClinic("Govt Vet Hospital, Hoskote",                 "Bengaluru Rural", 13.0707, 77.7986),
    VetClinic("Veterinary Dispensary, Devanahalli",         "Bengaluru Rural", 13.2450, 77.7135),
    VetClinic("Dist Veterinary Hospital, Mysuru",           "Mysuru",         12.3052, 76.6551),
    VetClinic("Govt Vet Hospital, Hunsur",                  "Mysuru",         12.3037, 76.2918),
    VetClinic("Livestock Dev Center, Nanjangud",            "Mysuru",         12.1120, 76.6842),
    VetClinic("Dist Vet Hospital, Hassan",                  "Hassan",         13.0068, 76.1004),
    VetClinic("Govt Vet Dispensary, Sakleshpur",            "Hassan",         12.9436, 75.7887),
    VetClinic("Veterinary Hospital, Belur",                 "Hassan",         13.1647, 75.8657),
    VetClinic("Dist Animal Husbandry Hospital, Mandya",     "Mandya",         12.5231, 76.8976),
    VetClinic("Govt Vet Hospital, Srirangapatna",           "Mandya",         12.4237, 76.6987),
    VetClinic("Veterinary Dispensary, Nagamangala",         "Mandya",         12.8193, 76.7521),
)

private val KARNATAKA_CENTER = LatLng(13.3409, 77.1280)

private fun mockLatLng(index: Int): LatLng {
    val rng = Random(index * 31L + 7)
    val radiusDeg = 0.04
    val angle = rng.nextDouble(0.0, 2 * Math.PI)
    val dist  = rng.nextDouble(0.005, radiusDeg)
    return LatLng(
        KARNATAKA_CENTER.latitude  + dist * sin(angle),
        KARNATAKA_CENTER.longitude + dist * cos(angle)
    )
}

// ─── Legend entry colours ──────────────────────────────────────────────────────
private val SPECIES_COLOURS = listOf(
    Triple("Cattle", BitmapDescriptorFactory.HUE_GREEN,  Color(0xFF4CAF50)),
    Triple("Goat",   BitmapDescriptorFactory.HUE_ORANGE, Color(0xFFFF9800)),
    Triple("Sheep",  BitmapDescriptorFactory.HUE_YELLOW, Color(0xFFFFEB3B)),
    Triple("Buffalo",BitmapDescriptorFactory.HUE_CYAN,   Color(0xFF00BCD4)),
    Triple("Other",  BitmapDescriptorFactory.HUE_VIOLET, Color(0xFF9C27B0)),
)

private fun speciesHue(species: String) = when (species.lowercase()) {
    "cow", "cattle" -> BitmapDescriptorFactory.HUE_GREEN
    "goat"          -> BitmapDescriptorFactory.HUE_ORANGE
    "sheep"         -> BitmapDescriptorFactory.HUE_YELLOW
    "buffalo"       -> BitmapDescriptorFactory.HUE_CYAN
    else            -> BitmapDescriptorFactory.HUE_VIOLET
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyVetScreen(
    onBack: () -> Unit,
    viewModel: AnimalViewModel = hiltViewModel()
) {
    val animals by viewModel.animals.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(KARNATAKA_CENTER, 10f)
    }

    var showLegend by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Vet Clinics & Animal Map", fontWeight = FontWeight.Bold, color = Primary)
                        Text("Karnataka Government Vet Hospitals", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLowest),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = Background
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Google Map ─────────────────────────────────────────────────────
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                // ── Vet Clinic markers (blue) ──────────────────────────────────
                KARNATAKA_VET_CLINICS.forEach { clinic ->
                    Marker(
                        state = MarkerState(position = LatLng(clinic.lat, clinic.lng)),
                        title = clinic.name,
                        snippet = "${clinic.district} District • Tap for directions",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        onInfoWindowClick = {
                            val uri = android.net.Uri.parse("geo:${clinic.lat},${clinic.lng}?q=${android.net.Uri.encode(clinic.name)}")
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }
                    )
                }

                // ── Animal markers (species colour) ────────────────────────────
                animals.forEachIndexed { index, animal ->
                    val position = mockLatLng(index)
                    Marker(
                        state = MarkerState(position = position),
                        title = animal.name,
                        snippet = "${animal.species} • ${animal.ownerName} • ${animal.villageName}",
                        icon = BitmapDescriptorFactory.defaultMarker(speciesHue(animal.species))
                    )
                }
            }

            // ── Bottom info panel ──────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest.copy(0.97f)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MapStat(Icons.Default.LocalHospital, "${KARNATAKA_VET_CLINICS.size}", "Vet Clinics", Color(0xFF1976D2))
                        VerticalDivider(modifier = Modifier.height(36.dp))
                        MapStat(Icons.Default.Agriculture, "${animals.size}", "Animals", Primary)
                    }

                    if (showLegend) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        // Legend row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Vet clinic
                            LegendChip("Vet Clinic", Color(0xFF2196F3))
                            // Animal species
                            LegendChip("Cattle", Color(0xFF4CAF50))
                            LegendChip("Goat", Color(0xFFFF9800))
                            LegendChip("Other", Color(0xFF9C27B0))
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { showLegend = !showLegend },
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (showLegend) "Hide Legend ▲" else "Show Legend ▼",
                            style = MaterialTheme.typography.labelSmall, color = Primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun MapStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = tint)
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
    }
}

@Composable
private fun LegendChip(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurface, fontSize = 10.sp)
    }
}
