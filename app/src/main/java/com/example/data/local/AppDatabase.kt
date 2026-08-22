package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccountEntity::class,
        TransactionEntity::class,
        PartyEntity::class,
        ProductEntity::class,
        BusinessProfileEntity::class,
        ChatMessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun partyDao(): PartyDao
    abstract fun productDao(): ProductDao
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hisab_khata_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateStarterData(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateStarterData(db: AppDatabase, targetUserId: String = "demo_user") {
            val userDao = db.userAccountDao()
            val profileDao = db.businessProfileDao()
            val partyDao = db.partyDao()
            val productDao = db.productDao()
            val txDao = db.transactionDao()
            val chatDao = db.chatMessageDao()

            if (userDao.getUserById(targetUserId) == null) {
                userDao.insertOrUpdate(
                    UserAccountEntity(
                        userId = targetUserId,
                        displayName = "রাসেল চৌধুরী",
                        username = "@rasel_traders",
                        emailOrPhone = "alexraselchodhury@gmail.com",
                        authProvider = "GOOGLE",
                        businessName = "মেসার্স বিসমিল্লাহ ট্রেডার্স",
                        businessCategory = "মুদি ও পাইকারি খাদ্যশস্য",
                        bio = "চকবাজারের সর্ববৃহৎ পাইকারি ও খুচরা খাদ্যশস্য ও ভোজ্য তেল বিক্রয় কেন্দ্র।",
                        address = "দোকান নং ১২, চকবাজার, ঢাকা",
                        phone = "01882-278234",
                        whatsapp = "+8801882278234",
                        telegram = "t.me/HANTER_XD_OFFICIAL",
                        avatarPreset = "avatar_1",
                        rating = 4.9,
                        reviewCount = 86,
                        totalSalesCount = 340,
                        isVerified = true,
                        preferredLanguage = "BN"
                    )
                )
            }

            if (profileDao.getProfile(targetUserId) == null) {
                profileDao.insertOrUpdate(
                    BusinessProfileEntity(
                        userId = targetUserId,
                        username = "@rasel_traders",
                        businessName = "মেসার্স বিসমিল্লাহ ট্রেডার্স",
                        businessCategory = "মুদি ও পাইকারি খাদ্যশস্য",
                        ownerName = "রাসেল চৌধুরী",
                        phone = "01882-278234",
                        whatsapp = "+8801882278234",
                        telegram = "t.me/HANTER_XD_OFFICIAL",
                        address = "দোকান নং ১২, চকবাজার, ঢাকা",
                        bio = "চকবাজারের সর্ববৃহৎ পাইকারি ও খুচরা খাদ্যশস্য ও ভোজ্য তেল বিক্রয় কেন্দ্র।",
                        avatarPreset = "avatar_1",
                        rating = 4.9,
                        reviewCount = 86,
                        totalSalesCount = 340,
                        isVerified = true,
                        currencySymbol = "৳",
                        tagline = "পাইকারি ও খুচরা বিক্রয় কেন্দ্র"
                    )
                )
            }

            if (productDao.getProductCount(targetUserId) == 0) {
                productDao.insertProduct(
                    ProductEntity(
                        userId = targetUserId,
                        name = "মিনিকেট চাল (৫০ কেজি)",
                        category = "মুদি",
                        buyPrice = 3200.0,
                        sellPrice = 3450.0,
                        stockQuantity = 25.0,
                        unit = ProductUnit.BOX,
                        minStockAlert = 5.0
                    )
                )
                productDao.insertProduct(
                    ProductEntity(
                        userId = targetUserId,
                        name = "সয়াবিন তেল (৫ লিটার)",
                        category = "মুদি",
                        buyPrice = 780.0,
                        sellPrice = 840.0,
                        stockQuantity = 40.0,
                        unit = ProductUnit.LITER,
                        minStockAlert = 8.0
                    )
                )
                productDao.insertProduct(
                    ProductEntity(
                        userId = targetUserId,
                        name = "চিনি (১ কেজি)",
                        category = "মুদি",
                        buyPrice = 135.0,
                        sellPrice = 145.0,
                        stockQuantity = 4.0,
                        unit = ProductUnit.KG,
                        minStockAlert = 10.0
                    )
                )
                productDao.insertProduct(
                    ProductEntity(
                        userId = targetUserId,
                        name = "মাদুর / প্লাস্টিক চেয়ার",
                        category = "প্লাস্টিক",
                        buyPrice = 450.0,
                        sellPrice = 620.0,
                        stockQuantity = 18.0,
                        unit = ProductUnit.PIECE,
                        minStockAlert = 3.0
                    )
                )

                // Parties
                val c1 = partyDao.insertParty(
                    PartyEntity(
                        userId = targetUserId,
                        name = "আব্দুর রহিম",
                        phone = "01811-123456",
                        address = "লালবাগ, ঢাকা",
                        type = PartyType.CUSTOMER,
                        currentBalance = 1500.0,
                        notes = "নিয়মিত কাস্টমার"
                    )
                )
                val c2 = partyDao.insertParty(
                    PartyEntity(
                        userId = targetUserId,
                        name = "করিম স্টোর",
                        phone = "01911-234567",
                        address = "নিউ মার্কেট, ঢাকা",
                        type = PartyType.CUSTOMER,
                        currentBalance = 3200.0,
                        notes = "সাপ্তাহিক বাকি পরিশোধ করেন"
                    )
                )
                val s1 = partyDao.insertParty(
                    PartyEntity(
                        userId = targetUserId,
                        name = "মেঘনা গ্রুপ ডিলার",
                        phone = "01711-345678",
                        address = "তেজগাঁও, ঢাকা",
                        type = PartyType.SUPPLIER,
                        currentBalance = 5400.0,
                        notes = "মেইন ডিস্ট্রিবিউটর"
                    )
                )

                val now = System.currentTimeMillis()
                val oneHourAgo = now - 3600000
                val twoHoursAgo = now - 7200000
                val yesterday = now - 86400000

                // Starter Transactions
                txDao.insertTransaction(
                    TransactionEntity(
                        userId = targetUserId,
                        invoiceNo = "INV-1001",
                        type = TransactionType.SALE,
                        amount = 4290.0,
                        paidAmount = 2790.0,
                        dueAmount = 1500.0,
                        partyId = c1,
                        partyName = "আব্দুর রহিম",
                        partyPhone = "01811-123456",
                        partyType = PartyType.CUSTOMER,
                        paymentMode = PaymentMode.CASH,
                        note = "১ বস্তা চাল ও ১ বোতল তেল",
                        timestamp = twoHoursAgo,
                        profitEstimate = 310.0
                    )
                )

                txDao.insertTransaction(
                    TransactionEntity(
                        userId = targetUserId,
                        invoiceNo = "INV-1002",
                        type = TransactionType.SALE,
                        amount = 1680.0,
                        paidAmount = 1680.0,
                        dueAmount = 0.0,
                        partyName = "নগদ ক্রেতা",
                        partyPhone = "",
                        partyType = PartyType.CUSTOMER,
                        paymentMode = PaymentMode.BKASH,
                        note = "২ বোতল সয়াবিন তেল",
                        timestamp = oneHourAgo,
                        profitEstimate = 120.0
                    )
                )

                txDao.insertTransaction(
                    TransactionEntity(
                        userId = targetUserId,
                        invoiceNo = "EXP-201",
                        type = TransactionType.EXPENSE,
                        amount = 500.0,
                        paidAmount = 500.0,
                        paymentMode = PaymentMode.CASH,
                        expenseCategory = ExpenseCategory.TRANSPORT,
                        note = "মাল ডেলিভারি রিকশা ভাড়া",
                        timestamp = now - 1800000
                    )
                )

                txDao.insertTransaction(
                    TransactionEntity(
                        userId = targetUserId,
                        invoiceNo = "PUR-301",
                        type = TransactionType.PURCHASE,
                        amount = 25000.0,
                        paidAmount = 12500.0,
                        dueAmount = 12500.0,
                        partyId = s1,
                        partyName = "মেঘনা গ্রুপ ডিলার",
                        partyPhone = "01711-345678",
                        partyType = PartyType.SUPPLIER,
                        paymentMode = PaymentMode.BANK,
                        note = "নতুন তেলের কার্টনের চালান",
                        timestamp = yesterday
                    )
                )

                // Starter sample messages
                chatDao.insertMessage(
                    ChatMessageEntity(
                        userId = targetUserId,
                        peerId = "@bhai_bhai_store",
                        peerName = "ভাই ভাই জেনারেল স্টোর",
                        peerAvatar = "avatar_2",
                        messageText = "আসসালামু আলাইকুম রাসেল ভাই, মিনিকেট চালের নতুন রেট কত?",
                        isOutgoing = false,
                        timestamp = now - 3600000 * 3
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        userId = targetUserId,
                        peerId = "@bhai_bhai_store",
                        peerName = "ভাই ভাই জেনারেল স্টোর",
                        peerAvatar = "avatar_2",
                        messageText = "ওয়ালাইকুম আসসালাম ভাই, বস্তা প্রতি ৩৪৫০ টাকা রেটে ডেলিভারি দেওয়া যাবে।",
                        isOutgoing = true,
                        timestamp = now - 3600000 * 2
                    )
                )
            }
        }
    }
}
