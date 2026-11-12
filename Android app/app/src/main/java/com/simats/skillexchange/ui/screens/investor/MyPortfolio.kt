package com.simats.skillexchange.ui.screens.investor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PieChart
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
fun MyPortfolioScreen(
    onBack: () -> Unit,
    onChat: (Int) -> Unit,
    vm: FundingViewModel = viewModel()
) {
    val portfolio by vm.portfolio.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadPortfolio()
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
            Text("My Portfolio", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (isLoading) {
            LoadingState()
        } else if (portfolio.isEmpty()) {
            EmptyState("You haven't made any investments yet.", Icons.Default.PieChart)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(portfolio) { investment ->
                    InvestmentCard(
                        investment = investment,
                        onChat = {
                            vm.getProjectChatId(investment.project_id) { convId ->
                                if (convId != null) onChat(convId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InvestmentCard(
    investment: com.simats.skillexchange.data.models.InvestmentOut,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(investment.project_title ?: "Deleted Project", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("Invested on ${investment.created_at.split("T").first()}", fontSize = 12.sp, color = TextGray)
                }
                StatusChip(investment.project_status ?: "unknown")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Your Investment", fontSize = 11.sp, color = TextGray)
                    Text("₹${investment.amount}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Purple500)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Project Progress", fontSize = 11.sp, color = TextGray)
                    Text("${investment.pct_funded}%", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Teal500)
                }
            }
            
            if (!investment.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Note: ${investment.note}", fontSize = 12.sp, color = TextGray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onChat,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Purple500),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chat with Team", fontWeight = FontWeight.Bold)
            }
        }
    }
}

