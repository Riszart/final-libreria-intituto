package com.app_frontend.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app_frontend.ui.components.OutlinedInputField
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.Cream
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary

/**
 * Piezas compartidas por las pantallas "Iniciar Sesión" y "Registrarse":
 * marca, pestañas segmentadas, redes sociales y pie promocional.
 */

/** Contenedor base: fondo papel + scroll + contenido centrado. */
@Composable
fun AuthScaffold(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

/** Logotipo cuadrado oscuro con el libro de la marca. */
@Composable
fun AuthLogo(size: Int = 64, background: Color = InkNavy) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(size.dp / 3))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.MenuBook,
            contentDescription = null,
            tint = TextOnDark,
            modifier = Modifier.size((size * 0.55).dp)
        )
    }
}

/** Título de marca + subtítulo de la pantalla de acceso. */
@Composable
fun AuthHeader(title: String, subtitle: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 16.dp)
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
    )
}

/** Pestañas segmentadas "Iniciar Sesión" / "Registrarse". */
@Composable
fun AuthTabs(
    isLogin: Boolean,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Cream)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AuthTabItem(
            text = "Iniciar Sesión",
            selected = isLogin,
            onClick = onLogin,
            modifier = Modifier.weight(1f)
        )
        AuthTabItem(
            text = "Registrarse",
            selected = !isLogin,
            onClick = onRegister,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AuthTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) SurfaceWhite else Color.Transparent)
            .then(
                if (selected) Modifier.border(1.dp, Border, RoundedCornerShape(9.dp)) else Modifier
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) TextPrimary else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/** Campo de correo con icono, reutilizado por ambas pantallas. */
@Composable
fun EmailField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = "tucorreo@ejemplo.com",
        leadingIcon = Icons.Rounded.Email,
        keyboardType = KeyboardType.Email
    )
}

/** Separador "o continúa con". */
@Composable
fun SocialDivider(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Border)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Border)
        )
    }
}

/** Botón de acceso con proveedor externo (Google / Apple). */
@Composable
fun SocialButton(
    label: String,
    monogram: String,
    monogramColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceWhite)
            .border(1.dp, Border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(50))
                .background(monogramColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = monogram,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

/** Fila con ambos botones sociales. */
@Composable
fun SocialRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SocialButton(
            label = "Google",
            monogram = "G",
            monogramColor = Color(0xFF4285F4),
            onClick = onClick,
            modifier = Modifier.weight(1f)
        )
        SocialButton(
            label = "Apple",
            monogram = "A",
            monogramColor = Color(0xFF111111),
            onClick = onClick,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Pie promocional: "Envío gratis en tu primer pedido de libros". */
@Composable
fun AuthPromoFooter() {
    Row(
        modifier = Modifier.padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.LocalShipping,
            contentDescription = null,
            tint = Terracota,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "Envío gratis en tu primer pedido de libros",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
