package com.simats.skillexchange.ui.screens.investor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.viewmodels.DashboardViewModel

@Composable
fun InvestorDashboard(
    fullName: String,
    onNavigateToInvest: () -> Unit,
    onNavigateToPortfolio: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    vm: DashboardViewModel = viewModel()
) {
    val stats by vm.fundingStats.collectAsState()
    val unreadCount by vm.unreadCount.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadDashboard("investor")
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        SETopBar(
            title = "Hi, ${fullName.split(" ").first()}",
            role = "Investor",
            unreadCount = unreadCount,
            onNotificationClick = onNavigateToNotifications,
            onProfileClick = onNavigateToProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portfolio Summary (Normal)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Portfolio Value", color = Color.White.copy(0.9f), fontSize = 14.sp)
                        Text("₹${stats?.portfolio_value ?: 0.0}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${stats?.roi_pct ?: 0.0}% ROI", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Stats Grid
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) { StatCard("Invested", "₹${stats?.total_invested ?: 0.0}", Icons.Default.AccountBalance, PrimaryBlue) }
                    Box(modifier = Modifier.weight(1f)) { StatCard("Active", "${stats?.active_investments ?: 0}", Icons.Default.Timeline, AccentTeal) }
                }
            }

            // Quick Action
            item {
                Button(
                    onClick = onNavigateToInvest,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Discover New Projects")
                }
            }

            // Portfolio breakdown
            item { SectionHeader("Portfolio Breakdown", "View All", onAction = onNavigateToPortfolio) }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InvestmentRow("Tech Startups", 45, PrimaryBlue)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderGray)
                        InvestmentRow("Social Impact", 30, AccentTeal)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderGray)
                        InvestmentRow("HealthTech", 25, Color(0xFFF59E0B))
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun InvestmentRow(label: String, pct: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = TextDark)
        Text("$pct%", fontWeight = FontWeight.Bold, color = TextDark)
    }
}

