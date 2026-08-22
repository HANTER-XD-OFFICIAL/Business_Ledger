package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.viewmodel.BusinessViewModel

enum class NavigationTab(val stringKey: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    DASHBOARD("nav_dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
    TRANSACTIONS("nav_transactions", Icons.Default.ReceiptLong, Icons.Outlined.ReceiptLong),
    KHATA("nav_khata", Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    NETWORK("nav_network", Icons.Default.Hub, Icons.Outlined.Hub),
    STOCK("nav_stock", Icons.Default.Inventory2, Icons.Outlined.Inventory2),
    REPORTS("nav_reports", Icons.Default.Assessment, Icons.Outlined.Assessment),
    SETTINGS("nav_settings", Icons.Default.Settings, Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BusinessViewModel) {
    val activeUserId by viewModel.activeUserId.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // If no active user is logged in, show the Authentication & Registration Screen
    if (activeUserId == null) {
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = {
                // User is now authenticated and activeUserId is updated reactively
            }
        )
        return
    }

    val dashboardSummary by viewModel.dashboardSummary.collectAsState()
    val parties by viewModel.parties.collectAsState()
    val products by viewModel.products.collectAsState()
    val businessProfile by viewModel.businessProfile.collectAsState()
    val activeChatPeer by viewModel.activeChatPeer.collectAsState()

    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    // Dialog trigger states
    var showStartupTelegramDialog by remember { mutableStateOf(true) }
    var showSaleDialog by remember { mutableStateOf(false) }
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var partyForDueCollection by remember { mutableStateOf<PartyEntity?>(null) }
    var partyForSupplierPayment by remember { mutableStateOf<PartyEntity?>(null) }
    var showAddPartyDialog by remember { mutableStateOf(false) }
    var defaultPartyTypeToAdd by remember { mutableStateOf(PartyType.CUSTOMER) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var viewingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showProfileCustomizationDialog by remember { mutableStateOf(false) }

    // If activeChatPeer is set, show the Chat Screen
    if (activeChatPeer != null) {
        ChatScreen(
            merchant = activeChatPeer!!,
            viewModel = viewModel,
            onBack = { viewModel.closeChat() }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    val labelText = AppStrings.get(tab.stringKey, currentLang)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = labelText
                            )
                        },
                        label = {
                            Text(
                                text = labelText,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentTab in listOf(NavigationTab.DASHBOARD, NavigationTab.TRANSACTIONS)) {
                ExtendedFloatingActionButton(
                    onClick = { showSaleDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Sale") },
                    text = { Text(AppStrings.get("new_sale_btn", currentLang), fontWeight = FontWeight.Bold) },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("main_sale_fab")
                )
            } else if (currentTab == NavigationTab.NETWORK) {
                ExtendedFloatingActionButton(
                    onClick = { showProfileCustomizationDialog = true },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Edit Profile") },
                    text = { Text("আমার প্রোফাইল", fontWeight = FontWeight.Bold) },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("network_edit_fab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    summary = dashboardSummary,
                    profile = businessProfile,
                    onOpenSale = { showSaleDialog = true },
                    onOpenPurchase = { showPurchaseDialog = true },
                    onOpenExpense = { showExpenseDialog = true },
                    onOpenCollectDue = {
                        val firstCustWithDue = parties.firstOrNull { it.type == PartyType.CUSTOMER && it.currentBalance > 0 }
                        if (firstCustWithDue != null) {
                            partyForDueCollection = firstCustWithDue
                        } else {
                            currentTab = NavigationTab.KHATA
                        }
                    },
                    onNavigateToKhata = { currentTab = NavigationTab.KHATA },
                    onNavigateToStock = { currentTab = NavigationTab.STOCK },
                    onNavigateToTransactions = { currentTab = NavigationTab.TRANSACTIONS },
                    onSelectTransaction = { viewingTransaction = it },
                    onOpenSupport = { showStartupTelegramDialog = true }
                )

                NavigationTab.TRANSACTIONS -> TransactionsScreen(
                    viewModel = viewModel,
                    profile = businessProfile,
                    onSelectTransaction = { viewingTransaction = it }
                )

                NavigationTab.KHATA -> PartyLedgerScreen(
                    viewModel = viewModel,
                    profile = businessProfile,
                    onAddParty = { type ->
                        defaultPartyTypeToAdd = type
                        showAddPartyDialog = true
                    },
                    onCollectDue = { party -> partyForDueCollection = party },
                    onPaySupplier = { party -> partyForSupplierPayment = party }
                )

                NavigationTab.NETWORK -> NetworkScreen(
                    viewModel = viewModel,
                    onOpenChat = { merchant ->
                        viewModel.openChatWith(merchant)
                    },
                    onOpenMyProfileEdit = { showProfileCustomizationDialog = true }
                )

                NavigationTab.STOCK -> StockScreen(
                    viewModel = viewModel,
                    summary = dashboardSummary,
                    profile = businessProfile,
                    onAddProduct = { showAddProductDialog = true }
                )

                NavigationTab.REPORTS -> ReportsScreen(
                    viewModel = viewModel,
                    profile = businessProfile
                )

                NavigationTab.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    profile = businessProfile,
                    onShowTelegramDialog = { showStartupTelegramDialog = true }
                )
            }
        }
    }

    // Startup Telegram notification / contact dialog
    if (showStartupTelegramDialog) {
        TelegramWelcomeDialog(
            onDismiss = { showStartupTelegramDialog = false }
        )
    }

    // Comprehensive Profile Customization Dialog
    if (showProfileCustomizationDialog) {
        ProfileEditDialog(
            currentUser = currentUser,
            currentLang = currentLang,
            onDismiss = { showProfileCustomizationDialog = false },
            onSave = { displayName, username, businessName, businessCategory, bio, address, phone, whatsapp, telegram, avatarPreset ->
                viewModel.updateFullUserProfile(
                    displayName = displayName,
                    username = username,
                    businessName = businessName,
                    businessCategory = businessCategory,
                    bio = bio,
                    address = address,
                    phone = phone,
                    whatsapp = whatsapp,
                    telegram = telegram,
                    avatarPreset = avatarPreset
                )
                showProfileCustomizationDialog = false
            }
        )
    }

    // Modal Dialogs
    if (showSaleDialog) {
        AddSaleDialog(
            parties = parties,
            products = products,
            onDismiss = { showSaleDialog = false },
            onSaveSale = { party, name, phone, product, qty, total, paid, mode, note ->
                viewModel.recordSale(party, name, phone, product, qty, total, paid, mode, note)
            }
        )
    }

    if (showPurchaseDialog) {
        AddPurchaseDialog(
            parties = parties,
            products = products,
            onDismiss = { showPurchaseDialog = false },
            onSavePurchase = { party, name, phone, product, qty, total, paid, mode, note ->
                viewModel.recordPurchase(party, name, phone, product, qty, total, paid, mode, note)
            }
        )
    }

    if (showExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showExpenseDialog = false },
            onSaveExpense = { amount, category, mode, note ->
                viewModel.recordExpense(amount, category, mode, note)
            }
        )
    }

    partyForDueCollection?.let { party ->
        CollectDueDialog(
            party = party,
            onDismiss = { partyForDueCollection = null },
            onCollect = { amount, mode, note ->
                viewModel.recordDueCollection(party, amount, mode, note)
            }
        )
    }

    partyForSupplierPayment?.let { party ->
        PaySupplierDialog(
            party = party,
            onDismiss = { partyForSupplierPayment = null },
            onPay = { amount, mode, note ->
                viewModel.recordSupplierPayment(party, amount, mode, note)
            }
        )
    }

    if (showAddPartyDialog) {
        AddEditPartyDialog(
            initialParty = null,
            defaultType = defaultPartyTypeToAdd,
            onDismiss = { showAddPartyDialog = false },
            onSave = { party ->
                viewModel.saveParty(party)
            }
        )
    }

    if (showAddProductDialog) {
        AddEditProductDialog(
            initialProduct = null,
            onDismiss = { showAddProductDialog = false },
            onSave = { product ->
                viewModel.saveProduct(product)
            }
        )
    }

    viewingTransaction?.let { tx ->
        InvoiceDialog(
            transaction = tx,
            profile = businessProfile,
            currencySymbol = businessProfile?.currencySymbol ?: "৳",
            onDismiss = { viewingTransaction = null },
            onDelete = {
                viewModel.deleteTransaction(tx)
                viewingTransaction = null
            }
        )
    }
}
