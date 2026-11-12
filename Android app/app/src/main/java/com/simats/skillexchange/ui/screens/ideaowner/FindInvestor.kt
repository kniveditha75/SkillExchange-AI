package com.simats.skillexchange.ui.screens.ideaowner

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
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
import com.simats.skillexchange.ui.viewmodels.FundingViewModel

@Composable
fun FindInvestorScreen(
    onBack: () -> Unit,
    vm: FundingViewModel = viewModel()
) {
    val fundingProjects by vm.fundingProjects.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadFundingProjects()
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
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
            Text("Funding Marketplace", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (isLoading) {
            LoadingState()
        } else if (fundingProjects.isEmpty()) {
            EmptyState("No projects currently seeking funding.", Icons.Default.TrendingUp)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(fundingProjects) { project ->
                    ProjectCard(
                        title = project.title,
                        category = project.category,
                        requiredSkills = null,
                        status = "Seeking Funding",
                        teamSize = project.team_size,
                        amountRaised = project.amount_raised,
                        fundingGoal = project.funding_goal,
                        onClick = { }
                    )
                }
            }
        }
    }
}

