package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BuyerProtectionBadge
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopyViewModel

@Composable
fun HomeScreen(
    viewModel: ShopyViewModel,
    modifier: Modifier = Modifier
) {
    val filterState by viewModel.filterState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()

    val drcCities = listOf(
        "Tout RDC",
        "Kinshasa",
        "Lubumbashi",
        "Goma",
        "Kolwezi",
        "Kisangani",
        "Matadi"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_feed_grid"),
        contentPadding = PaddingValues(bottom = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Top Bar
        item(span = { GridItemSpan(2) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = ShopyTeal,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Shopy",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = ShopyTeal
                                    )
                                )
                                Text(
                                    text = "Express",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = ShopyCoral
                                    )
                                )
                                Surface(
                                    color = ShopyGoldContainer,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Text(
                                        text = "RDC 🇨🇩",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopyOnTealContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Vide-dressing & bonnes affaires",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Search) },
                            modifier = Modifier.testTag("home_search_icon_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Profile) },
                            modifier = Modifier.testTag("home_profile_icon_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar Preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.Search) }
                        .testTag("home_search_bar_clickable")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Rechercher une robe, pagne Woodin, sneakers...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Hero Banner
        item(span = { GridItemSpan(2) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_hero),
                    contentDescription = "Bannière ShopyExpress",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Vendez ce que vous ne portez plus !",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Kinshasa • Lubumbashi • Goma • Paiement Mobile Money & Cash",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Sell) },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopyCoral),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("banner_sell_cta")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Vendre maintenant", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // DRC Cities Chips
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = "Villes",
                        tint = ShopyTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filtrer par Ville en RDC",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(drcCities) { city ->
                        val isSelected = filterState.city == city
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCity(city) },
                            label = { Text(city, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShopyTeal,
                                selectedLabelColor = Color.White
                            ),
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else null,
                            modifier = Modifier.testTag("city_chip_$city")
                        )
                    }
                }
            }
        }

        // Categories Row
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "Rayons populaires",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = filterState.categoryId == cat.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(cat.id) },
                            label = { Text(cat.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShopyTealContainer,
                                selectedLabelColor = ShopyOnTealContainer
                            ),
                            modifier = Modifier.testTag("cat_chip_${cat.id}")
                        )
                    }
                }
            }
        }

        // Buyer Protection Info
        item(span = { GridItemSpan(2) }) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                BuyerProtectionBadge()
            }
        }

        // Section Title: "Pépites & Articles Récents"
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Articles en vente (${filteredProducts.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (filterState.city != "Tout RDC" || filterState.categoryId != "all") {
                    TextButton(onClick = { viewModel.resetFilters() }) {
                        Text("Réinitialiser", fontSize = 12.sp, color = ShopyCoral)
                    }
                }
            }
        }

        // Product Cards Grid
        items(filteredProducts, key = { it.id }) { product ->
            Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                ProductCard(
                    product = product,
                    onClick = { viewModel.navigateTo(Screen.ProductDetail(product)) },
                    onFavoriteClick = { viewModel.toggleFavorite(product.id) }
                )
            }
        }
    }
}
