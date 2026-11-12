package com.simats.skillexchange.ui.screens.freelancer

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
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
import com.simats.skillexchange.data.models.ConversationOut
import com.simats.skillexchange.data.models.MessageOut
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.viewmodels.ChatViewModel

@Composable
fun ChatListScreen(
    onNavigateToChat: (Int) -> Unit,
    onBack: () -> Unit,
    vm: ChatViewModel = viewModel()
) {
    val conversations by vm.conversations.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadConversations()
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
            Text("Messages", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (isLoading) {
            LoadingState()
        } else if (conversations.isEmpty()) {
            EmptyState(message = "No conversations yet.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(conversations) { conversation ->
                    ConversationItem(conversation) { onNavigateToChat(conversation.id) }
                    Divider(color = BorderGray, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun ConversationItem(conv: ConversationOut, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Purple500.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                (conv.title ?: "Chat").take(1).uppercase(), 
                fontWeight = FontWeight.Bold, 
                color = Purple500
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(conv.title ?: "Project Chat", fontWeight = FontWeight.Bold, color = TextDark)
            Text(
                conv.last_message ?: "No messages yet", 
                fontSize = 13.sp, 
                color = TextGray, 
                maxLines = 1
            )
        }
        if (conv.unread_count > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Purple500),
                contentAlignment = Alignment.Center
            ) {
                Text("${conv.unread_count}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun IndividualChatScreen(
    convId: Int,
    onBack: () -> Unit,
    vm: ChatViewModel = viewModel()
) {
    val messages by vm.messages.collectAsState()
    val highlights by vm.highlights.collectAsState()
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showHighlights by remember { mutableStateOf(false) }

    LaunchedEffect(convId) {
        vm.openConversation(convId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    DisposableEffect(Unit) {
        onDispose { vm.stopPolling() }
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        // Chat Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Text("Project Chat", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.weight(1f))
            
            if (highlights.isNotEmpty()) {
                IconButton(onClick = { showHighlights = !showHighlights }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Highlights", tint = if(showHighlights) Teal500 else TextGray)
                }
            }
        }
        Divider(color = BorderGray)

        // AI Highlights Panel
        if (showHighlights && highlights.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Teal500.copy(0.05f),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Chat Highlights", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Teal500)
                    Spacer(modifier = Modifier.height(8.dp))
                    highlights.forEach { highlight ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("•", color = Teal500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(highlight, fontSize = 12.sp, color = TextDark)
                        }
                    }
                }
            }
            Divider(color = Teal500.copy(0.1f))
        }

        // Messages List
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message)
            }
        }

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple500,
                    unfocusedBorderColor = BorderGray
                ),
                maxLines = 4
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        vm.sendMessage(text)
                        text = ""
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = if (text.isNotBlank()) Purple500 else TextGray)
            }
        }
    }
}

@Composable
fun MessageBubble(message: MessageOut) {
    val alignment = if (message.is_mine) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.is_mine) Purple500 else Color.White
    val textColor = if (message.is_mine) Color.White else TextDark
    val shape = if (message.is_mine) 
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp) 
    else 
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = if (message.is_mine) Alignment.End else Alignment.Start) {
            if (!message.is_mine) {
                Text(message.sender_name, fontSize = 10.sp, color = TextGray, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
            }
            Surface(
                color = color,
                shape = shape,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = textColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

