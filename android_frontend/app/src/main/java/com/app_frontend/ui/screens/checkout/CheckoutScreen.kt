package com.app_frontend.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.model.DeliveryMethod
import com.app_frontend.data.model.PaymentMethod
import com.app_frontend.ui.components.AppDivider
import com.app_frontend.ui.components.AppRadioButton
import com.app_frontend.ui.components.HeaderAction
import com.app_frontend.ui.components.LabeledTextField
import com.app_frontend.ui.components.PillBadge
import com.app_frontend.ui.components.PriceText
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.components.SectionHeader
import com.app_frontend.ui.components.SelectableCard
import com.app_frontend.ui.components.SubScreenHeader
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InfoBlue
import com.app_frontend.ui.theme.InfoBlueBg
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.SuccessGreen
import com.app_frontend.ui.theme.SuccessGreenBg
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.theme.WarningAmber
import com.app_frontend.ui.theme.WarningBg
import com.app_frontend.ui.viewmodel.CartViewModel

/**
 * Pantalla "Método De Pago Y Entrega" (paso 1 del checkout).
 * Mockup: `intefase/Método de Pago y Entrega _ Papel & Tinta.png`
 */
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel()
) {
    val checkout by viewModel.checkout.collectAsStateWithLifecycle()
    val subtotal by viewModel.subtotal.collectAsStateWithLifecycle()
    val itemsCount by viewModel.itemsCount.collectAsStateWithLifecycle()

    val shipping = viewModel.shippingFor(checkout.deliveryMethod)
    val total = subtotal + shipping

    val homeAddress = listOf(checkout.address.street, checkout.address.city)
        .filter { it.isNotBlank() }
        .joinToString(", ")
    val storeAddress = listOf(checkout.storeAddress.street, checkout.storeAddress.city)
        .filter { it.isNotBlank() }
        .joinToString(", ")

    Column(modifier = modifier.fillMaxSize()) {
        SubScreenHeader(
            title = "Método De Pago Y Entrega",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ---------------------------------------------------------------
            // 1. Método de Entrega
            // ---------------------------------------------------------------
            SectionHeader(
                title = "1. Método de Entrega",
                trailing = {
                    PillBadge(
                        text = "Paso 1 de 2",
                        containerColor = SurfaceWhite,
                        contentColor = TextSecondary
                    )
                }
            )

            SelectableOptionCard(
                selected = checkout.deliveryMethod == DeliveryMethod.HOME,
                icon = Icons.Rounded.LocalShipping,
                iconTint = InkNavy,
                iconBackground = InfoBlueBg,
                title = "Envío a domicilio",
                subtitle = "Llegan en 2-3 días hábiles",
                onClick = { viewModel.onDeliveryMethodChange(DeliveryMethod.HOME) }
            ) {
                HomeDeliveryDetails(
                    address = homeAddress,
                    shippingCost = viewModel.shippingFor(DeliveryMethod.HOME),
                    onChangeAddress = { /* edición de dirección pendiente */ }
                )
            }

            SelectableOptionCard(
                selected = checkout.deliveryMethod == DeliveryMethod.STORE_PICKUP,
                icon = Icons.Rounded.Storefront,
                iconTint = InkNavy,
                iconBackground = CreamDeep,
                title = "Recojo en Tienda Principal",
                subtitle = "Disponible en 24 horas",
                onClick = { viewModel.onDeliveryMethodChange(DeliveryMethod.STORE_PICKUP) }
            ) {
                StorePickupDetails(address = storeAddress)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ---------------------------------------------------------------
            // 2. Método de Pago
            // ---------------------------------------------------------------
            SectionHeader(
                title = "2. Método de Pago",
                trailing = {
                    PillBadge(
                        text = "Pago seguro",
                        icon = Icons.Rounded.Lock,
                        containerColor = SurfaceWhite,
                        contentColor = TextSecondary
                    )
                }
            )

            SelectableOptionCard(
                selected = checkout.paymentMethod == PaymentMethod.CARD,
                icon = Icons.Rounded.CreditCard,
                iconTint = InkNavy,
                iconBackground = InfoBlueBg,
                title = PaymentMethod.CARD.label,
                subtitle = PaymentMethod.CARD.subtitle,
                onClick = { viewModel.onPaymentMethodChange(PaymentMethod.CARD) }
            ) {
                if (checkout.paymentMethod == PaymentMethod.CARD) {
                    Spacer(modifier = Modifier.height(14.dp))
                    LabeledTextField(
                        label = "Número de Tarjeta",
                        value = checkout.cardNumber,
                        onValueChange = viewModel::onCardNumberChange,
                        trailingIcon = Icons.Rounded.CreditCard,
                        keyboardType = KeyboardType.Number
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LabeledTextField(
                            label = "Vencimiento",
                            value = checkout.cardExpiry,
                            onValueChange = viewModel::onCardExpiryChange,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        LabeledTextField(
                            label = "CVV / CVC",
                            value = checkout.cardCvv,
                            onValueChange = viewModel::onCardCvvChange,
                            trailingIcon = Icons.Rounded.Help,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SelectableOptionCard(
                selected = checkout.paymentMethod == PaymentMethod.QR,
                icon = Icons.Rounded.QrCode,
                iconTint = InfoBlue,
                iconBackground = InfoBlueBg,
                title = PaymentMethod.QR.label,
                subtitle = PaymentMethod.QR.subtitle,
                onClick = { viewModel.onPaymentMethodChange(PaymentMethod.QR) }
            )

            SelectableOptionCard(
                selected = checkout.paymentMethod == PaymentMethod.CASH,
                icon = Icons.Rounded.Payments,
                iconTint = WarningAmber,
                iconBackground = WarningBg,
                title = PaymentMethod.CASH.label,
                subtitle = PaymentMethod.CASH.subtitle,
                onClick = { viewModel.onPaymentMethodChange(PaymentMethod.CASH) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ---------------------------------------------------------------
            // Resumen de la orden
            // ---------------------------------------------------------------
            OrderSummaryCard(
                itemsCount = itemsCount,
                subtotal = subtotal,
                shipping = shipping,
                total = total
            )

            PrimaryButton(
                text = "Continuar con la Confirmación →",
                onClick = onContinue
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Privados
// ---------------------------------------------------------------------------

/** Tarjeta seleccionable genérica: icono en círculo + título/subtítulo + radio. */
@Composable
private fun SelectableOptionCard(
    selected: Boolean,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    SelectableCard(selected = selected, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(50))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            AppRadioButton(selected = selected)
        }
        content()
    }
}

/** Detalle de la opción "Envío a domicilio": dirección, cambio y costo. */
@Composable
private fun HomeDeliveryDetails(
    address: String,
    shippingCost: Double,
    onChangeAddress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Place,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = address,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Cambiar",
            style = MaterialTheme.typography.labelMedium,
            color = Terracota,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable(onClick = onChangeAddress)
                .padding(start = 4.dp)
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Costo de envío:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        PriceText(
            price = shippingCost,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary
        )
    }
}

/** Detalle de la opción "Recojo en Tienda Principal" con badge "Gratis". */
@Composable
private fun StorePickupDetails(address: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CreamDeep)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Storefront,
            contentDescription = null,
            tint = InkNavy,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = address,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        PillBadge(
            text = "Gratis",
            containerColor = SuccessGreenBg,
            contentColor = SuccessGreen
        )
    }
}

/** Card "Resumen de la orden" con subtotal, envío y total a pagar. */
@Composable
private fun OrderSummaryCard(
    itemsCount: Int,
    subtotal: Double,
    shipping: Double,
    total: Double
) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Resumen de la orden",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Subtotal ($itemsCount artículo${if (itemsCount == 1) "" else "s"})",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            PriceText(
                price = subtotal,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
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
                text = "Costo de envío",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            PriceText(
                price = shipping,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        AppDivider()
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total a pagar",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            PriceText(
                price = total,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        }
    }
}
