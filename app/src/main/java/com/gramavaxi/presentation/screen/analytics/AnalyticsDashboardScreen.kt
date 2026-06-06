package com.gramavaxi.presentation.screen.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gramavaxi.presentation.screen.dashboard.DashboardViewModel
import com.gramavaxi.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // ── Derived metrics ────────────────────────────────────────────────────────
    val total       = state.animals.size
    val cows        = state.animals.count { it.species.lowercase().let { s -> s == "cow" || s == "cattle" } }
    val goats       = state.animals.count { it.species.lowercase() == "goat" }
    val sheep       = state.animals.count { it.species.lowercase() == "sheep" }
    val buffaloes   = state.animals.count { it.species.lowercase() == "buffalo" }
    val other       = total - cows - goats - sheep - buffaloes

    val overdue   = state.overdueVaccinations.map { it.animalId }.distinct().size
    val upcoming  = state.upcomingVaccinations.map { it.animalId }.distinct().size
    val healthy   = (total - overdue).coerceAtLeast(0)

    val healthRatio   = if (total > 0) healthy.toFloat() / total else 0f
    val overdueRatio  = if (total > 0) overdue.toFloat() / total else 0f
    val upcomingRatio = if (total > 0) upcoming.toFloat() / total else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "📊 Analytics Dashboard",
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest
                ),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = Background
    ) { padding ->

        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ── Hero stat row ──────────────────────────────────────────────
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HeroStatCard(
                        modifier = Modifier.weight(1f),
                        number = total,
                        label = "Total\nAnimals",
                        icon = Icons.Default.Agriculture,
                        tint = Primary
                    )
                    HeroStatCard(
                        modifier = Modifier.weight(1f),
                        number = healthy,
                        label = "Healthy\nAnimals",
                        icon = Icons.Default.Favorite,
                        tint = Color(0xFF4CAF50)
                    )
                    HeroStatCard(
                        modifier = Modifier.weight(1f),
                        number = overdue,
                        label = "Need\nAttention",
                        icon = Icons.Default.Warning,
                        tint = Error
                    )
                }
            }

            // ── Health overview bar chart ──────────────────────────────────
            item {
                SectionCard(title = "🏥 Herd Health Overview") {
                    Spacer(Modifier.height(8.dp))
                    AnalyticsBar(
                        label = "Healthy",
                        value = healthRatio,
                        color = Color(0xFF4CAF50),
                        count = healthy,
                        total = total
                    )
                    Spacer(Modifier.height(12.dp))
                    AnalyticsBar(
                        label = "Overdue Vaccination",
                        value = overdueRatio,
                        color = Error,
                        count = overdue,
                        total = total
                    )
                    Spacer(Modifier.height(12.dp))
                    AnalyticsBar(
                        label = "Due This Week",
                        value = upcomingRatio,
                        color = Color(0xFFFFA726),
                        count = upcoming,
                        total = total
                    )
                }
            }

            // ── Species distribution ───────────────────────────────────────
            item {
                SectionCard(title = "🐄 Species Distribution") {
                    Spacer(Modifier.height(8.dp))
                    val species = mapOf(
                        "Cattle / Cow" to Pair(cows, Primary),
                        "Goat"         to Pair(goats, Color(0xFF8D6E63)),
                        "Sheep"        to Pair(sheep, Color(0xFF42A5F5)),
                        "Buffalo"      to Pair(buffaloes, Color(0xFF5C6BC0)),
                        "Other"        to Pair(other, OnSurfaceVariant)
                    )
                    species.filter { (_, v) -> v.first > 0 }.forEach { (name, pair) ->
                        val (count, color) = pair
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(
                                "$count (${if (total > 0) (count * 100 / total) else 0}%)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = color
                            )
                        }
                        if (total > 0) {
                            LinearProgressIndicator(
                                progress = { count.toFloat() / total },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = color,
                                trackColor = color.copy(alpha = 0.15f),
                                strokeCap = StrokeCap.Round
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    if (total == 0) {
                        EmptyAnalyticsHint("Register animals to see species data")
                    }
                }
            }

            // ── Vaccination summary ────────────────────────────────────────
            item {
                SectionCard(title = "💉 Vaccination Summary") {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VaccineStatChip("Overdue", overdue, Error)
                        VaccineStatChip("Due Soon", upcoming, Color(0xFFFFA726))
                        VaccineStatChip("Healthy", healthy, Color(0xFF4CAF50))
                    }
                    if (total == 0) {
                        Spacer(Modifier.height(12.dp))
                        EmptyAnalyticsHint("Vaccination stats will appear after registration")
                    }
                }
            }

            // ── District coverage ──────────────────────────────────────────
            item {
                SectionCard(title = "📍 District Coverage") {
                    Spacer(Modifier.height(8.dp))
                    val districts = state.animals.groupBy { it.district.ifEmpty { "Unknown" } }
                    if (districts.isEmpty()) {
                        EmptyAnalyticsHint("No district data yet")
                    } else {
                        districts.entries.sortedByDescending { it.value.size }.take(5).forEach { (district, animals) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = Secondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(district.ifEmpty { "Unknown" }, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                Text(
                                    "${animals.size} animal${if (animals.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun HeroStatCard(
    modifier: Modifier = Modifier,
    number: Int,
    label: String,
    icon: ImageVector,
    tint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "$number",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tint
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
            content()
        }
    }
}

@Composable
private fun AnalyticsBar(label: String, value: Float, color: Color, count: Int, total: Int) {
    var animStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animStarted = true }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animStarted) value else 0f,
        animationSpec = tween(800),
        label = "bar_$label"
    )

    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), color = OnSurface)
            Text(
                "$count / $total",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.12f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun VaccineStatChip(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text("$count", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
    }
}

@Composable
private fun EmptyAnalyticsHint(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(0.6f))
    }
}
