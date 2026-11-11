package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ShopyViewModel,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userProducts by viewModel.userProducts.collectAsState()
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmount by remember { mutableStateOf("") }
    var withdrawPhone by remember { mutableStateOf(currentUser.phone) }
    var withdrawMethod by remember { mutableStateOf(PaymentMethod.MPESA) }

    val tabs = listOf(
        "Mon Dressing (${userProducts.size})",
        "Favoris (${favoriteProducts.size})",
        "Mes Commandes (${orders.size})"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil ShopyExpress", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Déconnexion",
                            tint = ShopyCoral
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile Header Card
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = currentUser.name,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Certifié",
                                    tint = ShopyTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = "${currentUser.handle} • ${currentUser.city} (${currentUser.commune})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ShopyGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentUser.rating} (${currentUser.reviewsCount} avis favorables)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ShopyExpress Wallet Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ShopyTealContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Portefeuille ShopyExpress",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopyOnTealContainer
                                )
                                Text(
                                    text = "${currentUser.walletBalanceUsd.toInt()} $",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = ShopyTeal
                                    )
                                )
                                Text(
                                    text = "≈ ${(currentUser.walletBalanceUsd * 2800).toLong()} Francs Congolais",
                                    fontSize = 10.sp,
                                    color = ShopyOnTealContainer.copy(alpha = 0.8f)
                                )
                            }

                            Button(
                                onClick = { showWithdrawDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ShopyTeal),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("withdraw_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retirer", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ShopyTeal
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("profile_tab_$index")
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Dressing
                    if (userProducts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Checkroom,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Votre dressing est vide", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Publiez un vêtement ou accessoire pour commencer à vendre.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.Sell) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ShopyTeal)
                                ) {
                                    Text("Ajouter un article")
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(userProducts, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { viewModel.navigateTo(Screen.ProductDetail(product)) },
                                    onFavoriteClick = { viewModel.toggleFavorite(product.id) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Favorites
                    if (favoriteProducts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Aucun coup de cœur pour le moment", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Cliquez sur le cœur d'un article pour le retrouver ici.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(favoriteProducts, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { viewModel.navigateTo(Screen.ProductDetail(product)) },
                                    onFavoriteClick = { viewModel.toggleFavorite(product.id) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Orders
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders, key = { it.id }) { order ->
                            OrderItemCard(order = order)
                        }
                    }
                }
            }
        }
    }

    // Withdrawal Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Text("Retirer vers Mobile Money en RDC", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Solde disponible : ${currentUser.walletBalanceUsd.toInt()} $",
                        style = MaterialTheme.typography.bodySmall,
                        color = ShopyTeal,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("Montant en USD ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawPhone,
                        onValueChange = { withdrawPhone = it },
                        label = { Text("Numéro Mobile Money (+243...)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Opérateur RDC :", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(6.dp))

                    listOf(
                        PaymentMethod.MPESA,
                        PaymentMethod.AIRTEL_MONEY,
                        PaymentMethod.ORANGE_MONEY
                    ).forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = withdrawMethod == method,
                                onClick = { withdrawMethod = method }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(method.title, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmount.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            viewModel.withdrawFunds(amount, withdrawMethod, withdrawPhone)
                            showWithdrawDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopyTeal),
                    modifier = Modifier.testTag("confirm_withdraw_btn")
                ) {
                    Text("Valider le retrait")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun OrderItemCard(order: Order) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (order.status) {
                        OrderStatus.DELIVERED -> PriceGreen.copy(alpha = 0.15f)
                        OrderStatus.SHIPPED -> ShopyTealContainer
                        else -> ShopyCoralContainer
                    }
                ) {
                    Text(
                        text = order.status.labelFr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            OrderStatus.DELIVERED -> PriceGreen
                            OrderStatus.SHIPPED -> ShopyTeal
                            else -> ShopyCoral
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = order.productImageUrl,
                    contentDescription = order.productTitle,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productTitle,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = "Vendu par ${order.sellerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Total payé : ${order.totalUsd.toInt()} $ • ${order.paymentMethod.title}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = ShopyTeal)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = ShopyTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Code suivi : ${order.trackingCode} • ${order.buyerCity}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
