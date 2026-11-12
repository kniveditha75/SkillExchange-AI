package com.simats.skillexchange.ui.screens.freelancer

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
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
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@Composable
fun MyWorkScreen(
    onNavigateToProjectDetail: (Int) -> Unit,
    onBack: () -> Unit,
    vm: ProjectViewModel = viewModel()
) {
    val myProjects by vm.myProjects.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyProjects()
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Text("My Work", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (isLoading) {
            LoadingState()
        } else if (myProjects.isEmpty()) {
            EmptyState(message = "You haven't joined or applied to any projects yet.", icon = Icons.Default.Assignment)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myProjects) { project ->
                    ProjectCard(
                        title = project.title,
                        category = project.category,
                        requiredSkills = project.required_skills,
                        status = project.status,
                        teamSize = project.team_size,
                        amountRaised = project.amount_raised,
                        fundingGoal = project.funding_goal,
                        onClick = { onNavigateToProjectDetail(project.id) }
                    )
                }
            }
        }
    }
}

