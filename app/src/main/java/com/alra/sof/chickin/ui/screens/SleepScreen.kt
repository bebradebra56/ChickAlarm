package com.alra.sof.chickin.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alra.sof.chickin.data.models.*
import com.alra.sof.chickin.viewmodel.SleepViewModel
import java.time.Duration
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(viewModel: SleepViewModel, paddingValues: PaddingValues) {
    val isTracking by viewModel.isTracking.collectAsState()
    val sleepStats by viewModel.sleepStats.collectAsState()
    val sleepSessions by viewModel.sleepSessions.collectAsState()
    var showBreathingDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TopAppBar(
            title = { Text("🌙 Roosting Time", fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = { showBreathingDialog = true }) {
                    Icon(Icons.Default.Air, "Peaceful Clucking")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SleepTrackerCard(
                    isTracking = isTracking,
                    onStartTracking = { viewModel.startTracking() },
                    onStopTracking = { viewModel.stopTracking() }
                )
            }

            item {
                SleepStatsCard(stats = sleepStats)
            }

            if (sleepSessions.isNotEmpty()) {
                item {
                Text(
                    "🏠 Recent Roost Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                }

                items(sleepSessions.take(5)) { session ->
                    SleepSessionCard(session)
                }
            }
        }
    }

    if (showBreathingDialog) {
        BreathingExerciseDialog(
            viewModel = viewModel,
            onDismiss = { showBreathingDialog = false }
        )
    }
}

@Composable
fun SleepTrackerCard(
    isTracking: Boolean,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Bedtime,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isTracking) "🐔 Chickens are sleeping" else "🛏️ Ready for the roost",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isTracking) "Monitoring your coop rest" else "Track when the chickens settle in",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = if (isTracking) onStopTracking else onStartTracking,
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTracking) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isTracking) "🌅 Morning has come" else "🌛 Close the coop")
            }
        }
    }
}

@Composable
fun SleepStatsCard(stats: SleepStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🐓 Coop Rest Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("🕐 Avg Roost", String.format("%.1fh", stats.averageDuration))
                StatItem("📊 Routine", String.format("%.0f%%", stats.consistency))
                StatItem("💤 Rest Debt", String.format("%.1fh", stats.sleepDebt))
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "🐔 Type: ${stats.chronotype.name.replace('_', ' ')} Chicken",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Your optimal roosting schedule",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SleepSessionCard(session: SleepSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    session.startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                    Text(
                        session.endTime?.let { end ->
                            val duration = Duration.between(session.startTime, end)
                            String.format("%.1fh roosting", duration.toMinutes() / 60.0)
                        } ?: "Still roosting...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
            SleepQualityBadge(session.quality)
        }
    }
}

@Composable
fun SleepQualityBadge(quality: SleepQuality) {
    val color = when (quality) {
        SleepQuality.EXCELLENT -> MaterialTheme.colorScheme.primary
        SleepQuality.GOOD -> MaterialTheme.colorScheme.tertiary
        SleepQuality.FAIR -> MaterialTheme.colorScheme.secondary
        SleepQuality.POOR -> MaterialTheme.colorScheme.error
        SleepQuality.UNKNOWN -> MaterialTheme.colorScheme.outline
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                quality.getEmoji(),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                quality.getDescription(),
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BreathingExerciseDialog(viewModel: SleepViewModel, onDismiss: () -> Unit) {
    val exercises by viewModel.breathingExercises.collectAsState()
    var selectedExercise by remember { mutableStateOf<BreathingExercise?>(null) }
    
    if (selectedExercise == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("🐔 Peaceful Clucking Exercises") },
            text = {
                Column {
                    exercises.forEach { exercise ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedExercise = exercise },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    exercise.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    exercise.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "${exercise.inhale}-${exercise.hold}-${exercise.exhale} pattern",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )
    } else {
        BreathingAnimationDialog(
            exercise = selectedExercise!!,
            onDismiss = {
                selectedExercise = null
                onDismiss()
            }
        )
    }
}

@Composable
fun BreathingAnimationDialog(exercise: BreathingExercise, onDismiss: () -> Unit) {
    var currentCycle by remember { mutableIntStateOf(0) }
    var phase by remember { mutableStateOf("Inhale") }
    var timeLeft by remember { mutableIntStateOf(exercise.inhale) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (exercise.inhale + exercise.hold + exercise.exhale) * 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    
    LaunchedEffect(Unit) {
        while (currentCycle < exercise.cycles) {
            // Inhale
            phase = "Inhale"
            for (i in exercise.inhale downTo 1) {
                timeLeft = i
                delay(1000)
            }
            
            // Hold
            phase = "Hold"
            for (i in exercise.hold downTo 1) {
                timeLeft = i
                delay(1000)
            }
            
            // Exhale
            phase = "Exhale"
            for (i in exercise.exhale downTo 1) {
                timeLeft = i
                delay(1000)
            }
            
            currentCycle++
        }
        delay(1000)
        onDismiss()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(exercise.name) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            phase,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            timeLeft.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text("Cycle ${currentCycle + 1} of ${exercise.cycles}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Stop")
            }
        }
    )
}

