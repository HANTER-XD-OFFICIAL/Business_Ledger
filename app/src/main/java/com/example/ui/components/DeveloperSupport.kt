package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

object DeveloperConfig {
    const val DEVELOPER_NAME = "MD RASEL"
    const val EMAIL = "alexraselchodhury@gmail.com"
    const val FACEBOOK_URL = "https://www.facebook.com/md.rasel.7.8.2.3.4"
    const val WHATSAPP_NUMBER = "+8801882278234"
    const val WHATSAPP_URL = "https://wa.me/8801882278234"
    const val TELEGRAM_URL = "https://t.me/HANTER_XD_OFFICIAL"
    const val TELEGRAM_USERNAME = "t.me/HANTER_XD_OFFICIAL"

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "লিংক ওপেন করা সম্ভব হয়নি: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openEmail(context: Context, email: String, subject: String = "Business Ledger App Support & Feedback") {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openUrl(context, "mailto:$email")
        }
    }

    fun openWhatsApp(context: Context, phoneWithCountryCode: String, message: String = "হ্যালো MD RASEL, Business Ledger অ্যাপ সম্পর্কে কিছু জানতে চাচ্ছি।") {
        try {
            val cleanPhone = phoneWithCountryCode.replace("+", "").replace(" ", "").replace("-", "")
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openUrl(context, WHATSAPP_URL)
        }
    }
}

/**
 * Startup welcome notification & Telegram join dialog.
 * Prompts user on app launch to connect with developer MD RASEL on Telegram/WhatsApp for updates and support.
 */
@Composable
fun TelegramWelcomeDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("telegram_welcome_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF229ED9).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Telegram",
                        tint = Color(0xFF229ED9),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "টেলিগ্রাম সাপোর্ট ও আপডেট",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "ডেভেলপার: ${DeveloperConfig.DEVELOPER_NAME}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnEmeraldContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "অ্যাপের যেকোনো আপডেট জানতে, নতুন ফিচার রিকোয়েস্ট করতে কিংবা যেকোনো সহায়তার জন্য সরাসরি আমাদের টেলিগ্রাম চ্যানেলে জয়েন করুন অথবা মেসেজ দিন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Telegram Primary Action
                Button(
                    onClick = {
                        DeveloperConfig.openUrl(context, DeveloperConfig.TELEGRAM_URL)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dialog_join_telegram_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9))
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "টেলিগ্রামে জয়েন / মেসেজ দিন",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // WhatsApp Option
                OutlinedButton(
                    onClick = {
                        DeveloperConfig.openWhatsApp(context, DeveloperConfig.WHATSAPP_NUMBER)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dialog_open_whatsapp_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                    border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "হোয়াটসঅ্যাপে যোগাযোগ",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dialog_continue_app_btn")
                ) {
                    Text(
                        text = "অ্যাপে প্রবেশ করুন",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Developer & Support Card displaying all contact channels of MD RASEL.
 */
@Composable
fun DeveloperSupportCard(
    modifier: Modifier = Modifier,
    onShowTelegramDialog: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("developer_support_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = OnEmeraldContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "ডেভেলপার ও সাপোর্ট",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MD RASEL (Developer)",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onShowTelegramDialog,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নোটিশ", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "কোনো সমস্যা হলে, নতুন কোনো ফিচার অ্যাড করতে চাইলে বা যেকোনো ব্যবসায়িক আপডেটের জন্য সরাসরি ডেভেলপারের সাথে যোগাযোগ করুন:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Contact Options Grid / List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Telegram
                ContactItemRow(
                    title = "Telegram",
                    detail = DeveloperConfig.TELEGRAM_USERNAME,
                    icon = Icons.Default.Send,
                    brandColor = Color(0xFF229ED9),
                    onClick = {
                        DeveloperConfig.openUrl(context, DeveloperConfig.TELEGRAM_URL)
                    }
                )

                // WhatsApp
                ContactItemRow(
                    title = "WhatsApp",
                    detail = DeveloperConfig.WHATSAPP_NUMBER,
                    icon = Icons.Default.Chat,
                    brandColor = Color(0xFF25D366),
                    onClick = {
                        DeveloperConfig.openWhatsApp(context, DeveloperConfig.WHATSAPP_NUMBER)
                    }
                )

                // Email
                ContactItemRow(
                    title = "Gmail / ইমেইল",
                    detail = DeveloperConfig.EMAIL,
                    icon = Icons.Default.Email,
                    brandColor = Color(0xFFEA4335),
                    onClick = {
                        DeveloperConfig.openEmail(context, DeveloperConfig.EMAIL)
                    }
                )

                // Facebook
                ContactItemRow(
                    title = "Facebook প্রোফাইল",
                    detail = "fb.com/md.rasel.7.8.2.3.4",
                    icon = Icons.Default.Person,
                    brandColor = Color(0xFF1877F2),
                    onClick = {
                        DeveloperConfig.openUrl(context, DeveloperConfig.FACEBOOK_URL)
                    }
                )
            }
        }
    }
}

@Composable
fun ContactItemRow(
    title: String,
    detail: String,
    icon: ImageVector,
    brandColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = brandColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, brandColor.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(brandColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Open",
                tint = brandColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
