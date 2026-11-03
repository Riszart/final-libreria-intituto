package com.app_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.NotInterested
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app_frontend.data.model.Product
import com.app_frontend.data.model.StockStatus
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.Terracota

/**
 * Tarjeta de producto.
 *
 * Se usa en el home (fila horizontal) y en el catálogo (rejilla). Los params
 * permiten mostrar u ocultar autor, corazón y badge de stock según la pantalla.
 */
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAuthor: Boolean = true,
    showFavorite: Boolean = false,
    showStockBadge: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onAddClick: (() -> Unit)? = null
) {
    val soldOut = product.stock == StockStatus.SOLD_OUT

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        // --- Imagen con badges ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.35f)
                .clip(RoundedCornerShape(10.dp))
        ) {
            AppImage(
                url = product.imageUrl,
                modifier = Modifier.fillMaxSize()
            )

            val badgeText = when {
                product.tag != null -> product.tag
                showStockBadge -> product.stockLabel
                else -> null
            }
            if (badgeText != null) {
                SolidBadge(
                    text = badgeText,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = badgeColor(product, showStockBadge)
                )
            }

            if (showFavorite && onFavoriteClick != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SurfaceWhite)
                        .clickable(onClick = onFavoriteClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (isFavorite) Terracota else InkNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // --- Categoría ---
        if (product.category.isNotBlank()) {
            Text(
                text = product.category,
                style = MaterialTheme.typography.labelMedium,
                color = Terracota,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        // --- Título ---
        Text(
            text = product.name,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold
        )

        // --- Autor ---
        if (showAuthor && product.author.isNotBlank()) {
            Text(
                text = product.author,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // --- Precio + acción ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                PriceText(
                    price = product.price,
                    style = MaterialTheme.typography.titleMedium,
                    color = Terracota
                )
                if (product.originalPrice != null && product.originalPrice > product.price) {
                    Text(
                        text = "${com.app_frontend.data.model.AppConfig.CURRENCY} ${String.format("%.2f", product.originalPrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                }
            }

            if (soldOut) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(CreamDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotInterested,
                        contentDescription = "Agotado",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                IconCircleButton(
                    onClick = onAddClick ?: onClick,
                    icon = Icons.Rounded.Add,
                    contentDescription = "Añadir al carrito",
                    containerColor = Terracota
                )
            }
        }
    }
}

/** Color del badge según el estado / etiqueta del producto. */
private fun badgeColor(product: Product, showStock: Boolean): Color = when {
    product.stock == StockStatus.SOLD_OUT -> com.app_frontend.ui.theme.DangerPink
    product.stock == StockStatus.LOW && showStock -> com.app_frontend.ui.theme.Terracota
    product.tag?.contains("%") == true -> com.app_frontend.ui.theme.Terracota
    product.tag != null -> InkNavy
    showStock -> com.app_frontend.ui.theme.SuccessGreen
    else -> InkNavy
}

/** Tarjeta ancha para la fila horizontal "Recomendados para ti". */
@Composable
fun WideProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit,
    onAddClick: () -> Unit
) {
    ProductCard(
        product = product,
        onClick = onClick,
        modifier = modifier,
        showAuthor = true,
        showFavorite = true,
        isFavorite = isFavorite,
        onFavoriteClick = onFavoriteClick,
        onAddClick = onAddClick
    )
}

/** Tarjeta compacta para la rejilla del catálogo. */
@Composable
fun CatalogCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit,
    onAddClick: () -> Unit
) {
    ProductCard(
        product = product,
        onClick = onClick,
        modifier = modifier,
        showAuthor = false,
        showFavorite = true,
        showStockBadge = true,
        isFavorite = isFavorite,
        onFavoriteClick = onFavoriteClick,
        onAddClick = onAddClick
    )
}

/** Fila de producto en listas (carrito, favoritos, pedidos). */
@Composable
fun ProductRowImage(
    url: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 10
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(CreamDeep)
    ) {
        AppImage(url = url, modifier = Modifier.fillMaxSize())
    }
}

/** Icono de "chevron" para filas navegables del perfil. */
@Composable
fun ChevronRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (badge != null) {
                    PillBadge(text = badge, containerColor = com.app_frontend.ui.theme.PeachBg, contentColor = Terracota)
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        trailing?.invoke() ?: Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
