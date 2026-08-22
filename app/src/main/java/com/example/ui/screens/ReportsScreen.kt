package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.BusinessViewModel
import com.example.viewmodel.DateFilterType
import java.util.Calendar

@Composable
fun ReportsScreen(
    viewModel: BusinessViewModel,
    profile: BusinessProfileEntity?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val parties by viewModel.parties.collectAsState()
    val products by viewModel.products.collectAsState()

    var selectedPeriod by remember { mutableStateOf(DateFilterType.THIS_MONTH) }

    val currency = profile?.currencySymbol ?: "৳"

    // Calculate period timestamps
    val (startTime, endTime) = remember(selectedPeriod) {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        when (selectedPeriod) {
            DateFilterType.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                Pair(calendar.timeInMillis, now)
            }
            DateFilterType.YESTERDAY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val start = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                Pair(start, calendar.timeInMillis)
            }
            DateFilterType.THIS_WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                Pair(calendar.timeInMillis, now)
            }
            DateFilterType.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                Pair(calendar.timeInMillis, now)
            }
            DateFilterType.ALL -> Pair(0L, Long.MAX_VALUE)
        }
    }

    val periodTransactions = remember(transactions, startTime, endTime) {
        transactions.filter { it.timestamp in startTime..endTime }
    }

    // Calculations for period
    val totalSales = remember(periodTransactions) {
        periodTransactions.filter { it.type == TransactionType.SALE }.sumOf { it.amount }
    }
    val totalPurchases = remember(periodTransactions) {
        periodTransactions.filter { it.type == TransactionType.PURCHASE }.sumOf { it.amount }
    }
    val totalExpenses = remember(periodTransactions) {
        periodTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val totalEstimatedGrossProfit = remember(periodTransactions) {
        periodTransactions.filter { it.type == TransactionType.SALE }.sumOf { it.profitEstimate }
    }
    val netProfit = totalEstimatedGrossProfit - totalExpenses

    val totalCashIn = remember(periodTransactions) {
        periodTransactions.filter {
            it.type in listOf(TransactionType.SALE, TransactionType.INCOME, TransactionType.DUE_COLLECTION)
        }.sumOf { it.paidAmount }
    }
    val totalCashOut = remember(periodTransactions) {
        periodTransactions.filter {
            it.type in listOf(TransactionType.PURCHASE, TransactionType.EXPENSE, TransactionType.DUE_PAYMENT)
        }.sumOf { it.paidAmount }
    }

    // Expenses by category
    val expensesByCategory = remember(periodTransactions) {
        val expList = periodTransactions.filter { it.type == TransactionType.EXPENSE }
        ExpenseCategory.entries.map { cat ->
            val sum = expList.filter { it.expenseCategory == cat }.sumOf { it.amount }
            Pair(cat, sum)
        }.filter { it.second > 0 }
    }

    val reportText = """
        ==============================
        📊 ${profile?.businessName ?: "হিসাব খাতা"}
        ব্যবসায়িক লাভ-ক্ষতি ও আর্থিক রিপোর্ট
        সময়কাল: ${selectedPeriod.labelBn}
        ==============================
        🟢 মোট বিক্রয় (Sales): $currency $totalSales
        🔵 মোট ক্রয় (Purchases): $currency $totalPurchases
        🔴 মোট অন্যান্য খরচ (Expenses): $currency $totalExpenses
        ------------------------------
        💰 নিট লাভ/ক্ষতি (Net Profit): $currency $netProfit
        ------------------------------
        💵 মোট ক্যাশ ইন: $currency $totalCashIn
        💳 মোট ক্যাশ আউট: $currency $totalCashOut
        ==============================
        তারিখ: ${formatDateTime(System.currentTimeMillis())}
    """.trimIndent()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("reports_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Period Filter Selector
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "রিপোর্টের সময়কাল নির্বাচন করুন:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DateFilterType.entries.forEach { df ->
                            FilterChip(
                                selected = selectedPeriod == df,
                                onClick = { selectedPeriod = df },
                                label = { Text(df.labelBn) }
                            )
                        }
                    }
                }
            }
        }

        // Net Profit & Loss Highlight Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (netProfit >= 0) EmeraldContainer else CrimsonContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (netProfit >= 0) "নিট লাভ (Net Profit)" else "নিট ক্ষতি (Net Loss)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (netProfit >= 0) OnEmeraldContainer else CrimsonExpense
                            )
                            Text(
                                text = "নির্বাচিত সময়কালে (${selectedPeriod.labelBn})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (netProfit >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (netProfit >= 0) EmeraldPrimary else CrimsonExpense,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = formatCurrency(netProfit, currency),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (netProfit >= 0) EmeraldPrimary else CrimsonExpense
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "মোট বিক্রয়: ${formatCurrency(totalSales, currency)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "দোকান খরচ: ${formatCurrency(totalExpenses, currency)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CrimsonExpense
                        )
                    }
                }
            }
        }

        // Cash Flow Comparison
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "ক্যাশ ফ্লো বিবরণী (Cash Flow)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "মোট ক্যাশ ইন",
                        amount = totalCashIn,
                        currencySymbol = currency,
                        icon = Icons.Default.ArrowDownward,
                        iconColor = EmeraldLight,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                        subtitle = "নগদ বিক্রয় + বাকি আদায়"
                    )

                    MetricCard(
                        title = "মোট ক্যাশ আউট",
                        amount = totalCashOut,
                        currencySymbol = currency,
                        icon = Icons.Default.ArrowUpward,
                        iconColor = CrimsonExpense,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                        subtitle = "পণ্য ক্রয় + অন্যান্য ব্যয়"
                    )
                }
            }
        }

        // Expense category breakdown
        if (expensesByCategory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "খরচের খাতভিত্তিক বিশ্লেষণ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            expensesByCategory.forEach { (cat, amount) ->
                                val percentage = if (totalExpenses > 0) (amount / totalExpenses).toFloat() else 0f
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(cat.labelBn, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        Text(
                                            formatCurrency(amount, currency),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = CrimsonExpense
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { percentage },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = CrimsonExpense,
                                        trackColor = CrimsonContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Share / Export Report Action
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reportText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "রিপোর্ট শেয়ার করুন"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("share_report_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("সম্পূর্ণ হিসাব রিপোর্ট শেয়ার করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
