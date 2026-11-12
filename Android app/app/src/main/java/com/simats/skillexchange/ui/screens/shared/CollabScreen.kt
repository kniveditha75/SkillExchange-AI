package com.simats.skillexchange.ui.screens.shared

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.data.models.ProjectOut
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@Composable
fun CollabScreen(
    onNavigateToChat: (Int) -> Unit,
    onNavigateToProjectDetail: (Int) -> Unit,
    onBack: () -> Unit,
    vm: ProjectViewModel = viewModel()
) {
    val projects by vm.myProjects.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyProjects()
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
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                }
                Text("Collaboration Hub", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            }
        }

        if (isLoading) {
            LoadingState()
        } else if (projects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    message = "No active teams yet.",
                    icon = Icons.Default.Groups
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(projects) { project ->
                    TeamProjectCard(
                        project = project,
                        onChatClick = {
                            vm.getProjectChatId(project.id) { convId ->
                                if (convId != null) onNavigateToChat(convId)
                            }
                        },
                        onDetailClick = { onNavigateToProjectDetail(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TeamProjectCard(
    project: ProjectOut,
    onChatClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onDetailClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(PrimaryBlue.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Work, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("${project.member_count} members", fontSize = 12.sp, color = TextGray)
                }
                StatusChip(project.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat", fontSize = 13.sp)
                }
                
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Details", fontSize = 13.sp)
                }
            }
        }
    }
}

