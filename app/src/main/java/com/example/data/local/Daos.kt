package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts ORDER BY lastLoginAt DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE emailOrPhone = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE username LIKE '%' || :query || '%' OR businessName LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserAccountEntity)

    @Query("UPDATE user_accounts SET lastLoginAt = :timestamp WHERE userId = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_accounts SET preferredLanguage = :language WHERE userId = :userId")
    suspend fun updateLanguage(userId: String, language: String)

    @Delete
    suspend fun deleteUser(user: UserAccountEntity)

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserCount(): Int
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE userId = :userId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsBetween(userId: String, startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY timestamp DESC")
    fun getTransactionsByType(userId: String, type: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND partyId = :partyId ORDER BY timestamp DESC")
    fun getTransactionsByPartyId(userId: String, partyId: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId")
    suspend fun getTransactionCount(userId: String): Int

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun clearUserTransactions(userId: String)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}

@Dao
interface PartyDao {
    @Query("SELECT * FROM parties WHERE userId = :userId ORDER BY lastUpdated DESC")
    fun getPartiesForUser(userId: String): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties ORDER BY lastUpdated DESC")
    fun getAllParties(): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE userId = :userId AND type = :type ORDER BY currentBalance DESC, name ASC")
    fun getPartiesByType(userId: String, type: PartyType): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE id = :id LIMIT 1")
    suspend fun getPartyById(id: Long): PartyEntity?

    @Query("SELECT * FROM parties WHERE id = :id LIMIT 1")
    fun getPartyFlowById(id: Long): Flow<PartyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: PartyEntity): Long

    @Update
    suspend fun updateParty(party: PartyEntity)

    @Delete
    suspend fun deleteParty(party: PartyEntity)

    @Query("DELETE FROM parties WHERE id = :id")
    suspend fun deletePartyById(id: Long)

    @Query("UPDATE parties SET currentBalance = currentBalance + :delta, lastUpdated = :timestamp WHERE id = :id")
    suspend fun adjustPartyBalance(id: Long, delta: Double, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM parties WHERE userId = :userId")
    suspend fun getPartyCount(userId: String): Int

    @Query("DELETE FROM parties WHERE userId = :userId")
    suspend fun clearUserParties(userId: String)

    @Query("DELETE FROM parties")
    suspend fun clearAll()
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE userId = :userId ORDER BY name ASC")
    fun getProductsForUser(userId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE userId = :userId AND stockQuantity <= minStockAlert ORDER BY stockQuantity ASC")
    fun getLowStockProducts(userId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :delta, lastUpdated = :timestamp WHERE id = :id")
    suspend fun adjustProductStock(id: Long, delta: Double, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM products WHERE userId = :userId")
    suspend fun getProductCount(userId: String): Int

    @Query("DELETE FROM products WHERE userId = :userId")
    suspend fun clearUserProducts(userId: String)

    @Query("DELETE FROM products")
    suspend fun clearAll()
}

@Dao
interface BusinessProfileDao {
    @Query("SELECT * FROM business_profile WHERE userId = :userId LIMIT 1")
    fun getProfileFlow(userId: String): Flow<BusinessProfileEntity?>

    @Query("SELECT * FROM business_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: String): BusinessProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: BusinessProfileEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE userId = :userId AND peerId = :peerId ORDER BY timestamp ASC")
    fun getMessagesForConversation(userId: String, peerId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllMessagesForUser(userId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE userId = :userId AND peerId = :peerId")
    suspend fun clearConversation(userId: String, peerId: String)
}
