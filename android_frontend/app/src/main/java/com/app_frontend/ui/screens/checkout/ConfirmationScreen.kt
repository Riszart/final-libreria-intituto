package com.app_frontend.ui.screens.checkout

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
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.local.MockData
import com.app_frontend.data.model.AppConfig
import com.app_frontend.data.model.DeliveryMethod
import com.app_frontend.data.model.OrderItem
import com.app_frontend.data.model.PaymentMethod
import com.app_frontend.data.model.asPrice
import com.app_frontend.ui.components.AppCheckbox
import com.app_frontend.ui.components.AppDivider
import com.app_frontend.ui.components.HeaderAction
import com.app_frontend.ui.components.OrderStatusBadge
import com.app_frontend.ui.components.OverlineText
import com.app_frontend.ui.components.PillBadge
import com.app_frontend.ui.components.PriceText
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.components.ProductRowImage
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.components.SectionHeader
import com.app_frontend.ui.components.SubScreenHeader
import com.app_frontend.ui.components.WarningBanner
import com.app_frontend.ui.theme.BrownHero
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.PeachBg
import com.app_frontend.ui.theme.PeachText
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.CartViewModel

/**
 * Pantalla "Confirmación De Pedido" (paso 2 del checkout).
 * Mockup: `intefase/Confirmación de Pedido _ Papel & Tinta.png`
 */
@Composable
fun ConfirmationScreen(
    onBack: () -> Unit,
    onModifyCart: () -> Unit,
    onOrderPlaced: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel()
) {
    val checkout by viewModel.checkout.collectAsStateWithLifecycle()

    // Artículos y montos del mockup (3 artículos → S/ 240.00 + IGV 18% = S/ 283.20).
    val items = MockData.checkoutItems
    val subtotal = MockData.checkoutSubtotal
    val tax = viewModel.taxFor(subtotal)
    val shipping = 0.0
    val total = subtotal + tax + shipping

    val address = if (checkout.deliveryMethod == DeliveryMethod.HOME) {
        checkout.address
    } else {
        checkout.storeAddress
    }
    val deliveryLabel = if (checkout.deliveryMethod == DeliveryMethod.HOME) {
        "ENVÍO A DOMICILIO"
    } else {
        "RECOJO EN TIENDA"
    }
    val addressLine = listOf(address.street, address.city)
        .filter { it.isNotBlank() }
        .joinToString(", ")
    val addressSecondary = listOf(
        listOf(address.state, address.country).filter { it.isNotBlank() }.joinToString(", "),
        address.reference.takeIf { it.isNotBlank() }?.let { "Referencia: $it" }
    ).filterNotNull().joinToString(" • ")

    Column(modifier = modifier.fillMaxSize()) {
        SubScreenHeader(
            title = "Confirmación De Pedido",
            onBack = onBack,
            actions = {
                HeaderAction(
                    icon = Icons.Rounded.MoreVert,
                    contentDescription = "Más opciones",
                    onClick = { /* menú contextual pendiente */ }
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReadyForOrderHero()

            SectionHeader(
                title = "Artículos en el carrito (${items.size})",
                icon = Icons.Rounded.ShoppingBag,
                trailing = {
                    Text(
                        text = "Modificar",
                        style = MaterialTheme.typography.labelLarge,
                        color = Terracota,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(onClick = onModifyCart)
                            .padding(start = 8.dp)
                    )
                }
            )
            items.forEach { item -> CartItemCard(item = item) }

            SectionHeader(
                title = "Detalles de la Entrega",
                icon = Icons.Rounded.LocalShipping,
                trailing = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(50))
                            .background(CreamDeep)
                            .clickable { /* edición de entrega pendiente */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar entrega",
                            tint = InkNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
            DeliveryDetailsCard(
                deliveryLabel = deliveryLabel,
                addressLine = addressLine,
                addressSecondary = addressSecondary
            )

            SectionHeader(
                title = "Método de Pago",
                icon = Icons.Rounded.CreditCard
            )
            PaymentMethodCard(method = checkout.paymentMethod)

            SectionHeader(
                title = "Resumen de Costos",
                icon = Icons.Rounded.Receipt
            )
            CostSummaryCard(
                itemsCount = items.size,
                subtotal = subtotal,
                tax = tax,
                shipping = shipping,
                total = total
            )

            checkout.error?.let { message ->
                WarningBanner(text = message, modifier = Modifier.fillMaxWidth())
            }

            TermsRow(
                checked = checkout.acceptTerms,
                onCheckedChange = viewModel::onAcceptTermsChange
            )

            PrimaryButton(
                text = "Realizar Pedido y Pagar (${AppConfig.CURRENCY} ${total.asPrice()})",
                onClick = {
                    viewModel.placeOrder { order -> onOrderPlaced(order.id) }
                },
                loading = checkout.isPlacingOrder,
                icon = Icons.Rounded.Lock
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Transacción 100% segura y encriptada",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Privados
// ---------------------------------------------------------------------------

/** Cabecera con check circular marrón: "¡Todo listo para tu pedido!". */
@Composable
private fun ReadyForOrderHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(CreamDeep, SurfaceWhite, PeachBg)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(50))
                    .background(PeachBg),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(50))
                        .background(BrownHero),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = TextOnDark,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Text(
                text = "¡Todo listo para tu pedido!",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp)
            )
            Text(
                text = "Revisa los detalles finales antes de confirmar tu selección de Papel & Tinta.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/** Fila de artículo con imagen, nombre, detalle, badge de estado y precio. */
@Composable
private fun CartItemCard(item: OrderItem) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            ProductRowImage(
                url = item.product.imageUrl,
                modifier = Modifier.size(64.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = checkoutItemDetail(item),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                PillBadge(
                    text = if (isLowStockItem(item)) "Últimas unidades" else "En Stock",
                    containerColor = PeachBg,
                    contentColor = PeachText
                )
            }
            PriceText(
                price = item.unitPrice,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }
    }
}

/** Card con el tipo de entrega, dirección, referencia y fecha estimada. */
@Composable
private fun DeliveryDetailsCard(
    deliveryLabel: String,
    addressLine: String,
    addressSecondary: String
) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        OverlineText(text = deliveryLabel, color = Terracota)
        Text(
            text = addressLine,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = addressSecondary,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarMonth,
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 1.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Fecha estimada de entrega: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimary)) {
                        append("Mañana, 15 de Octubre")
                    }
                    append(" (9:00 AM - 1:00 PM)")
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

/** Card del método de pago elegido con enlace "Cambiar". */
@Composable
private fun PaymentMethodCard(method: PaymentMethod) {
    val title = when (method) {
        PaymentMethod.CARD -> "Tarjeta de Crédito / Débito"
        else -> method.label
    }
    val subtitle = when (method) {
        PaymentMethod.CARD -> "Terminada en 4892 • Visa"
        else -> method.subtitle
    }
    val icon = when (method) {
        PaymentMethod.CARD -> Icons.Rounded.CreditCard
        PaymentMethod.QR -> Icons.Rounded.CreditCard
        PaymentMethod.CASH -> Icons.Rounded.CreditCard
    }

    SectionCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(InkNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextOnDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                text = "Cambiar",
                style = MaterialTheme.typography.labelLarge,
                color = Terracota,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { /* cambio de método pendiente */ }
                    .padding(start = 8.dp)
            )
        }
    }
}

/** Card "Resumen de Costos" con subtotal, IGV, envío gratis y total. */
@Composable
private fun CostSummaryCard(
    itemsCount: Int,
    subtotal: Double,
    tax: Double,
    shipping: Double,
    total: Double
) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        SummaryRow(label = "Subtotal ($itemsCount artículos)") {
            PriceText(
                price = subtotal,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
        SummaryRow(label = "IGV (18%)", modifier = Modifier.padding(top = 10.dp)) {
            PriceText(
                price = tax,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
        SummaryRow(label = "Costo de Envío", modifier = Modifier.padding(top = 10.dp)) {
            PillBadge(
                text = "¡Gratis!",
                containerColor = Terracota,
                contentColor = TextOnDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        AppDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total a Pagar",
                style = MaterialTheme.typography.titleLarge,
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

@Composable
private fun SummaryRow(
    label: String,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        value()
    }
}

/** Checkbox de aceptación de términos y condiciones. */
@Composable
private fun TermsRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        AppCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = buildAnnotatedString {
                append("Acepto los ")
                withStyle(
                    SpanStyle(
                        color = Terracota,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("términos y condiciones")
                }
                append(" de compra y la política de privacidad y devoluciones de Papel & Tinta.")
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

/** Detalle secundario de cada artículo del mockup (variante / empaque). */
private fun checkoutItemDetail(item: OrderItem): String = when (item.product.id) {
    "prd-012" -> "1 unidad • Papel bond avena 90g"
    "prd-003" -> "1 unidad • Punta fina F"
    "prd-016" -> "1 paquete • Edición limitada"
    else -> "${item.quantity} unidad${if (item.quantity == 1) "" else "es"} • ${item.product.category}"
}

/** En el mockup la pluma muestra el badge "Últimas unidades". */
private fun isLowStockItem(item: OrderItem): Boolean = item.product.id == "prd-003"
