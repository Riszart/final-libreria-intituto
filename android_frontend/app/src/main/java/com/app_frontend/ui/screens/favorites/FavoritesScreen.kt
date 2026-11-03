/**
 * Pantalla "Mis Favoritos" de la app Papel & Tinta.
 *
 * Mockup de referencia: `intefase/body mis favoritos.png`
 */
package com.app_frontend.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.model.Product
import com.app_frontend.data.repository.CartRepository
import com.app_frontend.ui.components.EmptyState
import com.app_frontend.ui.components.PriceText
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.components.ProductRowImage
import com.app_frontend.ui.components.SecondaryButton
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.components.SelectableChip
import com.app_frontend.ui.components.StockBadge
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.ShopViewModel

/** Categorías consideradas "papelería" a la hora de agrupar los favoritos. */
private val stationeryCategories = listOf(
    "Papelería", "Cuadernos", "Plumas", "Oficina", "Escritura", "Arte", "Accesorios"
)

private const val FILTER_ALL = "Todos"
private const val FILTER_BOOKS = "Libros"
private const val FILTER_STATIONERY = "Papelería"

/** `true` cuando la categoría del producto es útiles de papelería (no un libro). */
private fun isStationery(category: String): Boolean =
    stationeryCategories.any { category.contains(it, ignoreCase = true) }

/**
 * Pantalla "Mis Favoritos".
 *
 * Lista los productos guardados en la lista de deseos con sus filtros de
 * categoría, su estado de stock y la acción principal (añadir al carrito o
 * aviso de reposición cuando está agotado).
 */
@Composable
fun FavoritesScreen(
    onOpenProduct: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    var filter by rememberSaveable { mutableStateOf(FILTER_ALL) }

    // Refresca el catálogo (y por tanto la lista de favoritos) al entrar.
    LaunchedEffect(Unit) { viewModel.load() }

    // Se recorta contra `favoriteIds` para que el "X" quite el item al instante.
    val favorites = state.favorites.filter { it.id in favoriteIds }
    val books = favorites.count { !isStationery(it.category) }
    val stationery = favorites.count { isStationery(it.category) }
    val visible = when (filter) {
        FILTER_BOOKS -> favorites.filter { !isStationery(it.category) }
        FILTER_STATIONERY -> favorites.filter { isStationery(it.category) }
        else -> favorites
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FavoritesHeader(count = favorites.size)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectableChip(
                text = "$FILTER_ALL (${favorites.size})",
                selected = filter == FILTER_ALL,
                onClick = { filter = FILTER_ALL }
            )
            SelectableChip(
                text = "$FILTER_BOOKS ($books)",
                selected = filter == FILTER_BOOKS,
                onClick = { filter = FILTER_BOOKS }
            )
            SelectableChip(
                text = "$FILTER_STATIONERY ($stationery)",
                selected = filter == FILTER_STATIONERY,
                onClick = { filter = FILTER_STATIONERY }
            )
        }

        when {
            favorites.isEmpty() -> EmptyState(
                icon = Icons.Rounded.FavoriteBorder,
                title = "Aún no tienes favoritos",
                message = "Toca el corazón de cualquier producto para guardarlo en tu lista de deseos."
            )

            visible.isEmpty() -> EmptyState(
                icon = Icons.Rounded.FavoriteBorder,
                title = "No hay favoritos en esta categoría",
                message = "Prueba con otro filtro para ver los productos que has guardado."
            )

            else -> visible.forEach { product ->
                FavoriteCard(
                    product = product,
                    onClick = { onOpenProduct(product.id) },
                    onRemove = { viewModel.toggleFavorite(product.id) },
                    onAdd = {
                        // ShopViewModel no expone el carrito: se usa el repositorio.
                        CartRepository.add(product)
                    },
                    onNotify = {
                        // Aviso de reposición: pendiente del endpoint del backend.
                    }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------

@Composable
private fun FavoritesHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mis Favoritos",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$count productos guardados en tu lista de deseos",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(CreamDeep)
                .border(1.dp, Border, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = InkNavy,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp
                )
                Text(
                    text = "items",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    product: Product,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onAdd: () -> Unit,
    onNotify: () -> Unit
) {
    val soldOut = !product.isInStock

    SectionCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        containerColor = SurfaceWhite,
        cornerRadius = 16,
        contentPadding = 14
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProductRowImage(
                url = product.imageUrl,
                modifier = Modifier.size(112.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = product.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Terracota,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onRemove),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Quitar de favoritos",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (product.author.isNotBlank()) {
                    Text(
                        text = product.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriceText(
                        price = product.price,
                        modifier = Modifier.weight(1f, fill = false),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    StockBadge(
                        stock = product.stock,
                        stockCount = product.stockCount
                    )
                }

                if (soldOut) {
                    SecondaryButton(
                        text = "Avisarme cuando llegue",
                        onClick = onNotify,
                        icon = Icons.Rounded.Notifications
                    )
                } else {
                    PrimaryButton(
                        text = "Agregar",
                        onClick = onAdd,
                        icon = Icons.Rounded.ShoppingBag
                    )
                }
            }
        }
    }
}
