package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: ShopyViewModel,
    modifier: Modifier = Modifier
) {
    val filterState by viewModel.filterState.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    val drcCities = listOf("Tout RDC", "Kinshasa", "Lubumbashi", "Goma", "Kolwezi")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("search_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }

                    OutlinedTextField(
                        value = filterState.query,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Rechercher pagne, veste, Nike, iPhone...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = ShopyTeal
                            )
                        },
                        trailingIcon = {
                            if (filterState.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Effacer"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier.testTag("open_filters_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filtres",
                            tint = ShopyTeal
                        )
                    }
                }

                // Horizontal City Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(drcCities) { city ->
                        val isSelected = filterState.city == city
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCity(city) },
                            label = { Text(city, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShopyTeal,
                                selectedLabelColor = Color.White
                            )
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Results Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} résultat(s) trouvé(s)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                if (filterState.query.isNotBlank() || filterState.city != "Tout RDC" || filterState.categoryId != "all") {
                    TextButton(onClick = { viewModel.resetFilters() }) {
                        Text("Tout effacer", fontSize = 12.sp, color = ShopyCoral)
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aucun article trouvé",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Essayez un autre mot-clé ou élargissez la ville en RDC.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopyTeal)
                        ) {
                            Text("Voir tous les articles")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { viewModel.navigateTo(Screen.ProductDetail(product)) },
                            onFavoriteClick = { viewModel.toggleFavorite(product.id) }
                        )
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Filtres de recherche",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Price Slider
                Text(
                    text = "Prix maximum : ${filterState.maxPriceUsd.toInt()} $ (≈ ${(filterState.maxPriceUsd * 2800).toLong()} FC)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Slider(
                    value = filterState.maxPriceUsd,
                    onValueChange = { viewModel.updateFilters(it, filterState.condition, filterState.sortBy) },
                    valueRange = 5f..500f,
                    colors = SliderDefaults.colors(
                        thumbColor = ShopyTeal,
                        activeTrackColor = ShopyTeal
                    ),
                    modifier = Modifier.testTag("price_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Condition Selector
                Text(
                    text = "État de l'article",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                val conditions = listOf("Tout", "Neuf avec étiquette", "Très bon état", "Bon état")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    conditions.forEach { cond ->
                        val isSelected = filterState.condition == cond
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateFilters(filterState.maxPriceUsd, cond, filterState.sortBy) },
                            label = { Text(cond, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShopyTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sort By
                Text(
                    text = "Trier par",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val sorts = listOf(
                        "recommandes" to "Populaires",
                        "prix_asc" to "Prix croissant",
                        "prix_desc" to "Prix décroissant",
                        "recents" to "Plus récents"
                    )
                    sorts.take(3).forEach { (key, label) ->
                        FilterChip(
                            selected = filterState.sortBy == key,
                            onClick = { viewModel.updateFilters(filterState.maxPriceUsd, filterState.condition, key) },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopyTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Appliquer les filtres (${filteredProducts.size})")
                }
            }
        }
    }
}
