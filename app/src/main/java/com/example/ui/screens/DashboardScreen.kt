package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.BusinessViewModel
import com.example.viewmodel.DashboardSummary

@Composable
fun DashboardScreen(
    viewModel: BusinessViewModel,
    summary: DashboardSummary,
    profile: BusinessProfileEntity?,
    onOpenSale: () -> Unit,
    onOpenPurchase: () -> Unit,
    onOpenExpense: () -> Unit,
    onOpenCollectDue: () -> Unit,
    onNavigateToKhata: () -> Unit,
    onNavigateToStock: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onSelectTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier,
    onOpenSupport: () -> Unit = {}
) {
    val transactions by viewModel.transactions.collectAsState()
    val currency = profile?.currencySymbol ?: "৳"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Branding Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EmeraldDark,
                                EmeraldPrimary
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.businessName ?: "মেসার্স বিসমিল্লাহ ট্রেডার্স",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "স্বাগতম, ${profile?.ownerName ?: "ব্যবসায়ী ভাই"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                onClick = onOpenSupport,
                                shape = CircleShape,
                                color = Color(0xFF229ED9),
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("dashboard_telegram_support_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Telegram Support",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = "Shop",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Net Cash in Hand & Today's Pulse
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "আজকের নিট ক্যাশ ব্যালেন্স",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                val netCash = summary.todayCashIn - summary.todayCashOut
                                Text(
                                    text = formatCurrency(netCash, currency),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (netCash >= 0) Color(0xFF6EE7B7) else Color(0xFFFCA5A5)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "আজকের মোট বিক্রয়",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = formatCurrency(summary.todaySalesAmount, currency),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "লাভ: ${formatCurrency(summary.todayEstimatedProfit, currency)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF6EE7B7),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text(
                    text = "দ্রুত এন্ট্রি ও হিসাব",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        title = "নতুন বিক্রয়",
                        subtitle = "Sale",
                        icon = Icons.Default.TrendingUp,
                        backgroundColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_sale_btn",
                        onClick = onOpenSale
                    )

                    QuickActionButton(
                        title = "মাল ক্রয়",
                        subtitle = "Purchase",
                        icon = Icons.Default.ShoppingCart,
                        backgroundColor = TealSecondary,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_purchase_btn",
                        onClick = onOpenPurchase
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        title = "খরচ এন্ট্রি",
                        subtitle = "Expense",
                        icon = Icons.Default.TrendingDown,
                        backgroundColor = CrimsonExpense,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_expense_btn",
                        onClick = onOpenExpense
                    )

                    QuickActionButton(
                        title = "বাকি আদায়",
                        subtitle = "Due Collect",
                        icon = Icons.Default.AccountBalanceWallet,
                        backgroundColor = AmberDue,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_due_collect_btn",
                        onClick = onOpenCollectDue
                    )
                }
            }
        }

        // Today's Pulse Metrics Cards Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "আজকের হিসাব চিত্র",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "আজকের ক্যাশ ইন",
                        amount = summary.todayCashIn,
                        currencySymbol = currency,
                        icon = Icons.Default.ArrowDownward,
                        iconColor = EmeraldLight,
                        containerColor = EmeraldContainer.copy(alpha = 0.4f),
                        modifier = Modifier.weight(1f),
                        subtitle = "নগদ + আদায়"
                    )

                    MetricCard(
                        title = "আজকের ক্যাশ আউট",
                        amount = summary.todayCashOut,
                        currencySymbol = currency,
                        icon = Icons.Default.ArrowUpward,
                        iconColor = CrimsonExpense,
                        containerColor = CrimsonContainer.copy(alpha = 0.4f),
                        modifier = Modifier.weight(1f),
                        subtitle = "ক্রয় + খরচ"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "কাস্টমার বাকি (পাবেন)",
                        amount = summary.totalCustomerDue,
                        currencySymbol = currency,
                        icon = Icons.Default.Person,
                        iconColor = CrimsonExpense,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                        subtitle = "মোট পাওনা টাকা",
                        onClick = onNavigateToKhata
                    )

                    MetricCard(
                        title = "সাপ্লায়ার দেনা (দেবেন)",
                        amount = summary.totalSupplierDue,
                        currencySymbol = currency,
                        icon = Icons.Default.LocalShipping,
                        iconColor = AmberDue,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                        subtitle = "মহাজনের দেনা",
                        onClick = onNavigateToKhata
                    )
                }
            }
        }

        // Low stock alert banner
        if (summary.lowStockCount > 0) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToStock() },
                    colors = CardDefaults.cardColors(containerColor = CrimsonContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = CrimsonExpense,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "স্টক সতর্কবার্তা!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonExpense
                            )
                            Text(
                                text = "${summary.lowStockCount}টি পণ্যের স্টক শেষ হওয়ার পথে। দেখতে ক্লিক করুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = CrimsonExpense.copy(alpha = 0.85f)
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CrimsonExpense
                        )
                    }
                }
            }
        }

        // Recent Transactions Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাম্প্রতিক লেনদেন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToTransactions) {
                    Text("সব দেখুন", color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        val recentTx = transactions.take(5)
        if (recentTx.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো লেনদেন এন্ট্রি করা হয়নি",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recentTx) { tx ->
                TransactionRowItem(
                    tx = tx,
                    currencySymbol = currency,
                    onClick = { onSelectTransaction(tx) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
