package com.simats.skillexchange.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.simats.skillexchange.data.models.NotificationOut
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.theme.*
import com.simats.skillexchange.ui.viewmodels.ProfileViewModel

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val notifications by vm.notifications.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadNotifications()
    }

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
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                }
                Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { vm.markAllRead() }) {
                    Text("Mark all read", fontSize = 13.sp, color = PrimaryBlue)
                }
            }
        }

        if (isLoading) {
            LoadingState()
        } else if (notifications.isEmpty()) {
            EmptyState("No notifications yet.", Icons.Default.NotificationsNone)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notifications) { notification ->
                    NotificationItem_Normal(notification) { vm.markRead(notification.id) }
                }
            }
        }
    }
}

@Composable
fun NotificationItem_Normal(notif: NotificationOut, onClick: () -> Unit) {
    val bgColor = if (notif.is_read) Color.White else PrimaryBlue.copy(alpha = 0.05f)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BgLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when(notif.notif_type) {
                        "project_apply" -> Icons.Default.GroupAdd
                        "investment" -> Icons.Default.MonetizationOn
                        "message" -> Icons.Default.Chat
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notif.title, fontWeight = if (notif.is_read) FontWeight.Normal else FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                if (!notif.body.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(notif.body, fontSize = 13.sp, color = TextGray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(notif.created_at.split("T").first(), fontSize = 11.sp, color = TextGray)
            }
            if (!notif.is_read) {
                Box(modifier = Modifier.padding(top = 4.dp).size(8.dp).background(PrimaryBlue, CircleShape))
            }
        }
        HorizontalDivider(color = BorderGray, modifier = Modifier.padding(start = 68.dp))
    }
}

