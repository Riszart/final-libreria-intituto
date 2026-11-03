package com.app_frontend.data.remote.dto

/**
 * DTOs (Data Transfer Objects) del contrato REST con el backend Java.
 *
 * Todos los campos son anulables con valor por defecto para que la app siga
 * funcionando aunque el backend todavía no exista o envíe campos extra:
 * - Gson ignora los campos desconocidos.
 * - Un campo ausente no rompe la app (queda `null` / valor por defecto).
 *
 * **Importante:** ajusta estos nombres a los que exponga realmente la API Java
 * cuando se implemente; el mapeo a modelos de UI está en `*.toDomain()`.
 */

/** Envoltorio estándar de respuesta: `{ "success": true, "data": ..., "message": ... }`. */
data class ApiResponse<T>(
    val success: Boolean? = true,
    val data: T? = null,
    val message: String? = null
)

// ---------------------------------------------------------------------------
// Autenticación
// ---------------------------------------------------------------------------

data class LoginRequestDto(
    val email: String? = null,
    val password: String? = null
)

data class RegisterRequestDto(
    val fullName: String? = null,
    val email: String? = null,
    val password: String? = null
)

data class AuthResponseDto(
    val token: String? = null,
    val tokenType: String? = "Bearer",
    val user: UserDto? = null
)

// ---------------------------------------------------------------------------
// Usuario
// ---------------------------------------------------------------------------

data class UserDto(
    val id: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val memberSince: String? = null,
    val readingLevel: String? = null,
    val levelNumber: Int? = null,
    val points: Int? = null,
    val newsletter: Boolean? = null,
    val offers: Boolean? = null,
    val notificationsEnabled: Boolean? = null,
    val address: AddressDto? = null
)

data class AddressDto(
    val receiver: String? = null,
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val country: String? = null,
    val reference: String? = null,
    val phone: String? = null
)

data class UpdateProfileRequestDto(
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: AddressDto? = null,
    val newsletter: Boolean? = null,
    val offers: Boolean? = null
)

data class ChangePasswordRequestDto(
    val currentPassword: String? = null,
    val newPassword: String? = null
)

// ---------------------------------------------------------------------------
// Catálogo
// ---------------------------------------------------------------------------

data class ProductDto(
    val id: String? = null,
    val name: String? = null,
    val category: String? = null,
    val author: String? = null,
    val price: Double? = null,
    val originalPrice: Double? = null,
    val imageUrl: String? = null,
    val rating: Double? = null,
    val reviews: Int? = null,
    val stockStatus: String? = null,
    val stockCount: Int? = null,
    val tag: String? = null,
    val badge: String? = null,
    val description: String? = null,
    val favorite: Boolean? = null,
    val colors: List<ProductColorDto>? = null,
    val paperWeights: List<PaperWeightDto>? = null,
    val specs: List<SpecItemDto>? = null
)

data class ProductColorDto(
    val id: String? = null,
    val name: String? = null,
    val hex: String? = null
)

data class PaperWeightDto(
    val id: String? = null,
    val label: String? = null,
    val extraPrice: Double? = null
)

data class SpecItemDto(
    val label: String? = null,
    val value: String? = null
)

data class ProductQuery(
    val category: String? = null,
    val search: String? = null,
    val inStockOnly: Boolean? = null,
    val favoritesOnly: Boolean? = null
)

data class FavoriteRequestDto(val favorite: Boolean? = null)

// ---------------------------------------------------------------------------
// Carrito
// ---------------------------------------------------------------------------

data class CartItemDto(
    val id: String? = null,
    val product: ProductDto? = null,
    val quantity: Int? = null
)

data class CartDto(
    val items: List<CartItemDto>? = null,
    val subtotal: Double? = null,
    val tax: Double? = null,
    val shipping: Double? = null,
    val discount: Double? = null
)

data class AddToCartRequestDto(
    val productId: String? = null,
    val quantity: Int? = 1,
    val colorId: String? = null,
    val paperWeightId: String? = null
)

data class UpdateCartItemRequestDto(val quantity: Int? = null)

data class CouponRequestDto(val code: String? = null)

data class CouponResponseDto(
    val valid: Boolean? = null,
    val code: String? = null,
    val discount: Double? = null,
    val message: String? = null
)

// ---------------------------------------------------------------------------
// Checkout / Pedidos
// ---------------------------------------------------------------------------

data class CheckoutSummaryDto(
    val items: List<CartItemDto>? = null,
    val subtotal: Double? = null,
    val tax: Double? = null,
    val shipping: Double? = null,
    val discount: Double? = null,
    val total: Double? = null
)

data class DeliveryOptionDto(
    val id: String? = null,
    val label: String? = null,
    val description: String? = null,
    val cost: Double? = null,
    val address: AddressDto? = null
)

data class PlaceOrderRequestDto(
    val deliveryMethod: String? = null,
    val paymentMethod: String? = null,
    val cardNumber: String? = null,
    val cardExpiry: String? = null,
    val cardCvv: String? = null,
    val address: AddressDto? = null,
    val couponCode: String? = null,
    val acceptTerms: Boolean? = null
)

data class OrderDto(
    val id: String? = null,
    val date: String? = null,
    val status: String? = null,
    val items: List<OrderItemDto>? = null,
    val subtotal: Double? = null,
    val tax: Double? = null,
    val shipping: Double? = null,
    val shippingLabel: String? = null,
    val deliveryMethod: String? = null,
    val deliveryEstimate: String? = null,
    val shippingAddress: AddressDto? = null,
    val paymentMethod: String? = null,
    val paymentLastDigits: String? = null,
    val paymentBrand: String? = null,
    val pointsEarned: Int? = null,
    val summaryTitle: String? = null,
    val itemCount: Int? = null
)

data class OrderItemDto(
    val id: String? = null,
    val product: ProductDto? = null,
    val quantity: Int? = null,
    val unitPrice: Double? = null
)

data class OrderStatusUpdateDto(
    val status: String? = null,
    val message: String? = null,
    val updatedAt: String? = null
)

// ---------------------------------------------------------------------------
// Notificaciones
// ---------------------------------------------------------------------------

data class NotificationDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val message: String? = null,
    val timeLabel: String? = null,
    val read: Boolean? = null,
    val group: String? = null,
    val highlight: String? = null,
    val actionLabel: String? = null,
    val progressCurrent: Int? = null,
    val progressTotal: Int? = null
)
