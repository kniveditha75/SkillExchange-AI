package com.simats.skillexchange.ui.screens.freelancer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectDetail(
    projectId: Int,
    currentUserId: Int,
    onBack: () -> Unit,
    onNavigateToChat: (Int) -> Unit,
    vm: ProjectViewModel = viewModel()
) {
    val project     by vm.selectedProject.collectAsState()
    val matchScore  by vm.matchScore.collectAsState()
    val health      by vm.healthAnalysis.collectAsState()
    val isLoading   by vm.isLoading.collectAsState()
    val error       by vm.error.collectAsState()
    val successMsg  by vm.successMsg.collectAsState()
    var showApplyDialog by remember { mutableStateOf(false) }
    var applyMessage    by remember { mutableStateOf("") }

    LaunchedEffect(projectId) { vm.loadProject(projectId) }

    LaunchedEffect(error, successMsg) {
        if (error != null || successMsg != null) {
            kotlinx.coroutines.delay(3000)
            vm.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        // Normal Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextDark) }
                Text("Project Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            }
        }

        // Messages
        successMsg?.let {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = SuccessGreen)) {
                Text(it, color = Color.White, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Medium)
            }
        }
        error?.let {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = ErrorRed)) {
                Text(it, color = Color.White, modifier = Modifier.padding(12.dp))
            }
        }

        if (isLoading) {
            LoadingState()
        } else if (project == null) {
            EmptyState("Project not found", Icons.Default.ErrorOutline)
        } else {
            val p = project!!
            val isOwner = p.owner_id == currentUserId
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(p.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark, modifier = Modifier.weight(1f))
                                StatusChip(p.status)
                            }
                            if (p.category != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(p.category, fontSize = 13.sp, color = PrimaryBlue, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("By ${p.owner_name ?: "Unknown"}", fontSize = 13.sp, color = TextGray)
                        }
                    }
                }

                // AI Investor Analysis (Visible to owners and everyone if it's high quality)
                health?.let { h ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Teal500.copy(0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Analytics, null, tint = Teal500, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("AI Investor Insights", fontWeight = FontWeight.Bold, color = TextDark)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Health Score:", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${h.health_score.toInt()}/100", fontWeight = FontWeight.Bold, color = if(h.health_score >= 80) SuccessGreen else if(h.health_score >= 50) Orange500 else ErrorRed)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Risk:", fontSize = 12.sp, color = TextGray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(h.risk_level, fontWeight = FontWeight.Bold, color = if(h.risk_level == "Low") SuccessGreen else if(h.risk_level == "Medium") Orange500 else ErrorRed)
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                h.insights.forEach { insight ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text("•", color = Teal500)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(insight, fontSize = 12.sp, color = TextDark)
                                    }
                                }
                            }
                        }
                    }
                }

                // Match score (for freelancers)
                matchScore?.let { score ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(0.2f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("AI Match Score: ${score.toInt()}%", fontWeight = FontWeight.Bold, color = TextDark)
                                    Text(if (score >= 70) "Great match for your skills!" else "Good potential match", fontSize = 12.sp, color = TextGray)
                                }
                            }
                        }
                    }
                }

                // Description
                item {
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text("Description", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(p.description, color = TextGray, lineHeight = 22.sp, fontSize = 14.sp)
                    }
                }

                // Skills required
                if (!p.required_skills.isNullOrBlank()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Text("Required Skills", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                p.required_skills.split(",").forEach { skill ->
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
                                    ) {
                                        Text(
                                            text = skill.trim(),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            fontSize = 12.sp,
                                            color = TextDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Funding
                if (p.funding_goal > 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Funding Progress", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                val pct = (p.amount_raised / p.funding_goal).coerceIn(0.0, 1.0).toFloat()
                                LinearProgressIndicator(
                                    progress = { pct },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = PrimaryBlue,
                                    trackColor = BgLight,
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("₹${p.amount_raised.toInt()} raised", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    Text("Goal: ₹${p.funding_goal.toInt()}", color = TextGray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Action Section
                item {
                    if (isOwner || p.application_status == "accepted") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {
                                    vm.getProjectChatId(p.id) { convId ->
                                        if (convId != null) onNavigateToChat(convId)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Icon(Icons.Default.Chat, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open Project Chat")
                            }
                        }
                    } else {
                        when (p.application_status) {
                            "pending"  -> OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)) { Text("Application Pending") }
                            "rejected" -> OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp)) { Text("Application Rejected") }
                            else -> Button(
                                onClick = { showApplyDialog = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) { Text("Apply to Join") }
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // ── Apply Dialog ──────────────────────────────────────────────────────────
    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text("Project Application") },
            text = {
                Column {
                    Text("Write a short message to the project owner:", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = applyMessage,
                        onValueChange = { applyMessage = it },
                        placeholder = { Text("Briefly explain why you're a good fit") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BorderGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.applyToProject(projectId, applyMessage.ifBlank { null })
                        showApplyDialog = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Submit") }
            },
            dismissButton = { TextButton(onClick = { showApplyDialog = false }) { Text("Cancel") } }
        )
    }
}

