package com.simats.skillexchange.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.skillexchange.ui.theme.*
import android.net.Uri
import coil.compose.AsyncImage

// ── Top App Bar ───────────────────────────────────────────────────────────────
@Composable
fun SETopBar(
    title: String,
    role: String,
    unreadCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryBlue, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
                Text(
                    text = role.replace("_", " ").uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }

            IconButton(onClick = onNotificationClick) {
                Box {
                    Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = TextDark)
                    if (unreadCount > 0) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
                    }
                }
            }

            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TextDark)
            }
        }
    }
}

// ── Bottom Nav Bar ────────────────────────────────────────────────────────────
sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home      : NavItem("dashboard",    "Home",     Icons.Default.Home)
    object Projects  : NavItem("projects",     "Projects", Icons.Default.Work)
    object MyWork    : NavItem("my_work",      "My Work",  Icons.Default.Task)
    object Chat      : NavItem("chat_list",    "Chat",     Icons.Default.Chat)
    object MyProj    : NavItem("my_projects",  "My Projects", Icons.Default.Folder)
    object FindTeam  : NavItem("all_teams",    "Team",     Icons.Default.Group)
    object FindInvest: NavItem("find_investor","Funding",  Icons.Default.MonetizationOn)
    object Collab    : NavItem("collab",       "Collab",   Icons.Default.Dashboard)
    object Portfolio : NavItem("portfolio",    "Portfolio",Icons.Default.PieChart)
    object Invest    : NavItem("invest",       "Invest",   Icons.Default.TrendingUp)
    object Profile   : NavItem("profile",      "Profile",  Icons.Default.Person)
}

@Composable
fun SEBottomNavBar(role: String, currentRoute: String, onNavigate: (String) -> Unit) {
    val items = when (role) {
        "freelancer" -> listOf(NavItem.Home, NavItem.Projects, NavItem.MyWork, NavItem.Chat, NavItem.Profile)
        "idea_owner" -> listOf(NavItem.Home, NavItem.MyProj, NavItem.FindTeam, NavItem.Collab, NavItem.Profile)
        "investor"   -> listOf(NavItem.Home, NavItem.Invest, NavItem.Portfolio, NavItem.Chat, NavItem.Profile)
        else         -> listOf(NavItem.Home, NavItem.Profile)
    }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                label = { Text(item.label, fontSize = 10.sp) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}

// ── Project Card ──────────────────────────────────────────────────────────────
@Composable
fun ProjectCard(
    title: String,
    category: String?,
    requiredSkills: String?,
    matchScore: Double? = null,
    status: String,
    teamSize: Int,
    amountRaised: Double,
    fundingGoal: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    if (category != null) {
                        Text(category.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 2)
                }
                if (matchScore != null) {
                    Surface(color = AccentTeal.copy(0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text("${matchScore.toInt()}% Match", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (!requiredSkills.isNullOrEmpty()) {
                Text(requiredSkills, fontSize = 12.sp, color = TextGray, maxLines = 1)
            }
            if (fundingGoal > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                val progress = (amountRaised / fundingGoal).coerceIn(0.0, 1.0).toFloat()
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(4.dp), color = PrimaryBlue, trackColor = BorderGray)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("₹${amountRaised.toInt()} raised", fontSize = 11.sp, color = TextGray)
                    Text("${(progress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatusChip(status)
                Text("Team: $teamSize", fontSize = 12.sp, color = TextGray)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status.lowercase()) {
        "open"        -> AccentTeal
        "in_progress" -> PrimaryBlue
        "completed"   -> Color(0xFF10B981)
        else          -> TextGray
    }
    Surface(color = color.copy(0.1f), shape = RoundedCornerShape(4.dp)) {
        Text(status.replace("_", " ").uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun SectionHeader(title: String, actionText: String? = null, onAction: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        if (actionText != null) {
            TextButton(onClick = onAction) { Text(actionText, fontSize = 14.sp, color = PrimaryBlue) }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryBlue)
    }
}

@Composable
fun EmptyState(message: String, icon: ImageVector = Icons.Default.Inbox) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, modifier = Modifier.size(48.dp), tint = TextLight)
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, color = TextGray, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    // Renamed to GradientButton but now uses solid normal theme as per user request to revert
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        enabled = enabled
    ) {
        Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(label, fontSize = 12.sp, color = TextGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    initialName: String,
    initialGender: String?,
    initialPhone: String?,
    initialAvatar: String?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var gender by remember { mutableStateOf(initialGender ?: "") }
    var phone by remember { mutableStateOf(initialPhone ?: "") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(BgLight).clickable { launcher.launch("image/*") }.align(Alignment.CenterHorizontally)) {
                    if (selectedUri != null) {
                        AsyncImage(model = selectedUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                    } else if (!initialAvatar.isNullOrBlank()) {
                        val fullUrl = if (initialAvatar.startsWith("http")) initialAvatar else "${com.simats.skillexchange.BuildConfig.BASE_URL.removeSuffix("/")}$initialAvatar"
                        AsyncImage(model = fullUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.align(Alignment.Center), tint = TextLight)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                PremiumTextField(value = name, onValueChange = { name = it }, label = "Name", icon = Icons.Default.Person)
                Spacer(modifier = Modifier.height(12.dp))
                PremiumTextField(value = gender, onValueChange = { gender = it }, label = "Gender", icon = Icons.Default.Wc)
                Spacer(modifier = Modifier.height(12.dp))
                PremiumTextField(value = phone, onValueChange = { phone = it }, label = "Phone", icon = Icons.Default.Phone)
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { onSave(name, gender, phone, selectedUri) }) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String = "",
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        leadingIcon = { Icon(icon, null, modifier = Modifier.size(20.dp)) },
        trailingIcon = if (isPassword) {
            { IconButton(onClick = onPasswordToggle) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Premium Warning/Logout Icon with background circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFFEE2E2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Confirm Logout",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Are you sure you want to sign out? You will need to re-enter your credentials to access your account again.",
                    fontSize = 14.sp,
                    color = TextGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel / Keep Sign In button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderGray),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Logout Action button
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text(
                            text = "Yes, Logout",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsDialog(
    role: String,
    onDismiss: () -> Unit
) {
    var pushNotifications by remember { mutableStateOf(true) }
    var emailAlerts by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }
    
    // Role-specific states
    var toggleA by remember { mutableStateOf(true) }
    var toggleB by remember { mutableStateOf(true) }

    val roleName = role.replace("_", " ").uppercase()

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "App Settings ($roleName)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    Text("SYSTEM PREFERENCES", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingSwitchRow("Push Notifications", "Receive push alerts for chats & updates", pushNotifications) { pushNotifications = it }
                    HorizontalDivider(color = BorderGray, modifier = Modifier.padding(vertical = 8.dp))
                    SettingSwitchRow("Email Alerts", "Receive summary updates via email", emailAlerts) { emailAlerts = it }
                    HorizontalDivider(color = BorderGray, modifier = Modifier.padding(vertical = 8.dp))
                    SettingSwitchRow("Dark Theme", "Toggle dark visual styling (Beta)", darkMode) { darkMode = it }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("ROLE-SPECIFIC PREFERENCES", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(8.dp))

                    when (role.lowercase()) {
                        "freelancer" -> {
                            SettingSwitchRow("Looking for Work", "Enable profile visibility in match listings", toggleA) { toggleA = it }
                            HorizontalDivider(color = BorderGray, modifier = Modifier.padding(vertical = 8.dp))
                            SettingSwitchRow("Auto-apply Matcher", "Instantly highlight 80%+ match projects", toggleB) { toggleB = it }
                        }
                        "idea_owner" -> {
                            SettingSwitchRow("Investor Directory Listing", "Allow investors to search for your pitch deck", toggleA) { toggleA = it }
                            HorizontalDivider(color = BorderGray, modifier = Modifier.padding(vertical = 8.dp))
                            SettingSwitchRow("Applications Open", "Allow freelancers to apply for roles", toggleB) { toggleB = it }
                        }
                        "investor" -> {
                            SettingSwitchRow("Anonymous Pitch Scouting", "Scout ideas without disclosing your full name", toggleA) { toggleA = it }
                            HorizontalDivider(color = BorderGray, modifier = Modifier.padding(vertical = 8.dp))
                            SettingSwitchRow("Deal Flow Push Alerts", "Notify immediately for new 90%+ match ideas", toggleB) { toggleB = it }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save & Close", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
            Text(text = description, fontSize = 11.sp, color = TextGray)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryBlue,
                uncheckedThumbColor = TextGray,
                uncheckedTrackColor = BorderGray
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyDialog(
    role: String,
    onDismiss: () -> Unit
) {
    val sections = when (role.lowercase()) {
        "freelancer" -> listOf(
            "1. Freelancer Escrow Security" to "We protect your payment transactions using automated secure escrow contracts. All gig funds are audited and verified before work starts.",
            "2. Talent IP Protections" to "Any code, designs, or assets you produce on Skill Exchange remain your intellectual property until milestones are fully completed and paid.",
            "3. Portfolio Disclosure" to "You have complete control over whether completed projects are listed on your public resume and portfolio search results."
        )
        "idea_owner" -> listOf(
            "1. Intellectual Property Protection" to "Your project details, pitch decks, and conceptual wireframes are protected. NDAs can be digitally requested and signed before sharing specs with talent.",
            "2. Co-founder Matching Privacy" to "Only approved team candidates can view your product roadmaps, milestones, and development repos.",
            "3. Listing Visibility Control" to "Toggle project listings to private to hide your ideation details from public web indexing."
        )
        "investor" -> listOf(
            "1. High-Value Financial Privacy" to "Your bank accounts, capital commit histories, and investment strategies are encrypted end-to-end and visible only to verified financial clearings.",
            "2. Due Diligence Confidentials" to "Pitch data and project reports requested during due diligence are sandboxed and fully NDA-bound.",
            "3. SEC & FINRA Compliance" to "We align transaction logs and financial matches with global legal frameworks. Your accredited investor profiles remain protected."
        )
        else -> listOf(
            "1. Data Privacy" to "We protect your account data and credentials using industry-standard encryptions."
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Privacy Policy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable legal text
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 300.dp)
                ) {
                    item {
                        Text(
                            text = "Last updated: May 2026. Skill Exchange AI ensures a safe space for collaborative building. Here is how your data is protected:",
                            fontSize = 13.sp,
                            color = TextGray,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    items(sections.size) { index ->
                        val section = sections[index]
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(text = section.first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = section.second, fontSize = 12.sp, color = TextGray, lineHeight = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("I Understand", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportDialog(
    role: String,
    onDismiss: () -> Unit
) {
    val faqs = when (role.lowercase()) {
        "freelancer" -> listOf(
            "How do I withdraw payments?" to "Once the project owner approves a milestone, the escrow funds are credited to your connected bank profile within 2-3 business days.",
            "How does match score work?" to "Our AI scans your skills, interests, and profile summary and compares them against the required tech stack of open projects.",
            "Can I apply to multiple roles?" to "Yes! You can apply to as many projects as you want. You can view progress under the 'My Work' tab."
        )
        "idea_owner" -> listOf(
            "How do I recruit teammates?" to "Go to 'Find Team' in your project dashboard. Review candidate profiles, match scores, and invite them to collaborate.",
            "How do I attract investors?" to "Ensure your funding goal, raised amount, and project description are fully updated. Complete milestones to show active traction.",
            "Is project ideation protected?" to "Yes, Skill Exchange encrypts startup descriptions and matches candidates using secure, anonymized parameters."
        )
        "investor" -> listOf(
            "How do I fund a project?" to "Browse open projects in the 'Invest' tab. Once you find a match, click 'Invest' or chat directly with the project owners to arrange deal terms.",
            "Can I monitor my portfolio?" to "Absolutely! Click 'Portfolio' to see a visual chart, project performance metrics, and funding updates.",
            "How is risk managed?" to "We vet and accredit projects, perform milestone validations, and check code repos to verify development velocity."
        )
        else -> listOf(
            "How do I change my profile?" to "Click the 'Edit Profile' button on the header of the Profile screen to update your description and avatar."
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Help & Support",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable FAQ list
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 240.dp)
                ) {
                    items(faqs.size) { index ->
                        val faq = faqs[index]
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(text = "Q: ${faq.first}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = faq.second, fontSize = 12.sp, color = TextGray, lineHeight = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contact Section
                Surface(
                    color = BgLight,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Need human help?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("support@skillexchange.ai", fontSize = 11.sp, color = TextGray)
                        }
                        Text(
                            text = "Email Us",
                            color = PrimaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { /* action */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}



