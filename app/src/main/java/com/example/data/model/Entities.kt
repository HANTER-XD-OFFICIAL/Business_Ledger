package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey
    val userId: String, // email, phone, or google_id
    val displayName: String,
    val username: String = "",
    val emailOrPhone: String,
    val authProvider: String = "GOOGLE", // "GOOGLE", "PHONE", "GUEST"
    val passwordHash: String = "",
    val businessName: String = "আমার ব্যবসা খাতা",
    val businessCategory: String = "মুদি ও পাইকারি",
    val bio: String = "পাইকারি ও খুচরা বিক্রয় কেন্দ্র। সততা ও বিশ্বস্ততার সাথে সেবা দিয়ে আসছি।",
    val address: String = "ঢাকা, বাংলাদেশ",
    val phone: String = "",
    val whatsapp: String = "",
    val telegram: String = "",
    val photoUrl: String = "",
    val avatarPreset: String = "avatar_1",
    val rating: Double = 4.9,
    val reviewCount: Int = 54,
    val totalSalesCount: Int = 185,
    val isVerified: Boolean = true,
    val preferredLanguage: String = "BN",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user",
    val invoiceNo: String = "",
    val type: TransactionType,
    val amount: Double,
    val paidAmount: Double = amount,
    val dueAmount: Double = 0.0,
    val partyId: Long? = null,
    val partyName: String = "",
    val partyPhone: String = "",
    val partyType: PartyType? = null,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val expenseCategory: ExpenseCategory? = null,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val productId: Long? = null,
    val productName: String = "",
    val quantity: Double = 0.0,
    val unitPrice: Double = 0.0,
    val profitEstimate: Double = 0.0
)

@Entity(tableName = "parties")
data class PartyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user",
    val name: String,
    val phone: String = "",
    val address: String = "",
    val type: PartyType,
    val currentBalance: Double = 0.0, // Positive: receivable from customer OR payable to supplier
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user",
    val name: String,
    val codeOrBarcode: String = "",
    val category: String = "সাধারণ",
    val buyPrice: Double = 0.0,
    val sellPrice: Double = 0.0,
    val stockQuantity: Double = 0.0,
    val unit: ProductUnit = ProductUnit.PIECE,
    val minStockAlert: Double = 5.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "business_profile")
data class BusinessProfileEntity(
    @PrimaryKey
    val userId: String = "default_user",
    val username: String = "@bismillah_traders",
    val businessName: String = "মেসার্স বিসমিল্লাহ ট্রেডার্স",
    val businessCategory: String = "মুদি ও পাইকারি",
    val ownerName: String = "ব্যবসায়ী",
    val phone: String = "01700000000",
    val whatsapp: String = "01700000000",
    val telegram: String = "t.me/HANTER_XD_OFFICIAL",
    val address: String = "চকবাজার, ঢাকা, বাংলাদেশ",
    val bio: String = "পাইকারি ও খুচরা বিক্রয় কেন্দ্র। সুলভ মূল্যে খাঁটি পণ্য সরবরাহ করি।",
    val photoUrl: String = "",
    val avatarPreset: String = "avatar_1",
    val rating: Double = 4.9,
    val reviewCount: Int = 54,
    val totalSalesCount: Int = 185,
    val isVerified: Boolean = true,
    val currencySymbol: String = "৳",
    val tagline: String = "সততা ও বিশ্বস্ততার প্রতীক"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user", // local user scope
    val peerId: String,                  // peer username or userId
    val peerName: String,
    val peerAvatar: String = "avatar_1",
    val messageText: String,
    val isOutgoing: Boolean,            // true if sent by user, false if received
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: String = "TEXT",   // "TEXT", "PAYMENT_REMINDER", "INVOICE", "PRODUCT_QUERY"
    val isRead: Boolean = true
)

// Data class for Public Directory Merchant
data class MerchantPublicProfile(
    val id: String,
    val username: String,
    val name: String,
    val shopName: String,
    val category: String,
    val bio: String,
    val address: String,
    val phone: String,
    val whatsapp: String,
    val telegram: String,
    val avatarPreset: String,
    val rating: Double,
    val reviewCount: Int,
    val totalSalesCount: Int,
    val isVerified: Boolean,
    val responseRate: String = "৯৯%",
    val featuredProducts: List<String> = emptyList()
)
