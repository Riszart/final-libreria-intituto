package com.app_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.FieldBg
import com.app_frontend.ui.theme.FieldBorder
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary

/**
 * Tarjetas de sección, encabezados y campos de formulario con el estilo de
 * los mockups (etiqueta pequeña arriba + campo con fondo papel).
 */

/** Tarjeta blanca redondeada contenedora de una sección. */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceWhite,
    cornerRadius: Int = 16,
    contentPadding: Int = 16,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(containerColor)
            .padding(contentPadding.dp),
        content = content
    )
}

/** Cabecera de sección con icono opcional (ej. "Información Personal"). */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = InkNavy,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

/** Campo con etiqueta superior, tal como aparece en todos los mockups. */
@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(enabled = onTrailingIconClick != null) {
                                onTrailingIconClick?.invoke()
                            }
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = FieldBg,
                disabledContainerColor = FieldBg,
                focusedBorderColor = InkNavy,
                unfocusedBorderColor = FieldBorder,
                disabledBorderColor = FieldBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextSecondary,
                cursorColor = InkNavy
            ),
            shape = RoundedCornerShape(10.dp),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

/** Campo sin etiqueta superior (la etiqueta la pone la pantalla que lo usa). */
@Composable
fun OutlinedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceWhite,
            unfocusedContainerColor = FieldBg,
            disabledContainerColor = FieldBg,
            focusedBorderColor = InkNavy,
            unfocusedBorderColor = FieldBorder,
            disabledBorderColor = FieldBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            disabledTextColor = TextSecondary,
            cursorColor = InkNavy
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

/** Campo de contraseña con botón de visibilidad (ojito). */@Composable
fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Mínimo 8 caracteres",
    leadingIcon: ImageVector? = null
) {
    var visible by remember { mutableStateOf(false) }
    LabeledTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
        onTrailingIconClick = { visible = !visible },
        keyboardType = KeyboardType.Password,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

/** Barra de búsqueda con icono a la izquierda y botón de filtros a la derecha. */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar libros, libretas, plumas...",
    onFilterClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(FieldBg)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = TextPrimary
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(InkNavy)
                )
            }
        }
        if (onFilterClick != null) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(InkNavy)
                    .clickable(onClick = onFilterClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Filtros",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/** Selector de cantidad (− n +) con el estilo de los mockups. */
@Composable
fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CreamDeep)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        QuantityButton(
            icon = Icons.Rounded.Remove,
            contentDescription = "Restar",
            enabled = enabled && quantity > 1,
            onClick = onDecrease
        )
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp)
        )
        QuantityButton(
            icon = Icons.Rounded.Add,
            contentDescription = "Sumar",
            enabled = enabled,
            onClick = onIncrease
        )
    }
}

@Composable
private fun QuantityButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceWhite)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) InkNavy else TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

/** Estado vacío reutilizable (favoritos sin items, carrito vacío...). */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(50))
                .background(CreamDeep),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = InkNavy,
                modifier = Modifier.size(34.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(6.dp))
            SmallButton(text = actionText, onClick = onAction)
        }
    }
}

/** Separador fino entre filas. */
@Composable
fun AppDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Border)
    )
}

/** Fila clave-valor con etiqueta pequeña y valor en negrita (ficha técnica). */
@Composable
fun KeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
            modifier = Modifier.weight(1f)
        )
    }
}

/** Etiqueta de encabezado de columna (ej. "EDICIÓN LIMITADA • HECHO A MANO"). */
@Composable
fun OverlineText(text: String, modifier: Modifier = Modifier, color: Color = TextMuted) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
    )
}
