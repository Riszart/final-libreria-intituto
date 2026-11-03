package com.app_frontend.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.app_frontend.data.model.StockStatus
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.DangerBg
import com.app_frontend.ui.theme.DangerPink
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.PeachBg
import com.app_frontend.ui.theme.PeachText
import com.app_frontend.ui.theme.SuccessGreen
import com.app_frontend.ui.theme.SuccessGreenBg
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.WarningAmber
import com.app_frontend.ui.theme.WarningBg

/**
 * Píldoras, filtros y badges de estado (categorías, chips del catálogo,
 * estados de stock, etiquetas de pedido...).
 */

/** Chip píldora seleccionable (ej. "Todos / Libros / Cuadernos"). */
@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = if (selected) TextOnDark else InkNavy,
        maxLines = 1,
        modifier = modifier
            .clip(shape)
            .background(if (selected) InkNavy else SurfaceWhite)
            .then(if (selected) Modifier else Modifier.border(1.dp, Border, shape))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    )
}

/** Chip rectangular con icono para filtros secundarios (Disponible, Novedades...). */
@Composable
fun FilterChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selected: Boolean = false
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .clip(shape)
            .background(if (selected) InkNavy else CreamDeep)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selected) TextOnDark else InkNavy
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) TextOnDark else InkNavy
        )
    }
}

/** Badge genérico en píldora. */
@Composable
fun PillBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = CreamDeep,
    contentColor: Color = InkNavy,
    icon: ImageVector? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = contentColor
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1
        )
    }
}

/** Badge de stock con la semántica de la marca. */
@Composable
fun StockBadge(stock: StockStatus, stockCount: Int = 0, modifier: Modifier = Modifier) {
    when (stock) {
        StockStatus.AVAILABLE -> PillBadge(
            text = "Disponible",
            modifier = modifier,
            containerColor = SuccessGreenBg,
            contentColor = SuccessGreen
        )

        StockStatus.LOW -> PillBadge(
            text = if (stockCount > 0) "Pocas unidades ($stockCount)" else "Pocas unidades",
            modifier = modifier,
            containerColor = WarningBg,
            contentColor = Terracota
        )

        StockStatus.SOLD_OUT -> PillBadge(
            text = "Agotado / No disponible",
            modifier = modifier,
            containerColor = DangerBg,
            contentColor = DangerPink
        )
    }
}

/** Badge sólido de la tarjeta de producto (esquina superior). */
@Composable
fun SolidBadge(text: String, modifier: Modifier = Modifier, color: Color = InkNavy) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = TextOnDark,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

/** Badge de estado de pedido ("En camino", "Entregado"). */
@Composable
fun OrderStatusBadge(statusLabel: String, modifier: Modifier = Modifier) {
    val delivered = statusLabel.contains("Entregado", ignoreCase = true)
    PillBadge(
        text = statusLabel,
        modifier = modifier,
        containerColor = if (delivered) SuccessGreenBg else PeachBg,
        contentColor = if (delivered) SuccessGreen else PeachText,
        icon = if (delivered) Icons.Rounded.CheckCircle else Icons.Rounded.LocalShipping
    )
}

/** Chip de aviso con fondo de alerta (ej. stock insuficiente en el carrito). */
@Composable
fun WarningBanner(text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DangerBg)
            .padding(12.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = null,
            tint = DangerPink,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = DangerPink
        )
    }
}

/** Contenedor con borde fino (tarjeta seleccionable de entrega/pago). */
@Composable
fun SelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(SurfaceWhite)
            .border(BorderStroke(if (selected) 2.dp else 1.dp, if (selected) InkNavy else Border), shape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        content()
    }
}

/** Radio button personalizado (anillo exterior + punto interior). */
@Composable
fun AppRadioButton(selected: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(shape)
            .border(
                BorderStroke(if (selected) 6.dp else 1.5.dp, if (selected) InkNavy else Border),
                shape
            )
            .background(SurfaceWhite)
    )
}

/** Checkbox cuadrado con el estilo terracota de los mockups. */
@Composable
fun AppCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(5.dp)
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(shape)
            .background(if (checked) Terracota else SurfaceWhite)
            .border(1.5.dp, if (checked) Terracota else Border, shape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = TextOnDark,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/** Fila horizontal desplazable de chips (tabs de pedidos/notificaciones). */
@Composable
fun ChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            SelectableChip(
                text = option,
                selected = index == selectedIndex,
                onClick = { onSelect(index) }
            )
        }
    }
}

/** Texto de precio en el color de marca. */
@Composable
fun PriceText(
    price: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = Terracota
) {
    Text(
        text = "${com.app_frontend.data.model.AppConfig.CURRENCY} ${String.format("%.2f", price)}",
        style = style,
        color = color,
        modifier = modifier
    )
}
