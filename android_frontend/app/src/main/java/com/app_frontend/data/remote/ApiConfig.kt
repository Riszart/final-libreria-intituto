package com.app_frontend.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración del cliente REST hacia el backend Java (microservicios).
 *
 * ## Cómo apuntar a tu backend
 *
 * El backend será un conjunto de microservicios expuestos detrás de un único
 * punto de entrada (API Gateway). Aquí se define la URL base de ese punto de
 * entrada y las rutas de cada servicio viven en [ApiService].
 *
 * | Escenario                     | BASE_URL                              |
 * |-------------------------------|---------------------------------------|
 * | Emulador Android              | `http://10.0.2.2:8080/api/`           |
 * | Dispositivo físico (misma red)| `http://192.168.1.X:8080/api/`        |
 * | Producción                    | `https://api.tudominio.com/api/`      |
 *
 * Nota: `10.0.2.2` es el alias que el emulador usa para llegar al `localhost`
 * de la máquina host.
 *
 * ## Rutas esperadas por microservicio
 *
 * ```
 * /auth       -> servicio de autenticación
 * /catalog    -> servicio de catálogo y favoritos
 * /cart       -> servicio de carrito
 * /orders     -> servicio de pedidos
 * /users      -> servicio de usuarios y perfil
 * /notifications -> servicio de notificaciones
 * ```
 */
object ApiConfig {

    /**
     * URL base de la API Java. Cámbiala aquí (o inyecta la tuya desde
     * `local.properties` con `api.baseUrl=...`) para conectar el frontend.
     */
    var BASE_URL: String = defaultBaseUrl()
        private set

    private fun defaultBaseUrl(): String {
        // Permite sobreescribir sin tocar código: api.baseUrl=http://192.168.1.10:8080/api/
        val fromProps = runCatching {
            val properties = java.util.Properties()
            // Reserva para futura lectura desde BuildConfig; hoy usa el valor por defecto.
            properties.getProperty("api.baseUrl")
        }.getOrNull()
        return fromProps?.takeIf { it.isNotBlank() } ?: "http://10.0.2.2:8080/api/"
    }

    /** Fuerza otra URL base en tiempo de ejecución (útil en pruebas). */
    fun overrideBaseUrl(url: String) {
        BASE_URL = if (url.endsWith("/")) url else "$url/"
        retrofit = null
    }

    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 20L

    @Volatile
    private var retrofit: Retrofit? = null

    private val logging: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                // Adjunta el token de sesión cuando existe (JWT del backend Java).
                val token = SessionStore.token
                val request = if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }
                chain.proceed(request)
            }
            .build()
    }

    /** Instancia Retrofit compartida (lazy y reutilizable). */
    @Synchronized
    fun retrofit(): Retrofit = retrofit ?: Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .also { retrofit = it }

    /** API de servicios. */
    val api: ApiService get() = retrofit().create(ApiService::class.java)

    /** `true` cuando el backend responde; lo usan los repositorios para decidir. */
    @Volatile
    var backendAvailable: Boolean = false
        internal set
}
