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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.BusinessViewModel
import com.example.viewmodel.DashboardSummary

@Composable
fun StockScreen(
    viewModel: BusinessViewModel,
    summary: DashboardSummary,
    profile: BusinessProfileEntity?,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.productSearchQuery.collectAsState()
    var onlyLowStock by remember { mutableStateOf(false) }
    var selectedProductForEdit by remember { mutableStateOf<ProductEntity?>(null) }

    val currency = profile?.currencySymbol ?: "৳"

    val filteredProducts = remember(products, searchQuery, onlyLowStock) {
        products.filter { prod ->
            val matchesQuery = searchQuery.isBlank() ||
                    prod.name.contains(searchQuery, ignoreCase = true) ||
                    prod.category.contains(searchQuery, ignoreCase = true) ||
                    prod.codeOrBarcode.contains(searchQuery, ignoreCase = true)
            val matchesLowStock = !onlyLowStock || prod.stockQuantity <= prod.minStockAlert
            matchesQuery && matchesLowStock
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("stock_screen")
    ) {
        // Valuation Summary Card
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "স্টকের মোট ক্রয়মূল্য",
                        amount = summary.totalStockCostValue,
                        currencySymbol = currency,
                        icon = Icons.Default.Inventory,
                        iconColor = TealSecondary,
                        containerColor = TealContainer.copy(alpha = 0.4f),
                        modifier = Modifier.weight(1f),
                        subtitle = "${products.size}টি মোট আইটেম"
                    )

                    MetricCard(
                        title = "সম্ভাব্য মোট বিক্রয়মূল্য",
                        amount = summary.totalStockSellValue,
                        currencySymbol = currency,
                        icon = Icons.Default.PointOfSale,
                        iconColor = EmeraldLight,
                        containerColor = EmeraldContainer.copy(alpha = 0.4f),
                        modifier = Modifier.weight(1f),
                        subtitle = "সম্ভাব্য লাভ: $currency ${(summary.totalStockSellValue - summary.totalStockCostValue).coerceAtLeast(0.0)}"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search & Filter & Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.productSearchQuery.value = it },
                        placeholder = { Text("পণ্যের নাম বা ক্যাটাগরি...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = onAddProduct,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.height(52.dp).testTag("add_product_screen_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন পণ্য")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !onlyLowStock,
                        onClick = { onlyLowStock = false },
                        label = { Text("সকল পণ্য (${products.size})") }
                    )
                    FilterChip(
                        selected = onlyLowStock,
                        onClick = { onlyLowStock = true },
                        label = { Text("লো-স্টক পণ্য (${summary.lowStockCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonContainer,
                            selectedLabelColor = CrimsonExpense
                        )
                    )
                }
            }
        }

        // Product list
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "কোনো পণ্য পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onAddProduct) {
                        Text("পণ্য যোগ করুন")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCardItem(
                        product = product,
                        currencySymbol = currency,
                        onEdit = { selectedProductForEdit = product },
                        onAdjustStock = { delta -> viewModel.adjustStock(product.id, delta) }
                    )
                }
            }
        }
    }

    // Edit Product Dialog
    selectedProductForEdit?.let { product ->
        AddEditProductDialog(
            initialProduct = product,
            onDismiss = { selectedProductForEdit = null },
            onSave = { updated ->
                viewModel.saveProduct(updated)
                selectedProductForEdit = null
            }
        )
    }
}
