package com.app_frontend.data.model

/**
 * Modelos de dominio de la app "Papel & Tinta".
 *
 * Estos modelos son los que usan las pantallas (UI). El backend Java en
 * microservicios devuelve DTOs que se mapean a estos tipos en `data/remote/dto/`.
 */

// ---------------------------------------------------------------------------
// Moneda / formato
// ---------------------------------------------------------------------------

object AppConfig {
    /** Moneda única de la app (el backend devolverá montos numéricos). */
    const val CURRENCY = "S/"
    /** URL base de la API Java (ver `data/remote/ApiConfig.kt`). */
    const val APP_VERSION = "v2.4.1"
    const val STORE_NAME = "Papel & Tinta Librería Artesanal"
    const val STORE_LOCATION = "Lima, Perú"
}

fun Double.asPrice(): String = String.format("%.2f", this)

// ---------------------------------------------------------------------------
// Productos
// ---------------------------------------------------------------------------

enum class StockStatus { AVAILABLE, LOW, SOLD_OUT }

/** Opción de color de tapa (mockup "Detalles del Producto"). */
data class ProductColor(
    val id: String,
    val name: String,
    val hex: String
)

/** Opción de gramaje del papel con su recargo. */
data class PaperWeight(
    val id: String,
    val label: String,
    val extraPrice: Double = 0.0
)

/** Par clave/valor de la ficha técnica (Materiales, Dimensiones, ...). */
data class SpecItem(
    val label: String,
    val value: String
)

data class Product(
    val id: String,
    val name: String,
    /** Categoría corta mostrada en mayúsculas sobre el título. */
    val category: String,
    val author: String = "",
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String? = null,
    val rating: Double = 0.0,
    val reviews: Int = 0,
    val stock: StockStatus = StockStatus.AVAILABLE,
    /** Unidades restantes cuando [stock] == LOW. */
    val stockCount: Int = 0,
    /** Etiqueta superior de la tarjeta: "Novedad", "Más Vendido"... */
    val tag: String? = null,
    /** Etiqueta del héroe: "Edición limitada"... */
    val badge: String? = null,
    val colors: List<ProductColor> = emptyList(),
    val paperWeights: List<PaperWeight> = emptyList(),
    val description: String = "",
    val specs: List<SpecItem> = emptyList(),
    val isFavorite: Boolean = false
) {
    val isInStock: Boolean get() = stock != StockStatus.SOLD_OUT

    val stockLabel: String
        get() = when (stock) {
            StockStatus.AVAILABLE -> "Disponible"
            StockStatus.LOW -> "Pocas unidades"
            StockStatus.SOLD_OUT -> "Agotado"
        }
}

// ---------------------------------------------------------------------------
// Carrito
// ---------------------------------------------------------------------------

data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val lineTotal: Double get() = product.price * quantity
}

data class CartSummary(
    val subtotal: Double,
    val tax: Double,
    val shipping: Double,
    val discount: Double = 0.0
) {
    val total: Double get() = subtotal + tax + shipping - discount
}

// ---------------------------------------------------------------------------
// Pedidos
// ---------------------------------------------------------------------------

enum class OrderStatus(val label: String) {
    CONFIRMED("Confirmado"),
    PACKED("Empacado"),
    ON_ROUTE("En ruta"),
    DELIVERED("Entregado"),
    PREPARING("En preparación"),
    IN_TRANSIT("En camino");

    /** Posición en la barra de progreso 1..4 (0 = sin progreso). */
    val step: Int
        get() = when (this) {
            CONFIRMED -> 1
            PACKED -> 2
            ON_ROUTE, IN_TRANSIT, PREPARING -> 3
            DELIVERED -> 4
        }
}

data class OrderItem(
    val product: Product,
    val quantity: Int,
    val unitPrice: Double
) {
    val lineTotal: Double get() = unitPrice * quantity
}

data class Address(
    val receiver: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val country: String = "Perú",
    val reference: String = "",
    val phone: String = ""
) {
    val singleLine: String
        get() = listOf(street, city, state).filter { it.isNotBlank() }.joinToString(", ")
}

enum class DeliveryMethod { HOME, STORE_PICKUP }

enum class PaymentMethod(val label: String, val subtitle: String) {
    CARD("Tarjeta de Crédito o Débito", "Visa, Mastercard, Amex"),
    QR("Yape o Plin", "Pago instantáneo con QR"),
    CASH("Pago en efectivo al recibir", "Ten el monto exacto por favor")
}

data class Order(
    val id: String,
    val date: String,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val subtotal: Double,
    val tax: Double,
    val shipping: Double,
    val shippingMethod: DeliveryMethod = DeliveryMethod.HOME,
    val shippingAddress: Address = Address(),
    val paymentMethod: PaymentMethod = PaymentMethod.CARD,
    val paymentLastDigits: String = "4892",
    val paymentBrand: String = "Visa",
    val deliveryEstimate: String = "",
    val shippingLabel: String = "Envío estándar a domicilio",
    val pointsEarned: Int = 0
) {
    val total: Double get() = subtotal + tax + shipping
    val itemCount: Int get() = items.sumOf { it.quantity }
    val shortId: String get() = id.removePrefix("#")
}

// ---------------------------------------------------------------------------
// Notificaciones
// ---------------------------------------------------------------------------

enum class NotificationType { ORDER, REWARD, PREORDER, SYSTEM }

enum class NotificationTab(val label: String) {
    ALL("Todas"),
    ORDERS("Pedidos"),
    REWARDS("Recompensas"),
    NEWS("Novedades")
}

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    /** "Hace 2 h", "Ayer, 18:30"... */
    val timeLabel: String,
    val isRead: Boolean = false,
    /** Sección del agrupador: "HOY" / "AYER". */
    val group: String = "HOY",
    /** Línea opcional destacada dentro de la tarjeta. */
    val highlight: String? = null,
    val actionLabel: String? = null,
    /** Badge numérico de progreso (puntos). */
    val progressCurrent: Int? = null,
    val progressTotal: Int? = null
) {
    val tab: NotificationTab
        get() = when (type) {
            NotificationType.ORDER -> NotificationTab.ORDERS
            NotificationType.REWARD -> NotificationTab.REWARDS
            NotificationType.PREORDER -> NotificationTab.NEWS
            NotificationType.SYSTEM -> NotificationTab.NEWS
        }
}

// ---------------------------------------------------------------------------
// Usuario / perfil
// ---------------------------------------------------------------------------

data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val memberSince: String = "",
    val readingLevel: String = "Lector Voraz",
    val levelNumber: Int = 2,
    val points: Int = 450,
    val address: Address = Address(),
    val newsletter: Boolean = true,
    val offers: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val paymentSummary: String = "Visa terminada en •••• 4892 y billetera Yape/Plin",
    val addressSummary: String = "Principal: Av. San Martín 1420, Dpto 402 - Miraflores"
) {
    val pointsAsMoney: Double get() = points / 10.0
    val initials: String
        get() = fullName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}

data class AuthSession(
    val token: String? = null,
    val user: UserProfile? = null
) {
    val isLoggedIn: Boolean get() = token != null
}

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)
