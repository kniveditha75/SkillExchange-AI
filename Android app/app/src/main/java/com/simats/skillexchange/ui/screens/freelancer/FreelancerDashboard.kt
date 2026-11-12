package com.simats.skillexchange.ui.screens.freelancer

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
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.viewmodels.DashboardViewModel

@Composable
fun FreelancerDashboard(
    fullName: String,
    onNavigateToProjects: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToProjectDetail: (Int) -> Unit,
    onNavigateToChat: (Int) -> Unit,
    vm: DashboardViewModel = viewModel()
) {
    val recommendations by vm.recommendations.collectAsState()
    val myProjects      by vm.myProjects.collectAsState()
    val unreadCount     by vm.unreadCount.collectAsState()
    val isLoading       by vm.isLoading.collectAsState()
    val profile         by vm.profile.collectAsState()

    LaunchedEffect(Unit) { vm.loadDashboard("freelancer") }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        SETopBar(
            title = "Hi, ${fullName.split(" ").first()} 👋",
            role  = "Freelancer",
            unreadCount = unreadCount,
            onNotificationClick = onNavigateToNotifications,
            onProfileClick = onNavigateToProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Welcome Card (Normal) ─────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Find Your Next Project", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Browse projects matched to your skills.", color = Color.White.copy(0.9f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToProjects,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Browse Projects") }
                    }
                }
            }

            // ── Stats Row ─────────────────────────────────────────────────────
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) { StatCard("Active Projects", "${myProjects.size}", Icons.Default.Work, PrimaryBlue) }
                    Box(modifier = Modifier.weight(1f)) { StatCard("Skills", "${profile?.skills?.size ?: 0}", Icons.Default.Star, AccentTeal) }
                }
            }

            // ── AI Recommendations ────────────────────────────────────────────
            item { SectionHeader("Recommended for You", "See All", onAction = onNavigateToProjects) }

            if (isLoading) {
                item { LoadingState() }
            } else if (recommendations.isEmpty()) {
                item { EmptyState("Add skills to get AI recommendations!", Icons.Default.AutoAwesome) }
            } else {
                items(recommendations.take(3)) { proj ->
                    ProjectCard(
                        title         = proj.title,
                        category      = proj.category,
                        requiredSkills = proj.required_skills,
                        matchScore    = proj.match_score,
                        status        = "open",
                        teamSize      = proj.team_size,
                        amountRaised  = proj.amount_raised,
                        fundingGoal   = proj.funding_goal,
                        onClick       = { onNavigateToProjectDetail(proj.id) }
                    )
                }
            }

            // ── My Projects ───────────────────────────────────────────────────
            item { SectionHeader("My Projects") }

            if (myProjects.isEmpty()) {
                item { EmptyState("No active projects.", Icons.Default.FolderOpen) }
            } else {
                items(myProjects) { proj ->
                    ProjectCard(
                        title         = proj.title,
                        category      = proj.category,
                        requiredSkills = proj.required_skills,
                        status        = proj.status,
                        teamSize      = proj.team_size,
                        amountRaised  = proj.amount_raised,
                        fundingGoal   = proj.funding_goal,
                        onClick       = { onNavigateToProjectDetail(proj.id) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

