package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun ProfileEditDialog(
    currentUser: UserAccountEntity?,
    currentLang: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (
        displayName: String,
        username: String,
        businessName: String,
        businessCategory: String,
        bio: String,
        address: String,
        phone: String,
        whatsapp: String,
        telegram: String,
        avatarPreset: String
    ) -> Unit
) {
    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var username by remember { 
        mutableStateOf(
            currentUser?.username?.removePrefix("@") ?: "my_shop"
        ) 
    }
    var businessName by remember { mutableStateOf(currentUser?.businessName ?: "") }
    var businessCategory by remember { mutableStateOf(currentUser?.businessCategory ?: "মুদি ও পাইকারি") }
    var bio by remember { mutableStateOf(currentUser?.bio ?: "") }
    var address by remember { mutableStateOf(currentUser?.address ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var whatsapp by remember { mutableStateOf(currentUser?.whatsapp ?: currentUser?.phone ?: "") }
    var telegram by remember { mutableStateOf(currentUser?.telegram ?: "t.me/HANTER_XD_OFFICIAL") }
    var selectedAvatar by remember { mutableStateOf(currentUser?.avatarPreset ?: "avatar_1") }

    val categories = listOf(
        "মুদি ও পাইকারি",
        "ইলেকট্রনিক্স ও গ্যাজেট",
        "কৃষি ও পোল্ট্রি ফিড",
        "কাপড় ও তৈরি পোশাক",
        "ফার্মেসি ও হেলথকেয়ার",
        "হোটেল ও রেস্টুরেন্ট",
        "অন্যান্য ব্যবসা"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MerchantAvatar(
                            presetId = selectedAvatar,
                            size = 48.dp,
                            showVerifiedBadge = true
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = AppStrings.get("profile_customization", currentLang),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "ব্যবসায়িক পরিচিতি ও ডিজিটাল প্রোফাইল সাজান",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar Selector
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = AppStrings.get("choose_avatar", currentLang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            AvatarSelector(
                                selectedPresetId = selectedAvatar,
                                onSelectPreset = { selectedAvatar = it }
                            )
                        }
                    }

                    // Full Name
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("আপনার পূর্ণ নাম (Full Name)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_display_name_input"),
                        singleLine = true
                    )

                    // Username (@handle)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { 
                            // Only allow alphanumeric and underscore
                            username = it.filter { char -> char.isLetterOrDigit() || char == '_' }.lowercase()
                        },
                        label = { Text(AppStrings.get("username_handle", currentLang)) },
                        prefix = { Text("@", fontWeight = FontWeight.Bold, color = EmeraldPrimary) },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = EmeraldPrimary) },
                        supportingText = { Text("অন্যান্য ব্যবসায়ী এই ইউজারনেম দিয়ে আপনাকে খুঁজবে ও চ্যাট করবে") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_username_input"),
                        singleLine = true
                    )

                    // Business / Shop Name
                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("দোকান / ব্যবসার নাম (Shop Name)") },
                        leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_shop_name_input"),
                        singleLine = true
                    )

                    // Business Category Selection Chips
                    Column {
                        Text(
                            text = AppStrings.get("business_category", currentLang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.take(3).forEach { cat ->
                                val isSelected = businessCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { businessCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldContainer,
                                        selectedLabelColor = EmeraldPrimary
                                    )
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.drop(3).take(3).forEach { cat ->
                                val isSelected = businessCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { businessCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldContainer,
                                        selectedLabelColor = EmeraldPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Business Bio / Description
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text(AppStrings.get("business_bio", currentLang)) },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = EmeraldPrimary) },
                        placeholder = { Text("যেমন: পাইকারি চাল, ডাল ও তেলের নির্ভরযোগ্য আড়ৎ...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("profile_bio_input"),
                        maxLines = 4
                    )

                    // Address / Location
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("দোকানের পূর্ণ ঠিকানা ও মার্কেট (Address)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_address_input"),
                        singleLine = true
                    )

                    // Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("ফোন নম্বর (Phone)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_phone_input"),
                        singleLine = true
                    )

                    // WhatsApp Number
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("হোয়াটসঅ্যাপ নম্বর (WhatsApp)") },
                        leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366)) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_whatsapp_input"),
                        singleLine = true
                    )

                    // Telegram Handle
                    OutlinedTextField(
                        value = telegram,
                        onValueChange = { telegram = it },
                        label = { Text("টেলিগ্রাম লিংক / আইডি (Telegram)") },
                        leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF0088CC)) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_telegram_input"),
                        singleLine = true
                    )

                    // Rating & Sales Preview Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("৪.৯ ★", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                }
                                Text("৮৬+ রেটিং", fontSize = 11.sp, color = Color.Gray)
                            }
                            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.LightGray)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("৩৪০+ সেল", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                Text("সম্পন্ন ডেলিভারি", fontSize = 11.sp, color = Color.Gray)
                            }
                            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.LightGray)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("১০০%", fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                                Text("ভেরিফাইড ব্যাজ", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(AppStrings.get("cancel", currentLang))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val cleanUsername = if (username.isNotBlank()) "@$username" else "@user_${System.currentTimeMillis() % 10000}"
                            onSave(
                                displayName,
                                cleanUsername,
                                businessName,
                                businessCategory,
                                bio,
                                address,
                                phone,
                                whatsapp,
                                telegram,
                                selectedAvatar
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("save_profile_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppStrings.get("save", currentLang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
