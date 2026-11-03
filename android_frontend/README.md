# Papel & Tinta — Frontend Android

App de librería y papelería escrita en **Kotlin + Jetpack Compose (Material3)**.
La interfaz replica los mockups de la carpeta `../intefase/`.

---

## 1. Pantallas implementadas

| Pantalla | Archivo | Mockup de referencia |
|---|---|---|
| Iniciar Sesión | `ui/screens/auth/LoginScreen.kt` | `Iniciar Sesión _ Papel & Tinta.png` |
| Registrarse | `ui/screens/auth/RegisterScreen.kt` | `Registrarse (Clásica) _ Papel & Tinta.png` |
| Inicio | `ui/screens/home/HomeScreen.kt` | `Html → Body.png` |
| Catálogo | `ui/screens/catalog/CatalogScreen.kt` | `Catálogo de Productos _ Papel & Tinta.png` |
| Detalle de producto | `ui/screens/product/ProductDetailScreen.kt` | `Detalles del Producto _ Papel & Tinta.png` |
| Carrito | `ui/screens/cart/CartScreen.kt` | `Carrito de Compras _ Papel & Tinta.png` |
| Método de pago y entrega | `ui/screens/checkout/CheckoutScreen.kt` | `Método de Pago y Entrega _ Papel & Tinta.png` |
| Confirmación de pedido | `ui/screens/checkout/ConfirmationScreen.kt` | `Confirmación de Pedido _ Papel & Tinta.png` |
| Pedido exitoso | `ui/screens/checkout/SuccessScreen.kt` | `Pedido Exitoso _ Papel & Tinta.png` |
| Historial de pedidos | `ui/screens/orders/OrdersScreen.kt` | `Historial de Pedidos _ Papel & Tinta.png` |
| Detalle del pedido | `ui/screens/orders/OrderDetailScreen.kt` | `Detalles del Pedido.png` |
| Favoritos | `ui/screens/favorites/FavoritesScreen.kt` | `body mis favoritos.png` |
| Notificaciones | `ui/screens/notifications/NotificationsScreen.kt` | `Centro de Notificaciones _ Papel & Tinta.png` |
| Mi Perfil | `ui/screens/profile/ProfileScreen.kt` | `Html → Body (1).png` |
| Editar perfil | `ui/screens/profile/EditProfileScreen.kt` | `Main.png` |

---

## 2. Cómo conectar el backend Java (microservicios)

El frontend ya está preparado para consumir la API; **solo hay que apuntar la URL base**.

### 2.1 URL base

Está definida en `data/remote/ApiConfig.kt`:

```kotlin
// Por defecto, apunta al localhost de la máquina host desde el emulador:
"http://10.0.2.2:8080/api/"
```

| Escenario | Valor a usar |
|---|---|
| Emulador Android | `http://10.0.2.2:8080/api/` |
| Dispositivo físico (misma Wi-Fi) | `http://192.168.1.X:8080/api/` |
| Producción | `https://api.tudominio.com/api/` |

Para cambiarla sin tocar más código:

```kotlin
ApiConfig.overrideBaseUrl("http://192.168.1.50:8080/api/")
```

### 2.2 Rutas esperadas por microservicio

El contrato REST está en `data/remote/ApiService.kt`:

```
/auth            POST /login, POST /register, GET /me, POST /logout
/catalog         GET  /products, GET /products/{id}, GET /favorites
                 PUT  /products/{id}/favorite
/cart            GET  /cart, POST /cart/items, PUT|DELETE /cart/items/{id}
                 POST /cart/coupon, DELETE /cart
/orders          GET  /orders/checkout/summary, GET /orders/checkout/delivery-options
                 POST /orders, GET /orders, GET /orders/{id}
/users           GET  /users/me, PUT /users/me, PUT /users/me/password
/notifications   GET  /notifications, PUT /notifications/{id}/read, POST /read-all
```

### 2.3 Formato de respuesta esperado

```json
{ "success": true, "data": { ... }, "message": null }
```

Los DTOs están en `data/remote/dto/Dtos.kt` y todos sus campos son anulables,
de modo que un backend que envíe menos campos no rompe la app.

### 2.4 Estrategia de degradación (hoy funciona sin backend)

Los repositorios (`data/repository/Repositories.kt`) siguen este patrón:

```
¿responde la API?  →  sí: usa los datos remotos
                   →  no: usa datos locales de demostración (MockData)
```

El flag `ApiConfig.backendAvailable` indica de qué origen vienen los datos.

**En cuanto el backend Java esté desplegado, la app pasará a usarlo automáticamente
sin modificar ninguna pantalla.**

### 2.5 Autenticación

- El token JWT devuelto por `POST /auth/login` se guarda en
  `data/remote/SessionStore.kt` (SharedPreferences).
- `ApiConfig.okHttpClient` lo adjunta automáticamente como `Authorization: Bearer …`
  en cada petición.

### 2.6 Notas de red

- Permiso `INTERNET` añadido en `AndroidManifest.xml`.
- `res/xml/network_security_config.xml` permite HTTP en desarrollo
  (cambiar a `false` cuando haya HTTPS en producción).

---

## 3. Estructura del código

```
app/src/main/java/com/app_frontend/
├── MainActivity.kt              # Activity + inicialización de sesión
├── PapelTintaApp.kt             # NavHost + scaffold (barra superior/inferior)
├── data/
│   ├── model/Models.kt          # Modelos de dominio
│   ├── local/MockData.kt        # Datos de demostración
│   ├── remote/
│   │   ├── ApiConfig.kt         # ← URL BASE DEL BACKEND
│   │   ├── ApiService.kt        # ← ENDPOINTS
│   │   ├── SessionStore.kt      # Token de sesión
│   │   └── dto/                 # DTOs + mapeo a dominio
│   └── repository/Repositories.kt  # API con respaldo local
├── ui/
│   ├── theme/                   # Paleta "Papel & Tinta" + tipografía
│   ├── components/              # Botones, chips, campos, barras, tarjetas
│   ├── navigation/Routes.kt     # Rutas
│   ├── viewmodel/AppViewModels.kt
│   └── screens/                 # Las 15 pantallas
```

---

## 4. Compilar y ejecutar

```bash
cd android_frontend

# APK de depuración
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
```

Requisitos: JDK 17+ y Android SDK (platform-37).

---

## 5. Decisiones pendientes / personalizables

- **Moneda**: `AppConfig.CURRENCY = "S/"` (se unificó; los mockups mezclan `€` y `S/`).
- **Portadas de producto**: `Product.imageUrl` es `null` en los datos de
  demostración, así que se muestra una portada con el estilo de la marca.
  Cuando el backend envíe la URL real, Coil la cargará automáticamente.
- **Login social (Google/Apple)**: botones visibles, OAuth pendiente del backend.
- **Filtros y búsquedas**: funcionan sobre datos locales hoy; el parámetro
  `GET /catalog/products?category=&search=` ya está preparado.
