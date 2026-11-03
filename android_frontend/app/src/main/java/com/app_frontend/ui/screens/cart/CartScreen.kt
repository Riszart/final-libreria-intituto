package com.app_frontend.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.model.AppConfig
import com.app_frontend.data.model.CartItem
import com.app_frontend.data.model.Product
import com.app_frontend.data.model.StockStatus
import com.app_frontend.ui.components.AppDivider
import com.app_frontend.ui.components.EmptyState
import com.app_frontend.ui.components.OutlinedInputField
import com.app_frontend.ui.components.PillBadge
import com.app_frontend.ui.components.PriceText
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.components.ProductRowImage
import com.app_frontend.ui.components.QuantitySelector
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.components.SmallButton
import com.app_frontend.ui.components.WarningBanner
import com.app_frontend.ui.theme.DangerRed
import com.app_frontend.ui.theme.SuccessGreen
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.CartViewModel

/**
 * Pantalla "Carrito de Compras".
 * Mockup: `intefase/Carrito de Compras _ Papel & Tinta.png`
 */
@Composable
fun CartScreen(
    onOpenProduct: (String) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(),
    onBackToCatalog: () -> Unit = {}
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val checkout by viewModel.checkout.collectAsStateWithLifecycle()
    val subtotal by viewModel.subtotal.collectAsStateWithLifecycle()
    val itemCount = items.sumOf { it.quantity }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Título + contador de artículos ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tu Carrito",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            PillBadge(
                text = "$itemCount ${if (itemCount == 1) "artículo" else "artículos"}"
            )
        }

        if (items.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.ShoppingBag,
                title = "Tu carrito está vacío",
                message = "Aún no has añadido artículos. Explora el catálogo y llévate a casa tu próxima lectura.",
                actionText = "Ver Catálogo",
                onAction = onBackToCatalog
            )
        } else {
            // --- Lista de artículos ---
            items.forEach { item ->
                CartItemCard(
                    item = item,
                    onOpenProduct = { onOpenProduct(item.product.id) },
                    onIncrease = { viewModel.increase(item.product.id) },
                    onDecrease = { viewModel.decrease(item.product.id) },
                    onRemove = { viewModel.remove(item.product.id) }
                )
            }

            // --- Código de descuento ---
            CouponSection(
                code = checkout.couponCode,
                message = checkout.couponMessage,
                isApplied = checkout.discount > 0.0,
                onCodeChange = viewModel::onCouponChange,
                onApply = viewModel::applyCoupon
            )

            // --- Resumen del pedido (se recalcula en vivo) ---
            OrderSummary(
                subtotal = subtotal,
                tax = viewModel.taxFor(subtotal),
                shipping = viewModel.shippingFor(checkout.deliveryMethod),
                discount = checkout.discount
            )

            PrimaryButton(
                text = "Continuar con el Pago →",
                onClick = onCheckout
            )
        }
    }
}

// ---------------------------------------------------------------------------

/** Tarjeta de un artículo: imagen, categoría, título, variantes, precio y cantidad. */
@Composable
private fun CartItemCard(
    item: CartItem,
    onOpenProduct: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = item.product
    // Stock limitado: la cantidad no debe superar las unidades restantes.
    val exceedsStock = product.stock == StockStatus.LOW && item.quantity > product.stockCount

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            ProductRowImage(
                url = product.imageUrl,
                modifier = Modifier
                    .size(72.dp)
                    .clickable(onClick = onOpenProduct)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Terracota,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = subtitleOf(product),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriceText(
                        price = product.price,
                        style = MaterialTheme.typography.titleMedium
                    )
                    QuantitySelector(
                        quantity = item.quantity,
                        onDecrease = onDecrease,
                        onIncrease = onIncrease
                    )
                }
            }

            // Quitar el artículo del carrito
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Eliminar del carrito",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (exceedsStock) {
            WarningBanner(
                text = "Solo quedan ${product.stockCount} unidades disponibles en stock para este artículo. Por favor, ajusta la cantidad."
            )
        }
    }
}

/** Subtítulo con las variantes del artículo (una línea, con elipsis). */
private fun subtitleOf(product: Product): String =
    product.description.ifBlank { product.author }

/** Campo de cupón + botón "Aplicar" con el mensaje de respuesta. */
@Composable
private fun CouponSection(
    code: String,
    message: String?,
    isApplied: Boolean,
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit
) {
    SectionCard(cornerRadius = 14, contentPadding = 14) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedInputField(
                value = code,
                onValueChange = onCodeChange,
                placeholder = "Código de descuento",
                modifier = Modifier.weight(1f)
            )
            SmallButton(text = "Aplicar", onClick = onApply)
        }

        if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = if (isApplied) SuccessGreen else DangerRed,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

/** Tarjeta "Resumen del pedido". */
@Composable
private fun OrderSummary(
    subtotal: Double,
    tax: Double,
    shipping: Double,
    discount: Double
) {
    val total = subtotal + tax + shipping - discount

    SectionCard(cornerRadius = 16, contentPadding = 18) {
        Text(
            text = "Resumen del pedido",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        SummaryRow(label = "Subtotal (Productos)") {
            PriceText(
                price = subtotal,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
        SummaryRow(label = "IGV (18%)") {
            PriceText(
                price = tax,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
        SummaryRow(label = "Envío estimado") {
            if (shipping <= 0.0) {
                Text(
                    text = "¡Gratis!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                PriceText(
                    price = shipping,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }
        }
        if (discount > 0.0) {
            SummaryRow(label = "Descuento") {
                Text(
                    text = "− ${AppConfig.CURRENCY} ${String.format("%.2f", discount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        AppDivider()
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total a Pagar",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            PriceText(
                price = total,
                style = MaterialTheme.typography.headlineMedium,
                color = Terracota
            )
        }
    }
}

/** Fila etiqueta + valor del resumen. */
@Composable
private fun SummaryRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        content()
    }
}
