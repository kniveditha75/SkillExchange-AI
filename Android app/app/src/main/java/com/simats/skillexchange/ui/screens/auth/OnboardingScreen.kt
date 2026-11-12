package com.simats.skillexchange.ui.screens.auth

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import com.simats.skillexchange.ui.viewmodels.AuthViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.skillexchange.ui.theme.SkillExchangeTheme

// ── Colors ─────────────────────────────────────────────────────────────────────
private val OBPurple   = Color(0xFF6C63FF)
private val OBTeal     = Color(0xFF00B8A0)
private val OBTextDark = Color(0xFF1A1A2E)
private val OBTextGray = Color(0xFF6B6B8A)
private val OBBorder   = Color(0xFFE2E2F0)
private val OBBgStart  = BgLight
private val OBBgEnd    = Color(0xFFFFFFFF)

// ── Data ───────────────────────────────────────────────────────────────────────
private val allSkills = listOf(
    "React", "Vue.js", "Angular", "Node.js",
    "Python", "Django", "TypeScript", "JavaScript",
    "Java", "Kotlin", "Swift", "Flutter",
    "React Native", "UI/UX Design", "Figma",
    "Graphic Design", "Motion Graphics", "Machine Learning",
    "TensorFlow", "NLP", "Computer Vision", "Data Science",
    "AWS", "Docker", "Kubernetes", "DevOps",
    "PostgreSQL", "MongoDB", "Product Management",
    "Business Strategy", "Marketing", "Finance",
    "SEO", "Content Writing", "Video Editing",
    "Photography", "Blockchain", "Solidity"
)

private val allInterests = listOf(
    "🏥" to "HealthTech",
    "📚" to "EdTech",
    "💰" to "FinTech",
    "🔗" to "Web3 /\nBlockchain",
    "🤖" to "AI / Machine\nLearning",
    "🌱" to "CleanTech /\nSustainability",
    "🌍" to "Social Impact",
    "🛒" to "E-Commerce",
    "⚡" to "SaaS /\nProductivity",
    "🎮" to "Gaming /\nEntertainment",
    "🔌" to "IoT / Hardware",
    "🌾" to "AgriTech"
)

private val allGoals = listOf(
    "💸" to ("Earn Income" to "Get paid for my skills on projects"),
    "📖" to ("Learn & Grow" to "Develop new skills through real projects"),
    "📈" to ("Invest & Grow Capital" to "Fund promising projects for returns"),
    "🏗️" to ("Build My Idea" to "Launch my project with a skilled team"),
    "🤝" to ("Network & Connect" to "Build relationships with top professionals"),
    "🗂️" to ("Build Portfolio" to "Showcase real-world projects")
)

// ── Main Onboarding Screen ─────────────────────────────────────────────────────
@Composable
fun OnboardingScreen(
    viewModel: AuthViewModel,
    onNavigateToDashboard: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var currentStep       by remember { mutableStateOf(0) } // 0=Skills, 1=Interests, 2=Goals
    var skillSearch       by remember { mutableStateOf("") }
    val selectedSkills    = remember { mutableStateListOf<String>() }
    val selectedInterests = remember { mutableStateListOf<String>() }
    val selectedGoals     = remember { mutableStateListOf<String>() }

    val filteredSkills = if (skillSearch.isBlank()) allSkills
                         else allSkills.filter { it.contains(skillSearch, ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(OBBgStart, OBBgEnd)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ───────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(OBPurple, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Skill Exchange AI", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OBTextDark)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step indicator
                StepIndicator(currentStep = currentStep)

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Step ${currentStep + 1} of 3",
                    fontSize = 11.sp,
                    color = OBTextGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

            // ── Step Content ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (currentStep) {
                    0 -> SkillsStep(
                        searchQuery    = skillSearch,
                        onSearchChange = { skillSearch = it },
                        filteredSkills = filteredSkills,
                        selectedSkills = selectedSkills
                    )
                    1 -> InterestsStep(selectedInterests = selectedInterests)
                    2 -> GoalsStep(selectedGoals = selectedGoals)
                }
            }

            // ── Bottom Navigation ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStep > 0) currentStep-- else onBack()
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OBBorder)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OBTextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back", color = OBTextGray, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                        } else {
                            viewModel.saveOnboardingData(
                                selectedSkills.toList(),
                                selectedInterests.toList(),
                                selectedGoals.toList()
                            ) {
                                onNavigateToDashboard()
                            }
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OBPurple)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (currentStep == 2) "Get Started! ⚡" else "Next  →",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ── Step Indicator ─────────────────────────────────────────────────────────────
@Composable
private fun StepIndicator(currentStep: Int) {
    val steps = listOf("Skills", "Interests", "Goals")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            val isDone    = index < currentStep
            val isCurrent = index == currentStep
            val circleColor = when {
                isDone || isCurrent -> OBPurple
                else                -> OBBorder
            }
            val textColor = when {
                isDone || isCurrent -> OBPurple
                else                -> OBTextGray
            }

            // Circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(circleColor),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                } else {
                    Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal, color = textColor)

            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (isDone) OBPurple else OBBorder)
                )
            }
        }
    }
}

// ── Step 1: Skills ─────────────────────────────────────────────────────────────
@Composable
private fun SkillsStep(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    filteredSkills: List<String>,
    selectedSkills: MutableList<String>
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("What are your skills? 💡", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OBTextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Select all that apply. AI will use these to match you with perfect projects.", fontSize = 13.sp, color = OBTextGray, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search skills...", color = OBTextGray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OBTextGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = OBPurple,
                unfocusedBorderColor    = OBBorder,
                focusedTextColor        = OBTextDark,
                unfocusedTextColor      = OBTextDark,
                cursorColor             = OBPurple,
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Skill chips wrapped
        FlowRow(skills = filteredSkills, selected = selectedSkills)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FlowRow(skills: List<String>, selected: MutableList<String>) {
    // Manual wrapping using columns of rows
    val chunked = skills.chunked(4)
    chunked.forEach { rowSkills ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowSkills.forEach { skill ->
                val isSelected = skill in selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) OBPurple else Color.White)
                        .border(1.dp, if (isSelected) OBPurple else OBBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            if (isSelected) selected.remove(skill) else selected.add(skill)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = skill,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else OBTextDark,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ── Step 2: Interests ──────────────────────────────────────────────────────────
@Composable
private fun InterestsStep(selectedInterests: MutableList<String>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("What interests you? 🎯", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OBTextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Pick the industries and project types you're passionate about.", fontSize = 13.sp, color = OBTextGray, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val chunked = allInterests.chunked(2)
        chunked.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { (emoji, label) ->
                    val isSelected = label in selectedInterests
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) OBPurple else OBBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                if (isSelected) selectedInterests.remove(label)
                                else selectedInterests.add(label)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) OBPurple.copy(alpha = 0.06f) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) OBPurple else OBTextDark,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
                // Fill empty slot if odd
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── Step 3: Goals ──────────────────────────────────────────────────────────────
@Composable
private fun GoalsStep(selectedGoals: MutableList<String>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("What are your goals? 🚀", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OBTextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Select all that apply. We'll personalize your experience accordingly.", fontSize = 13.sp, color = OBTextGray, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val chunked = allGoals.chunked(2)
        chunked.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { (emoji, pair) ->
                    val (title, subtitle) = pair
                    val isSelected = title in selectedGoals
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) OBPurple else OBBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                if (isSelected) selectedGoals.remove(title)
                                else selectedGoals.add(title)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) OBPurple.copy(alpha = 0.06f) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) OBPurple else OBTextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                color = OBTextGray,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// @Preview(name = "Onboarding Screen", showBackground = true, widthDp = 390, heightDp = 844)
// @Composable
// fun OnboardingScreenPreview() {
//     SkillExchangeTheme { OnboardingScreen(viewModel = ...) }
// }

