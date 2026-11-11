package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.components.BuyerProtectionBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: ShopyViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showOfferDialog by remember { mutableStateOf(false) }
    var customOfferPrice by remember { mutableStateOf("") }

    val products by viewModel.products.collectAsState()
    val currentProduct = products.find { it.id == product.id } ?: product

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentProduct.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFavorite(currentProduct.id) },
                        modifier = Modifier.testTag("detail_fav_btn")
                    ) {
                        Icon(
                            imageVector = if (currentProduct.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (currentProduct.isFavorite) HeartRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Make Offer Button
                    OutlinedButton(
                        onClick = { showOfferDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("make_offer_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ShopyTeal
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Faire une offre", fontWeight = FontWeight.Bold)
                    }

                    // Buy Now Button
                    Button(
                        onClick = {
                            viewModel.navigateTo(Screen.Checkout(currentProduct, currentProduct.priceUsd))
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("buy_now_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShopyCoral
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Acheter ${currentProduct.formattedPriceUsd}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Main Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(Color(0xFFEEEEEE))
            ) {
                AsyncImage(
                    model = currentProduct.imageUrl,
                    contentDescription = currentProduct.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Condition chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = currentProduct.condition,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Price & Title Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentProduct.formattedPriceUsd,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = ShopyTeal
                            )
                        )
                        Text(
                            text = "Soit environ ${currentProduct.formattedPriceCdf}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    if (currentProduct.originalPriceUsd != null) {
                        Surface(
                            color = ShopyCoralContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Prix boutique : ${currentProduct.originalPriceUsd.toInt()} $",
                                color = ShopyOnCoralContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = currentProduct.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                BuyerProtectionBadge()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Specs Table
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Détails de l'article",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SpecItem(label = "Marque", value = currentProduct.brand)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SpecItem(label = "Taille", value = currentProduct.size)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SpecItem(label = "État", value = currentProduct.condition)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SpecItem(label = "Emplacement", value = "${currentProduct.city} (${currentProduct.commune})")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SpecItem(label = "Frais de port estimés", value = "2.00 $ (Livraison Express RDC)")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentProduct.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Seller Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Vendeur",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = currentProduct.sellerAvatarUrl,
                            contentDescription = currentProduct.sellerName,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentProduct.sellerName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Certifié",
                                    tint = ShopyTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ShopyGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${currentProduct.sellerRating} (${currentProduct.sellerSalesCount} ventes)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Basé(e) à ${currentProduct.city}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val conv = viewModel.getConversation("conv_1")
                                if (conv != null) {
                                    viewModel.navigateTo(Screen.ChatDetail(conv.id))
                                } else {
                                    viewModel.navigateTo(Screen.Messages)
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Contacter", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Offer Dialog
    if (showOfferDialog) {
        val minus10 = (currentProduct.priceUsd * 0.9).toInt()
        val minus20 = (currentProduct.priceUsd * 0.8).toInt()

        AlertDialog(
            onDismissRequest = { showOfferDialog = false },
            title = {
                Text(
                    text = "Faire une offre de prix",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Prix affiché : ${currentProduct.formattedPriceUsd}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Proposez un montant raisonnable au vendeur.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuggestionChip(
                            onClick = { customOfferPrice = "$minus10" },
                            label = { Text("-10% ($minus10 $)") }
                        )
                        SuggestionChip(
                            onClick = { customOfferPrice = "$minus20" },
                            label = { Text("-20% ($minus20 $)") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customOfferPrice,
                        onValueChange = { customOfferPrice = it },
                        label = { Text("Votre offre en USD ($)") },
                        placeholder = { Text("Ex: 20") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("offer_input_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = customOfferPrice.toDoubleOrNull()
                        if (price != null && price > 0) {
                            viewModel.makeOffer(currentProduct, price)
                            showOfferDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopyTeal),
                    modifier = Modifier.testTag("send_offer_confirm_btn")
                ) {
                    Text("Envoyer l'offre")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfferDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
