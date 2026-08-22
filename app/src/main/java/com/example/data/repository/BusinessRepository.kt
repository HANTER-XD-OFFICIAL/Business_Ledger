package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class BusinessRepository(
    private val db: AppDatabase
) {
    private val userDao = db.userAccountDao()
    private val transactionDao = db.transactionDao()
    private val partyDao = db.partyDao()
    private val productDao = db.productDao()
    private val profileDao = db.businessProfileDao()
    private val chatDao = db.chatMessageDao()

    // Users
    val allUsers: Flow<List<UserAccountEntity>> = userDao.getAllUsers()

    suspend fun getUserById(userId: String): UserAccountEntity? = userDao.getUserById(userId)
    suspend fun getUserByIdentifier(identifier: String): UserAccountEntity? = userDao.getUserByIdentifier(identifier)
    suspend fun getUserByUsername(username: String): UserAccountEntity? = userDao.getUserByUsername(username)
    fun searchUsers(query: String): Flow<List<UserAccountEntity>> = userDao.searchUsers(query)
    suspend fun saveUser(user: UserAccountEntity) = userDao.insertOrUpdate(user)
    suspend fun updateLastLogin(userId: String) = userDao.updateLastLogin(userId)
    suspend fun updateLanguage(userId: String, lang: String) = userDao.updateLanguage(userId, lang)

    // User-scoped streams
    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsForUser(userId)

    fun getPartiesForUser(userId: String): Flow<List<PartyEntity>> =
        partyDao.getPartiesForUser(userId)

    fun getPartiesByTypeForUser(userId: String, type: PartyType): Flow<List<PartyEntity>> =
        partyDao.getPartiesByType(userId, type)

    fun getProductsForUser(userId: String): Flow<List<ProductEntity>> =
        productDao.getProductsForUser(userId)

    fun getLowStockForUser(userId: String): Flow<List<ProductEntity>> =
        productDao.getLowStockProducts(userId)

    fun getProfileForUser(userId: String): Flow<BusinessProfileEntity?> =
        profileDao.getProfileFlow(userId)

    suspend fun getProfileDirect(userId: String): BusinessProfileEntity? =
        profileDao.getProfile(userId)

    // Chat
    fun getMessagesForConversation(userId: String, peerId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForConversation(userId, peerId)

    fun getAllMessagesForUser(userId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getAllMessagesForUser(userId)

    suspend fun sendChatMessage(message: ChatMessageEntity): Long =
        chatDao.insertMessage(message)

    suspend fun deleteChatMessage(message: ChatMessageEntity) =
        chatDao.deleteMessage(message)

    suspend fun clearConversation(userId: String, peerId: String) =
        chatDao.clearConversation(userId, peerId)

    suspend fun addTransaction(tx: TransactionEntity): Long {
        val txId = transactionDao.insertTransaction(tx)

        // Adjust Party Balance
        if (tx.partyId != null && tx.partyId > 0) {
            when (tx.type) {
                TransactionType.SALE -> {
                    if (tx.dueAmount > 0) {
                        partyDao.adjustPartyBalance(tx.partyId, tx.dueAmount)
                    }
                }
                TransactionType.PURCHASE -> {
                    if (tx.dueAmount > 0) {
                        partyDao.adjustPartyBalance(tx.partyId, tx.dueAmount)
                    }
                }
                TransactionType.DUE_COLLECTION -> {
                    // Customer pays money -> reduces customer due
                    partyDao.adjustPartyBalance(tx.partyId, -tx.amount)
                }
                TransactionType.DUE_PAYMENT -> {
                    // We pay supplier -> reduces supplier payable
                    partyDao.adjustPartyBalance(tx.partyId, -tx.amount)
                }
                else -> {}
            }
        }

        // Adjust Product Stock
        if (tx.productId != null && tx.productId > 0 && tx.quantity > 0) {
            when (tx.type) {
                TransactionType.SALE -> {
                    productDao.adjustProductStock(tx.productId, -tx.quantity)
                }
                TransactionType.PURCHASE -> {
                    productDao.adjustProductStock(tx.productId, tx.quantity)
                }
                else -> {}
            }
        }

        return txId
    }

    suspend fun deleteTransaction(tx: TransactionEntity) {
        // Revert party balance
        if (tx.partyId != null && tx.partyId > 0) {
            when (tx.type) {
                TransactionType.SALE -> {
                    if (tx.dueAmount > 0) {
                        partyDao.adjustPartyBalance(tx.partyId, -tx.dueAmount)
                    }
                }
                TransactionType.PURCHASE -> {
                    if (tx.dueAmount > 0) {
                        partyDao.adjustPartyBalance(tx.partyId, -tx.dueAmount)
                    }
                }
                TransactionType.DUE_COLLECTION -> {
                    partyDao.adjustPartyBalance(tx.partyId, tx.amount)
                }
                TransactionType.DUE_PAYMENT -> {
                    partyDao.adjustPartyBalance(tx.partyId, tx.amount)
                }
                else -> {}
            }
        }

        // Revert stock
        if (tx.productId != null && tx.productId > 0 && tx.quantity > 0) {
            when (tx.type) {
                TransactionType.SALE -> productDao.adjustProductStock(tx.productId, tx.quantity)
                TransactionType.PURCHASE -> productDao.adjustProductStock(tx.productId, -tx.quantity)
                else -> {}
            }
        }

        transactionDao.deleteTransaction(tx)
    }

    // Party CRUD
    suspend fun addParty(party: PartyEntity): Long = partyDao.insertParty(party)
    suspend fun updateParty(party: PartyEntity) = partyDao.updateParty(party)
    suspend fun deleteParty(party: PartyEntity) = partyDao.deleteParty(party)

    // Product CRUD
    suspend fun addProduct(product: ProductEntity): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)
    suspend fun adjustProductStock(productId: Long, delta: Double) = productDao.adjustProductStock(productId, delta)

    // Profile
    suspend fun updateProfile(profile: BusinessProfileEntity) = profileDao.insertOrUpdate(profile)

    // Clear / Reset per user
    suspend fun clearUserData(userId: String) {
        transactionDao.clearUserTransactions(userId)
        partyDao.clearUserParties(userId)
        productDao.clearUserProducts(userId)
    }

    suspend fun resetUserWithSampleData(userId: String) {
        clearUserData(userId)
        AppDatabase.populateStarterData(db, userId)
    }
}
