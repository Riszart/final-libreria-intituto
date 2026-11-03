package com.app_frontend.data.remote

import com.app_frontend.data.remote.dto.AddToCartRequestDto
import com.app_frontend.data.remote.dto.ApiResponse
import com.app_frontend.data.remote.dto.AuthResponseDto
import com.app_frontend.data.remote.dto.CartDto
import com.app_frontend.data.remote.dto.ChangePasswordRequestDto
import com.app_frontend.data.remote.dto.CheckoutSummaryDto
import com.app_frontend.data.remote.dto.CouponRequestDto
import com.app_frontend.data.remote.dto.CouponResponseDto
import com.app_frontend.data.remote.dto.DeliveryOptionDto
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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Contrato REST con el backend Java (microservicios).
 *
 * Cada bloque de endpoints corresponde a un microservicio. Las rutas siguen la
 * convención REST de Spring Boot y están pensadas para exponerse detrás de un
 * API Gateway con el prefijo `/api`.
 *
 * Todas las llamadas devuelven `Response<ApiResponse<T>>` para que los
 * repositorios puedan detectar tanto errores HTTP como fallos de red.
 */
interface ApiService {

    // -----------------------------------------------------------------------
    // /auth  -> microservicio de autenticación
    // -----------------------------------------------------------------------

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ApiResponse<AuthResponseDto>>

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<ApiResponse<AuthResponseDto>>

    @GET("auth/me")
    suspend fun me(): Response<ApiResponse<UserDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // -----------------------------------------------------------------------
    // /catalog  -> microservicio de catálogo y favoritos
    // -----------------------------------------------------------------------

    @GET("catalog/products")
    suspend fun getProducts(@QueryMap filters: Map<String, String>): Response<ApiResponse<List<ProductDto>>>

    @GET("catalog/products/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<ApiResponse<ProductDto>>

    @GET("catalog/favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<ProductDto>>>

    @PUT("catalog/products/{id}/favorite")
    suspend fun setFavorite(
        @Path("id") id: String,
        @Body body: FavoriteRequestDto
    ): Response<ApiResponse<Unit>>

    // -----------------------------------------------------------------------
    // /cart  -> microservicio de carrito
    // -----------------------------------------------------------------------

    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<CartDto>>

    @POST("cart/items")
    suspend fun addToCart(@Body body: AddToCartRequestDto): Response<ApiResponse<CartDto>>

    @PUT("cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: String,
        @Body body: UpdateCartItemRequestDto
    ): Response<ApiResponse<CartDto>>

    @DELETE("cart/items/{id}")
    suspend fun removeCartItem(@Path("id") id: String): Response<ApiResponse<CartDto>>

    @POST("cart/coupon")
    suspend fun applyCoupon(@Body body: CouponRequestDto): Response<ApiResponse<CouponResponseDto>>

    @DELETE("cart")
    suspend fun clearCart(): Response<ApiResponse<Unit>>

    // -----------------------------------------------------------------------
    // /orders  -> microservicio de pedidos y checkout
    // -----------------------------------------------------------------------

    @GET("orders/checkout/summary")
    suspend fun getCheckoutSummary(): Response<ApiResponse<CheckoutSummaryDto>>

    @GET("orders/checkout/delivery-options")
    suspend fun getDeliveryOptions(): Response<ApiResponse<List<DeliveryOptionDto>>>

    @POST("orders")
    suspend fun placeOrder(@Body body: PlaceOrderRequestDto): Response<ApiResponse<OrderDto>>

    @GET("orders")
    suspend fun getOrders(@QueryMap filters: Map<String, String>): Response<ApiResponse<List<OrderDto>>>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): Response<ApiResponse<OrderDto>>

    // -----------------------------------------------------------------------
    // /users  -> microservicio de usuarios y perfil
    // -----------------------------------------------------------------------

    @GET("users/me")
    suspend fun getProfile(): Response<ApiResponse<UserDto>>

    @PUT("users/me")
    suspend fun updateProfile(@Body body: UpdateProfileRequestDto): Response<ApiResponse<UserDto>>

    @PUT("users/me/password")
    suspend fun changePassword(@Body body: ChangePasswordRequestDto): Response<ApiResponse<Unit>>

    // -----------------------------------------------------------------------
    // /notifications  -> microservicio de notificaciones
    // -----------------------------------------------------------------------

    @GET("notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<NotificationDto>>>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("notifications/read-all")
    suspend fun markAllAsRead(): Response<ApiResponse<Unit>>
}
