package com.alra.sof.chickin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alra.sof.chickin.data.models.*
import com.alra.sof.chickin.viewmodel.MorningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorningScreen(viewModel: MorningViewModel, paddingValues: PaddingValues) {
    val scenario by viewModel.currentScenario.collectAsState()
    val isActive by viewModel.isScenarioActive.collectAsState()
    
    // Calculate completion percentage based on current tasks
    val completionPercentage = remember(scenario) {
        derivedStateOf {
            if (scenario.tasks.isEmpty()) 0f
            else scenario.tasks.count { it.isCompleted }.toFloat() / scenario.tasks.size.toFloat()
        }
    }.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TopAppBar(
            title = { Text("🌅 Sunrise at the Coop", fontWeight = FontWeight.Bold) },
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
                ProgressCard(
                    isActive = isActive,
                    completionPercentage = completionPercentage,
                    onStart = { viewModel.startScenario() },
                    onComplete = { viewModel.completeScenario() }
                )
            }

            item {
                Text(
                    "🥚 Today's Farm Chores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(scenario.tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onToggle = { viewModel.toggleTaskCompletion(task.id) }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProgressCard(
    isActive: Boolean,
    completionPercentage: Float,
    onStart: () -> Unit,
    onComplete: () -> Unit
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (isActive) "Chickens are busy! 🐔" else "Time to wake the flock! 🐓",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isActive) "Complete your farm chores" else "Start your day at the coop",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    Icons.Default.WbSunny,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isActive) {
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { completionPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "🌾 ${(completionPercentage * 100).toInt()}% of chores done",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    when {
                        isActive && completionPercentage >= 1f -> onComplete()
                        !isActive -> onStart()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isActive || completionPercentage >= 1f,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (completionPercentage >= 1f) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    when {
                        completionPercentage >= 1f -> "🎉 Collect Fresh Eggs!"
                        isActive -> "🐥 Finish all chores first"
                        else -> "🌄 Open the Coop!"
                    }
                )
            }
        }
    }
}

@Composable
fun TaskCard(task: MorningTask, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    getTaskIcon(task.icon),
                    contentDescription = null,
                    tint = if (task.isCompleted) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (task.isCompleted) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${task.duration} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (task.isCompleted) 
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (task.isCompleted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Not completed",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun getTaskIcon(icon: TaskIcon): ImageVector {
    return when (icon) {
        TaskIcon.WATER -> Icons.Default.WaterDrop
        TaskIcon.EXERCISE -> Icons.Default.FitnessCenter
        TaskIcon.SHOWER -> Icons.Default.Shower
        TaskIcon.BREAKFAST -> Icons.Default.Restaurant
        TaskIcon.MEDITATION -> Icons.Default.SelfImprovement
        TaskIcon.READING -> Icons.Default.Book
        TaskIcon.WEATHER -> Icons.Default.WbSunny
        TaskIcon.CALENDAR -> Icons.Default.CalendarToday
    }
}

