package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary

data class AvatarOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val bgColors: List<Color>
)

val AVATAR_PRESETS = listOf(
    AvatarOption(
        id = "avatar_1",
        label = "ব্যবসায়ী",
        icon = Icons.Default.Storefront,
        bgColors = listOf(Color(0xFF006C4C), Color(0xFF004D36))
    ),
    AvatarOption(
        id = "avatar_2",
        label = "মুদি পাইকারি",
        icon = Icons.Default.ShoppingBag,
        bgColors = listOf(Color(0xFF006874), Color(0xFF004F58))
    ),
    AvatarOption(
        id = "avatar_3",
        label = "ইলেকট্রনিক্স",
        icon = Icons.Default.Devices,
        bgColors = listOf(Color(0xFF005AC1), Color(0xFF003E8A))
    ),
    AvatarOption(
        id = "avatar_4",
        label = "কৃষি ও ফিড",
        icon = Icons.Default.Grass,
        bgColors = listOf(Color(0xFF436600), Color(0xFF2E4600))
    ),
    AvatarOption(
        id = "avatar_5",
        label = "বস্ত্র ও তৈরি পোশাক",
        icon = Icons.Default.Checkroom,
        bgColors = listOf(Color(0xFF7E3B9C), Color(0xFF5A2274))
    ),
    AvatarOption(
        id = "avatar_6",
        label = "ফার্মেসি",
        icon = Icons.Default.LocalPharmacy,
        bgColors = listOf(Color(0xFFBA1A1A), Color(0xFF8C0009))
    )
)

@Composable
fun MerchantAvatar(
    presetId: String,
    size: Dp = 48.dp,
    showVerifiedBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    val option = AVATAR_PRESETS.find { it.id == presetId } ?: AVATAR_PRESETS[0]

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.linearGradient(option.bgColors)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.label,
                tint = Color.White,
                modifier = Modifier.size(size * 0.55f)
            )
        }

        if (showVerifiedBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.36f)
                    .clip(CircleShape)
                    .background(Color(0xFF0288D1))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.22f)
                )
            }
        }
    }
}

@Composable
fun AvatarSelector(
    selectedPresetId: String,
    onSelectPreset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AVATAR_PRESETS.forEach { option ->
            val isSelected = option.id == selectedPresetId
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelectPreset(option.id) }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(option.bgColors))
                        .then(
                            if (isSelected) Modifier.border(3.dp, EmeraldPrimary, CircleShape)
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.label,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = option.label,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) EmeraldPrimary else Color.Gray
                )
            }
        }
    }
}
