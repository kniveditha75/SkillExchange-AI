package com.simats.skillexchange.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.viewmodels.AuthViewModel

data class Role(
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val accentColor: Color
)

@Composable
fun RoleSelectScreen(
    viewModel: AuthViewModel,
    onNavigateToOnboarding: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val roles = listOf(
        Role("freelancer", Icons.Default.Work, "Freelancer", "I want to work on projects and build my skills.", PurplePrimary),
        Role("idea_owner", Icons.Default.Lightbulb, "Idea Owner", "I have a project idea and need a team.", OrangeAccent),
        Role("investor", Icons.Default.TrendingUp, "Investor", "I want to fund promising AI projects.", TealAccent)
    )

    var selectedRole by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Your Role",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LightOnSurface,
            letterSpacing = (-1).sp
        )
        Text(
            text = "Tell us how you'll use Skill Exchange AI.",
            fontSize = 16.sp,
            color = LightTextGray
        )

        Spacer(modifier = Modifier.height(40.dp))

        roles.forEach { role ->
            val isSelected = selectedRole == role.id
            PremiumRoleCard(
                role = role,
                isSelected = isSelected,
                onClick = { selectedRole = role.id }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (error != null) {
            Text(text = error!!, color = ErrorRed, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
        }

        GradientButton(
            text = if (isLoading) "Processing..." else "Confirm Role",
            onClick = {
                selectedRole?.let { role ->
                    viewModel.saveRole(role) { onNavigateToOnboarding() }
                }
            },
            modifier = Modifier.fillMaxWidth().alpha(if (selectedRole == null) 0.5f else 1.0f),
            enabled = selectedRole != null && !isLoading
        )
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun PremiumRoleCard(role: Role, isSelected: Boolean, onClick: () -> Unit) {
    // Premium dynamic gradient brushes for selected states
    val selectGradient = when (role.id) {
        "freelancer" -> Brush.horizontalGradient(listOf(PrimaryBlue, Color(0xFF8B5CF6)))
        "idea_owner" -> Brush.horizontalGradient(listOf(Orange500, Color(0xFFEF4444)))
        "investor" -> Brush.horizontalGradient(listOf(AccentTeal, Color(0xFF10B981)))
        else -> Brush.horizontalGradient(listOf(PrimaryBlue, AccentTeal))
    }

    val iconGradient = when (role.id) {
        "freelancer" -> Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6)))
        "idea_owner" -> Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))
        "investor" -> Brush.linearGradient(listOf(Color(0xFF0D9488), Color(0xFF10B981)))
        else -> Brush.linearGradient(listOf(PrimaryBlue, AccentTeal))
    }

    val cardBackground = if (isSelected) {
        Brush.verticalGradient(
            colors = listOf(
                role.accentColor.copy(alpha = 0.16f),
                role.accentColor.copy(alpha = 0.03f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White,
                Color.White
            )
        )
    }

    val scale = if (isSelected) 1.03f else 1.0f
    val elevation = if (isSelected) 10.dp else 2.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                shadowElevation = if (isSelected) 12f else 3f,
                shape = RoundedCornerShape(24.dp),
                clip = true
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackground)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    brush = if (isSelected) selectGradient else Brush.linearGradient(listOf(BorderGray, BorderGray)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(vertical = 22.dp, horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Glowy / Fully-colored Gradient Icon Bubble when selected
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = if (isSelected) iconGradient else Brush.linearGradient(listOf(role.accentColor.copy(alpha = 0.1f), role.accentColor.copy(alpha = 0.1f))),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = role.icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else role.accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                // Text details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = role.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = TextDark,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = role.description,
                        fontSize = 13.sp,
                        color = if (isSelected) TextDark.copy(alpha = 0.8f) else TextGray,
                        lineHeight = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Dynamic Glowing Selection Badge
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(brush = iconGradient, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(1.5.dp, BorderGray, CircleShape)
                    )
                }
            }
        }
    }
}

