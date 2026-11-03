package com.app_frontend.data.remote.dto

import com.app_frontend.data.model.Address
import com.app_frontend.data.model.AppNotification
import com.app_frontend.data.model.CartItem
import com.app_frontend.data.model.DeliveryMethod
import com.app_frontend.data.model.NotificationType
import com.app_frontend.data.model.Order
import com.app_frontend.data.model.OrderItem
import com.app_frontend.data.model.OrderStatus
import com.app_frontend.data.model.PaperWeight
import com.app_frontend.data.model.PaymentMethod
import com.app_frontend.data.model.Product
import com.app_frontend.data.model.ProductColor
import com.app_frontend.data.model.SpecItem
import com.app_frontend.data.model.StockStatus
import com.app_frontend.data.model.UserProfile

/**
 * Conversión de DTOs (backend Java) a modelos de dominio (UI).
 *
 * Tolerante a campos nulos: si el backend aún no envía un dato, se usa un
 * valor por defecto razonable en lugar de romper la pantalla.
 */

fun AddressDto?.toDomain(): Address = Address(
    receiver = this?.receiver.orEmpty(),
    street = this?.street.orEmpty(),
    city = this?.city.orEmpty(),
    state = this?.state.orEmpty(),
    zip = this?.zip.orEmpty(),
    country = this?.country ?: "Perú",
    reference = this?.reference.orEmpty(),
    phone = this?.phone.orEmpty()
)

fun ProductDto.toDomain(): Product = Product(
    id = id ?: "",
    name = name ?: "Producto",
    category = category ?: "Papelería",
    author = author ?: "",
    price = price ?: 0.0,
    originalPrice = originalPrice,
    imageUrl = imageUrl,
    rating = rating ?: 0.0,
    reviews = reviews ?: 0,
    stock = stockStatus.toStockStatus(),
    stockCount = stockCount ?: 0,
    tag = tag,
    badge = badge,
    description = description ?: "",
    isFavorite = favorite ?: false,
    colors = colors.orEmpty().map {
        ProductColor(it.id ?: "", it.name ?: "", it.hex ?: "#16263F")
    },
    paperWeights = paperWeights.orEmpty().map {
        PaperWeight(it.id ?: "", it.label ?: "", it.extraPrice ?: 0.0)
    },
    specs = specs.orEmpty().map { SpecItem(it.label ?: "", it.value ?: "") }
)

private fun String?.toStockStatus(): StockStatus = when (this?.uppercase()) {
    "SOLD_OUT", "AGOTADO", "OUT_OF_STOCK" -> StockStatus.SOLD_OUT
    "LOW", "POCAS", "LOW_STOCK" -> StockStatus.LOW
    else -> StockStatus.AVAILABLE
}

fun CartItemDto.toDomain(): CartItem = CartItem(
    product = (product ?: ProductDto()).toDomain(),
    quantity = quantity ?: 1
)

fun OrderItemDto.toDomain(): OrderItem = OrderItem(
    product = (product ?: ProductDto()).toDomain(),
    quantity = quantity ?: 1,
    unitPrice = unitPrice ?: 0.0
)

fun OrderDto.toDomain(): Order {
    val rawStatus = status?.uppercase().orEmpty()
    val parsed = when (rawStatus) {
        "DELIVERED", "ENTREGADO" -> OrderStatus.DELIVERED
        "ON_ROUTE", "EN_RUTA", "IN_TRANSIT" -> OrderStatus.ON_ROUTE
        "PACKED", "EMPACADO" -> OrderStatus.PACKED
        "PREPARING", "EN_PREPARACION" -> OrderStatus.PREPARING
        "IN_TRANSIT", "EN_CAMINO" -> OrderStatus.IN_TRANSIT
        else -> OrderStatus.CONFIRMED
    }
    val delivery = deliveryMethod?.uppercase()
    return Order(
        id = id ?: "",
        date = date ?: "",
        status = parsed,
        items = items.orEmpty().map { it.toDomain() },
        subtotal = subtotal ?: 0.0,
        tax = tax ?: 0.0,
        shipping = shipping ?: 0.0,
        shippingMethod = if (delivery == "STORE_PICKUP") DeliveryMethod.STORE_PICKUP else DeliveryMethod.HOME,
        shippingAddress = shippingAddress.toDomain(),
        paymentMethod = paymentMethod.toPaymentMethod(),
        paymentLastDigits = paymentLastDigits ?: "",
        paymentBrand = paymentBrand ?: "Visa",
        deliveryEstimate = deliveryEstimate ?: "",
        shippingLabel = shippingLabel ?: "",
        pointsEarned = pointsEarned ?: 0
    )
}

private fun String?.toPaymentMethod(): PaymentMethod = when (this?.uppercase()) {
    "QR", "YAPE", "PLIN" -> PaymentMethod.QR
    "CASH", "EFECTIVO" -> PaymentMethod.CASH
    else -> PaymentMethod.CARD
}

fun UserDto.toDomain(): UserProfile = UserProfile(
    id = id ?: "",
    fullName = fullName ?: "",
    email = email ?: "",
    phone = phone ?: "",
    avatarUrl = avatarUrl,
    memberSince = memberSince ?: "",
    readingLevel = readingLevel ?: "Lector Voraz",
    levelNumber = levelNumber ?: 1,
    points = points ?: 0,
    address = address.toDomain(),
    newsletter = newsletter ?: true,
    offers = offers ?: true,
    notificationsEnabled = notificationsEnabled ?: true
)

fun NotificationDto.toDomain(): AppNotification {
    val parsedType = when (type?.uppercase()) {
        "ORDER", "PEDIDO" -> NotificationType.ORDER
        "REWARD", "RECOMPENSA" -> NotificationType.REWARD
        "PREORDER", "PREVENTA" -> NotificationType.PREORDER
        else -> NotificationType.SYSTEM
    }
    return AppNotification(
        id = id ?: "",
        type = parsedType,
        title = title ?: "",
        message = message ?: "",
        timeLabel = timeLabel ?: "",
        isRead = read ?: false,
        group = group ?: "HOY",
        highlight = highlight,
        actionLabel = actionLabel,
        progressCurrent = progressCurrent,
        progressTotal = progressTotal
    )
}
