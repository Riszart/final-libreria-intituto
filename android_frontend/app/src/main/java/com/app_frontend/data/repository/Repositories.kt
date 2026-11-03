package com.app_frontend.data.repository

import com.app_frontend.data.local.MockData
import com.app_frontend.data.model.AppNotification
import com.app_frontend.data.model.AuthSession
import com.app_frontend.data.model.CartItem
import com.app_frontend.data.model.CartSummary
import com.app_frontend.data.model.LoginRequest
import com.app_frontend.data.model.Order
import com.app_frontend.data.model.OrderStatus
import com.app_frontend.data.model.Product
import com.app_frontend.data.model.RegisterRequest
import com.app_frontend.data.model.UserProfile
import com.app_frontend.data.remote.ApiConfig
import com.app_frontend.data.remote.SessionStore
import com.app_frontend.data.remote.dto.AddToCartRequestDto
import com.app_frontend.data.remote.dto.ApiResponse
import com.app_frontend.data.remote.dto.AddressDto
import com.app_frontend.data.remote.dto.AuthResponseDto
import com.app_frontend.data.remote.dto.CartDto
import com.app_frontend.data.remote.dto.ChangePasswordRequestDto
import com.app_frontend.data.remote.dto.CouponRequestDto
import com.app_frontend.data.remote.dto.FavoriteRequestDto
import com.app_frontend.data.remote.dto.LoginRequestDto
import com.app_frontend.data.remote.dto.NotificationDto
import com.app_frontend.data.remote.dto.OrderDto
import com.app_frontend.data.remote.dto.PlaceOrderRequestDto
import com.app_frontend.data.remote.dto.ProductDto
import com.app_frontend.data.remote.dto.RegisterRequestDto
import com.app_frontend.data.remote.dto.UpdateCartItemRequestDto
import com.app_frontend.data.remote.dto.UpdateProfileRequestDto
import com.app_frontend.data.remote.dto.UserDto
import com.app_frontend.data.remote.dto.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repositorios de datos.
 *
 * Estrategia: **primero la API Java, si no responde → datos locales**.
 *
 * Así el frontend es navegable hoy mismo y, en cuanto se despliegue el
 * backend en microservicios, pasará a usar las respuestas reales sin tocar
 * ninguna pantalla. El flag [ApiConfig.backendAvailable] indica de qué origen
 * vienen los datos (útil para depurar).
 */

/** Ámbito para notificaciones "fire and forget" al backend. */
private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

/** Lanza una petición opcional al backend sin bloquear la UI. */
private fun fireAndForget(block: suspend () -> Unit) {
    repositoryScope.launch {
        runCatching { block() }
    }
}

/**
 * Intenta leer de la API y, si falla, devuelve [fallback].
 * [map] convierte el DTO remoto en el modelo de dominio.
 */
private suspend fun <R, D> fetchOrFallback(
    fallback: D,
    map: (R) -> D,
    call: suspend () -> Response<ApiResponse<R>>
): D {
    val remote = fetchOrNull(call)
    return remote?.let(map) ?: fallback
}

/** Devuelve `null` cuando el backend no está disponible o no responde. */
private suspend fun <R> fetchOrNull(call: suspend () -> Response<ApiResponse<R>>): R? {
    return try {
        val response = call()
        val body = response.body()
        val data = body?.data
        if (response.isSuccessful && body?.success != false && data != null) {
            ApiConfig.backendAvailable = true
            data
        } else {
            ApiConfig.backendAvailable = false
            null
        }
    } catch (error: Exception) {
        // Backend aún no existe / sin red / timeout → datos locales.
        ApiConfig.backendAvailable = false
        null
    }
}

// ---------------------------------------------------------------------------
// Autenticación
// ---------------------------------------------------------------------------

object AuthRepository {

    suspend fun login(request: LoginRequest): Result<AuthSession> = runCatching {
        if (request.email.isBlank() || request.password.isBlank()) {
            throw IllegalArgumentException("Completa tu correo y tu contraseña.")
        }
        val remote = fetchOrNull<AuthResponseDto> {
            ApiConfig.api.login(LoginRequestDto(email = request.email, password = request.password))
        }
        val user = remote?.user?.toDomain()
            ?: MockData.user.copy(email = request.email)
        val token = remote?.token ?: "demo-token"
        SessionStore.saveSession(token, user.id, user.fullName, user.email)
        AuthSession(token = token, user = user)
    }

    suspend fun register(request: RegisterRequest): Result<AuthSession> = runCatching {
        if (request.password.length < 8) {
            throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.")
        }
        val remote = fetchOrNull<AuthResponseDto> {
            ApiConfig.api.register(
                RegisterRequestDto(
                    fullName = request.fullName,
                    email = request.email,
                    password = request.password
                )
            )
        }
        val user = remote?.user?.toDomain()
            ?: MockData.user.copy(fullName = request.fullName, email = request.email)
        val token = remote?.token ?: "demo-token"
        SessionStore.saveSession(token, user.id, user.fullName, user.email)
        AuthSession(token = token, user = user)
    }

    fun logout() {
        fireAndForget { ApiConfig.api.logout() }
        SessionStore.clear()
    }

    fun currentSession(): AuthSession =
        if (SessionStore.isLoggedIn) {
            AuthSession(
                token = SessionStore.token,
                user = MockData.user.copy(
                    fullName = SessionStore.userName.ifBlank { MockData.user.fullName },
                    email = SessionStore.userEmail.ifBlank { MockData.user.email }
                )
            )
        } else AuthSession()
}

// ---------------------------------------------------------------------------
// Catálogo y favoritos
// ---------------------------------------------------------------------------

object ProductRepository {

    /** Favoritos en memoria (sobreviven a la navegación). */
    private val _favoriteIds = MutableStateFlow(
        setOf("prd-008", "prd-009", "prd-010", "prd-011")
    )
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    suspend fun fetchProducts(category: String = "", search: String = ""): List<Product> {
        val filters = buildMap {
            if (category.isNotBlank() && category != "Todos") put("category", category)
            if (search.isNotBlank()) put("search", search)
        }
        return fetchOrFallback(
            fallback = MockData.products,
            map = { dtos: List<ProductDto> -> dtos.map { it.toDomain() } }
        ) { ApiConfig.api.getProducts(filters) }
    }

    suspend fun fetchProduct(id: String): Product? {
        val remote = fetchOrNull<ProductDto> { ApiConfig.api.getProduct(id) }
        return remote?.toDomain() ?: MockData.product(id)
    }

    /** Productos marcados como favoritos (estado local + backend). */
    suspend fun fetchFavorites(): List<Product> {
        val remote = fetchOrNull<List<ProductDto>> { ApiConfig.api.getFavorites() }
        val source = remote?.map { it.toDomain() } ?: MockData.products
        return source.filter { _favoriteIds.value.contains(it.id) }
    }

    fun isFavorite(id: String): Boolean = _favoriteIds.value.contains(id)

    fun toggleFavorite(id: String): Boolean {
        val next = _favoriteIds.value.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
        _favoriteIds.value = next
        val nowFavorite = next.contains(id)
        // Notifica al backend si está disponible (el fallo no rompe la UI).
        fireAndForget { ApiConfig.api.setFavorite(id, FavoriteRequestDto(nowFavorite)) }
        return nowFavorite
    }
}

// ---------------------------------------------------------------------------
// Carrito
// ---------------------------------------------------------------------------

object CartRepository {

    private val _items = MutableStateFlow(MockData.cart)
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    suspend fun refresh() {
        val remote = fetchOrNull<CartDto> { ApiConfig.api.getCart() }
        val remoteItems = remote?.items?.map { it.toDomain() }
        if (remoteItems != null) _items.value = remoteItems
    }

    fun add(product: Product, quantity: Int = 1) {
        val current = _items.value
        val existing = current.indexOfFirst { it.product.id == product.id }
        _items.value = if (existing >= 0) {
            current.toMutableList().apply {
                this[existing] = this[existing].copy(quantity = this[existing].quantity + quantity)
            }
        } else current + CartItem(product, quantity)

        fireAndForget {
            ApiConfig.api.addToCart(AddToCartRequestDto(productId = product.id, quantity = quantity))
        }
    }

    fun changeQuantity(productId: String, delta: Int) {
        _items.value = _items.value.mapNotNull { item ->
            if (item.product.id != productId) item
            else {
                val next = item.quantity + delta
                if (next <= 0) null else item.copy(quantity = next)
            }
        }
        val quantity = _items.value.firstOrNull { it.product.id == productId }?.quantity
        if (quantity != null) {
            fireAndForget {
                ApiConfig.api.updateCartItem(productId, UpdateCartItemRequestDto(quantity))
            }
        }
    }

    fun remove(productId: String) {
        _items.value = _items.value.filterNot { it.product.id == productId }
        fireAndForget { ApiConfig.api.removeCartItem(productId) }
    }

    fun clear() {
        _items.value = emptyList()
        fireAndForget { ApiConfig.api.clearCart() }
    }

    fun count(): Int = _items.value.sumOf { it.quantity }

    /** Subtotal real del carrito actual. */
    fun subtotal(): Double = _items.value.sumOf { it.lineTotal }
}

// ---------------------------------------------------------------------------
// Pedidos
// ---------------------------------------------------------------------------

object OrderRepository {

    suspend fun fetchOrders(): List<Order> = fetchOrFallback(
        fallback = MockData.orders,
        map = { dtos: List<OrderDto> -> dtos.map { it.toDomain() } }
    ) { ApiConfig.api.getOrders(emptyMap()) }

    suspend fun fetchOrder(id: String): Order? {
        val remote = fetchOrNull<OrderDto> { ApiConfig.api.getOrder(id) }
        return remote?.toDomain() ?: MockData.order(id)
    }

    suspend fun placeOrder(request: PlaceOrderRequestDto): Order? {
        val remote = fetchOrNull<OrderDto> { ApiConfig.api.placeOrder(request) }
        if (remote != null) return remote.toDomain()
        // Sin backend: simula un pedido confirmado con los datos del checkout.
        return MockData.orders.first().copy(
            id = "#PT-${(84921..89999).random()}",
            status = OrderStatus.PREPARING
        )
    }

    suspend fun applyCoupon(code: String): Pair<Boolean, String> {
        val remote = fetchOrNull<com.app_frontend.data.remote.dto.CouponResponseDto> {
            ApiConfig.api.applyCoupon(CouponRequestDto(code))
        }
        return when {
            remote != null -> (remote.valid ?: false) to (remote.message ?: "")
            code.isBlank() -> false to "Ingresa un código de descuento."
            else -> true to "Cupón aplicado (modo demostración)."
        }
    }
}

// ---------------------------------------------------------------------------
// Perfil
// ---------------------------------------------------------------------------

object ProfileRepository {

    suspend fun fetchProfile(): UserProfile = fetchOrFallback(
        fallback = MockData.user,
        map = { dto: UserDto -> dto.toDomain() }
    ) { ApiConfig.api.getProfile() }

    suspend fun updateProfile(user: UserProfile): UserProfile {
        val remote = fetchOrNull<UserDto> {
            ApiConfig.api.updateProfile(
                UpdateProfileRequestDto(
                    fullName = user.fullName,
                    email = user.email,
                    phone = user.phone,
                    newsletter = user.newsletter,
                    offers = user.offers,
                    address = AddressDto(
                        receiver = user.address.receiver,
                        street = user.address.street,
                        city = user.address.city,
                        state = user.address.state,
                        zip = user.address.zip,
                        country = user.address.country
                    )
                )
            )
        }
        return remote?.toDomain() ?: user
    }

    suspend fun changePassword(current: String, new: String): Result<Unit> = runCatching {
        if (new.length < 8) {
            throw IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.")
        }
        if (ApiConfig.backendAvailable) {
            val response = ApiConfig.api.changePassword(
                ChangePasswordRequestDto(currentPassword = current, newPassword = new)
            )
            if (!response.isSuccessful) error(response.message())
        }
        Unit
    }
}

// ---------------------------------------------------------------------------
// Notificaciones
// ---------------------------------------------------------------------------

object NotificationRepository {

    private val _notifications = MutableStateFlow(MockData.notifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    suspend fun refresh() {
        val remote = fetchOrNull<List<NotificationDto>> { ApiConfig.api.getNotifications() }
        val mapped = remote?.map { it.toDomain() }
        if (mapped != null) _notifications.value = mapped
    }

    fun markRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        fireAndForget { ApiConfig.api.markAsRead(id) }
    }

    fun markAllRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        fireAndForget { ApiConfig.api.markAllAsRead() }
    }

    fun unreadCount(): Int = _notifications.value.count { !it.isRead }
}

/** Expuesto para que las pantallas muestren el resumen del pedido en checkout. */
fun buildCartSummary(subtotal: Double, tax: Double, shipping: Double, discount: Double): CartSummary =
    CartSummary(subtotal = subtotal, tax = tax, shipping = shipping, discount = discount)
