package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.BusinessRepository
import com.example.util.AppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

enum class DateFilterType(val labelBn: String, val labelEn: String) {
    TODAY("আজ", "Today"),
    YESTERDAY("গতকাল", "Yesterday"),
    THIS_WEEK("এই সপ্তাহ", "This Week"),
    THIS_MONTH("এই মাস", "This Month"),
    ALL("সব সময়", "All Time")
}

data class DashboardSummary(
    val todayCashIn: Double = 0.0,
    val todayCashOut: Double = 0.0,
    val todaySalesAmount: Double = 0.0,
    val todayEstimatedProfit: Double = 0.0,
    val totalCustomerDue: Double = 0.0,
    val totalSupplierDue: Double = 0.0,
    val totalStockCostValue: Double = 0.0,
    val totalStockSellValue: Double = 0.0,
    val lowStockCount: Int = 0,
    val todayTxCount: Int = 0
)

class BusinessViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = BusinessRepository(db)
    private val prefs = application.getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE)

    // Active User State
    private val _activeUserId = MutableStateFlow<String?>(prefs.getString("active_user_id", null))
    val activeUserId: StateFlow<String?> = _activeUserId.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAccountEntity?>(null)
    val currentUser: StateFlow<UserAccountEntity?> = _currentUser.asStateFlow()

    // App Language State
    private val _currentLanguage = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString("app_language", "BN") ?: "BN")
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val allUsers: StateFlow<List<UserAccountEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe and load user
        viewModelScope.launch {
            _activeUserId.collect { uid ->
                if (uid != null) {
                    val user = repository.getUserById(uid)
                    if (user != null) {
                        _currentUser.value = user
                        val lang = AppLanguage.fromCode(user.preferredLanguage)
                        _currentLanguage.value = lang
                    } else {
                        _currentUser.value = null
                    }
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    // Dynamic User Scoped Data Flows
    val transactions: StateFlow<List<TransactionEntity>> = _activeUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getTransactionsForUser(uid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parties: StateFlow<List<PartyEntity>> = _activeUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getPartiesForUser(uid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<ProductEntity>> = _activeUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getProductsForUser(uid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = _activeUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getLowStockForUser(uid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val businessProfile: StateFlow<BusinessProfileEntity?> = _activeUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getProfileForUser(uid) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filter states
    val txSearchQuery = MutableStateFlow("")
    val txTypeFilter = MutableStateFlow<TransactionType?>(null)
    val txDateFilter = MutableStateFlow(DateFilterType.ALL)

    val partySearchQuery = MutableStateFlow("")
    val partyTypeFilter = MutableStateFlow(PartyType.CUSTOMER)

    val productSearchQuery = MutableStateFlow("")

    // Selected items for detail modals
    val selectedTransaction = MutableStateFlow<TransactionEntity?>(null)
    val selectedParty = MutableStateFlow<PartyEntity?>(null)

    // Directory & Merchant Search
    val directorySearchQuery = MutableStateFlow("")
    val directoryCategoryFilter = MutableStateFlow("সব")
    val selectedMerchantForDetail = MutableStateFlow<MerchantPublicProfile?>(null)

    // Pre-defined verified merchants in the merchant network directory
    private val communityMerchants = listOf(
        MerchantPublicProfile(
            id = "merchant_bhai_bhai",
            username = "@bhai_bhai_store",
            name = "হাজী দেলোয়ার হোসেন",
            shopName = "ভাই ভাই জেনারেল স্টোর",
            category = "মুদি ও পাইকারি",
            bio = "পাইকারি চাল, ডাল, চিনি, আটা ও ভোজ্য তেলের নির্ভরযোগ্য প্রতিষ্ঠান। ক্যাশ অন ডেলিভারি সুবিধা আছে।",
            address = "দোকান ৪৪, মৌলভীবাজার, পুরান ঢাকা",
            phone = "01711-234567",
            whatsapp = "+8801711234567",
            telegram = "t.me/HANTER_XD_OFFICIAL",
            avatarPreset = "avatar_2",
            rating = 4.9,
            reviewCount = 142,
            totalSalesCount = 1250,
            isVerified = true,
            responseRate = "৯৮%",
            featuredProducts = listOf("মিনিকেট চাল ৫০ কেজি", "তীর সয়াবিন তেল ৫ লিটার", "ফ্রেশ চিনি ৫০ কেজি")
        ),
        MerchantPublicProfile(
            id = "merchant_dhaka_wholesaler",
            username = "@dhaka_wholesale",
            name = "তারেক মাহমুদ",
            shopName = "ঢাকা হোলসেল কর্পোরেশন",
            category = "ইলেকট্রনিক্স",
            bio = "সকল ব্র্যান্ডের স্মার্টফোন এক্সেসরিজ, চার্জার, পাওয়ার ব্যাংক ও গ্যাজেটের সরাসরি আমদানিকারক ও পাইকারি বিক্রেতা।",
            address = "সুন্দরবন স্কয়ার সুপার মার্কেট, গুলিস্তান, ঢাকা",
            phone = "01819-876543",
            whatsapp = "+8801819876543",
            telegram = "t.me/HANTER_XD_OFFICIAL",
            avatarPreset = "avatar_3",
            rating = 4.8,
            reviewCount = 98,
            totalSalesCount = 890,
            isVerified = true,
            responseRate = "৯৫%",
            featuredProducts = listOf("20W ফাস্ট চার্জার", "TWS ব্লুটুথ ইয়ারবাডস", "10000mAh পাওয়ার ব্যাংক")
        ),
        MerchantPublicProfile(
            id = "merchant_green_agro",
            username = "@green_agro_farm",
            name = "কামরুল হাসান",
            shopName = "গ্রিন এগ্রো অ্যান্ড পোল্ট্রি ফিড",
            category = "কৃষি ও ফিড",
            bio = "১০০% খাঁটি সরিষার তেল, দেশি ঘি, সুন্দরবনের মধু ও উন্নতমানের পোল্ট্রি ফিড সরবরাহকারী।",
            address = "স্টেডিয়াম রোড, বগুড়া",
            phone = "01912-345678",
            whatsapp = "+8801912345678",
            telegram = "t.me/HANTER_XD_OFFICIAL",
            avatarPreset = "avatar_4",
            rating = 5.0,
            reviewCount = 76,
            totalSalesCount = 620,
            isVerified = true,
            responseRate = "১০০%",
            featuredProducts = listOf("ঘানি ভাঙা সরিষার তেল", "খাঁটি গাওয়া ঘি ১ কেজি", "প্রাকৃতিক সুন্দরবনের মধু")
        ),
        MerchantPublicProfile(
            id = "merchant_ctg_textile",
            username = "@ctg_fabrics",
            name = "ইকবাল হোসাইন",
            shopName = "চিটাগাং ফেব্রিক্স অ্যান্ড টেক্সটাইল",
            category = "কাপড় ও বস্ত্র",
            bio = "সুতি থান কাপড়, প্রিমিয়াম পাঞ্জাবি কাপড় এবং পাইকারি গজ কাপড়ের প্রস্তুতকারক ও ব্যবসায়ী।",
            address = "টেরিবাজার, কোতোয়ালী, চট্টগ্রাম",
            phone = "01611-998877",
            whatsapp = "+8801611998877",
            telegram = "t.me/HANTER_XD_OFFICIAL",
            avatarPreset = "avatar_5",
            rating = 4.7,
            reviewCount = 64,
            totalSalesCount = 450,
            isVerified = true,
            responseRate = "৯২%",
            featuredProducts = listOf("প্রিমিয়াম কটন সুতি থান", "সফট জ্যাকার্ড পাঞ্জাবি পিস")
        ),
        MerchantPublicProfile(
            id = "merchant_al_madina_pharma",
            username = "@almadina_pharma",
            name = "ডাঃ শফিকুর রহমান",
            shopName = "আল মদিনা ফার্মেসি ও সার্জিক্যাল",
            category = "ফার্মেসি",
            bio = "সকল প্রকার জীবনরক্ষাকারী ওষুধ ও সার্জিক্যাল সামগ্রী ন্যায্যমূল্যে পাইকারি ও খুচরা বিক্রয় কেন্দ্র।",
            address = "মিটফোর্ড রোড, ঢাকা",
            phone = "01722-334455",
            whatsapp = "+8801722334455",
            telegram = "t.me/HANTER_XD_OFFICIAL",
            avatarPreset = "avatar_6",
            rating = 4.9,
            reviewCount = 115,
            totalSalesCount = 1100,
            isVerified = true,
            responseRate = "৯৯%",
            featuredProducts = listOf("BP মনিটর মেশিন", "গ্লুকোমিটার কিট", "সার্জিক্যাল গ্লাভস বক্স")
        )
    )

    // Directory list combines registered accounts + verified community directory
    val merchantDirectory: StateFlow<List<MerchantPublicProfile>> = combine(
        allUsers,
        directorySearchQuery,
        directoryCategoryFilter
    ) { users, query, category ->
        val registeredList = users.map { u ->
            MerchantPublicProfile(
                id = u.userId,
                username = if (u.username.isNotBlank()) u.username else "@${u.displayName.lowercase().replace(" ", "_")}",
                name = u.displayName,
                shopName = u.businessName,
                category = u.businessCategory.ifBlank { "মুদি ও ব্যবসা" },
                bio = u.bio.ifBlank { "সততা ও নিষ্ঠার সাথে ব্যবসা পরিচালনাকারী।" },
                address = u.address.ifBlank { "বাংলাদেশ" },
                phone = u.phone,
                whatsapp = u.whatsapp.ifBlank { u.phone },
                telegram = u.telegram.ifBlank { "t.me/HANTER_XD_OFFICIAL" },
                avatarPreset = u.avatarPreset,
                rating = u.rating,
                reviewCount = u.reviewCount,
                totalSalesCount = u.totalSalesCount,
                isVerified = u.isVerified,
                featuredProducts = listOf("পণ্য সরবরাহ সচল রয়েছে")
            )
        }

        val combined = (communityMerchants + registeredList).distinctBy { it.username.lowercase() }

        combined.filter { merchant ->
            val matchCategory = category == "সব" || merchant.category.contains(category, ignoreCase = true)
            val matchQuery = query.isBlank() ||
                    merchant.username.contains(query, ignoreCase = true) ||
                    merchant.name.contains(query, ignoreCase = true) ||
                    merchant.shopName.contains(query, ignoreCase = true) ||
                    merchant.phone.contains(query, ignoreCase = true) ||
                    merchant.bio.contains(query, ignoreCase = true) ||
                    merchant.address.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), communityMerchants)

    // Chat Active State
    val activeChatPeer = MutableStateFlow<MerchantPublicProfile?>(null)

    val currentChatMessages: StateFlow<List<ChatMessageEntity>> = combine(
        _activeUserId,
        activeChatPeer
    ) { uid, peer ->
        if (uid != null && peer != null) {
            repository.getMessagesForConversation(uid, peer.username)
        } else {
            flowOf(emptyList())
        }
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecentMessages: StateFlow<List<ChatMessageEntity>> = _activeUserId
        .flatMapLatest { uid ->
            if (uid != null) repository.getAllMessagesForUser(uid) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Computed Dashboard Metrics
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        transactions,
        parties,
        products
    ) { txList, partyList, productList ->
        val startOfToday = getStartOfDay(0)
        val endOfToday = getEndOfDay(0)

        var todayIn = 0.0
        var todayOut = 0.0
        var todaySales = 0.0
        var todayProfitEst = 0.0
        var todayCount = 0

        for (tx in txList) {
            if (tx.timestamp in startOfToday..endOfToday) {
                todayCount++
                when (tx.type) {
                    TransactionType.SALE -> {
                        todayIn += tx.paidAmount
                        todaySales += tx.amount
                        todayProfitEst += tx.profitEstimate
                    }
                    TransactionType.PURCHASE -> {
                        todayOut += tx.paidAmount
                    }
                    TransactionType.EXPENSE -> {
                        todayOut += tx.amount
                        todayProfitEst -= tx.amount
                    }
                    TransactionType.INCOME -> {
                        todayIn += tx.amount
                        todayProfitEst += tx.amount
                    }
                    TransactionType.DUE_COLLECTION -> {
                        todayIn += tx.amount
                    }
                    TransactionType.DUE_PAYMENT -> {
                        todayOut += tx.amount
                    }
                }
            }
        }

        val totalCustDue = partyList
            .filter { it.type == PartyType.CUSTOMER && it.currentBalance > 0 }
            .sumOf { it.currentBalance }

        val totalSuppDue = partyList
            .filter { it.type == PartyType.SUPPLIER && it.currentBalance > 0 }
            .sumOf { it.currentBalance }

        var stockCostVal = 0.0
        var stockSellVal = 0.0
        var lowStockNum = 0

        for (p in productList) {
            stockCostVal += (p.buyPrice * p.stockQuantity)
            stockSellVal += (p.sellPrice * p.stockQuantity)
            if (p.stockQuantity <= p.minStockAlert) {
                lowStockNum++
            }
        }

        DashboardSummary(
            todayCashIn = todayIn,
            todayCashOut = todayOut,
            todaySalesAmount = todaySales,
            todayEstimatedProfit = todayProfitEst,
            totalCustomerDue = totalCustDue,
            totalSupplierDue = totalSuppDue,
            totalStockCostValue = stockCostVal,
            totalStockSellValue = stockSellVal,
            lowStockCount = lowStockNum,
            todayTxCount = todayCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        transactions,
        txSearchQuery,
        txTypeFilter,
        txDateFilter
    ) { list, query, typeFilter, dateFilter ->
        val (startTime, endTime) = getDateRange(dateFilter)
        list.filter { tx ->
            val matchesType = typeFilter == null || tx.type == typeFilter
            val matchesSearch = query.isBlank() ||
                    tx.partyName.contains(query, ignoreCase = true) ||
                    tx.invoiceNo.contains(query, ignoreCase = true) ||
                    tx.note.contains(query, ignoreCase = true) ||
                    tx.productName.contains(query, ignoreCase = true)
            val matchesDate = tx.timestamp in startTime..endTime
            matchesType && matchesSearch && matchesDate
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Language Setter
    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
        prefs.edit().putString("app_language", lang.code).apply()
        val uid = _activeUserId.value
        if (uid != null) {
            viewModelScope.launch {
                repository.updateLanguage(uid, lang.code)
            }
        }
    }

    // Authentication Actions
    fun loginWithGoogle(
        email: String,
        displayName: String,
        photoUrl: String = "",
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val cleanEmail = email.trim().lowercase()
                val uid = "google_${cleanEmail.replace("@", "_").replace(".", "_")}"
                var existingUser = repository.getUserById(uid)
                if (existingUser == null) {
                    val defaultUsername = "@${displayName.lowercase().replace(" ", "_").take(15)}"
                    existingUser = UserAccountEntity(
                        userId = uid,
                        displayName = displayName.ifBlank { "রাসেল চৌধুরী" },
                        username = defaultUsername,
                        emailOrPhone = cleanEmail,
                        authProvider = "GOOGLE",
                        businessName = "মেসার্স বিসমিল্লাহ ট্রেডার্স",
                        businessCategory = "মুদি ও পাইকারি",
                        bio = "চকবাজারের সর্ববৃহৎ পাইকারি ও খুচরা খাদ্যশস্য ও ভোজ্য তেল বিক্রয় কেন্দ্র।",
                        address = "দোকান নং ১২, চকবাজার, ঢাকা",
                        phone = "01882-278234",
                        whatsapp = "+8801882278234",
                        telegram = "t.me/HANTER_XD_OFFICIAL",
                        avatarPreset = "avatar_1",
                        photoUrl = photoUrl,
                        rating = 4.9,
                        reviewCount = 86,
                        totalSalesCount = 340,
                        isVerified = true,
                        preferredLanguage = _currentLanguage.value.code
                    )
                    repository.saveUser(existingUser)
                    AppDatabase.populateStarterData(db, uid)
                } else {
                    repository.updateLastLogin(uid)
                }
                setActiveUser(uid)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun loginWithPhone(
        phone: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val cleanPhone = phone.trim()
            val existing = repository.getUserByIdentifier(cleanPhone)
            if (existing != null) {
                if (existing.passwordHash.isEmpty() || existing.passwordHash == password) {
                    repository.updateLastLogin(existing.userId)
                    setActiveUser(existing.userId)
                    onComplete(true, null)
                } else {
                    onComplete(false, "ভুল পাসওয়ার্ড। আবার চেষ্টা করুন।")
                }
            } else {
                onComplete(false, "এই নম্বরে কোনো অ্যাকাউন্ট পাওয়া যায়নি। রেজিস্টার করুন।")
            }
        }
    }

    fun registerWithPhone(
        fullName: String,
        shopName: String,
        identifier: String,
        passwordInput: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val cleanPhone = identifier.trim()
            val existing = repository.getUserByIdentifier(cleanPhone)
            if (existing != null) {
                onComplete(false, "এই নম্বর দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট রয়েছে। লগইন করুন।")
                return@launch
            }
            val uid = "phone_${cleanPhone.replace("+", "").replace("-", "")}"
            val username = "@${fullName.lowercase().replace(" ", "_").take(15).ifBlank { "user_${System.currentTimeMillis() % 1000}" }}"
            val newUser = UserAccountEntity(
                userId = uid,
                displayName = fullName.ifBlank { "ব্যবসায়ী" },
                username = username,
                emailOrPhone = cleanPhone,
                authProvider = "PHONE",
                passwordHash = passwordInput,
                businessName = shopName.ifBlank { "আমার দোকান" },
                businessCategory = "মুদি ও পাইকারি",
                bio = "সততা ও বিশ্বস্ততার সাথে সেবা দিয়ে আসছি।",
                address = "ঢাকা, বাংলাদেশ",
                phone = cleanPhone,
                whatsapp = cleanPhone,
                telegram = "t.me/HANTER_XD_OFFICIAL",
                avatarPreset = "avatar_1",
                rating = 4.9,
                reviewCount = 12,
                totalSalesCount = 45,
                isVerified = true,
                preferredLanguage = _currentLanguage.value.code
            )
            repository.saveUser(newUser)
            AppDatabase.populateStarterData(db, uid)
            setActiveUser(uid)
            onComplete(true, "রেজিস্ট্রেশন সফল হয়েছে!")
        }
    }

    fun loginAsDemoUser() {
        viewModelScope.launch {
            val uid = "demo_user"
            val user = repository.getUserById(uid)
            if (user == null) {
                AppDatabase.populateStarterData(db, uid)
            }
            setActiveUser(uid)
        }
    }

    fun continueAsGuest(onComplete: () -> Unit) {
        loginAsDemoUser()
        onComplete()
    }

    fun setActiveUser(userId: String) {
        prefs.edit().putString("active_user_id", userId).apply()
        _activeUserId.value = userId
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _currentUser.value = user
        }
    }

    fun logout() {
        prefs.edit().remove("active_user_id").apply()
        _activeUserId.value = null
        _currentUser.value = null
    }

    // Profile Customization
    fun updateFullUserProfile(
        displayName: String,
        username: String,
        businessName: String,
        businessCategory: String,
        bio: String,
        address: String,
        phone: String,
        whatsapp: String,
        telegram: String,
        avatarPreset: String,
        photoUrl: String = ""
    ) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val existing = repository.getUserById(uid)
            val updatedUsername = if (username.startsWith("@")) username else "@$username"
            
            val updatedUser = (existing ?: UserAccountEntity(
                userId = uid,
                displayName = displayName,
                emailOrPhone = phone
            )).copy(
                displayName = displayName.ifBlank { "ব্যবসায়ী" },
                username = updatedUsername,
                businessName = businessName.ifBlank { "আমার ব্যবসা খাতা" },
                businessCategory = businessCategory.ifBlank { "মুদি ও পাইকারি" },
                bio = bio.ifBlank { "সততা ও বিশ্বস্ততার সাথে সেবা দিয়ে আসছি।" },
                address = address.ifBlank { "ঢাকা, বাংলাদেশ" },
                phone = phone,
                whatsapp = whatsapp.ifBlank { phone },
                telegram = telegram.ifBlank { "t.me/HANTER_XD_OFFICIAL" },
                avatarPreset = avatarPreset,
                photoUrl = photoUrl
            )
            repository.saveUser(updatedUser)
            _currentUser.value = updatedUser

            val profile = BusinessProfileEntity(
                userId = uid,
                username = updatedUsername,
                businessName = businessName.ifBlank { "আমার ব্যবসা খাতা" },
                businessCategory = businessCategory.ifBlank { "মুদি ও পাইকারি" },
                ownerName = displayName.ifBlank { "ব্যবসায়ী" },
                phone = phone,
                whatsapp = whatsapp.ifBlank { phone },
                telegram = telegram.ifBlank { "t.me/HANTER_XD_OFFICIAL" },
                address = address.ifBlank { "ঢাকা, বাংলাদেশ" },
                bio = bio,
                avatarPreset = avatarPreset,
                photoUrl = photoUrl,
                rating = updatedUser.rating,
                reviewCount = updatedUser.reviewCount,
                totalSalesCount = updatedUser.totalSalesCount,
                isVerified = updatedUser.isVerified
            )
            repository.updateProfile(profile)
        }
    }

    // Chat / Messaging Operations
    fun openChatWithMerchant(merchant: MerchantPublicProfile) {
        activeChatPeer.value = merchant
    }

    fun openChatWith(merchant: MerchantPublicProfile) {
        activeChatPeer.value = merchant
    }

    fun openChat(merchant: MerchantPublicProfile) {
        activeChatPeer.value = merchant
    }

    fun closeChat() {
        activeChatPeer.value = null
    }

    fun sendChatMessage(text: String, messageType: String = "TEXT") {
        val peer = activeChatPeer.value ?: return
        if (text.isBlank()) return
        val uid = getCurrentUserId()
        val senderUser = _currentUser.value

        viewModelScope.launch {
            val outgoingMsg = ChatMessageEntity(
                userId = uid,
                peerId = peer.username,
                peerName = peer.shopName,
                peerAvatar = peer.avatarPreset,
                messageText = text.trim(),
                isOutgoing = true,
                timestamp = System.currentTimeMillis(),
                messageType = messageType
            )
            repository.sendChatMessage(outgoingMsg)

            // Realistic automated merchant response simulation
            delay(1200)
            val replyText = when (messageType) {
                "PAYMENT_REMINDER" -> "ধন্যবাদ তাগাদার জন্য। আমি হিসাব দেখে আজ সন্ধ্যার মধ্যেই বিকাশ/নগদে বাকি টাকা পরিশোধ করে দিচ্ছি।"
                "INVOICE" -> "চালান ও পণ্যের বিবরণ পেয়েছি। মাল ডেলিভারির পর বাকি টাকা ক্লিয়ার করে দেওয়া হবে ইনশাআল্লাহ।"
                "PRODUCT_QUERY" -> "জি আলহামদুলিল্লাহ স্টকে পর্যাপ্ত মাল আছে। আপনি কত বস্তা/কার্টন নেবেন জানালে রেট আরও কিছুটা ছাড় দেব।"
                else -> {
                    if (text.contains("দর", ignoreCase = true) || text.contains("দাম", ignoreCase = true) || text.contains("price", ignoreCase = true)) {
                        "আজকের হোলসেল রেট বেশ সুবিধাজনক। সরাসরি ফোনে বা হোয়াটসঅ্যাপেও কথা বলতে পারেন।"
                    } else if (text.contains("সালাম", ignoreCase = true) || text.contains("hello", ignoreCase = true) || text.contains("হাই", ignoreCase = true)) {
                        "ওয়ালাইকুম আসসালাম! কেমন আছেন? আপনার কী ধরণের মালামাল লাগবে জানান।"
                    } else {
                        "জি মেসেজ পেয়েছি। আমাদের শপ সবসময় সচল রয়েছে। কোনো জরুরি জিজ্ঞাসা থাকলে সরাসরি কল দিন।"
                    }
                }
            }

            val incomingReply = ChatMessageEntity(
                userId = uid,
                peerId = peer.username,
                peerName = peer.shopName,
                peerAvatar = peer.avatarPreset,
                messageText = replyText,
                isOutgoing = false,
                timestamp = System.currentTimeMillis(),
                messageType = "TEXT"
            )
            repository.sendChatMessage(incomingReply)
        }
    }

    fun clearActiveChat() {
        val peer = activeChatPeer.value ?: return
        val uid = getCurrentUserId()
        viewModelScope.launch {
            repository.clearConversation(uid, peer.username)
        }
    }

    // External communication intents
    fun makePhoneCall(context: Context, phoneNumber: String) {
        if (phoneNumber.isBlank()) return
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber.trim()}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openWhatsApp(context: Context, phoneNumber: String, prefillText: String = "") {
        if (phoneNumber.isBlank()) return
        try {
            val cleanPhone = phoneNumber.replace("+", "").replace("-", "").replace(" ", "")
            val uri = Uri.parse("https://wa.me/$cleanPhone?text=${Uri.encode(prefillText)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openTelegram(context: Context, telegramHandle: String) {
        try {
            val cleanHandle = telegramHandle.removePrefix("t.me/").removePrefix("@").trim()
            val uri = Uri.parse("https://t.me/$cleanHandle")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    // Transaction CRUD
    fun addSale(
        party: PartyEntity?,
        product: ProductEntity?,
        amount: Double,
        paidAmount: Double,
        paymentMode: PaymentMode,
        quantity: Double,
        unitPrice: Double,
        note: String
    ) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val due = (amount - paidAmount).coerceAtLeast(0.0)
            val inv = "INV-${System.currentTimeMillis() % 100000}"
            val profitEst = if (product != null && quantity > 0) {
                (unitPrice - product.buyPrice) * quantity
            } else {
                amount * 0.10 // 10% default estimate
            }

            val tx = TransactionEntity(
                userId = uid,
                invoiceNo = inv,
                type = TransactionType.SALE,
                amount = amount,
                paidAmount = paidAmount,
                dueAmount = due,
                partyId = party?.id,
                partyName = party?.name ?: "নগদ বিক্রয়",
                partyPhone = party?.phone ?: "",
                partyType = PartyType.CUSTOMER,
                paymentMode = paymentMode,
                note = note,
                productId = product?.id,
                productName = product?.name ?: "",
                quantity = quantity,
                unitPrice = unitPrice,
                profitEstimate = profitEst
            )
            repository.addTransaction(tx)
        }
    }

    fun addPurchase(
        party: PartyEntity?,
        product: ProductEntity?,
        amount: Double,
        paidAmount: Double,
        paymentMode: PaymentMode,
        quantity: Double,
        unitPrice: Double,
        note: String
    ) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val due = (amount - paidAmount).coerceAtLeast(0.0)
            val inv = "PUR-${System.currentTimeMillis() % 100000}"

            val tx = TransactionEntity(
                userId = uid,
                invoiceNo = inv,
                type = TransactionType.PURCHASE,
                amount = amount,
                paidAmount = paidAmount,
                dueAmount = due,
                partyId = party?.id,
                partyName = party?.name ?: "মহাজন / ডিলার",
                partyPhone = party?.phone ?: "",
                partyType = PartyType.SUPPLIER,
                paymentMode = paymentMode,
                note = note,
                productId = product?.id,
                productName = product?.name ?: "",
                quantity = quantity,
                unitPrice = unitPrice
            )
            repository.addTransaction(tx)
        }
    }

    fun addExpense(
        category: ExpenseCategory,
        amount: Double,
        paymentMode: PaymentMode,
        note: String
    ) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val inv = "EXP-${System.currentTimeMillis() % 100000}"
            val tx = TransactionEntity(
                userId = uid,
                invoiceNo = inv,
                type = TransactionType.EXPENSE,
                amount = amount,
                paidAmount = amount,
                expenseCategory = category,
                paymentMode = paymentMode,
                note = note
            )
            repository.addTransaction(tx)
        }
    }

    fun addIncome(
        amount: Double,
        paymentMode: PaymentMode,
        note: String
    ) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val inv = "INC-${System.currentTimeMillis() % 100000}"
            val tx = TransactionEntity(
                userId = uid,
                invoiceNo = inv,
                type = TransactionType.INCOME,
                amount = amount,
                paidAmount = amount,
                paymentMode = paymentMode,
                note = note
            )
            repository.addTransaction(tx)
        }
    }

    fun recordCustomerDueCollection(
        party: PartyEntity,
        amount: Double,
        paymentMode: PaymentMode,
        note: String
    ) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val inv = "COL-${System.currentTimeMillis() % 100000}"
            val tx = TransactionEntity(
                userId = uid,
                invoiceNo = inv,
                type = TransactionType.DUE_COLLECTION,
                amount = amount,
                paidAmount = amount,
                partyId = party.id,
                partyName = party.name,
                partyPhone = party.phone,
                partyType = PartyType.CUSTOMER,
                paymentMode = paymentMode,
                note = note.ifBlank { "বাকি টাকা আদায়" }
            )
            repository.addTransaction(tx)
        }
    }

    fun recordSupplierPayment(
        party: PartyEntity,
        amount: Double,
        paymentMode: PaymentMode,
        note: String
    ) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val inv = "PAY-${System.currentTimeMillis() % 100000}"
            val tx = TransactionEntity(
                userId = uid,
                invoiceNo = inv,
                type = TransactionType.DUE_PAYMENT,
                amount = amount,
                paidAmount = amount,
                partyId = party.id,
                partyName = party.name,
                partyPhone = party.phone,
                partyType = PartyType.SUPPLIER,
                paymentMode = paymentMode,
                note = note.ifBlank { "সাপ্লায়ারকে দেনা পরিশোধ" }
            )
            repository.addTransaction(tx)
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    // Party CRUD
    fun saveParty(party: PartyEntity) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val partyToSave = if (party.userId.isBlank()) party.copy(userId = uid) else party
            if (partyToSave.id > 0) {
                repository.updateParty(partyToSave)
            } else {
                repository.addParty(partyToSave)
            }
        }
    }

    fun deleteParty(party: PartyEntity) {
        viewModelScope.launch {
            repository.deleteParty(party)
        }
    }

    // Product CRUD
    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val productToSave = if (product.userId.isBlank()) product.copy(userId = uid) else product
            if (productToSave.id > 0) {
                repository.updateProduct(productToSave)
            } else {
                repository.addProduct(productToSave)
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun adjustStock(productId: Long, delta: Double) {
        viewModelScope.launch {
            repository.adjustProductStock(productId, delta)
        }
    }

    // Profile & Reset
    fun recordSale(
        party: PartyEntity?,
        customerName: String,
        customerPhone: String,
        product: ProductEntity?,
        quantity: Double,
        totalAmount: Double,
        paidAmount: Double,
        paymentMode: PaymentMode,
        note: String
    ) {
        val unitPrice = if (quantity > 0) totalAmount / quantity else totalAmount
        val actualParty = party ?: if (customerName.isNotBlank()) {
            PartyEntity(
                name = customerName,
                phone = customerPhone,
                type = PartyType.CUSTOMER
            )
        } else null
        addSale(
            party = actualParty,
            product = product,
            amount = totalAmount,
            paidAmount = paidAmount,
            paymentMode = paymentMode,
            quantity = quantity,
            unitPrice = unitPrice,
            note = note
        )
    }

    fun recordPurchase(
        party: PartyEntity?,
        supplierName: String,
        supplierPhone: String,
        product: ProductEntity?,
        quantity: Double,
        totalAmount: Double,
        paidAmount: Double,
        paymentMode: PaymentMode,
        note: String
    ) {
        val unitPrice = if (quantity > 0) totalAmount / quantity else totalAmount
        val actualParty = party ?: if (supplierName.isNotBlank()) {
            PartyEntity(
                name = supplierName,
                phone = supplierPhone,
                type = PartyType.SUPPLIER
            )
        } else null
        addPurchase(
            party = actualParty,
            product = product,
            amount = totalAmount,
            paidAmount = paidAmount,
            paymentMode = paymentMode,
            quantity = quantity,
            unitPrice = unitPrice,
            note = note
        )
    }

    fun recordExpense(
        amount: Double,
        category: ExpenseCategory,
        paymentMode: PaymentMode,
        note: String
    ) {
        addExpense(
            category = category,
            amount = amount,
            paymentMode = paymentMode,
            note = note
        )
    }

    fun recordDueCollection(
        party: PartyEntity,
        amount: Double,
        paymentMode: PaymentMode,
        note: String
    ) {
        recordCustomerDueCollection(
            party = party,
            amount = amount,
            paymentMode = paymentMode,
            note = note
        )
    }

    fun updateProfile(profile: BusinessProfileEntity) {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            val profileToSave = if (profile.userId.isBlank()) profile.copy(userId = uid) else profile
            repository.updateProfile(profileToSave)
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            repository.resetUserWithSampleData(uid)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            val uid = getCurrentUserId()
            repository.clearUserData(uid)
        }
    }

    private fun getCurrentUserId(): String {
        return _activeUserId.value ?: "demo_user"
    }

    private fun getStartOfDay(daysAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(daysAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    private fun getDateRange(type: DateFilterType): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        return when (type) {
            DateFilterType.TODAY -> Pair(getStartOfDay(0), getEndOfDay(0))
            DateFilterType.YESTERDAY -> Pair(getStartOfDay(1), getEndOfDay(1))
            DateFilterType.THIS_WEEK -> Pair(getStartOfDay(7), now)
            DateFilterType.THIS_MONTH -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                Pair(cal.timeInMillis, now)
            }
            DateFilterType.ALL -> Pair(0L, Long.MAX_VALUE)
        }
    }
}
