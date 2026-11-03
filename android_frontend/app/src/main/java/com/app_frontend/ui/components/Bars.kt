package com.app_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_frontend.ui.theme.AmberLogo
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextPrimary

/**
 * Barras de navegación de la app (barra de marca superior + barra inferior).
 */

/** Rutas principales compartidas por la barra inferior. */
object BottomTabs {
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val CART = "cart"
    const val PROFILE = "profile"
}

private data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val tabs = listOf(
    BottomTab(BottomTabs.HOME, "Inicio", Icons.Rounded.Home),
    BottomTab(BottomTabs.CATALOG, "Catálogo", Icons.Rounded.MenuBook),
    BottomTab(BottomTabs.FAVORITES, "Favoritos", Icons.Rounded.FavoriteBorder),
    BottomTab(BottomTabs.CART, "Carrito", Icons.Rounded.ShoppingBag),
    BottomTab(BottomTabs.PROFILE, "Perfil", Icons.Rounded.Person)
)

/**
 * Barra inferior con las 5 secciones de la app.
 * Marca la pestaña activa y muestra el contador del carrito.
 */
@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    cartCount: Int = 0
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .navigationBarsPadding()
            .padding(top = 8.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigate(tab.route) }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Box {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) InkNavy else com.app_frontend.ui.theme.TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    if (tab.route == BottomTabs.CART && cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Terracota),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartCount.toString(),
                                color = TextOnDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) InkNavy else com.app_frontend.ui.theme.TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Barra superior de la marca: logo "Papel & Tinta" + acciones
 * (buscar, favoritos, notificaciones con badge, perfil).
 */
@Composable
fun AppTopBar(
    onSearch: () -> Unit,
    onFavorites: () -> Unit,
    onNotifications: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier,
    unreadCount: Int = 0,
    showSearch: Boolean = true,
    showFavorites: Boolean = true,
    showNotifications: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoStories,
            contentDescription = null,
            tint = AmberLogo,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = "Papel & Tinta",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .clickable { onSearch() }
                .padding(start = 6.dp)
        )

        if (showSearch) {
            TopBarAction(icon = Icons.Rounded.Search, contentDescription = "Buscar", onClick = onSearch)
        }
        if (showFavorites) {
            TopBarAction(
                icon = Icons.Rounded.FavoriteBorder,
                contentDescription = "Favoritos",
                onClick = onFavorites
            )
        }
        if (showNotifications) {
            TopBarAction(
                icon = Icons.Rounded.Notifications,
                contentDescription = "Notificaciones",
                onClick = onNotifications,
                badgeCount = unreadCount
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(50))
                .background(InkNavy)
                .clickable(onClick = onProfile),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Perfil",
                tint = TextOnDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TopBarAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    badgeCount: Int = 0
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = InkNavy,
            modifier = Modifier.size(23.dp)
        )
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(9.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AmberLogo)
            )
        }
    }
}

/**
 * Cabecera de pantalla secundaria: botón atrás + título centrado + acciones.
 * Usada en detalle de producto, pedidos, checkout, notificaciones, etc.
 */
@Composable
fun SubScreenHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: (@Composable () -> Unit)? = null,
    showBack: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(50))
                    .background(com.app_frontend.ui.theme.CreamDeep)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Volver",
                    tint = InkNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        actions?.invoke()
    }
}

/** Icono de acción suelto para cabeceras. */
@Composable
fun HeaderAction(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = InkNavy,
            modifier = Modifier.size(22.dp)
        )
    }
}

/** Línea divisoria usada dentro de cabeceras. */
@Composable
fun HeaderDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(1.dp)
            .background(Border)
    )
}
