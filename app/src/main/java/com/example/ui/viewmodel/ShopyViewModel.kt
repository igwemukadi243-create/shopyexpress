package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.ShopyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Feed : Screen()
    object Search : Screen()
    object Sell : Screen()
    object Messages : Screen()
    object Profile : Screen()
    data class ProductDetail(val product: Product) : Screen()
    data class ChatDetail(val conversationId: String) : Screen()
    data class Checkout(val product: Product, val agreedPriceUsd: Double) : Screen()
}

data class FilterState(
    val query: String = "",
    val categoryId: String = "all",
    val city: String = "Tout RDC",
    val maxPriceUsd: Float = 500f,
    val condition: String = "Tout",
    val sortBy: String = "recommandes" // "recommandes", "prix_asc", "prix_desc", "recents"
)

class ShopyViewModel(
    private val repository: ShopyRepository = ShopyRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Feed)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenStack = mutableListOf<Screen>(Screen.Feed)

    val currentUser = repository.currentUser
    val categories = repository.categories
    val products = repository.products
    val orders = repository.orders
    val conversations = repository.conversations

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Filtered products flow
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        filterState
    ) { allProducts, filter ->
        allProducts.filter { prod ->
            val matchesQuery = filter.query.isBlank() ||
                    prod.title.contains(filter.query, ignoreCase = true) ||
                    prod.brand.contains(filter.query, ignoreCase = true) ||
                    prod.description.contains(filter.query, ignoreCase = true)

            val matchesCategory = filter.categoryId == "all" || prod.categoryId == filter.categoryId
            val matchesCity = filter.city == "Tout RDC" || prod.city.equals(filter.city, ignoreCase = true)
            val matchesPrice = prod.priceUsd <= filter.maxPriceUsd
            val matchesCondition = filter.condition == "Tout" || prod.condition.equals(filter.condition, ignoreCase = true)

            matchesQuery && matchesCategory && matchesCity && matchesPrice && matchesCondition
        }.sortedWith { a, b ->
            when (filter.sortBy) {
                "prix_asc" -> a.priceUsd.compareTo(b.priceUsd)
                "prix_desc" -> b.priceUsd.compareTo(a.priceUsd)
                "recents" -> b.createdAtMillis.compareTo(a.createdAtMillis)
                else -> b.likesCount.compareTo(a.likesCount) // recommandés / populaires
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteProducts: StateFlow<List<Product>> = products.map { list ->
        list.filter { it.isFavorite }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProducts: StateFlow<List<Product>> = combine(
        products,
        currentUser
    ) { allProds, user ->
        allProds.filter { it.sellerId == user.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        _screenStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenStack.size > 1) {
            _screenStack.removeAt(_screenStack.size - 1)
            _currentScreen.value = _screenStack.last()
            return true
        }
        return false
    }

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun selectCategory(catId: String) {
        _filterState.value = _filterState.value.copy(categoryId = catId)
    }

    fun selectCity(city: String) {
        _filterState.value = _filterState.value.copy(city = city)
    }

    fun updateFilters(
        maxPrice: Float,
        condition: String,
        sortBy: String
    ) {
        _filterState.value = _filterState.value.copy(
            maxPriceUsd = maxPrice,
            condition = condition,
            sortBy = sortBy
        )
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun toggleFavorite(productId: String) {
        repository.toggleFavorite(productId)
    }

    fun makeOffer(product: Product, offeredPriceUsd: Double) {
        viewModelScope.launch {
            val conv = repository.makeOffer(product, offeredPriceUsd)
            _userMessage.value = "Offre de ${offeredPriceUsd.toInt()} $ envoyée à ${product.sellerName} !"
            navigateTo(Screen.ChatDetail(conv.id))
        }
    }

    fun sendMessage(conversationId: String, text: String) {
        if (text.isNotBlank()) {
            repository.sendMessage(conversationId, text)
        }
    }

    fun getMessagesForConversation(convId: String): List<ChatMessage> {
        return repository.getMessagesForConversation(convId)
    }

    fun getConversation(convId: String): Conversation? {
        return conversations.value.find { it.id == convId }
    }

    fun publishProduct(
        title: String,
        description: String,
        priceUsd: Double,
        brand: String,
        size: String,
        categoryId: String,
        condition: String,
        city: String,
        commune: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            val newProd = repository.addProduct(
                title = title,
                description = description,
                priceUsd = priceUsd,
                brand = brand,
                size = size,
                categoryId = categoryId,
                condition = condition,
                city = city,
                commune = commune,
                imageUrl = imageUrl
            )
            _userMessage.value = "Article publié avec succès sur ShopyExpress !"
            navigateTo(Screen.ProductDetail(newProd))
        }
    }

    private val ikeepayManager = com.example.data.payment.IkeepayPaymentManager()

    fun confirmOrder(
        product: Product,
        finalPriceUsd: Double,
        city: String,
        address: String,
        phone: String,
        paymentMethod: PaymentMethod
    ) {
        viewModelScope.launch {
            val totalAmount = finalPriceUsd + 1.0 + 2.0
            val tempRef = "ORD-" + System.currentTimeMillis().toString().takeLast(6)
            val paymentResult = ikeepayManager.processPayment(
                amountUsd = totalAmount,
                phoneNumber = phone,
                paymentMethod = paymentMethod,
                orderId = tempRef,
                productTitle = product.title
            )

            val order = repository.placeOrder(
                product = product,
                city = city,
                deliveryAddress = address,
                phone = phone,
                paymentMethod = paymentMethod,
                finalPriceUsd = finalPriceUsd
            )

            val tx = paymentResult.getOrNull()
            val feedback = if (tx != null && tx.transactionId.isNotBlank()) {
                "Paiement ${paymentMethod.title} validé (${tx.transactionId}) ! Suivi : ${order.trackingCode}"
            } else {
                "Commande confirmée ! Suivi : ${order.trackingCode}"
            }
            _userMessage.value = feedback
            navigateTo(Screen.Profile)
        }
    }

    fun withdrawFunds(amount: Double, method: PaymentMethod, phone: String) {
        val success = repository.withdrawFunds(amount, method, phone)
        if (success) {
            _userMessage.value = "Retrait de ${amount.toInt()} $ vers $phone (${method.title}) réussi !"
        } else {
            _userMessage.value = "Solde insuffisant pour ce retrait."
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
