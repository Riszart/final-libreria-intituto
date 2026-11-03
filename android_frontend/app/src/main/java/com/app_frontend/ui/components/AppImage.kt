package com.app_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.app_frontend.ui.theme.AmberLogo
import com.app_frontend.ui.theme.CreamDeep

/**
 * Imagen de producto con portada de respaldo.
 *
 * - Si el backend envía `imageUrl`, Coil la descarga y la muestra.
 * - Si no hay URL (datos de demostración) o la descarga falla, se muestra una
 *   portada con el estilo de la marca en lugar de un hueco vacío.
 */
@Composable
fun AppImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackIcon: ImageVector = Icons.Rounded.MenuBook,
    fallbackTint: Color = AmberLogo
) {
    if (url.isNullOrBlank()) {
        ImagePlaceholder(modifier, contentScale, fallbackIcon, fallbackTint)
        return
    }
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
        loading = { ImagePlaceholder(Modifier.fillMaxSize(), contentScale, fallbackIcon, fallbackTint) },
        error = { ImagePlaceholder(Modifier.fillMaxSize(), contentScale, fallbackIcon, fallbackTint) }
    )
}

@Composable
private fun ImagePlaceholder(
    modifier: Modifier,
    contentScale: ContentScale,
    icon: ImageVector,
    tint: Color
) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(Color(0xFFFFF6E9), CreamDeep, Color(0xFFF0E4D4))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint.copy(alpha = 0.55f),
            modifier = Modifier.size(38.dp)
        )
    }
}
