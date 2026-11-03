package com.app_frontend

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.app_frontend.data.repository.AuthRepository
import com.app_frontend.ui.components.AppBottomBar
import com.app_frontend.ui.components.AppTopBar
import com.app_frontend.ui.navigation.Routes
import com.app_frontend.ui.screens.auth.LoginScreen
import com.app_frontend.ui.screens.auth.RegisterScreen
import com.app_frontend.ui.screens.cart.CartScreen
import com.app_frontend.ui.screens.catalog.CatalogScreen
import com.app_frontend.ui.screens.checkout.CheckoutScreen
import com.app_frontend.ui.screens.checkout.ConfirmationScreen
import com.app_frontend.ui.screens.checkout.SuccessScreen
import com.app_frontend.ui.screens.favorites.FavoritesScreen
import com.app_frontend.ui.screens.home.HomeScreen
import com.app_frontend.ui.screens.notifications.NotificationsScreen
import com.app_frontend.ui.screens.orders.OrderDetailScreen
import com.app_frontend.ui.screens.orders.OrdersScreen
import com.app_frontend.ui.screens.product.ProductDetailScreen
import com.app_frontend.ui.screens.profile.EditProfileScreen
import com.app_frontend.ui.screens.profile.ProfileScreen
import com.app_frontend.ui.theme.Cream
import com.app_frontend.ui.viewmodel.AuthViewModel
import com.app_frontend.ui.viewmodel.CartViewModel
import com.app_frontend.ui.viewmodel.NotificationViewModel
import com.app_frontend.ui.viewmodel.OrderViewModel
import com.app_frontend.ui.viewmodel.ProfileViewModel
import com.app_frontend.ui.viewmodel.ShopViewModel

/**
 * Grafo de navegación de la app + scaffold con barra de marca superior y
 * barra de pestañas inferior.
 *
 * Los ViewModels se crean UNA sola vez aquí (ámbito de la Activity) y se
 * pasan explícitamente a cada pantalla: así el carrito, los favoritos, los
 * pedidos y el perfil comparten estado al navegar entre pestañas.
 */
@Composable
fun PapelTintaApp() {
    val navController = rememberNavController()

    // --- ViewModels compartidos por toda la app ---
    val authViewModel: AuthViewModel = viewModel()
    val shopViewModel: ShopViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val notifications by notificationViewModel.notifications.collectAsStateWithLifecycle()
    val cartCount by cartViewModel.itemsCount.collectAsStateWithLifecycle()
    val unreadCount = notifications.count { !it.isRead }

    val startDestination = remember {
        if (AuthRepository.currentSession().isLoggedIn) Routes.HOME else Routes.LOGIN
    }

    val showTopBar = currentRoute in Routes.topBarRoutes
    val showBottomBar = currentRoute in Routes.bottomBarRoutes

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun logout() {
        authViewModel.logout()
        navController.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            if (showTopBar) {
                AppTopBar(
                    onSearch = { navigateToTab(Routes.CATALOG) },
                    onFavorites = { navigateToTab(Routes.FAVORITES) },
                    onNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                    onProfile = { navigateToTab(Routes.PROFILE) },
                    unreadCount = unreadCount
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { navigateToTab(it) },
                    cartCount = cartCount
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            contentModifier = Modifier
                .padding(innerPadding)
                .then(
                    // Sin barra de marca, la pantalla cede espacio a las barras
                    // del sistema (estado y navegación) por su cuenta.
                    if (showTopBar) Modifier
                    else Modifier.statusBarsPadding().navigationBarsPadding()
                ),
            startDestination = startDestination,
            onNavigateTab = { navigateToTab(it) },
            authViewModel = authViewModel,
            shopViewModel = shopViewModel,
            cartViewModel = cartViewModel,
            orderViewModel = orderViewModel,
            notificationViewModel = notificationViewModel,
            profileViewModel = profileViewModel,
            onLogout = { logout() }
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    contentModifier: Modifier,
    startDestination: String,
    onNavigateTab: (String) -> Unit,
    authViewModel: AuthViewModel,
    shopViewModel: ShopViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    notificationViewModel: NotificationViewModel,
    profileViewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = contentModifier
    ) {

        // ---------------------------------------------------------------
        // Autenticación
        // ---------------------------------------------------------------
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoRegister = { navController.navigate(Routes.REGISTER) },
                viewModel = authViewModel
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoLogin = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        // ---------------------------------------------------------------
        // Pestañas principales
        // ---------------------------------------------------------------
        composable(Routes.HOME) {
            HomeScreen(
                onOpenProduct = { navController.navigate(Routes.product(it)) },
                onTrackOrder = { navController.navigate(Routes.orderDetail("#PT-84920")) },
                onOpenCart = { onNavigateTab(Routes.CART) },
                viewModel = shopViewModel
            )
        }

        composable(Routes.CATALOG) {
            CatalogScreen(
                onOpenProduct = { navController.navigate(Routes.product(it)) },
                onAddToCart = { cartViewModel.add(it) },
                viewModel = shopViewModel
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                onOpenProduct = { navController.navigate(Routes.product(it)) },
                viewModel = shopViewModel
            )
        }

        composable(Routes.CART) {
            CartScreen(
                onOpenProduct = { navController.navigate(Routes.product(it)) },
                onCheckout = { navController.navigate(Routes.CHECKOUT) },
                viewModel = cartViewModel
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onOpenOrders = { navController.navigate(Routes.ORDERS) },
                onOpenFavorites = { onNavigateTab(Routes.FAVORITES) },
                onLogout = onLogout,
                viewModel = profileViewModel
            )
        }

        // ---------------------------------------------------------------
        // Detalle de producto
        // ---------------------------------------------------------------
        composable(
            route = Routes.PRODUCT,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { entry ->
            val productId = entry.arguments?.getString("productId").orEmpty()
            ProductDetailScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onOpenCart = { onNavigateTab(Routes.CART) },
                viewModel = shopViewModel,
                cartViewModel = cartViewModel
            )
        }

        // ---------------------------------------------------------------
        // Checkout
        // ---------------------------------------------------------------
        composable(Routes.CHECKOUT) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.CONFIRMATION) },
                viewModel = cartViewModel
            )
        }

        composable(Routes.CONFIRMATION) {
            ConfirmationScreen(
                onBack = { navController.popBackStack() },
                onModifyCart = { onNavigateTab(Routes.CART) },
                onOrderPlaced = { orderId ->
                    navController.navigate(Routes.success(orderId)) {
                        popUpTo(Routes.CART) { inclusive = true }
                    }
                },
                viewModel = cartViewModel
            )
        }

        composable(
            route = Routes.SUCCESS,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { entry ->
            val orderId = entry.arguments?.getString("orderId").orEmpty()
            SuccessScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onViewOrders = {
                    navController.navigate(Routes.ORDERS) {
                        popUpTo(Routes.HOME)
                    }
                },
                onBackHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                viewModel = orderViewModel
            )
        }

        // ---------------------------------------------------------------
        // Pedidos
        // ---------------------------------------------------------------
        composable(Routes.ORDERS) {
            OrdersScreen(
                onOpenOrder = { navController.navigate(Routes.orderDetail(it)) },
                viewModel = orderViewModel
            )
        }

        composable(
            route = Routes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { entry ->
            val orderId = entry.arguments?.getString("orderId").orEmpty()
            OrderDetailScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onContinueShopping = { onNavigateTab(Routes.CATALOG) },
                viewModel = orderViewModel
            )
        }

        // ---------------------------------------------------------------
        // Notificaciones
        // ---------------------------------------------------------------
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(viewModel = notificationViewModel)
        }

        // ---------------------------------------------------------------
        // Perfil
        // ---------------------------------------------------------------
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                viewModel = profileViewModel
            )
        }
    }
}

