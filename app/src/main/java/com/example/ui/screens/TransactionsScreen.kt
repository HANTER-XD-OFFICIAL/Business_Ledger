package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.BusinessViewModel
import com.example.viewmodel.DateFilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: BusinessViewModel,
    profile: BusinessProfileEntity?,
    onSelectTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val searchQuery by viewModel.txSearchQuery.collectAsState()
    val typeFilter by viewModel.txTypeFilter.collectAsState()
    val dateFilter by viewModel.txDateFilter.collectAsState()

    val currency = profile?.currencySymbol ?: "৳"

    val totalFilteredIn = remember(transactions) {
        transactions.filter {
            it.type in listOf(TransactionType.SALE, TransactionType.INCOME, TransactionType.DUE_COLLECTION)
        }.sumOf { it.paidAmount }
    }

    val totalFilteredOut = remember(transactions) {
        transactions.filter {
            it.type in listOf(TransactionType.PURCHASE, TransactionType.EXPENSE, TransactionType.DUE_PAYMENT)
        }.sumOf { it.paidAmount }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("transactions_screen")
    ) {
        // Top Search Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.txSearchQuery.value = it },
                    placeholder = { Text("গ্রাহক, চালান বা পণ্য দিয়ে খুঁজুন...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.txSearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("tx_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Date Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DateFilterType.entries.forEach { df ->
                        FilterChip(
                            selected = dateFilter == df,
                            onClick = { viewModel.txDateFilter.value = df },
                            label = { Text(df.labelBn, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Transaction Type Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = typeFilter == null,
                        onClick = { viewModel.txTypeFilter.value = null },
                        label = { Text("সব লেনদেন", fontSize = 12.sp) }
                    )
                    TransactionType.entries.forEach { tt ->
                        FilterChip(
                            selected = typeFilter == tt,
                            onClick = { viewModel.txTypeFilter.value = tt },
                            label = { Text(tt.labelBn, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // Summary of filtered items
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transactions.size}টি লেনদেন পাওয়া গেছে",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ইন: +$currency $totalFilteredIn",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "আউট: -$currency $totalFilteredOut",
                        style = MaterialTheme.typography.labelSmall,
                        color = CrimsonExpense,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Transaction List
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "কোনো লেনদেন পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "ফিল্টার পরিবর্তন করুন বা নতুন এন্ট্রি দিন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(transactions, key = { it.id }) { tx ->
                    TransactionRowItem(
                        tx = tx,
                        currencySymbol = currency,
                        onClick = { onSelectTransaction(tx) }
                    )
                }
            }
        }
    }
}
