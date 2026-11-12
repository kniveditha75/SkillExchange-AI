package com.simats.skillexchange.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.viewmodels.AuthViewModel

@Composable
fun SignupScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRoleSelect: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Validation
    val isPhoneValid = remember(phone) { phone.length == 10 && phone.firstOrNull() in listOf('6','7','8','9') }
    val isPasswordStrong = remember(password) { 
        password.length >= 8 && 
        password.any { it.isUpperCase() } && 
        password.any { !it.isLetter() } 
    }
    val passwordsMatch = remember(password, confirmPassword) { password == confirmPassword }

    LaunchedEffect(Unit) { viewModel.clearError() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8EAF6), // Light Indigo
                        Color(0xFFF3F4FF), // BgLight
                        Color(0xFFEDE7F6)  // Light Purple
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Join our community of experts and learners.",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Centered Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PremiumTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name",
                        placeholder = "Alex Chen",
                        icon = Icons.Outlined.Person
                    )

                    PremiumTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "you@example.com",
                        icon = Icons.Outlined.Email,
                        keyboardType = KeyboardType.Email
                    )

                    PremiumTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it.filter { c -> c.isDigit() } },
                        label = "Phone Number",
                        placeholder = "10-digit mobile number",
                        icon = Icons.Outlined.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    Column {
                        PremiumTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            placeholder = "Create a strong password",
                            icon = Icons.Outlined.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordToggle = { passwordVisible = !passwordVisible }
                        )
                        
                        if (password.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                PasswordReq("Min. 8 characters", password.length >= 8)
                                PasswordReq("At least one uppercase", password.any { it.isUpperCase() })
                                PasswordReq("Number or special char", password.any { !it.isLetter() })
                            }
                        }
                    }

                    PremiumTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        placeholder = "Confirm your password",
                        icon = Icons.Outlined.LockReset,
                        isPassword = true,
                        passwordVisible = confirmVisible,
                        onPasswordToggle = { confirmVisible = !confirmVisible }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { agreedToTerms = !agreedToTerms }.padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                        )
                        Text(
                            "I agree to the Terms & Privacy Policy",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }

                    if (error != null) {
                        Text(
                            text = error!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    GradientButton(
                        text = if (isLoading) "Creating..." else "Sign Up",
                        onClick = {
                            viewModel.signup(fullName, email, password, confirmPassword, "Not Specified", phone) {
                                onNavigateToRoleSelect()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = agreedToTerms && isPhoneValid && isPasswordStrong && passwordsMatch
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", color = TextGray, fontSize = 14.sp)
                Text(
                    text = "Sign In",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PasswordReq(label: String, isMet: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) Color(0xFF10B981) else TextGray.copy(alpha = 0.5f),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, color = if (isMet) Color(0xFF10B981) else TextGray)
    }
}
