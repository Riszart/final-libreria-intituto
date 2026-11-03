package com.app_frontend.ui.navigation

/**
 * Rutas de navegación de la app.
 *
 * Se usan con Navigation Compose. Las rutas con argumentos se construyen con
 * sus funciones auxiliares (`Routes.product("prd-001")`).
 */
object Routes {

    // Autenticación
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Pestañas de la barra inferior
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val CART = "cart"
    const val PROFILE = "profile"

    // Detalle de producto
    const val PRODUCT = "product/{productId}"
    fun product(productId: String) = "product/$productId"

    // Perfil
    const val EDIT_PROFILE = "profile/edit"

    // Checkout
    const val CHECKOUT = "checkout"                       // Método de pago y entrega
    const val CONFIRMATION = "checkout/confirmation"      // Confirmación de pedido
    const val SUCCESS = "checkout/success/{orderId}"      // Pedido exitoso
    fun success(orderId: String) = "checkout/success/$orderId"

    // Pedidos
    const val ORDERS = "orders"                           // Historial de pedidos
    const val ORDER_DETAIL = "orders/{orderId}"           // Detalle del pedido
    fun orderDetail(orderId: String) = "orders/$orderId"

    // Notificaciones
    const val NOTIFICATIONS = "notifications"

    /** Rutas que muestran la barra inferior. */
    val bottomBarRoutes = setOf(HOME, CATALOG, FAVORITES, CART, PROFILE, PRODUCT, ORDERS, NOTIFICATIONS)

    /** Rutas que muestran la barra de marca superior. */
    val topBarRoutes = setOf(HOME, CATALOG, FAVORITES, CART, PROFILE, PRODUCT, ORDERS, NOTIFICATIONS)
}
