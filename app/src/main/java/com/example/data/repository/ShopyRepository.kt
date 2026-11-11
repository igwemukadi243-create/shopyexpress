package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ShopyRepository(
    private val db: FirebaseFirestore? = null
) {
    // Secondary constructor resolving named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth
    private val scope = CoroutineScope(Dispatchers.IO)

    private var productsListener: ListenerRegistration? = null
    private var ordersListener: ListenerRegistration? = null

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_me",
            userId = auth.currentUser?.uid ?: "user_me",
            name = auth.currentUser?.displayName ?: "Igwé Mukadi",
            handle = "@igwemukadi_kin",
            email = auth.currentUser?.email ?: "igwemukadi243@gmail.com",
            phone = "+243 82 000 1234",
            city = "Kinshasa",
            commune = "Gombe",
            avatarUrl = auth.currentUser?.photoUrl?.toString()
                ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            rating = 4.9,
            reviewsCount = 38,
            walletBalanceUsd = 65.0,
            memberSinceYear = 2024,
            isVerified = true
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _categories = MutableStateFlow(
        listOf(
            Category("all", "Tout", "category_all", 120),
            Category("wax", "Pagne & Wax", "category_wax", 34),
            Category("femmes", "Femmes", "category_femmes", 58),
            Category("hommes", "Hommes", "category_hommes", 42),
            Category("chaussures", "Chaussures", "category_shoes", 29),
            Category("sacs", "Sacs & Bijoux", "category_bags", 21),
            Category("tech", "Tech & Téléphones", "category_tech", 18),
            Category("enfants", "Enfants", "category_kids", 15)
        )
    )
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val initialSampleProducts = listOf(
        Product(
            id = "prod_1",
            title = "Robe évasée en Pagne Woodin véritable",
            description = "Superbe robe confectionnée sur mesure à Kinshasa avec pagne Woodin 100% coton. Portée une seule fois pour une collation. Doublée, finition impeccable. Coupe flatteuse et couleurs éclatantes.",
            priceUsd = 28.0,
            originalPriceUsd = 45.0,
            brand = "Woodin Couture",
            size = "M / 38",
            categoryId = "wax",
            condition = "Très bon état",
            city = "Kinshasa",
            commune = "Bandalungwa",
            imageUrl = "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_rachel",
            sellerName = "Rachel Bilonda",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=400&q=80",
            sellerRating = 5.0,
            sellerSalesCount = 47,
            isVerifiedSeller = true,
            isFavorite = false,
            viewsCount = 89,
            likesCount = 14
        ),
        Product(
            id = "prod_2",
            title = "Baskets Nike Air Force 1 '07 Originales",
            description = "Sneakers iconiques achetées neuves, très peu portées, semelle propre. Emballées avec boîte d'origine. Idéales pour le style street kinois. Remise en main propre possible à Gombe.",
            priceUsd = 48.0,
            originalPriceUsd = 75.0,
            brand = "Nike",
            size = "42",
            categoryId = "chaussures",
            condition = "Très bon état",
            city = "Kinshasa",
            commune = "Gombe",
            imageUrl = "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_junior",
            sellerName = "Junior Kabeya",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
            sellerRating = 4.8,
            sellerSalesCount = 29,
            isVerifiedSeller = true,
            isFavorite = true,
            viewsCount = 135,
            likesCount = 28
        ),
        Product(
            id = "prod_3",
            title = "Blazer tailleur oversize vintage beige",
            description = "Blazer chic coupe oversize tendance. Tissu de haute qualité, boutons dorés élégants. Parfait pour le travail ou les soirées chic à Lubumbashi.",
            priceUsd = 22.0,
            originalPriceUsd = 35.0,
            brand = "Zara",
            size = "L / 40",
            categoryId = "femmes",
            condition = "Très bon état",
            city = "Lubumbashi",
            commune = "Golf",
            imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_nadia",
            sellerName = "Nadia Tshilombo",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=400&q=80",
            sellerRating = 4.9,
            sellerSalesCount = 63,
            isVerifiedSeller = true,
            isFavorite = false,
            viewsCount = 64,
            likesCount = 9
        ),
        Product(
            id = "prod_4",
            title = "Chemise Homme Motif Africain Vlisco Grand Luxe",
            description = "Chemise manche courte imprimé wax Vlisco haut de gamme. Tissu doux et respirant adapté au climat chaud. Jamais portée, étiquette encore attachée.",
            priceUsd = 25.0,
            originalPriceUsd = 40.0,
            brand = "Vlisco Édition",
            size = "XL",
            categoryId = "hommes",
            condition = "Neuf avec étiquette",
            city = "Kinshasa",
            commune = "Limete",
            imageUrl = "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_yannick",
            sellerName = "Yannick Mwamba",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80",
            sellerRating = 4.7,
            sellerSalesCount = 18,
            isVerifiedSeller = true,
            isFavorite = false,
            viewsCount = 112,
            likesCount = 19
        ),
        Product(
            id = "prod_5",
            title = "Sac à main bandoulière cuir camel avec détails dorés",
            description = "Joli sac bandoulière compact et pratique. Plusieurs compartiments intérieurs zippés. Cuir synthétique résistant, état presque neuf.",
            priceUsd = 16.0,
            originalPriceUsd = 30.0,
            brand = "Mango",
            size = "Taille Unique",
            categoryId = "sacs",
            condition = "Très bon état",
            city = "Goma",
            commune = "Himbi",
            imageUrl = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_dorcas",
            sellerName = "Dorcas Kanyeba",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=400&q=80",
            sellerRating = 5.0,
            sellerSalesCount = 52,
            isVerifiedSeller = true,
            isFavorite = true,
            viewsCount = 76,
            likesCount = 22
        ),
        Product(
            id = "prod_6",
            title = "iPhone 12 Pro Max 128Go Bleu Pacifique (Batterie 88%)",
            description = "Smartphone en excellent état sans rayures sur l'écran, protégé avec verre trempé et coque silicone. Débloqué tout opérateur (Vodacom, Airtel, Orange RDC). Livré avec câble rapide.",
            priceUsd = 310.0,
            originalPriceUsd = 420.0,
            brand = "Apple",
            size = "128 Go",
            categoryId = "tech",
            condition = "Très bon état",
            city = "Kinshasa",
            commune = "Kintambo",
            imageUrl = "https://images.unsplash.com/photo-1605236453806-6ff36851218e?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_patrick",
            sellerName = "Patrick Luvualu",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=400&q=80",
            sellerRating = 4.9,
            sellerSalesCount = 84,
            isVerifiedSeller = true,
            isFavorite = false,
            viewsCount = 310,
            likesCount = 45
        ),
        Product(
            id = "prod_7",
            title = "Robe de gala sirène velours noir et broderies",
            description = "Robe de soirée élégante coupée pour sublimer la silhouette. Parfaite pour mariages, dîners de gala ou fêtes de fin d'année. Portée une seule fois quelques heures.",
            priceUsd = 35.0,
            originalPriceUsd = 60.0,
            brand = "Création Kin",
            size = "S / 36",
            categoryId = "femmes",
            condition = "Très bon état",
            city = "Lubumbashi",
            commune = "Makomeno",
            imageUrl = "https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_grace",
            sellerName = "Grâce Kalonji",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            sellerRating = 4.9,
            sellerSalesCount = 31,
            isVerifiedSeller = true,
            isFavorite = false,
            viewsCount = 94,
            likesCount = 18
        ),
        Product(
            id = "prod_8",
            title = "Montre Homme Chronographe Cadran Noir et Bracelet Cuir",
            description = "Montre authentique étanche 5 ATM, verre minéral inrayable. Pile neuve changée récemment. Style classe et sobre pour homme d'affaires.",
            priceUsd = 32.0,
            originalPriceUsd = 55.0,
            brand = "Fossil",
            size = "Standard",
            categoryId = "hommes",
            condition = "Bon état",
            city = "Kinshasa",
            commune = "Lemba",
            imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?auto=format&fit=crop&w=800&q=80",
            sellerId = "seller_michel",
            sellerName = "Michel Kasongo",
            sellerAvatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=400&q=80",
            sellerRating = 4.8,
            sellerSalesCount = 22,
            isVerifiedSeller = true,
            isFavorite = false,
            viewsCount = 88,
            likesCount = 11
        )
    )

    private val _products = MutableStateFlow(initialSampleProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _orders = MutableStateFlow(
        listOf(
            Order(
                id = "CMD-243-891",
                productId = "prod_3",
                productTitle = "Blazer tailleur oversize vintage beige",
                productPriceUsd = 22.0,
                buyerProtectionFeeUsd = 1.10,
                deliveryFeeUsd = 2.50,
                totalUsd = 25.60,
                productImageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=800&q=80",
                sellerName = "Nadia Tshilombo",
                buyerCity = "Kinshasa",
                deliveryAddress = "Av. Colonel Lukusa n°14, Gombe",
                phoneNumber = "+243 82 000 1234",
                paymentMethod = PaymentMethod.MPESA,
                status = OrderStatus.SHIPPED,
                trackingCode = "SHP-KIN-LSH-7721"
            )
        )
    )
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val initialConversations = listOf(
        Conversation(
            id = "conv_1",
            productId = "prod_1",
            productTitle = "Robe évasée en Pagne Woodin véritable",
            productPriceUsd = 28.0,
            productImageUrl = "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?auto=format&fit=crop&w=800&q=80",
            otherUserId = "seller_rachel",
            otherUserName = "Rachel Bilonda",
            otherUserAvatar = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=400&q=80",
            lastMessage = "Offre acceptée à 24 $ ! Vous pouvez finaliser la commande.",
            lastMessageTimeMillis = System.currentTimeMillis() - 1000 * 60 * 25,
            unreadCount = 1,
            currentOffer = Offer(
                id = "off_1",
                productId = "prod_1",
                productTitle = "Robe évasée en Pagne Woodin véritable",
                productImageUrl = "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?auto=format&fit=crop&w=800&q=80",
                buyerId = "user_me",
                buyerName = "Igwé Mukadi",
                sellerId = "seller_rachel",
                initialPriceUsd = 28.0,
                offeredPriceUsd = 24.0,
                status = OfferStatus.ACCEPTED
            )
        ),
        Conversation(
            id = "conv_2",
            productId = "prod_2",
            productTitle = "Baskets Nike Air Force 1 '07 Originales",
            productPriceUsd = 48.0,
            productImageUrl = "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=800&q=80",
            otherUserId = "seller_junior",
            otherUserName = "Junior Kabeya",
            otherUserAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
            lastMessage = "Bonjour mon frère, la remise en main propre est faisable vers le rond-point Socimat ?",
            lastMessageTimeMillis = System.currentTimeMillis() - 1000 * 60 * 180,
            unreadCount = 0
        )
    )

    private val _conversations = MutableStateFlow(initialConversations)
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messagesMap = MutableStateFlow(
        mapOf(
            "conv_1" to listOf(
                ChatMessage(
                    id = "msg_1",
                    conversationId = "conv_1",
                    senderId = "user_me",
                    senderName = "Moi",
                    text = "Bonjour Rachel, est-ce que la robe est encore disponible ?",
                    timestampMillis = System.currentTimeMillis() - 1000 * 60 * 60,
                    isFromMe = true
                ),
                ChatMessage(
                    id = "msg_2",
                    conversationId = "conv_1",
                    senderId = "seller_rachel",
                    senderName = "Rachel Bilonda",
                    text = "Bonjour ! Oui elle est toujours là, prête à être livrée à Kinshasa.",
                    timestampMillis = System.currentTimeMillis() - 1000 * 60 * 50,
                    isFromMe = false
                ),
                ChatMessage(
                    id = "msg_3",
                    conversationId = "conv_1",
                    senderId = "user_me",
                    senderName = "Moi",
                    text = "Je vous propose une offre de 24 $ pour la prendre aujourd'hui.",
                    timestampMillis = System.currentTimeMillis() - 1000 * 60 * 35,
                    isFromMe = true,
                    linkedOffer = Offer(
                        id = "off_1",
                        productId = "prod_1",
                        productTitle = "Robe évasée en Pagne Woodin véritable",
                        productImageUrl = "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?auto=format&fit=crop&w=800&q=80",
                        buyerId = "user_me",
                        buyerName = "Igwé Mukadi",
                        sellerId = "seller_rachel",
                        initialPriceUsd = 28.0,
                        offeredPriceUsd = 24.0,
                        status = OfferStatus.ACCEPTED
                    )
                ),
                ChatMessage(
                    id = "msg_4",
                    conversationId = "conv_1",
                    senderId = "seller_rachel",
                    senderName = "Rachel Bilonda",
                    text = "Offre acceptée à 24 $ ! Vous pouvez finaliser la commande sur ShopyExpress.",
                    timestampMillis = System.currentTimeMillis() - 1000 * 60 * 25,
                    isFromMe = false
                )
            ),
            "conv_2" to listOf(
                ChatMessage(
                    id = "msg_20",
                    conversationId = "conv_2",
                    senderId = "user_me",
                    senderName = "Moi",
                    text = "Salut Junior, les Nike sont originales ?",
                    timestampMillis = System.currentTimeMillis() - 1000 * 60 * 240,
                    isFromMe = true
                ),
                ChatMessage(
                    id = "msg_21",
                    conversationId = "conv_2",
                    senderId = "seller_junior",
                    senderName = "Junior Kabeya",
                    text = "Bonjour mon frère, 100% authentiques avec code QR vérifiable.",
                    timestampMillis = System.currentTimeMillis() - 1000 * 60 * 200,
                    isFromMe = false
                ),
                ChatMessage(
                    id = "msg_22",
                    conversationId = "conv_2",
                    senderId = "seller_junior",
                    senderName = "Junior Kabeya",
                    text = "Bonjour mon frère, la remise en main propre est faisable vers le rond-point Socimat ?",
                    timestampMillis = System.currentTimeMillis() - 1000 * 60 * 180,
                    isFromMe = false
                )
            )
        )
    )

    init {
        // Start observing Firestore if authenticated
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                _currentUser.value = _currentUser.value.copy(
                    userId = user.uid,
                    name = user.displayName ?: _currentUser.value.name,
                    email = user.email ?: _currentUser.value.email,
                    avatarUrl = user.photoUrl?.toString() ?: _currentUser.value.avatarUrl
                )
                startFirestoreSync()
            } else {
                stopFirestoreSync()
            }
        }

        if (auth.currentUser != null) {
            startFirestoreSync()
        }
    }

    private fun startFirestoreSync() {
        val firestore = db ?: return
        // Observe products collection
        productsListener?.remove()
        productsListener = firestore.collection("products").addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "products")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    // Seed initial data once to Firestore so the database is pre-populated
                    seedInitialProductsToFirestore()
                } else {
                    val remoteProducts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)?.copy(id = doc.id)
                    }
                    if (remoteProducts.isNotEmpty()) {
                        _products.value = remoteProducts
                    }
                }
            }
        }

        // Observe orders collection
        val uid = auth.currentUser?.uid
        if (uid != null) {
            ordersListener?.remove()
            ordersListener = firestore.collection("orders")
                .whereEqualTo("buyerId", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        handleFirestoreError(error, OperationType.LIST, "orders")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val remoteOrders = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Order::class.java)?.copy(id = doc.id)
                        }
                        _orders.value = remoteOrders
                    }
                }
        }
    }

    private fun stopFirestoreSync() {
        productsListener?.remove()
        productsListener = null
        ordersListener?.remove()
        ordersListener = null
    }

    private fun seedInitialProductsToFirestore() {
        scope.launch {
            val firestore = db ?: return@launch
            try {
                for (prod in initialSampleProducts) {
                    val payload = mapOf(
                        "title" to prod.title,
                        "description" to prod.description,
                        "priceUsd" to prod.priceUsd,
                        "originalPriceUsd" to (prod.originalPriceUsd ?: (prod.priceUsd * 1.3)),
                        "brand" to prod.brand,
                        "size" to prod.size,
                        "categoryId" to prod.categoryId,
                        "condition" to prod.condition,
                        "city" to prod.city,
                        "commune" to prod.commune,
                        "imageUrl" to prod.imageUrl,
                        "sellerId" to (auth.currentUser?.uid ?: prod.sellerId),
                        "sellerName" to prod.sellerName,
                        "sellerAvatarUrl" to prod.sellerAvatarUrl,
                        "sellerRating" to prod.sellerRating,
                        "sellerSalesCount" to prod.sellerSalesCount,
                        "isVerifiedSeller" to prod.isVerifiedSeller,
                        "isSold" to prod.isSold,
                        "likesCount" to prod.likesCount,
                        "viewsCount" to prod.viewsCount,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    firestore.collection("products").document(prod.id).set(payload).await()
                }
            } catch (e: Exception) {
                Log.w("ShopyRepository", "Could not seed sample products to Firestore: ${e.message}")
            }
        }
    }

    fun getMessagesForConversation(convId: String): List<ChatMessage> {
        return _messagesMap.value[convId] ?: emptyList()
    }

    fun toggleFavorite(productId: String) {
        _products.value = _products.value.map { prod ->
            if (prod.id == productId) {
                val newFav = !prod.isFavorite
                val newLikes = if (newFav) prod.likesCount + 1 else maxOf(0, prod.likesCount - 1)
                prod.copy(isFavorite = newFav, likesCount = newLikes)
            } else prod
        }

        // Persist update in Firestore if authenticated
        if (db != null && auth.currentUser != null) {
            db.collection("products").document(productId)
                .update("likesCount", _products.value.find { it.id == productId }?.likesCount ?: 0)
                .addOnFailureListener { e ->
                    handleFirestoreError(e, OperationType.UPDATE, "products/$productId")
                }
        }
    }

    fun addProduct(
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
    ): Product {
        val user = _currentUser.value
        val sellerUid = auth.currentUser?.uid ?: user.userId.ifBlank { "user_me" }
        val prodId = "prod_${UUID.randomUUID().toString().take(8)}"

        val newProduct = Product(
            id = prodId,
            title = title,
            description = description,
            priceUsd = priceUsd,
            originalPriceUsd = priceUsd * 1.3,
            brand = brand.ifBlank { "Sans marque" },
            size = size.ifBlank { "Unique" },
            categoryId = categoryId,
            condition = condition,
            city = city,
            commune = commune,
            imageUrl = imageUrl,
            sellerId = sellerUid,
            sellerName = user.name,
            sellerAvatarUrl = user.avatarUrl,
            sellerRating = user.rating,
            sellerSalesCount = 14,
            isVerifiedSeller = true,
            isFavorite = false,
            viewsCount = 1,
            likesCount = 0
        )

        _products.value = listOf(newProduct) + _products.value

        // Write directly to Firestore
        if (db != null && auth.currentUser != null) {
            val payload = mapOf(
                "title" to title,
                "description" to description,
                "priceUsd" to priceUsd,
                "originalPriceUsd" to (priceUsd * 1.3),
                "brand" to brand.ifBlank { "Sans marque" },
                "size" to size.ifBlank { "Unique" },
                "categoryId" to categoryId,
                "condition" to condition,
                "city" to city,
                "commune" to commune,
                "imageUrl" to imageUrl,
                "sellerId" to sellerUid,
                "sellerName" to user.name,
                "sellerAvatarUrl" to user.avatarUrl,
                "sellerRating" to user.rating,
                "sellerSalesCount" to 14,
                "isVerifiedSeller" to true,
                "isSold" to false,
                "likesCount" to 0,
                "viewsCount" to 1,
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("products").document(prodId).set(payload)
                .addOnFailureListener { exception ->
                    handleFirestoreError(exception, OperationType.CREATE, "products/$prodId")
                }
        }

        return newProduct
    }

    fun makeOffer(product: Product, offeredPriceUsd: Double): Conversation {
        val user = _currentUser.value
        val convId = "conv_${product.id}_${user.id}"
        val offer = Offer(
            id = "off_${UUID.randomUUID().toString().take(8)}",
            productId = product.id,
            productTitle = product.title,
            productImageUrl = product.imageUrl,
            buyerId = user.id,
            buyerName = user.name,
            sellerId = product.sellerId,
            initialPriceUsd = product.priceUsd,
            offeredPriceUsd = offeredPriceUsd,
            status = OfferStatus.PENDING
        )

        val newMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = convId,
            senderId = user.id,
            senderName = user.name,
            text = "Offre envoyée pour ${product.title} : $offeredPriceUsd $ (Prix initial ${product.formattedPriceUsd})",
            isFromMe = true,
            linkedOffer = offer
        )

        val existingConv = _conversations.value.find { it.id == convId }
        val updatedConv = existingConv?.copy(
            lastMessage = "Offre proposée : $offeredPriceUsd $",
            lastMessageTimeMillis = System.currentTimeMillis(),
            currentOffer = offer
        ) ?: Conversation(
            id = convId,
            productId = product.id,
            productTitle = product.title,
            productPriceUsd = product.priceUsd,
            productImageUrl = product.imageUrl,
            otherUserId = product.sellerId,
            otherUserName = product.sellerName,
            otherUserAvatar = product.sellerAvatarUrl,
            lastMessage = "Offre proposée : $offeredPriceUsd $",
            lastMessageTimeMillis = System.currentTimeMillis(),
            currentOffer = offer
        )

        val currentList = _conversations.value.filter { it.id != convId }
        _conversations.value = listOf(updatedConv) + currentList

        val currMsgs = _messagesMap.value[convId] ?: emptyList()
        val updatedMsgsMap = _messagesMap.value.toMutableMap()
        updatedMsgsMap[convId] = currMsgs + newMsg
        _messagesMap.value = updatedMsgsMap

        return updatedConv
    }

    fun sendMessage(conversationId: String, text: String): ChatMessage {
        val user = _currentUser.value
        val newMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = user.id,
            senderName = user.name,
            text = text,
            isFromMe = true
        )

        val currMsgs = _messagesMap.value[conversationId] ?: emptyList()
        val updatedMap = _messagesMap.value.toMutableMap()
        updatedMap[conversationId] = currMsgs + newMsg
        _messagesMap.value = updatedMap

        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(
                    lastMessage = text,
                    lastMessageTimeMillis = System.currentTimeMillis()
                )
            } else conv
        }

        return newMsg
    }

    fun placeOrder(
        product: Product,
        city: String,
        deliveryAddress: String,
        phone: String,
        paymentMethod: PaymentMethod,
        finalPriceUsd: Double
    ): Order {
        val protectionFee = 1.0
        val deliveryFee = 2.0
        val total = finalPriceUsd + protectionFee + deliveryFee
        val orderId = "CMD-243-${(100..999).random()}"
        val buyerUid = auth.currentUser?.uid ?: "user_me"

        val newOrder = Order(
            id = orderId,
            buyerId = buyerUid,
            sellerId = product.sellerId,
            productId = product.id,
            productTitle = product.title,
            productPriceUsd = finalPriceUsd,
            buyerProtectionFeeUsd = protectionFee,
            deliveryFeeUsd = deliveryFee,
            totalUsd = total,
            productImageUrl = product.imageUrl,
            sellerName = product.sellerName,
            buyerCity = city,
            deliveryAddress = deliveryAddress,
            phoneNumber = phone,
            paymentMethod = paymentMethod,
            status = OrderStatus.PROCESSING,
            trackingCode = "SHP-RDC-${(1000..9999).random()}"
        )

        _orders.value = listOf(newOrder) + _orders.value
        _products.value = _products.value.map {
            if (it.id == product.id) it.copy(isSold = true) else it
        }

        // Persist to Firestore
        if (db != null && auth.currentUser != null) {
            val payload = mapOf(
                "buyerId" to buyerUid,
                "sellerId" to product.sellerId,
                "productId" to product.id,
                "productTitle" to product.title,
                "productPriceUsd" to finalPriceUsd,
                "buyerProtectionFeeUsd" to protectionFee,
                "deliveryFeeUsd" to deliveryFee,
                "totalUsd" to total,
                "productImageUrl" to product.imageUrl,
                "sellerName" to product.sellerName,
                "buyerCity" to city,
                "deliveryAddress" to deliveryAddress,
                "phoneNumber" to phone,
                "paymentMethod" to paymentMethod.name,
                "status" to "PROCESSING",
                "trackingCode" to newOrder.trackingCode,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("orders").document(orderId).set(payload)
                .addOnFailureListener { exception ->
                    handleFirestoreError(exception, OperationType.CREATE, "orders/$orderId")
                }

            db.collection("products").document(product.id).update("isSold", true)
                .addOnFailureListener { exception ->
                    handleFirestoreError(exception, OperationType.UPDATE, "products/${product.id}")
                }
        }

        return newOrder
    }

    fun withdrawFunds(amountUsd: Double, method: PaymentMethod, phone: String): Boolean {
        val current = _currentUser.value
        if (amountUsd <= 0 || amountUsd > current.walletBalanceUsd) return false
        _currentUser.value = current.copy(walletBalanceUsd = current.walletBalanceUsd - amountUsd)
        return true
    }
}
