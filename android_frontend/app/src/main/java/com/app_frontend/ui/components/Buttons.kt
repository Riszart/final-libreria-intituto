package com.app_frontend.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.Terracota

/**
 * Botonería de la app.
 *
 * - [PrimaryButton]: relleno tinta (azul noche) → acciones principales.
 * - [SecondaryButton]: relleno papel → acciones secundarias.
 * - [TextActionButton]: solo texto → cancelar / enlaces.
 */

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = InkNavy,
            contentColor = TextOnDark,
            disabledContainerColor = CreamDeep,
            disabledContentColor = com.app_frontend.ui.theme.TextMuted
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = TextOnDark,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    androidx.compose.foundation.Image(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(text = text, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    containerColor: Color = CreamDeep,
    contentColor: Color = InkNavy
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = CreamDeep,
            disabledContentColor = com.app_frontend.ui.theme.TextMuted
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                androidx.compose.foundation.Image(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun TextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = InkNavy
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

/** Botón pequeño compacto (ej. "Aplicar", "Reservar", "Ver Detalles"). */
@Composable
fun SmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = InkNavy,
    contentColor: Color = TextOnDark
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Botón circular con icono (ej. "+" de las tarjetas de producto). */
@Composable
fun IconCircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    containerColor: Color = com.app_frontend.ui.theme.AmberLogo,
    contentColor: Color = TextOnDark,
    enabled: Boolean = true
) {
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        shape = RoundedCornerShape(50),
        containerColor = if (enabled) containerColor else CreamDeep,
        contentColor = if (enabled) contentColor else com.app_frontend.ui.theme.TextMuted,
        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp
        )
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}
