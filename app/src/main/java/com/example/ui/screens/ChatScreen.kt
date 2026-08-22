package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MerchantPublicProfile
import com.example.ui.components.MerchantAvatar
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.viewmodel.BusinessViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    merchant: MerchantPublicProfile,
    viewModel: BusinessViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val messages by viewModel.currentChatMessages.collectAsState()
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickTemplates = listOf(
        Pair("💰 বাকি টাকার তাগাদা", "PAYMENT_REMINDER"),
        Pair("📦 পণ্য স্টক ও রেট যাচাই", "PRODUCT_QUERY"),
        Pair("🧾 চালান / মেমো পাঠানো হলো", "INVOICE"),
        Pair("🤝 টাকা বুঝে পেয়েছি, ধন্যবাদ", "TEXT")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MerchantAvatar(
                            presetId = merchant.avatarPreset,
                            size = 40.dp,
                            showVerifiedBadge = merchant.isVerified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = merchant.shopName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                if (merchant.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = merchant.username,
                                    fontSize = 11.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = " • ⭐ ${merchant.rating} (${merchant.totalSalesCount}+ সেল)",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Call
                    IconButton(onClick = { viewModel.makePhoneCall(context, merchant.phone) }) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = EmeraldPrimary)
                    }
                    // WhatsApp
                    IconButton(onClick = { viewModel.openWhatsApp(context, merchant.whatsapp, "আসসালামু আলাইকুম ${merchant.shopName}") }) {
                        Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                    }
                    // More options / Clear chat
                    IconButton(onClick = { viewModel.clearActiveChat() }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Quick Action Business Templates
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    ) {
                        items(quickTemplates) { (label, type) ->
                            SuggestionChip(
                                onClick = {
                                    val textToSend = when (type) {
                                        "PAYMENT_REMINDER" -> "আসসালামু আলাইকুম, আপনার খাতার বাকি টাকা পরিশোধের জন্য সবিনয় অনুরোধ জানাচ্ছি।"
                                        "PRODUCT_QUERY" -> "আসসালামু আলাইকুম, আপনাদের কাছে কি নতুন স্টক ও আকর্ষণীয় ডিসকাউন্ট অফার আছে?"
                                        "INVOICE" -> "সম্মানিত গ্রাহক, আপনার ক্রয়কৃত পণ্যের চালান ও মেমো তৈরি হয়েছে।"
                                        else -> label
                                    }
                                    viewModel.sendChatMessage(textToSend, type)
                                },
                                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = EmeraldContainer.copy(alpha = 0.6f),
                                    labelColor = EmeraldPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Message Input Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text(AppStrings.get("type_message", currentLang), fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input"),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    viewModel.sendChatMessage(messageInput, "TEXT")
                                    messageInput = ""
                                }
                            },
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("chat_send_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Merchant Info Trust Card
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("অ্যাকাউন্ট ভেরিফাইড ও এনক্রিপ্টেড চ্যাট", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text(
                        text = "গড় রেসপন্স: ${merchant.responseRate}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }

            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        MerchantAvatar(presetId = merchant.avatarPreset, size = 64.dp, showVerifiedBadge = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${merchant.shopName}-এর সাথে সরাসরি চ্যাট",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${merchant.username} • ${merchant.address}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "নিচে থাকা টেমপ্লেট থেকে দ্রুত বাকি তাগাদা বা পণ্যের জিজ্ঞাসা পাঠান",
                            fontSize = 12.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
                ) {
                    items(messages) { msg ->
                        ChatMessageBubble(message = msg)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageEntity) {
    val isOutgoing = message.isOutgoing
    val alignment = if (isOutgoing) Alignment.End else Alignment.Start
    val bgColor = if (isOutgoing) EmeraldPrimary else MaterialTheme.colorScheme.surface
    val textColor = if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOutgoing) 16.dp else 2.dp,
                bottomEnd = if (isOutgoing) 2.dp else 16.dp
            ),
            color = bgColor,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (message.messageType != "TEXT") {
                    val badgeLabel = when (message.messageType) {
                        "PAYMENT_REMINDER" -> "💰 বাকি টাকার তাগাদা"
                        "INVOICE" -> "🧾 মেমো / ইনভয়েস"
                        "PRODUCT_QUERY" -> "📦 পণ্যের স্টক যাচাই"
                        else -> "বাণিজ্যিক মেসেজ"
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isOutgoing) Color.White.copy(alpha = 0.2f) else EmeraldContainer,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = badgeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOutgoing) Color.White else EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = message.messageText,
                    fontSize = 14.sp,
                    color = textColor,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeFormatter.format(Date(message.timestamp)),
                        fontSize = 10.sp,
                        color = if (isOutgoing) Color.White.copy(alpha = 0.7f) else Color.Gray
                    )
                    if (isOutgoing) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
