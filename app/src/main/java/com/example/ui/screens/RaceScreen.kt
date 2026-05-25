package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RaceUiState
import com.example.ui.RaceViewModel
import com.example.ui.components.SimpleTrack
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddPointsDialog by remember { mutableStateOf(false) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var rewardMessage by remember { mutableStateOf("") }
    
    var lastPoints by remember { mutableIntStateOf(-1) }
    var recentAddedPoints by remember { mutableIntStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("מרוץ ההצלחות") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "חזור")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPointsDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_points_fab")
            ) {
                Text("+ נקודות", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is RaceUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RaceUiState.NeedsSetup -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading Setup...")
                }
            }
            is RaceUiState.Success -> {
                val profile = state.profile
                val progress = state.trackProgress
                val currentPoints = progress.currentPoints
                
                LaunchedEffect(currentPoints) {
                    if (lastPoints != -1 && currentPoints > lastPoints) {
                        recentAddedPoints = currentPoints - lastPoints
                        showFeedback = true
                        
                        val milestones = listOf(100, 250, 400)
                        if (currentPoints >= progress.targetPoints) {
                            rewardMessage = "אלופים! סיימתם את המסלול וקיבלתם 200 מטבעות בונוס!"
                            showRewardDialog = true
                        } else if (milestones.any { it in (lastPoints + 1)..currentPoints }) {
                            rewardMessage = "הגעתם לתחנה חדשה או פרס! בואו נראה מה מחכה לכם במוסך!"
                            showRewardDialog = true
                        }
                    }
                    if (currentPoints == 0 && lastPoints > 0) { // Reset track
                        lastPoints = 0
                    } else {
                        lastPoints = currentPoints
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${progress.currentPoints} / ${progress.targetPoints} נקודות",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Track Visualization
                    SimpleTrack(
                        currentPoints = progress.currentPoints,
                        targetPoints = progress.targetPoints,
                        carId = profile.currentCarId,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Box(modifier = Modifier.height(80.dp), contentAlignment = Alignment.Center) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showFeedback,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = "+$recentAddedPoints כל הכבוד!", 
                                color = MaterialTheme.colorScheme.primary, 
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            LaunchedEffect(showFeedback) {
                                if (showFeedback) {
                                    delay(2000)
                                    showFeedback = false
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quick add buttons for parents
                    Text("הוספה מהירה (להורים):", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickAddButton("+5", 5, viewModel)
                        QuickAddButton("+10", 10, viewModel)
                        QuickAddButton("+20", 20, viewModel)
                        QuickAddButton("+50", 50, viewModel)
                    }
                }
            }
        }
    }

    if (showAddPointsDialog) {
        AddPointsDialog(
            onDismiss = { showAddPointsDialog = false },
            onConfirm = { points, note ->
                viewModel.addPoints(points, note)
                showAddPointsDialog = false
            }
        )
    }
    
    com.example.ui.components.ConfettiOverlay(
        isRunning = showRewardDialog
    )
    
    if (showRewardDialog) {
        AlertDialog(
            onDismissRequest = { showRewardDialog = false },
            confirmButton = {
                Button(onClick = { showRewardDialog = false }) {
                    Text("יש!")
                }
            },
            title = { Text("הפתעה חדשה נפתחה!") },
            text = { Text(rewardMessage) }
        )
    }
}

@Composable
fun QuickAddButton(label: String, points: Int, viewModel: RaceViewModel) {
    FilledTonalButton(
        onClick = { viewModel.addPoints(points, "הוספה מהירה") },
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun AddPointsDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var pointsText by remember { mutableStateOf("15") }
    var noteText by remember { mutableStateOf("כל הכבוד על המאמץ!") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val p = pointsText.toIntOrNull() ?: 10
                onConfirm(p, noteText)
            }) {
                Text("הוסף")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ביטול") }
        },
        title = { Text("הוספת נקודות (להורה)") },
        text = {
            Column {
                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { pointsText = it },
                    label = { Text("כמות נקודות") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("על מה קיבל/ה?") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
