package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.auth.AuthManager
import com.example.data.repository.ShopyRepository
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopyViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authManager = AuthManager(this)

        setContent {
            MyApplicationTheme {
                val currentUser by authManager.currentUser.collectAsState()

                if (currentUser == null) {
                    SignInScreen(authManager = authManager)
                } else {
                    val context = LocalContext.current
                    val databaseId = remember {
                        val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
                        if (resId != 0) context.getString(resId) else "ai-studio-android-shopyexp-99792ee8-e257-4240-9d18-dbe8f9696463"
                    }
                    val firestore = remember(databaseId) {
                        FirebaseFirestore.getInstance(databaseId)
                    }
                    val repository = remember(firestore) {
                        ShopyRepository(firestore)
                    }
                    val viewModel: ShopyViewModel = viewModel(
                        key = currentUser?.uid,
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ShopyViewModel(repository) as T
                            }
                        }
                    )

                    ShopyExpressApp(
                        viewModel = viewModel,
                        onSignOut = {
                            authManager.signOut(lifecycleScope)
                        }
                    )
                }
            }
        }
    }
}

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val targetScreen: Screen,
    val isPrimaryAction: Boolean = false
)

@Composable
fun ShopyExpressApp(
    viewModel: ShopyViewModel,
    onSignOut: () -> Unit = {}
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Handle back button for secondary screens
    val isRootScreen = currentScreen is Screen.Feed ||
            currentScreen is Screen.Search ||
            currentScreen is Screen.Sell ||
            currentScreen is Screen.Messages ||
            currentScreen is Screen.Profile

    BackHandler(enabled = !isRootScreen) {
        viewModel.navigateBack()
    }

    val navItems = listOf(
        NavItem(
            title = "Accueil",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            targetScreen = Screen.Feed
        ),
        NavItem(
            title = "Recherche",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
            targetScreen = Screen.Search
        ),
        NavItem(
            title = "Vendre",
            selectedIcon = Icons.Filled.AddCircle,
            unselectedIcon = Icons.Filled.AddCircle,
            targetScreen = Screen.Sell,
            isPrimaryAction = true
        ),
        NavItem(
            title = "Messages",
            selectedIcon = Icons.Default.ChatBubble,
            unselectedIcon = Icons.Default.ChatBubbleOutline,
            targetScreen = Screen.Messages
        ),
        NavItem(
            title = "Profil",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            targetScreen = Screen.Profile
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (isRootScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("shopy_bottom_nav")
                ) {
                    navItems.forEach { item ->
                        val isSelected = when (item.targetScreen) {
                            Screen.Feed -> currentScreen is Screen.Feed
                            Screen.Search -> currentScreen is Screen.Search
                            Screen.Sell -> currentScreen is Screen.Sell
                            Screen.Messages -> currentScreen is Screen.Messages
                            Screen.Profile -> currentScreen is Screen.Profile
                            else -> false
                        }

                        if (item.isPrimaryAction) {
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.targetScreen) },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(ShopyCoral),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Vendre",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                label = { Text(item.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag("nav_sell_btn")
                            )
                        } else {
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.targetScreen) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ShopyTeal,
                                    selectedTextColor = ShopyTeal,
                                    indicatorColor = ShopyTealContainer
                                ),
                                modifier = Modifier.testTag("nav_${item.title.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    is Screen.Feed -> HomeScreen(viewModel = viewModel)
                    is Screen.Search -> SearchScreen(viewModel = viewModel)
                    is Screen.Sell -> SellScreen(viewModel = viewModel)
                    is Screen.Messages -> MessagesScreen(viewModel = viewModel)
                    is Screen.Profile -> ProfileScreen(
                        viewModel = viewModel,
                        onSignOut = onSignOut
                    )
                    is Screen.ProductDetail -> ProductDetailScreen(
                        product = screen.product,
                        viewModel = viewModel
                    )
                    is Screen.ChatDetail -> ChatDetailScreen(
                        conversationId = screen.conversationId,
                        viewModel = viewModel
                    )
                    is Screen.Checkout -> CheckoutScreen(
                        product = screen.product,
                        agreedPriceUsd = screen.agreedPriceUsd,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
