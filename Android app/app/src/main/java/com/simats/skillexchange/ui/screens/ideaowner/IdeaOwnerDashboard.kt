package com.simats.skillexchange.ui.screens.ideaowner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.simats.skillexchange.ui.viewmodels.DashboardViewModel

@Composable
fun IdeaOwnerDashboard(
    fullName: String,
    onNavigateToCreate: () -> Unit,
    onNavigateToManage: (Int) -> Unit,
    onNavigateToFindTeam: (Int) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    vm: DashboardViewModel = viewModel()
) {
    val myProjects by vm.myProjects.collectAsState()
    val unreadCount by vm.unreadCount.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadDashboard("idea_owner")
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        SETopBar(
            title = "Hi, ${fullName.split(" ").first()} 👋",
            role = "Idea Owner",
            unreadCount = unreadCount,
            onNotificationClick = onNavigateToNotifications,
            onProfileClick = onNavigateToProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Action (Normal)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Launch Your Idea", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Connect with talented freelancers to build your vision.", color = Color.White.copy(0.9f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToCreate,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Project")
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) { StatCard("Projects", "${myProjects.size}", Icons.Default.Folder, PrimaryBlue) }
                    Box(modifier = Modifier.weight(1f)) { StatCard("Team", "${myProjects.sumOf { it.member_count }}", Icons.Default.Groups, AccentTeal) }
                }
            }

            // My Projects Section
            item { SectionHeader("My Projects") }

            if (isLoading) {
                item { LoadingState() }
            } else if (myProjects.isEmpty()) {
                item { EmptyState("Start by creating your first project idea!", Icons.Outlined.Lightbulb) }
            } else {
                items(myProjects) { project ->
                    IdeaOwnerProjectCard(
                        project = project,
                        onManage = { onNavigateToManage(project.id) },
                        onFindTeam = { onNavigateToFindTeam(project.id) }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun IdeaOwnerProjectCard(
    project: com.simats.skillexchange.data.models.ProjectOut,
    onManage: () -> Unit,
    onFindTeam: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text(project.category ?: "Idea Stage", fontSize = 12.sp, color = PrimaryBlue)
                }
                StatusChip(project.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onManage,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
                ) {
                    Text("Manage")
                }
                Button(
                    onClick = onFindTeam,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Team")
                }
            }
        }
    }
}

