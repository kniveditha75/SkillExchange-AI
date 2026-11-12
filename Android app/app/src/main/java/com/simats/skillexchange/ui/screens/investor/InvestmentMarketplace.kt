package com.simats.skillexchange.ui.screens.investor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
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
import com.simats.skillexchange.ui.theme.*

@Composable
fun InvestmentMarketplaceScreen(
    onBack: () -> Unit,
    vm: FundingViewModel = viewModel()
) {
    val projects by vm.fundingProjects.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val successMsg by vm.successMsg.collectAsState()
    val error by vm.error.collectAsState()
    
    var showInvestDialog by remember { mutableStateOf(false) }
    var selectedProjectId by remember { mutableStateOf(-1) }
    var investAmount by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.loadFundingProjects()
    }
    
    LaunchedEffect(successMsg, error) {
        if (successMsg != null || error != null) {
            kotlinx.coroutines.delay(3000)
            vm.clearMessages()
        }
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
            Text("Investment Opportunities", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = TextGray)
            }
        }
        Divider(color = BorderGray)

        if (successMsg != null) {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth().background(Teal500.copy(0.1f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                Text(successMsg!!, color = Teal500, fontWeight = FontWeight.Bold)
            }
        }

        if (isLoading) {
            LoadingState()
        } else if (projects.isEmpty()) {
            EmptyState("No projects seeking funding at the moment.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(projects) { project ->
                    InvestmentProjectCard(
                        project = project,
                        onInvestClick = {
                            selectedProjectId = project.id
                            showInvestDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showInvestDialog) {
        val selectedProject = projects.find { it.id == selectedProjectId }
        val remainingBudget = if (selectedProject != null) {
            selectedProject.funding_goal - (selectedProject.amount_raised ?: 0.0)
        } else 0.0

        AlertDialog(
            onDismissRequest = { showInvestDialog = false },
            title = { Text("Invest in Project") },
            text = {
                Column {
                    Text(selectedProject?.title ?: "Project", fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Remaining Budget: ₹$remainingBudget", color = Teal500, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Enter amount to invest (1 - $remainingBudget):", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = investAmount,
                        onValueChange = { investAmount = it },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    if (investAmount.isNotEmpty()) {
                        val amt = investAmount.toDoubleOrNull()
                        if (amt == null) {
                            Text("Please enter a valid number", color = Color.Red, fontSize = 12.sp)
                        } else if (amt <= 0) {
                            Text("Amount must be greater than 0", color = Color.Red, fontSize = 12.sp)
                        } else if (amt > remainingBudget) {
                            Text("Amount exceeds remaining budget (Max: ₹$remainingBudget)", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = investAmount.toDoubleOrNull()
                        if (amt != null && amt > 0 && amt <= remainingBudget) {
                            vm.invest(selectedProjectId, amt)
                            showInvestDialog = false
                            investAmount = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple500),
                    enabled = investAmount.toDoubleOrNull()?.let { it > 0 && it <= remainingBudget } ?: false
                ) { Text("Confirm Investment") }
            },
            dismissButton = {
                TextButton(onClick = { showInvestDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun InvestmentProjectCard(
    project: com.simats.skillexchange.data.models.FundingProjectOut,
    onInvestClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text(project.category ?: "Sector TBD", fontSize = 12.sp, color = Purple500)
                }
                Box(
                    modifier = Modifier.background(Teal500.copy(0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${project.pct_funded}% funded", color = Teal500, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(project.description, color = TextGray, fontSize = 13.sp, maxLines = 2)
            Spacer(modifier = Modifier.height(16.dp))
            
            val progress = (project.pct_funded / 100).toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Teal500,
                trackColor = BorderGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("₹${project.amount_raised} raised", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Target: ₹${project.funding_goal}", fontSize = 12.sp, color = TextGray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(text = "Invest Now", onClick = onInvestClick, modifier = Modifier.fillMaxWidth().height(40.dp))
        }
    }
}

