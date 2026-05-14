package com.gramavaxi.presentation.screen.animal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gramavaxi.domain.model.Animal
import com.gramavaxi.presentation.theme.GreenMedium
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                title = { Text("Register Animal", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMedium)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader("Animal details")

            GramaTextField(value = name, onValueChange = { name = it }, label = "Animal name *")

            ExposedDropdownMenuBox(expanded = expandedSpecies, onExpandedChange = { expandedSpecies = it }) {
                OutlinedTextField(
                    value = species,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Species *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSpecies) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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

            GramaTextField(value = breed, onValueChange = { breed = it }, label = "Breed")
            GramaTextField(
                value = ageMonths,
                onValueChange = { ageMonths = it },
                label = "Age in months *",
                keyboardType = KeyboardType.Number
            )

            ExposedDropdownMenuBox(expanded = expandedSex, onExpandedChange = { expandedSex = it }) {
                OutlinedTextField(
                    value = sex,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sex *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSex) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            SectionHeader("Owner details")
            GramaTextField(value = ownerName, onValueChange = { ownerName = it }, label = "Owner name *")
            GramaTextField(
                value = ownerPhone,
                onValueChange = { ownerPhone = it },
                label = "Mobile number *",
                keyboardType = KeyboardType.Phone
            )
            GramaTextField(value = village, onValueChange = { village = it }, label = "Village name *")
            GramaTextField(value = district, onValueChange = { district = it }, label = "District *")

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            SectionHeader("Additional notes")
            GramaTextField(value = notes, onValueChange = { notes = it }, label = "Notes", minLines = 2)

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
                        notes = notes
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = name.isNotBlank() &&
                    ownerName.isNotBlank() &&
                    ownerPhone.isNotBlank() &&
                    village.isNotBlank() &&
                    district.isNotBlank() &&
                    ageMonths.toIntOrNull() != null &&
                    uiState !is AnimalUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = GreenMedium),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState is AnimalUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Register and create schedule", fontWeight = FontWeight.Bold)
                }
            }

            if (uiState is AnimalUiState.Error) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Text(
                        text = (uiState as AnimalUiState.Error).message,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFFB71C1C)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = GreenMedium
    )
}

@Composable
private fun GramaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenMedium,
            focusedLabelColor = GreenMedium
        )
    )
}

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
                title = { Text("My Animals", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMedium)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAnimal, containerColor = GreenMedium) {
                Icon(Icons.Default.Add, contentDescription = "Add animal", tint = Color.White)
            }
        }
    ) { padding ->
        if (animals.isEmpty()) {
            EmptyAnimalList(onAddAnimal = onAddAnimal, modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "${animals.size} registered animal${if (animals.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(animals, key = { it.animalId }) { animal ->
                    AnimalListCard(animal = animal, onClick = { onAnimalClick(animal.animalId) })
                }
            }
        }
    }
}

@Composable
private fun EmptyAnimalList(onAddAnimal: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Pets, contentDescription = null, tint = GreenMedium, modifier = Modifier.size(48.dp))
            Text("No animals registered yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Add an animal to create vaccination dates, reminders, and a health record.")
            Button(onClick = onAddAnimal, colors = ButtonDefaults.buttonColors(containerColor = GreenMedium)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add animal")
            }
        }
    }
}

@Composable
private fun AnimalListCard(animal: Animal, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Pets, contentDescription = null, tint = GreenMedium, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(animal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${animal.species} - ${animal.breed} - ${animal.ageMonths} months")
                Text("Health ID: ${animal.healthId}", style = MaterialTheme.typography.bodySmall)
                Text("Registered: ${dateFormat.format(Date(animal.createdAt))}", style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

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
                title = { Text(animal?.name ?: "Animal Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMedium)
            )
        }
    ) { padding ->
        if (animal == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Animal not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { AnimalProfileSummary(animal) }
                item {
                    ActionCard(
                        icon = Icons.Default.CalendarMonth,
                        title = "Vaccination calendar",
                        subtitle = "View due dates, overdue vaccines, and log completed doses.",
                        onClick = onNavigateToCalendar
                    )
                }
                item {
                    ActionCard(
                        icon = Icons.Default.HealthAndSafety,
                        title = "Health card",
                        subtitle = "Open this animal's health record.",
                        onClick = onNavigateToHealthCard
                    )
                }
                item {
                    ActionCard(
                        icon = Icons.Default.Psychology,
                        title = "Ask Grama-Vaxi AI",
                        subtitle = "Get help for symptoms, nutrition, and care questions.",
                        onClick = onNavigateToAIChat
                    )
                }
                item {
                    OutlinedButton(onClick = onNavigateToDiseaseDetection, modifier = Modifier.fillMaxWidth()) {
                        Text("Open disease detection")
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimalProfileSummary(animal: Animal) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(animal.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${animal.species} - ${animal.breed}")
            Text("Age: ${animal.ageMonths} months | Sex: ${animal.sex}")
            Text("Health ID: ${animal.healthId}")
            Text("Owner: ${animal.ownerName} (${animal.ownerPhone})")
            Text("Village: ${animal.villageName}, ${animal.district}")
            animal.notes?.let { Text("Notes: $it") }
        }
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = GreenMedium, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
