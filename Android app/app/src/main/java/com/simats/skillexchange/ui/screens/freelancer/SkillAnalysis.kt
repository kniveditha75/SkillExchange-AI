package com.simats.skillexchange.ui.screens.freelancer

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.data.models.SkillStat
import com.simats.skillexchange.data.models.SuggestedSkill
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@Composable
fun SkillAnalysisScreen(
    onBack: () -> Unit,
    vm: ProjectViewModel = viewModel()
) {
    val analysis by produceState<com.simats.skillexchange.data.models.SkillAnalysis?>(initialValue = null) {
        value = com.simats.skillexchange.data.repository.ProjectRepository().getSkillAnalysis()
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
            Text("AI Skill Analysis", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (analysis == null) {
            LoadingState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overall Score Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Market Compatibility Score", fontSize = 14.sp, color = TextGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${analysis?.overall_score}%", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Purple500)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Based on analysis of ${analysis?.total_projects_analyzed} active projects", fontSize = 12.sp, color = TextGray)
                        }
                    }
                }

                // AI Roadmap Section
                item { SectionHeader("AI Personalized Roadmap") }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Teal500.copy(0.05f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Teal500.copy(0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            RoadmapItem(1, "Master your high-demand skill: ${analysis?.user_skills?.firstOrNull { it.level == "high" }?.skill ?: analysis?.user_skills?.firstOrNull()?.skill ?: "Current Skills"}")
                            RoadmapItem(2, "Learn ${analysis?.suggested_skills?.getOrNull(0)?.skill ?: "new skills"} to unlock ${analysis?.suggested_skills?.getOrNull(0)?.demand_count ?: 0} more projects")
                            RoadmapItem(3, "Apply to Top 3 projects matching your ${analysis?.overall_score}% compatibility")
                        }
                    }
                }

                // User Skills Analysis
                item { SectionHeader("Your Skills & Market Demand") }
                items(analysis?.user_skills ?: emptyList()) { skillStat ->
                    SkillStatRow(skillStat)
                }

                // Suggested Skills
                item { SectionHeader("Top Demanded Skills (Suggestions)") }
                items(analysis?.suggested_skills ?: emptyList()) { suggested ->
                    SuggestedSkillRow(suggested)
                }
                
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun RoadmapItem(step: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Teal500),
            contentAlignment = Alignment.Center
        ) {
            Text("$step", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SkillStatRow(stat: SkillStat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(stat.skill, fontWeight = FontWeight.Bold, color = TextDark)
                Box(
                    modifier = Modifier
                        .background(
                            when(stat.level) {
                                "high" -> Teal500.copy(0.1f)
                                "medium" -> Orange500.copy(0.1f)
                                else -> TextGray.copy(0.1f)
                            }, 
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        stat.level.uppercase(), 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold,
                        color = when(stat.level) {
                            "high" -> Teal500
                            "medium" -> Orange500
                            else -> TextGray
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (stat.demand_pct / 100).toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Purple500,
                trackColor = BorderGray
            )
            Text("In demand by ${stat.demand_pct}% of projects", fontSize = 11.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun SuggestedSkillRow(skill: SuggestedSkill) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Orange500.copy(0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Orange500, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(skill.skill, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text("Needed by ${skill.demand_count} open projects", fontSize = 11.sp, color = TextGray)
        }
    }
}

