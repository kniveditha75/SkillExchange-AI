package com.simats.skillexchange.ui.screens.ideaowner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.viewmodels.ProfileViewModel
import com.simats.skillexchange.data.models.*

import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.utils.FileUtils

@Composable
fun IdeaOwnerProfileScreen(
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val profile by vm.profile.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.loadProfile()
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        if (isLoading) {
            LoadingState()
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
                    Button(onClick = { vm.loadProfile() }) {
                        Text("Retry")
                    }
                }
            }
        } else if (profile != null) {
            val user = profile!!
            var showEditDialog by remember { mutableStateOf(false) }
            var showLogoutConfirmation by remember { mutableStateOf(false) }
            var showSettingsDialog by remember { mutableStateOf(false) }
            var showPrivacyDialog by remember { mutableStateOf(false) }
            var showHelpDialog by remember { mutableStateOf(false) }

            if (showEditDialog) {
                EditProfileDialog(
                    initialName = user.full_name,
                    initialGender = user.gender,
                    initialPhone = user.phone,
                    initialAvatar = user.profile_picture,
                    onDismiss = { showEditDialog = false },
                    onSave = { n, g, p, uri: android.net.Uri? ->
                        val update = UserUpdate(full_name = n, gender = g, phone = p)
                        vm.updateProfile(update) {
                            if (uri != null) {
                                val part = FileUtils.uriToMultipart(context, uri, "file")
                                if (part != null) {
                                    vm.uploadAvatar(part) { showEditDialog = false }
                                } else {
                                    showEditDialog = false
                                }
                            } else {
                                showEditDialog = false
                            }
                        }
                    }
                )
            }

            if (showLogoutConfirmation) {
                LogoutConfirmationDialog(
                    onDismiss = { showLogoutConfirmation = false },
                    onConfirm = {
                        showLogoutConfirmation = false
                        onLogout()
                    }
                )
            }

            if (showSettingsDialog) {
                AppSettingsDialog(role = "idea_owner", onDismiss = { showSettingsDialog = false })
            }

            if (showPrivacyDialog) {
                PrivacyPolicyDialog(role = "idea_owner", onDismiss = { showPrivacyDialog = false })
            }

            if (showHelpDialog) {
                HelpSupportDialog(role = "idea_owner", onDismiss = { showHelpDialog = false })
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Normal Header
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(BgLight),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!user.profile_picture.isNullOrBlank()) {
                                    val fullUrl = if (user.profile_picture.startsWith("http")) user.profile_picture else "${com.simats.skillexchange.BuildConfig.BASE_URL.removeSuffix("/")}${user.profile_picture}"
                                    AsyncImage(model = fullUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(90.dp), tint = TextLight)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(user.full_name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(user.email, fontSize = 14.sp, color = TextGray)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showEditDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("Edit Profile")
                            }
                        }
                    }
                }

                // Info Section
                item {
                    Column(modifier = Modifier.padding(16.dp).background(Color.White, RoundedCornerShape(12.dp)).padding(16.dp)) {
                        InfoRow_IO(Icons.Default.Phone, "Phone", user.phone ?: "Not added")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderGray)
                        InfoRow_IO(Icons.Default.Wc, "Gender", user.gender ?: "Not specified")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderGray)
                        InfoRow_IO(Icons.Default.RocketLaunch, "Role", "Project Visionary")
                    }
                }

                // Industry Focus
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Interests, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Industry Focus", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow_IO_Normal(user.interests)
                    }
                }

                // Menu Items
                item {
                    Column(modifier = Modifier.padding(16.dp).background(Color.White, RoundedCornerShape(12.dp))) {
                        ProfileMenuItem_IO_Normal("Account Settings", Icons.Default.Settings) { showSettingsDialog = true }
                        ProfileMenuItem_IO_Normal("Privacy Policy", Icons.Default.Security) { showPrivacyDialog = true }
                        ProfileMenuItem_IO_Normal("Help & Support", Icons.Default.Help) { showHelpDialog = true }
                        ProfileMenuItem_IO_Normal("Logout", Icons.Default.Logout, isLogout = true) { showLogoutConfirmation = true }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun InfoRow_IO(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TextGray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextGray)
            Text(value, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun FlowRow_IO_Normal(items: List<String>) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = TextDark
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem_IO_Normal(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isLogout: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (isLogout) Color.Red else TextGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 15.sp, color = if (isLogout) Color.Red else TextDark)
        if (!isLogout) Icon(Icons.Default.ChevronRight, null, tint = BorderGray)
    }
}

