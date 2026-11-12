package com.simats.skillexchange.ui.screens.ideaowner

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.viewmodels.DashboardViewModel

@Composable
fun IdeaOwnerAllTeamsScreen(
    onNavigateToFindTeam: (Int) -> Unit,
    onBack: () -> Unit,
    vm: DashboardViewModel = viewModel()
) {
    val myProjects by vm.myProjects.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadDashboard("idea_owner")
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
            Text("Manage My Teams", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (isLoading) {
            LoadingState()
        } else if (myProjects.isEmpty()) {
            EmptyState("You haven't created any projects yet.", Icons.Default.Groups)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Select a project to review applicants or manage team members.",
                        fontSize = 13.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(myProjects) { project ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        onClick = { onNavigateToFindTeam(project.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Purple500.copy(0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = Purple500)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(project.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                                Text("${project.member_count} active members", fontSize = 12.sp, color = TextGray)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BorderGray)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

