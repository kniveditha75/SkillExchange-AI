package com.simats.skillexchange.ui.screens.ideaowner

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.simats.skillexchange.data.models.ProjectUpdate
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@Composable
fun ManageProjectScreen(
    projectId: Int,
    onBack: () -> Unit,
    onNavigateToFindTeam: (Int) -> Unit,
    onNavigateToChat: (Int) -> Unit,
    vm: ProjectViewModel = viewModel(),
    rvm: com.simats.skillexchange.ui.viewmodels.RatingViewModel = viewModel()
) {
    val project by vm.selectedProject.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val members by vm.members.collectAsState()
    val successMsg by vm.successMsg.collectAsState()
    val ratingSuccessMsg by rvm.successMsg.collectAsState()
    val ratingError by rvm.error.collectAsState()

    var showStatusDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showFundingDialog by remember { mutableStateOf(false) }
    var showTeamDialog by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        vm.loadProject(projectId)
        vm.loadMembers(projectId)
    }

    LaunchedEffect(successMsg, ratingSuccessMsg, ratingError) {
        if (successMsg != null || ratingSuccessMsg != null || ratingError != null) {
            kotlinx.coroutines.delay(3000)
            vm.clearMessages()
            rvm.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Text("Manage Project", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (successMsg != null || ratingSuccessMsg != null) {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth().background(Teal500.copy(0.1f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                Text(successMsg ?: ratingSuccessMsg!!, color = Teal500, fontWeight = FontWeight.Bold)
            }
        }
        if (ratingError != null) {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth().background(Color.Red.copy(0.1f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                Text(ratingError!!, color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        if (isLoading) {
            LoadingState()
        } else if (project != null) {
            val p = project!!
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(p.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark, modifier = Modifier.weight(1f))
                                StatusChip(p.status)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(p.description, color = TextGray, fontSize = 14.sp)
                            
                            if (p.category != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(p.category, color = Purple500, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Quick Actions
                item { SectionHeader("Project Settings") }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ManageActionItem("Change Project Status", Icons.Default.Edit) { showStatusDialog = true }
                        ManageActionItem("Edit Details", Icons.Default.Description) { showEditDialog = true }
                        ManageActionItem("Funding Settings", Icons.Default.MonetizationOn) { showFundingDialog = true }
                        ManageActionItem("Team Settings", Icons.Default.People) { showTeamDialog = true }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ManageActionItem("Find New Talent", Icons.Default.GroupAdd) { onNavigateToFindTeam(p.id) }
                        
                        ManageActionItem("Group Chat", Icons.Default.Chat) { 
                            vm.getProjectChatId(p.id) { convId ->
                                if (convId != null) onNavigateToChat(convId)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        ManageActionItem("Delete Project", Icons.Default.Delete, Color.Red) {
                            vm.deleteProject(p.id) { onBack() }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    if (showStatusDialog && project != null) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val statuses = listOf("open", "in_progress", "completed", "closed")
                    statuses.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    vm.updateProject(project!!.id, ProjectUpdate(status = status))
                                    showStatusDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusChip(status)
                            if (project!!.status == status) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.Check, contentDescription = null, tint = Teal500)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showStatusDialog = false }) { Text("Cancel") } }
        )
    }

    if (showEditDialog && project != null) {
        var title by remember { mutableStateOf(project!!.title) }
        var desc by remember { mutableStateOf(project!!.description) }
        var category by remember { mutableStateOf(project!!.category ?: "Tech") }
        var teamSize by remember { mutableStateOf(project!!.team_size.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Project Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = teamSize, onValueChange = { teamSize = it }, label = { Text("Max Team Size") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.updateProject(project!!.id, ProjectUpdate(title = title, description = desc, category = category, team_size = teamSize.toIntOrNull()))
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple500)
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Cancel") } }
        )
    }

    if (showFundingDialog && project != null) {
        var seeking by remember { mutableStateOf(project!!.seeking_funding) }
        var goal by remember { mutableStateOf(project!!.funding_goal.toString()) }

        AlertDialog(
            onDismissRequest = { showFundingDialog = false },
            title = { Text("Funding Settings") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = seeking, onCheckedChange = { seeking = it })
                        Text("Seeking Funding?")
                    }
                    if (seeking) {
                        OutlinedTextField(value = goal, onValueChange = { goal = it }, label = { Text("Funding Goal (₹)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.updateProject(project!!.id, ProjectUpdate(seeking_funding = seeking, funding_goal = goal.toDoubleOrNull()))
                        showFundingDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple500)
                ) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = { showFundingDialog = false }) { Text("Cancel") } }
        )
    }

    if (showTeamDialog && project != null) {
        AlertDialog(
            onDismissRequest = { showTeamDialog = false },
            title = { Text("Team Members") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (members.isEmpty()) {
                        Text("No members yet.", color = TextGray, fontSize = 14.sp)
                    } else {
                        members.forEach { member ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(member.full_name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                                    Text(member.role, fontSize = 12.sp, color = TextGray)
                                }
                                var showRateMemberDialog by remember { mutableStateOf(false) }
                                IconButton(onClick = { showRateMemberDialog = true }) {
                                    Icon(Icons.Default.Star, contentDescription = "Rate", tint = Orange500)
                                }
                                IconButton(onClick = { vm.removeMember(project!!.id, member.user_id) }) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Remove", tint = Color.Red)
                                }
                                
                                if (showRateMemberDialog) {
                                    var score by remember { mutableFloatStateOf(5.0f) }
                                    var comment by remember { mutableStateOf("") }
                                    
                                    AlertDialog(
                                        onDismissRequest = { showRateMemberDialog = false },
                                        title = { Text("Rate ${member.full_name}") },
                                        text = {
                                            Column {
                                                Text("Performance Score: ${score.toInt()}/5", fontWeight = FontWeight.Bold)
                                                Slider(
                                                    value = score,
                                                    onValueChange = { score = it },
                                                    valueRange = 1f..5f,
                                                    steps = 3
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                OutlinedTextField(
                                                    value = comment,
                                                    onValueChange = { comment = it },
                                                    label = { Text("Performance Note (Optional)") },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = {
                                                    rvm.submitRating(project!!.id, member.user_id, score, comment)
                                                    showRateMemberDialog = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Purple500)
                                            ) { Text("Submit Rating") }
                                        },
                                        dismissButton = { TextButton(onClick = { showRateMemberDialog = false }) { Text("Cancel") } }
                                    )
                                }
                            }
                            Divider(color = BorderGray.copy(alpha = 0.5f))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showTeamDialog = false }) { Text("Close") } }
        )
    }
}

@Composable
fun ManageActionItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color = TextDark, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (color == TextDark) Purple500 else color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = color, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BorderGray)
        }
    }
}

private fun Modifier.clickable(onClick: () -> Unit) = this.then(Modifier.clickable(onClick = onClick))


