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
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectMarketplace(
    onNavigateToDetail: (Int) -> Unit,
    onBack: () -> Unit,
    vm: ProjectViewModel = viewModel()
) {
    val projects  by vm.projects.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    var searchQ   by remember { mutableStateOf("") }
    var category  by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.loadProjects() }

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
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark) }
                Text("Marketplace", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            }
        }

        // Search Section
        Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
            OutlinedTextField(
                value = searchQ,
                onValueChange = { searchQ = it },
                placeholder = { Text("Search projects...", color = TextGray) },
                leadingIcon  = { Icon(Icons.Default.Search, null, tint = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderGray
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Categories
            val cats = listOf("All", "HealthTech", "EdTech", "FinTech", "AI/ML")
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cats) { cat ->
                    val isSelected = category == cat || (cat == "All" && category.isEmpty())
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            category = if (cat == "All") "" else cat
                            vm.loadProjects(q = searchQ.ifBlank { null }, category = category.ifBlank { null })
                        },
                        label = { Text(cat) },
                        shape = RoundedCornerShape(4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) PrimaryBlue else BorderGray,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { vm.loadProjects(q = searchQ.ifBlank { null }, category = category.ifBlank { null }) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Apply Search")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                item { LoadingState() }
            } else if (projects.isEmpty()) {
                item { EmptyState("No projects match your search.", Icons.Default.SearchOff) }
            } else {
                items(projects) { proj ->
                    ProjectCard(
                        title          = proj.title,
                        category       = proj.category,
                        requiredSkills = proj.required_skills,
                        status         = proj.status,
                        teamSize       = proj.team_size,
                        amountRaised   = proj.amount_raised,
                        fundingGoal    = proj.funding_goal,
                        onClick        = { onNavigateToDetail(proj.id) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

