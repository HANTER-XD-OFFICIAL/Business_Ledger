package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.BusinessViewModel

@Composable
fun PartyLedgerScreen(
    viewModel: BusinessViewModel,
    profile: BusinessProfileEntity?,
    onAddParty: (defaultType: PartyType) -> Unit,
    onCollectDue: (PartyEntity) -> Unit,
    onPaySupplier: (PartyEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val parties by viewModel.parties.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val partyTypeFilter by viewModel.partyTypeFilter.collectAsState()
    val searchQuery by viewModel.partySearchQuery.collectAsState()

    var selectedPartyForDetail by remember { mutableStateOf<PartyEntity?>(null) }
    var editingParty by remember { mutableStateOf<PartyEntity?>(null) }

    val currency = profile?.currencySymbol ?: "৳"

    val currentList = remember(parties, partyTypeFilter, searchQuery) {
        parties.filter { party ->
            val matchesType = party.type == partyTypeFilter
            val matchesQuery = searchQuery.isBlank() ||
                    party.name.contains(searchQuery, ignoreCase = true) ||
                    party.phone.contains(searchQuery, ignoreCase = true) ||
                    party.address.contains(searchQuery, ignoreCase = true)
            matchesType && matchesQuery
        }
    }

    val totalBalance = remember(parties, partyTypeFilter) {
        parties.filter { it.type == partyTypeFilter && it.currentBalance > 0 }
            .sumOf { it.currentBalance }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("party_ledger_screen")
    ) {
        // Tab Row: Customers vs Suppliers
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                TabRow(
                    selectedTabIndex = if (partyTypeFilter == PartyType.CUSTOMER) 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = partyTypeFilter == PartyType.CUSTOMER,
                        onClick = { viewModel.partyTypeFilter.value = PartyType.CUSTOMER },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (partyTypeFilter == PartyType.CUSTOMER) EmeraldPrimary else Color.Transparent
                            ),
                        text = {
                            Text(
                                "কাস্টমার খাতা (বাকি পাওনা)",
                                color = if (partyTypeFilter == PartyType.CUSTOMER) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = partyTypeFilter == PartyType.SUPPLIER,
                        onClick = { viewModel.partyTypeFilter.value = PartyType.SUPPLIER },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (partyTypeFilter == PartyType.SUPPLIER) AmberDue else Color.Transparent
                            ),
                        text = {
                            Text(
                                "সাপ্লায়ার খাতা (দেনা)",
                                color = if (partyTypeFilter == PartyType.SUPPLIER) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search & Add Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.partySearchQuery.value = it },
                        placeholder = { Text(if (partyTypeFilter == PartyType.CUSTOMER) "কাস্টমার খুঁজুন..." else "সাপ্লায়ার খুঁজুন...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = { onAddParty(partyTypeFilter) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (partyTypeFilter == PartyType.CUSTOMER) EmeraldPrimary else AmberDue
                        ),
                        modifier = Modifier.height(52.dp).testTag("add_party_screen_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (partyTypeFilter == PartyType.CUSTOMER) "নতুন কাস্টমার" else "নতুন সাপ্লায়ার")
                    }
                }
            }
        }

        // Outstanding Balance Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (partyTypeFilter == PartyType.CUSTOMER) CrimsonContainer else AmberContainer
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
                        text = if (partyTypeFilter == PartyType.CUSTOMER) "মোট কাস্টমার বাকি (পাবেন)" else "মোট সাপ্লায়ার বাকি (দেবেন)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatCurrency(totalBalance, currency),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (partyTypeFilter == PartyType.CUSTOMER) CrimsonExpense else AmberDue
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (partyTypeFilter == PartyType.CUSTOMER) Icons.Default.Groups else Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (partyTypeFilter == PartyType.CUSTOMER) CrimsonExpense else AmberDue
                        )
                    }
                }
            }
        }

        // Party List
        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PersonSearch,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (partyTypeFilter == PartyType.CUSTOMER) "কোনো কাস্টমার পাওয়া যায়নি" else "কোনো সাপ্লায়ার পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAddParty(partyTypeFilter) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("নতুন যোগ করুন")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentList, key = { it.id }) { party ->
                    PartyCardItem(
                        party = party,
                        currencySymbol = currency,
                        onClick = { selectedPartyForDetail = party },
                        onDirectAction = {
                            if (party.type == PartyType.CUSTOMER) {
                                onCollectDue(party)
                            } else {
                                onPaySupplier(party)
                            }
                        }
                    )
                }
            }
        }
    }

    // Party Statement & Details Dialog
    selectedPartyForDetail?.let { party ->
        val partyTxList = remember(party.id, transactions) {
            transactions.filter { it.partyId == party.id }
        }

        PartyStatementDialog(
            party = party,
            transactions = partyTxList,
            currencySymbol = currency,
            profile = profile,
            onDismiss = { selectedPartyForDetail = null },
            onEditParty = {
                editingParty = party
                selectedPartyForDetail = null
            },
            onDeleteParty = {
                viewModel.deleteParty(party)
                selectedPartyForDetail = null
            }
        )
    }

    // Edit Party Dialog
    editingParty?.let { party ->
        AddEditPartyDialog(
            initialParty = party,
            defaultType = party.type,
            onDismiss = { editingParty = null },
            onSave = { updated ->
                viewModel.saveParty(updated)
                editingParty = null
            }
        )
    }
}

@Composable
fun PartyStatementDialog(
    party: PartyEntity,
    transactions: List<TransactionEntity>,
    currencySymbol: String = "৳",
    profile: BusinessProfileEntity?,
    onDismiss: () -> Unit,
    onEditParty: () -> Unit,
    onDeleteParty: () -> Unit
) {
    val context = LocalContext.current
    val isCustomer = party.type == PartyType.CUSTOMER

    val statementSummary = """
        ==============================
        📑 ${profile?.businessName ?: "হিসাব খাতা"}
        হিসাব বিবরণী (Statement)
        ==============================
        পার্টি: ${party.name}
        মোবাইল: ${party.phone}
        ঠিকানা: ${party.address}
        ধরন: ${if (isCustomer) "কাস্টমার (ক্রেতা)" else "সাপ্লায়ার (মহাজন)"}
        ------------------------------
        বর্তমান মোট বাকি: $currencySymbol ${party.currentBalance}
        ==============================
        তারিখ: ${formatDateOnly(System.currentTimeMillis())}
        ধন্যবাদান্তে, ${profile?.ownerName ?: "কর্তৃপক্ষ"}
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = party.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (party.phone.isNotBlank()) {
                            Text(
                                text = party.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Balance summary box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCustomer) CrimsonContainer else AmberContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isCustomer) "মোট বাকি পাওনা" else "মোট দেনা বাকি",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatCurrency(party.currentBalance, currencySymbol),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isCustomer) CrimsonExpense else AmberDue
                            )
                        }

                        IconButton(onClick = onEditParty) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Party")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "লেনদেন খতিয়ান (${transactions.size}টি):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Transaction list for this party
                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এই পার্টির সাথে কোনো লেনদেন পাওয়া যায়নি",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(transactions) { tx ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${tx.type.labelBn} ${if (tx.invoiceNo.isNotBlank()) "(${tx.invoiceNo})" else ""}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = formatDateTime(tx.timestamp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (tx.note.isNotBlank()) {
                                            Text(
                                                text = tx.note,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = formatCurrency(tx.amount, currencySymbol),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (tx.dueAmount > 0) {
                                            Text(
                                                text = "বাকি: $currencySymbol ${tx.dueAmount}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CrimsonExpense,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Actions: Share statement / Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, statementSummary)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "বিবরণী পাঠান"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("হিসাব পাঠান")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("সম্পন্ন")
                    }
                }
            }
        }
    }
}
