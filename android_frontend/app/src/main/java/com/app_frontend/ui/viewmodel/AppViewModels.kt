package com.app_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_frontend.data.local.MockData
import com.app_frontend.data.model.Address
import com.app_frontend.data.model.AppNotification
import com.app_frontend.data.model.AuthSession
import com.app_frontend.data.model.CartItem
import com.app_frontend.data.model.DeliveryMethod
import com.app_frontend.data.model.LoginRequest
import com.app_frontend.data.model.NotificationTab
import com.app_frontend.data.model.Order
import com.app_frontend.data.model.OrderStatus
import com.app_frontend.data.model.PaymentMethod
import com.app_frontend.data.model.Product
import com.app_frontend.data.model.RegisterRequest
import com.app_frontend.data.model.UserProfile
import com.app_frontend.data.repository.AuthRepository
import com.app_frontend.data.repository.CartRepository
import com.app_frontend.data.repository.NotificationRepository
import com.app_frontend.data.repository.OrderRepository
import com.app_frontend.data.repository.ProductRepository
import com.app_frontend.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ===========================================================================
// Autenticación
// ===========================================================================

sealed interface AuthEvent {
    data object LoggedIn : AuthEvent
    data object Registered : AuthEvent
}

data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptTerms: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    val isLoggedIn: Boolean get() = AuthRepository.currentSession().isLoggedIn

    fun onFullNameChange(value: String) = _uiState.update { it.copy(fullName = value, error = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, error = null) }
    fun onAcceptTermsChange(value: Boolean) = _uiState.update { it.copy(acceptTerms = value, error = null) }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Completa tu correo y tu contraseña.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            AuthRepository.login(LoginRequest(state.email.trim(), state.password))
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.tryEmit(AuthEvent.LoggedIn)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "No se pudo iniciar sesión.")
                    }
                }
        }
    }

    fun register() {
        val state = _uiState.value
        when {
            state.fullName.isBlank() -> {
                _uiState.update { it.copy(error = "Ingresa tu nombre completo.") }; return
            }
            state.email.isBlank() -> {
                _uiState.update { it.copy(error = "Ingresa tu correo electrónico.") }; return
            }
            state.password.length < 8 -> {
                _uiState.update { it.copy(error = "La contraseña debe tener al menos 8 caracteres.") }; return
            }
            state.password != state.confirmPassword -> {
                _uiState.update { it.copy(error = "Las contraseñas no coinciden.") }; return
            }
            !state.acceptTerms -> {
                _uiState.update { it.copy(error = "Debes aceptar los términos y la política de privacidad.") }; return
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            AuthRepository.register(
                RegisterRequest(state.fullName.trim(), state.email.trim(), state.password)
            )
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.tryEmit(AuthEvent.Registered)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "No se pudo crear la cuenta.")
                    }
                }
        }
    }

    fun logout() = AuthRepository.logout()

    fun clearError() = _uiState.update { it.copy(error = null) }
}

// ===========================================================================
// Catálogo, búsqueda y favoritos
// ===========================================================================

data class ShopUiState(
    val products: List<Product> = emptyList(),
    val favorites: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val search: String = "",
    val category: String = "Todos",
    val filter: String? = null
)

class ShopViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = ProductRepository.favoriteIds

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val products = ProductRepository.fetchProducts()
            _uiState.update { it.copy(products = products, isLoading = false) }
            loadFavorites(products)
        }
    }

    private suspend fun loadFavorites(source: List<Product> = _uiState.value.products) {
        val ids = ProductRepository.favoriteIds.value
        _uiState.update { state ->
            state.copy(favorites = source.filter { ids.contains(it.id) })
        }
    }

    fun onSearchChange(value: String) {
        _uiState.update { it.copy(search = value) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value) }
        reloadFiltered()
    }

    fun onFilterChange(value: String?) {
        _uiState.update { current ->
            current.copy(filter = if (current.filter == value) null else value)
        }
        reloadFiltered()
    }

    private fun reloadFiltered() {
        val state = _uiState.value
        viewModelScope.launch {
            val products = ProductRepository.fetchProducts(state.category, state.search)
            _uiState.update { it.copy(products = products) }
            loadFavorites(products)
        }
    }

    /** Productos ya filtrados por búsqueda / filtro secundario, listos para la UI. */
    fun visibleProducts(state: ShopUiState = _uiState.value): List<Product> {
        val query = state.search.trim().lowercase()
        return state.products.filter { product ->
            val matchesQuery = query.isBlank() ||
                product.name.lowercase().contains(query) ||
                product.author.lowercase().contains(query) ||
                product.category.lowercase().contains(query)
            val matchesFilter = when (state.filter) {
                "Disponible" -> product.isInStock
                "Novedades" -> !product.tag.isNullOrBlank()
                "Menos de S/ 20" -> product.price < 20.0
                "Favoritos" -> favoriteIds.value.contains(product.id)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    fun toggleFavorite(productId: String) {
        ProductRepository.toggleFavorite(productId)
        viewModelScope.launch { loadFavorites() }
    }

    fun product(id: String): Product? =
        _uiState.value.products.firstOrNull { it.id == id } ?: MockData.product(id)

    fun relatedTo(id: String): List<Product> =
        _uiState.value.products.filter { it.id != id }.take(6)
}

// ===========================================================================
// Carrito y checkout
// ===========================================================================

data class CheckoutUiState(
    val deliveryMethod: DeliveryMethod = DeliveryMethod.HOME,
    val paymentMethod: PaymentMethod = PaymentMethod.CARD,
    val cardNumber: String = "4532 8821 9012 8921",
    val cardExpiry: String = "08/26",
    val cardCvv: String = "456",
    val address: Address = Address(
        receiver = "Sofía Valentina Valdés",
        street = "Av. Larco 456, Dpto. 302",
        city = "Miraflores",
        state = "Lima",
        reference = "A espaldas del parque central",
        country = "Perú"
    ),
    val storeAddress: Address = Address(
        street = "Calle de los Libros 123",
        city = "San Isidro",
        country = "Perú"
    ),
    val couponCode: String = "",
    val couponMessage: String? = null,
    val discount: Double = 0.0,
    val acceptTerms: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val placedOrder: Order? = null,
    val error: String? = null
)

class CartViewModel : ViewModel() {

    val items: StateFlow<List<CartItem>> = CartRepository.items

    private val _checkout = MutableStateFlow(CheckoutUiState())
    val checkout: StateFlow<CheckoutUiState> = _checkout.asStateFlow()

    val itemsCount: StateFlow<Int> = items.map { list -> list.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val subtotal: StateFlow<Double> = items.map { list -> list.sumOf { it.lineTotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    fun increase(productId: String) = CartRepository.changeQuantity(productId, +1)
    fun decrease(productId: String) = CartRepository.changeQuantity(productId, -1)
    fun remove(productId: String) = CartRepository.remove(productId)
    fun add(product: Product, quantity: Int = 1) = CartRepository.add(product, quantity)

    /** Impuesto (IGV 18%) simulado sobre el subtotal. */
    fun taxFor(subtotal: Double): Double = String.format("%.2f", subtotal * 0.18).toDouble()

    fun shippingFor(method: DeliveryMethod): Double =
        if (method == DeliveryMethod.STORE_PICKUP) 0.0 else 12.0

    fun summary(method: DeliveryMethod = _checkout.value.deliveryMethod): com.app_frontend.data.model.CartSummary {
        val sub = subtotal.value
        val ship = shippingFor(method)
        return com.app_frontend.data.model.CartSummary(
            subtotal = sub,
            tax = taxFor(sub),
            shipping = ship,
            discount = _checkout.value.discount
        )
    }

    // --- Checkout ---

    fun onDeliveryMethodChange(value: DeliveryMethod) =
        _checkout.update { it.copy(deliveryMethod = value) }

    fun onPaymentMethodChange(value: PaymentMethod) =
        _checkout.update { it.copy(paymentMethod = value) }

    fun onCardNumberChange(value: String) = _checkout.update { it.copy(cardNumber = value) }
    fun onCardExpiryChange(value: String) = _checkout.update { it.copy(cardExpiry = value) }
    fun onCardCvvChange(value: String) = _checkout.update { it.copy(cardCvv = value) }
    fun onCouponChange(value: String) = _checkout.update { it.copy(couponCode = value, couponMessage = null) }
    fun onAcceptTermsChange(value: Boolean) = _checkout.update { it.copy(acceptTerms = value, error = null) }

    fun applyCoupon() {
        val code = _checkout.value.couponCode
        viewModelScope.launch {
            val (valid, message) = OrderRepository.applyCoupon(code)
            _checkout.update {
                it.copy(
                    couponMessage = message,
                    discount = if (valid) 20.0 else 0.0
                )
            }
        }
    }

    fun placeOrder(onDone: (Order) -> Unit) {
        val state = _checkout.value
        if (!state.acceptTerms) {
            _checkout.update { it.copy(error = "Debes aceptar los términos y condiciones.") }
            return
        }
        viewModelScope.launch {
            _checkout.update { it.copy(isPlacingOrder = true, error = null) }
            val order = OrderRepository.placeOrder(
                com.app_frontend.data.remote.dto.PlaceOrderRequestDto(
                    deliveryMethod = state.deliveryMethod.name,
                    paymentMethod = state.paymentMethod.name,
                    cardNumber = state.cardNumber.replace(" ", ""),
                    cardExpiry = state.cardExpiry,
                    cardCvv = state.cardCvv,
                    acceptTerms = state.acceptTerms
                )
            )
            _checkout.update { it.copy(isPlacingOrder = false, placedOrder = order) }
            order?.let { onDone(it) }
        }
    }

    fun clearPlacedOrder() = _checkout.update { it.copy(placedOrder = null) }
}

// ===========================================================================
// Pedidos
// ===========================================================================

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val filter: Int = 0,
    val selectedOrder: Order? = null
)

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val orders = OrderRepository.fetchOrders()
            _uiState.update { it.copy(orders = orders, isLoading = false) }
        }
    }

    fun onFilterChange(index: Int) = _uiState.update { it.copy(filter = index) }

    fun filteredOrders(): List<Order> = when (_uiState.value.filter) {
        1 -> _uiState.value.orders.filter { it.status != OrderStatus.DELIVERED }
        2 -> _uiState.value.orders.filter { it.status == OrderStatus.DELIVERED }
        else -> _uiState.value.orders
    }

    fun loadOrder(id: String) {
        viewModelScope.launch {
            val order = OrderRepository.fetchOrder(id)
            _uiState.update { it.copy(selectedOrder = order) }
        }
    }

    fun order(id: String): Order? =
        _uiState.value.selectedOrder?.takeIf { it.id == id || it.shortId == id }
            ?: MockData.order(id)
}

// ===========================================================================
// Notificaciones
// ===========================================================================

data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList(),
    val tab: NotificationTab = NotificationTab.ALL
)

class NotificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    val notifications: StateFlow<List<AppNotification>> = NotificationRepository.notifications

    init {
        viewModelScope.launch { NotificationRepository.refresh() }
    }

    fun onTabChange(tab: NotificationTab) = _uiState.update { it.copy(tab = tab) }

    fun visible(current: List<AppNotification>): List<AppNotification> =
        if (_uiState.value.tab == NotificationTab.ALL) current
        else current.filter { it.tab == _uiState.value.tab }

    fun markRead(id: String) = NotificationRepository.markRead(id)
    fun markAllRead() = NotificationRepository.markAllRead()

    fun unreadCount(current: List<AppNotification>): Int = current.count { !it.isRead }
}

// ===========================================================================
// Perfil
// ===========================================================================

data class ProfileUiState(
    val profile: UserProfile = MockData.user,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val message: String? = null,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val profile = ProfileRepository.fetchProfile()
            _uiState.update { it.copy(profile = profile, isLoading = false) }
        }
    }

    fun onFullNameChange(v: String) = _uiState.update { it.copy(profile = it.profile.copy(fullName = v), message = null) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(profile = it.profile.copy(email = v), message = null) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(profile = it.profile.copy(phone = v), message = null) }
    fun onStreetChange(v: String) = _uiState.update { it.copy(profile = it.profile.copy(address = it.profile.address.copy(street = v)), message = null) }
    fun onCityChange(v: String) = _uiState.update { it.copy(profile = it.profile.copy(address = it.profile.address.copy(city = v)), message = null) }
    fun onZipChange(v: String) = _uiState.update { it.copy(profile = it.profile.copy(address = it.profile.address.copy(zip = v)), message = null) }
    fun onNewsletterChange(v: Boolean) = _uiState.update { it.copy(profile = it.profile.copy(newsletter = v), message = null) }
    fun onOffersChange(v: Boolean) = _uiState.update { it.copy(profile = it.profile.copy(offers = v), message = null) }

    fun onCurrentPasswordChange(v: String) = _uiState.update { it.copy(currentPassword = v, message = null) }
    fun onNewPasswordChange(v: String) = _uiState.update { it.copy(newPassword = v, message = null) }
    fun onConfirmPasswordChange(v: String) = _uiState.update { it.copy(confirmPassword = v, message = null) }

    fun save() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, message = null) }
            val result = ProfileRepository.updateProfile(_uiState.value.profile)
            val passwordResult = if (_uiState.value.newPassword.isNotBlank()) {
                ProfileRepository.changePassword(_uiState.value.currentPassword, _uiState.value.newPassword)
            } else null

            passwordResult?.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = error.message ?: "No se pudo actualizar la contraseña."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    profile = result,
                    isSaving = false,
                    currentPassword = "",
                    newPassword = "",
                    confirmPassword = "",
                    message = "Cambios guardados correctamente."
                )
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null, error = null) }
}
