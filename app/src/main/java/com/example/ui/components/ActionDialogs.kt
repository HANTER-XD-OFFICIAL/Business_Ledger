package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleDialog(
    parties: List<PartyEntity>,
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onSaveSale: (
        party: PartyEntity?,
        customerName: String,
        customerPhone: String,
        product: ProductEntity?,
        quantity: Double,
        totalAmount: Double,
        paidAmount: Double,
        paymentMode: PaymentMode,
        note: String
    ) -> Unit
) {
    var selectedParty by remember { mutableStateOf<PartyEntity?>(null) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var quantityStr by remember { mutableStateOf("1") }
    var totalAmountStr by remember { mutableStateOf("") }
    var paidAmountStr by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var note by remember { mutableStateOf("") }
    var isFullPaid by remember { mutableStateOf(true) }

    val customerList = remember(parties) { parties.filter { it.type == PartyType.CUSTOMER } }

    fun updateCalculatedTotal() {
        val qty = quantityStr.toDoubleOrNull() ?: 1.0
        val price = selectedProduct?.sellPrice ?: 0.0
        if (price > 0) {
            val total = (price * qty).toString()
            totalAmountStr = total
            if (isFullPaid) {
                paidAmountStr = total
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🟢 নতুন বিক্রয় এন্ট্রি (Sale)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Product Selection dropdown/picker if available
                if (products.isNotEmpty()) {
                    Text(
                        text = "পণ্য নির্বাচন করুন (ঐচ্ছিক):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedProduct == null,
                            onClick = {
                                selectedProduct = null
                                totalAmountStr = ""
                                paidAmountStr = ""
                            },
                            label = { Text("সরাসরি টাকা") }
                        )
                        products.forEach { prod ->
                            FilterChip(
                                selected = selectedProduct?.id == prod.id,
                                onClick = {
                                    selectedProduct = prod
                                    updateCalculatedTotal()
                                },
                                label = { Text("${prod.name} (৳${prod.sellPrice})") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (selectedProduct != null) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = {
                            quantityStr = it
                            updateCalculatedTotal()
                        },
                        label = { Text("পরিমাণ (${selectedProduct?.unit?.labelBn ?: "টি"})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Total Amount
                OutlinedTextField(
                    value = totalAmountStr,
                    onValueChange = {
                        totalAmountStr = it
                        if (isFullPaid) paidAmountStr = it
                    },
                    label = { Text("মোট বিক্রয় মূল্য (৳ Total Amount) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("sale_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Full paid vs Due toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "সম্পূর্ণ নগদ পরিশোধ?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isFullPaid,
                        onCheckedChange = {
                            isFullPaid = it
                            if (it) {
                                paidAmountStr = totalAmountStr
                            } else {
                                paidAmountStr = "0"
                            }
                        }
                    )
                }

                if (!isFullPaid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = paidAmountStr,
                        onValueChange = { paidAmountStr = it },
                        label = { Text("নগদ জমা দেওয়া হয়েছে (৳ Paid)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    val tot = totalAmountStr.toDoubleOrNull() ?: 0.0
                    val paid = paidAmountStr.toDoubleOrNull() ?: 0.0
                    val due = (tot - paid).coerceAtLeast(0.0)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "বাকি থাকবে: ৳ $due",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonExpense
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Customer Selection
                Text(
                    text = "কাস্টমার নির্বাচন করুন (ঐচ্ছিক):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (customerList.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedParty == null,
                            onClick = { selectedParty = null },
                            label = { Text("নতুন / নগদ কাস্টমার") }
                        )
                        customerList.forEach { cust ->
                            FilterChip(
                                selected = selectedParty?.id == cust.id,
                                onClick = {
                                    selectedParty = cust
                                    customerName = cust.name
                                    customerPhone = cust.phone
                                },
                                label = { Text(cust.name) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedParty == null) {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("কাস্টমারের নাম") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("মোবাইল নাম্বার") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Mode Chips
                Text(
                    text = "পেমেন্ট মাধ্যম:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMode.entries.forEach { mode ->
                        FilterChip(
                            selected = paymentMode == mode,
                            onClick = { paymentMode = mode },
                            label = { Text(mode.labelBn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("নোট বা পণ্যের বিবরণ") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val total = totalAmountStr.toDoubleOrNull() ?: 0.0
                        if (total <= 0) return@Button
                        val paid = if (isFullPaid) total else (paidAmountStr.toDoubleOrNull() ?: 0.0)
                        val qty = quantityStr.toDoubleOrNull() ?: 1.0

                        onSaveSale(
                            selectedParty,
                            customerName,
                            customerPhone,
                            selectedProduct,
                            qty,
                            total,
                            paid,
                            paymentMode,
                            note
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_sale_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("বিক্রয় নিশ্চিত করুন (Save Sale)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddPurchaseDialog(
    parties: List<PartyEntity>,
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onSavePurchase: (
        party: PartyEntity?,
        supplierName: String,
        supplierPhone: String,
        product: ProductEntity?,
        quantity: Double,
        totalAmount: Double,
        paidAmount: Double,
        paymentMode: PaymentMode,
        note: String
    ) -> Unit
) {
    var selectedParty by remember { mutableStateOf<PartyEntity?>(null) }
    var supplierName by remember { mutableStateOf("") }
    var supplierPhone by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var quantityStr by remember { mutableStateOf("1") }
    var totalAmountStr by remember { mutableStateOf("") }
    var paidAmountStr by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var note by remember { mutableStateOf("") }
    var isFullPaid by remember { mutableStateOf(true) }

    val supplierList = remember(parties) { parties.filter { it.type == PartyType.SUPPLIER } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔵 নতুন পণ্য ক্রয় এন্ট্রি (Purchase)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TealSecondary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Product selection
                if (products.isNotEmpty()) {
                    Text(
                        text = "ক্রয়কৃত পণ্য (স্টক বাড়বে):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedProduct == null,
                            onClick = { selectedProduct = null },
                            label = { Text("অন্যান্য মাল") }
                        )
                        products.forEach { prod ->
                            FilterChip(
                                selected = selectedProduct?.id == prod.id,
                                onClick = {
                                    selectedProduct = prod
                                    val price = prod.buyPrice
                                    val qty = quantityStr.toDoubleOrNull() ?: 1.0
                                    if (price > 0) {
                                        val total = (price * qty).toString()
                                        totalAmountStr = total
                                        if (isFullPaid) paidAmountStr = total
                                    }
                                },
                                label = { Text(prod.name) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (selectedProduct != null) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = {
                            quantityStr = it
                            val price = selectedProduct?.buyPrice ?: 0.0
                            val qty = it.toDoubleOrNull() ?: 1.0
                            if (price > 0) {
                                val total = (price * qty).toString()
                                totalAmountStr = total
                                if (isFullPaid) paidAmountStr = total
                            }
                        },
                        label = { Text("পরিমাণ (${selectedProduct?.unit?.labelBn ?: "টি"})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = totalAmountStr,
                    onValueChange = {
                        totalAmountStr = it
                        if (isFullPaid) paidAmountStr = it
                    },
                    label = { Text("মোট ক্রয় মূল্য (৳ Total Purchase) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("purchase_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("সম্পূর্ণ পরিশোধ করা হয়েছে?", fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isFullPaid,
                        onCheckedChange = {
                            isFullPaid = it
                            if (it) paidAmountStr = totalAmountStr else paidAmountStr = "0"
                        }
                    )
                }

                if (!isFullPaid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = paidAmountStr,
                        onValueChange = { paidAmountStr = it },
                        label = { Text("নগদ পরিশোধ করা হয়েছে (৳ Paid)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Supplier selection
                Text(
                    text = "সাপ্লায়ার / মহাজন:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (supplierList.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedParty == null,
                            onClick = { selectedParty = null },
                            label = { Text("নতুন সাপ্লায়ার") }
                        )
                        supplierList.forEach { supp ->
                            FilterChip(
                                selected = selectedParty?.id == supp.id,
                                onClick = {
                                    selectedParty = supp
                                    supplierName = supp.name
                                    supplierPhone = supp.phone
                                },
                                label = { Text(supp.name) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedParty == null) {
                    OutlinedTextField(
                        value = supplierName,
                        onValueChange = { supplierName = it },
                        label = { Text("সাপ্লায়ারের নাম / ডিলার") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = supplierPhone,
                        onValueChange = { supplierPhone = it },
                        label = { Text("মোবাইল নাম্বার") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment mode
                Text("পেমেন্ট মাধ্যম:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMode.entries.forEach { mode ->
                        FilterChip(
                            selected = paymentMode == mode,
                            onClick = { paymentMode = mode },
                            label = { Text(mode.labelBn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("চালান বা নোট") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val total = totalAmountStr.toDoubleOrNull() ?: 0.0
                        if (total <= 0) return@Button
                        val paid = if (isFullPaid) total else (paidAmountStr.toDoubleOrNull() ?: 0.0)
                        val qty = quantityStr.toDoubleOrNull() ?: 1.0

                        onSavePurchase(
                            selectedParty,
                            supplierName,
                            supplierPhone,
                            selectedProduct,
                            qty,
                            total,
                            paid,
                            paymentMode,
                            note
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_purchase_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ক্রয় হিসাব সেভ করুন (Save Purchase)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSaveExpense: (amount: Double, category: ExpenseCategory, paymentMode: PaymentMode, note: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.SHOP_RENT) }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var note by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔴 দোকানের খরচ এন্ট্রি (Expense)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonExpense
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("খরচের পরিমাণ (৳ Amount) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("expense_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("খরচের খাত / ক্যাটাগরি:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ExpenseCategory.entries.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat.labelBn, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("পেমেন্ট মাধ্যম:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMode.entries.filter { it != PaymentMode.DUE }.forEach { mode ->
                        FilterChip(
                            selected = paymentMode == mode,
                            onClick = { paymentMode = mode },
                            label = { Text(mode.labelBn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("খরচের বিবরণ (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (amount <= 0) return@Button
                        onSaveExpense(amount, selectedCategory, paymentMode, note)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_expense_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonExpense)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("খরচ সেভ করুন (Save Expense)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CollectDueDialog(
    party: PartyEntity,
    onDismiss: () -> Unit,
    onCollect: (amount: Double, paymentMode: PaymentMode, note: String) -> Unit
) {
    var amountStr by remember { mutableStateOf(party.currentBalance.toString()) }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var note by remember { mutableStateOf("বাকি টাকা জমা পেয়েছি") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰 কাস্টমার বাকি আদায়",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "কাস্টমার: ${party.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnEmeraldContainer
                        )
                        Text(
                            text = "বর্তমান মোট বাকি: ৳ ${party.currentBalance}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrimsonExpense,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("জমা টাকার পরিমাণ (৳ Amount) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("collect_due_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("পেমেন্ট মাধ্যম:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(PaymentMode.CASH, PaymentMode.BKASH, PaymentMode.NAGAD, PaymentMode.BANK).forEach { mode ->
                        FilterChip(
                            selected = paymentMode == mode,
                            onClick = { paymentMode = mode },
                            label = { Text(mode.labelBn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("বিবরণ / নোট") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (amount <= 0) return@Button
                        onCollect(amount, paymentMode, note)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_collect_due_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("টাকা জমা নিশ্চিত করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PaySupplierDialog(
    party: PartyEntity,
    onDismiss: () -> Unit,
    onPay: (amount: Double, paymentMode: PaymentMode, note: String) -> Unit
) {
    var amountStr by remember { mutableStateOf(party.currentBalance.toString()) }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var note by remember { mutableStateOf("সাপ্লায়ারকে দেনা পরিশোধ করা হলো") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💳 সাপ্লায়ার দেনা পরিশোধ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AmberDue
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = AmberContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "সাপ্লায়ার: ${party.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "বর্তমান দেনা বাকি: ৳ ${party.currentBalance}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrimsonExpense,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("পরিশোধের পরিমাণ (৳ Amount) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("পেমেন্ট মাধ্যম:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(PaymentMode.CASH, PaymentMode.BKASH, PaymentMode.NAGAD, PaymentMode.BANK).forEach { mode ->
                        FilterChip(
                            selected = paymentMode == mode,
                            onClick = { paymentMode = mode },
                            label = { Text(mode.labelBn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("বিবরণ / নোট") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (amount <= 0) return@Button
                        onPay(amount, paymentMode, note)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberDue)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("দেনা পরিশোধ সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddEditPartyDialog(
    initialParty: PartyEntity?,
    defaultType: PartyType = PartyType.CUSTOMER,
    onDismiss: () -> Unit,
    onSave: (PartyEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialParty?.name ?: "") }
    var phone by remember { mutableStateOf(initialParty?.phone ?: "") }
    var address by remember { mutableStateOf(initialParty?.address ?: "") }
    var partyType by remember { mutableStateOf(initialParty?.type ?: defaultType) }
    var balanceStr by remember { mutableStateOf(initialParty?.currentBalance?.toString() ?: "0") }
    var notes by remember { mutableStateOf(initialParty?.notes ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialParty == null) "নতুন কাস্টমার / সাপ্লায়ার" else "তথ্য পরিবর্তন করুন",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Party Type toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = partyType == PartyType.CUSTOMER,
                        onClick = { partyType = PartyType.CUSTOMER },
                        label = { Text("কাস্টমার (ক্রেতা)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = partyType == PartyType.SUPPLIER,
                        onClick = { partyType = PartyType.SUPPLIER },
                        label = { Text("সাপ্লায়ার (মহাজন)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("নাম (Name) *") },
                    modifier = Modifier.fillMaxWidth().testTag("party_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নাম্বার") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("ঠিকানা (Address)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = balanceStr,
                    onValueChange = { balanceStr = it },
                    label = { Text(if (partyType == PartyType.CUSTOMER) "প্রারম্ভিক বাকি পাওনা (৳)" else "প্রারম্ভিক দেনা (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("মন্তব্য / নোট") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        val balance = balanceStr.toDoubleOrNull() ?: 0.0
                        val party = (initialParty ?: PartyEntity(name = name, type = partyType)).copy(
                            name = name,
                            phone = phone,
                            address = address,
                            type = partyType,
                            currentBalance = balance,
                            notes = notes,
                            lastUpdated = System.currentTimeMillis()
                        )
                        onSave(party)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_party_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("সেভ করুন (Save)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddEditProductDialog(
    initialProduct: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "মুদি") }
    var buyPriceStr by remember { mutableStateOf(initialProduct?.buyPrice?.toString() ?: "") }
    var sellPriceStr by remember { mutableStateOf(initialProduct?.sellPrice?.toString() ?: "") }
    var stockQuantityStr by remember { mutableStateOf(initialProduct?.stockQuantity?.toString() ?: "0") }
    var unit by remember { mutableStateOf(initialProduct?.unit ?: ProductUnit.PIECE) }
    var minStockLimitStr by remember { mutableStateOf(initialProduct?.minStockAlert?.toString() ?: "5") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialProduct == null) "নতুন পণ্য যোগ করুন" else "পণ্যের তথ্য পরিবর্তন",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("পণ্যের নাম *") },
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("ক্যাটাগরি") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = buyPriceStr,
                        onValueChange = { buyPriceStr = it },
                        label = { Text("ক্রয় মূল্য (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sellPriceStr,
                        onValueChange = { sellPriceStr = it },
                        label = { Text("বিক্রয় মূল্য (৳) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stockQuantityStr,
                        onValueChange = { stockQuantityStr = it },
                        label = { Text("বর্তমান স্টক") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minStockLimitStr,
                        onValueChange = { minStockLimitStr = it },
                        label = { Text("লো-স্টক এলার্ট") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("একক (Unit):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProductUnit.entries.forEach { u ->
                        FilterChip(
                            selected = unit == u,
                            onClick = { unit = u },
                            label = { Text(u.labelBn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        val buyPrice = buyPriceStr.toDoubleOrNull() ?: 0.0
                        val sellPrice = sellPriceStr.toDoubleOrNull() ?: 0.0
                        val stock = stockQuantityStr.toDoubleOrNull() ?: 0.0
                        val minStock = minStockLimitStr.toDoubleOrNull() ?: 5.0

                        val prod = (initialProduct ?: ProductEntity(name = name)).copy(
                            name = name,
                            category = category.ifBlank { "সাধারণ" },
                            buyPrice = buyPrice,
                            sellPrice = sellPrice,
                            stockQuantity = stock,
                            unit = unit,
                            minStockAlert = minStock,
                            lastUpdated = System.currentTimeMillis()
                        )
                        onSave(prod)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_product_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("পণ্য সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
