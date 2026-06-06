package com.gramavaxi.presentation.screen.animal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gramavaxi.domain.model.Animal
import com.gramavaxi.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.gramavaxi.presentation.util.ImageHelper

// ─────────────────────────────────────────────────────────────────────────────
// REGISTER ANIMAL SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAnimalScreen(
    viewModel: AnimalViewModel = hiltViewModel(),
    onAnimalRegistered: (String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("Cow") }
    var breed by remember { mutableStateOf("") }
    var ageMonths by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("Female") }
    var ownerName by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val savedPath = ImageHelper.saveImageLocally(context, it)
                if (savedPath != null) {
                    photoUri = savedPath
                }
            }
        }
    )

    val speciesOptions = listOf("Cow", "Buffalo", "Goat", "Sheep", "Pig")
    val sexOptions = listOf("Female", "Male")
    var expandedSpecies by remember { mutableStateOf(false) }
    var expandedSex by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AnimalUiState.Success) {
            onAnimalRegistered((uiState as AnimalUiState.Success).animal.animalId)
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Animal", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Add a new animal to your registry to start tracking health and vaccinations.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            SectionHeader("🐄 Animal Details")
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Animal Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Add Photo",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            GramaTextField(value = name, onValueChange = { name = it }, label = "Animal Name *", placeholder = "e.g. Lakshmi")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expandedSpecies, 
                    onExpandedChange = { expandedSpecies = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = species,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Species *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSpecies) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )
                    ExposedDropdownMenu(expanded = expandedSpecies, onDismissRequest = { expandedSpecies = false }) {
                        speciesOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                species = option
                                expandedSpecies = false
                            })
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedSex, 
                    onExpandedChange = { expandedSex = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = sex,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sex *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSex) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )
                    ExposedDropdownMenu(expanded = expandedSex, onDismissRequest = { expandedSex = false }) {
                        sexOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                sex = option
                                expandedSex = false
                            })
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GramaTextField(
                    modifier = Modifier.weight(1.2f),
                    value = breed, 
                    onValueChange = { breed = it }, 
                    label = "Breed", 
                    placeholder = "e.g. Gir"
                )
                GramaTextField(
                    modifier = Modifier.weight(0.8f),
                    value = ageMonths,
                    onValueChange = { ageMonths = it },
                    label = "Age (Months) *",
                    keyboardType = KeyboardType.Number,
                    placeholder = "24"
                )
            }

            Spacer(Modifier.height(8.dp))
            SectionHeader("👤 Owner Details")
            
            GramaTextField(value = ownerName, onValueChange = { ownerName = it }, label = "Owner Name *")
            GramaTextField(
                value = ownerPhone,
                onValueChange = { ownerPhone = it },
                label = "Mobile Number *",
                keyboardType = KeyboardType.Phone,
                placeholder = "9876543210"
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GramaTextField(modifier = Modifier.weight(1f), value = village, onValueChange = { village = it }, label = "Village *")
                GramaTextField(modifier = Modifier.weight(1f), value = district, onValueChange = { district = it }, label = "District *")
            }

            Spacer(Modifier.height(8.dp))
            SectionHeader("📝 Additional Notes")
            GramaTextField(value = notes, onValueChange = { notes = it }, label = "Notes", minLines = 3, placeholder = "Add any health history...")

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.registerAnimal(
                        name = name,
                        species = species,
                        breed = breed,
                        ageMonths = ageMonths.toIntOrNull() ?: 0,
                        sex = sex,
                        ownerName = ownerName,
                        ownerPhone = ownerPhone,
                        village = village,
                        district = district,
                        notes = notes,
                        photoUri = photoUri
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp)),
                enabled = name.isNotBlank() &&
                    ownerName.isNotBlank() &&
                    ownerPhone.isNotBlank() &&
                    village.isNotBlank() &&
                    district.isNotBlank() &&
                    ageMonths.toIntOrNull() != null &&
                    uiState !is AnimalUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (uiState is AnimalUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Register Animal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (uiState is AnimalUiState.Error) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = (uiState as AnimalUiState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = OnErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ANIMAL LIST SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    viewModel: AnimalViewModel = hiltViewModel(),
    onAnimalClick: (String) -> Unit,
    onAddAnimal: () -> Unit,
    onBack: () -> Unit
) {
    val animals by viewModel.animals.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Animals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddAnimal) {
                        Icon(Icons.Default.Add, "Add", tint = Primary)
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
        if (animals.isEmpty()) {
            EmptyAnimalList(onAddAnimal = onAddAnimal, modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        "${animals.size} registered animal${if (animals.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(animals, key = { it.animalId }) { animal ->
                    AnimalListCard(animal = animal, onClick = { onAnimalClick(animal.animalId) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun EmptyAnimalList(onAddAnimal: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(PrimaryContainer.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = Primary, modifier = Modifier.size(48.dp))
            }
            Text("No animals found", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Text(
                "Register your livestock to manage their health records and vaccination schedules.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = OnSurfaceVariant
            )
            Button(
                onClick = onAddAnimal, 
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.height(52.dp).padding(horizontal = 24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Register Animal", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AnimalListCard(animal: Animal, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, OutlineVariant.copy(0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryContainer.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (animal.species == "Cow") "🐄" else if (animal.species == "Goat") "🐐" else "🐾", fontSize = 28.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(animal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
                Text(
                    "${animal.species} · ${animal.breed} · ${animal.ageMonths}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode, null, modifier = Modifier.size(12.dp), tint = OnSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(animal.healthId, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Outline)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ANIMAL PROFILE SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalProfileScreen(
    animalId: String,
    viewModel: AnimalViewModel = hiltViewModel(),
    onNavigateToCalendar: () -> Unit,
    onNavigateToHealthCard: () -> Unit,
    onNavigateToAIChat: () -> Unit,
    onNavigateToDiseaseDetection: () -> Unit,
    onBack: () -> Unit
) {
    val animals by viewModel.animals.collectAsStateWithLifecycle()
    val animal = animals.firstOrNull { it.animalId == animalId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(animal?.name ?: "Profile", fontWeight = FontWeight.Bold) },
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
        if (animal == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { AnimalProfileHero(animal, viewModel) }
                
                item { SectionTitle("Health Services") }
                
                item {
                    ProfileServiceCard(
                        icon = Icons.Default.CalendarMonth,
                        title = "Vaccination Calendar",
                        subtitle = "Track due dates and log doses",
                        accentColor = Primary,
                        onClick = onNavigateToCalendar
                    )
                }
                item {
                    ProfileServiceCard(
                        icon = Icons.Default.HealthAndSafety,
                        title = "Digital Health Card",
                        subtitle = "Full medical history & QR ID",
                        accentColor = Secondary,
                        onClick = onNavigateToHealthCard
                    )
                }
                
                item { SectionTitle("AI Support") }
                
                item {
                    ProfileServiceCard(
                        icon = Icons.Default.SmartToy,
                        title = "Consult Grama AI",
                        subtitle = "AI diagnosis & care advice",
                        accentColor = Tertiary,
                        onClick = onNavigateToAIChat
                    )
                }
                item {
                    ProfileServiceCard(
                        icon = Icons.Default.CameraAlt,
                        title = "Disease Detection",
                        subtitle = "Scan for visible symptoms",
                        accentColor = Color(0xFFE91E63),
                        onClick = onNavigateToDiseaseDetection
                    )
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun AnimalProfileHero(
    animal: Animal,
    viewModel: AnimalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val savedPath = ImageHelper.saveImageLocally(context, it)
                if (savedPath != null) {
                    viewModel.updateAnimalPhoto(animal.animalId, savedPath)
                }
            }
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, Primary.copy(0.1f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Brush.verticalGradient(listOf(Primary, PrimaryContainer)))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (animal.photoUri != null) {
                    AsyncImage(
                        model = animal.photoUri,
                        contentDescription = "Animal Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(if (animal.species == "Cow") "🐄" else if (animal.species == "Goat") "🐐" else "🐾", fontSize = 64.sp)
                }
            }
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(animal.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primary)
                        Text("${animal.species} · ${animal.breed}", style = MaterialTheme.typography.titleSmall, color = OnSurfaceVariant)
                    }
                    Surface(
                        color = PrimaryContainer.copy(0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            animal.healthId,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                HorizontalDivider(color = OutlineVariant.copy(0.5f))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(Modifier.weight(1f), "Age", "${animal.ageMonths}m")
                    InfoItem(Modifier.weight(1f), "Sex", animal.sex)
                    InfoItem(Modifier.weight(1f), "Village", animal.villageName)
                }
                
                if (!animal.notes.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Background),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Note, null, modifier = Modifier.size(16.dp), tint = OnSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Text(animal.notes ?: "", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(modifier: Modifier, label: String, value: String) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OnSurface)
    }
}

@Composable
private fun ProfileServiceCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, OutlineVariant.copy(0.5f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Outline)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHARED COMPONENTS
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Primary,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = OnSurfaceVariant,
        letterSpacing = 1.sp
    )
}

@Composable
private fun GramaTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            cursorColor = Primary
        )
    )
}
