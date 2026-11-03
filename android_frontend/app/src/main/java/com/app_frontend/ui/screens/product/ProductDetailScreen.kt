package com.app_frontend.ui.screens.product

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.model.Product
import com.app_frontend.data.model.ProductColor
import com.app_frontend.data.model.SpecItem
import com.app_frontend.ui.components.AppImage
import com.app_frontend.ui.components.EmptyState
import com.app_frontend.ui.components.HeaderDivider
import com.app_frontend.ui.components.OverlineText
import com.app_frontend.ui.components.PillBadge
import com.app_frontend.ui.components.PriceText
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.components.QuantitySelector
import com.app_frontend.ui.components.SecondaryButton
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.components.SectionHeader
import com.app_frontend.ui.theme.AmberLogo
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.PeachBg
import com.app_frontend.ui.theme.PeachText
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.CartViewModel
import com.app_frontend.ui.viewmodel.ShopViewModel

/** Número de puntitos indicadores de la galería del mockup. */
private const val GALLERY_DOTS = 3

/**
 * Pantalla "Detalles del Producto".
 * Mockup: `intefase/Detalles del Producto _ Papel & Tinta.png`
 */
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val product = viewModel.product(productId)
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    if (product == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Rounded.MenuBook,
                title = "Producto no encontrado",
                message = "El artículo que buscas ya no está disponible en el catálogo.",
                actionText = "Volver",
                onAction = onBack
            )
        }
        return
    }

    // --- Selección de la ficha: color de tapa, gramaje y cantidad ---
    var selectedColor by remember(product.id) { mutableStateOf(0) }
    var selectedWeight by remember(product.id) { mutableStateOf(0) }
    var quantity by remember(product.id) { mutableStateOf(1) }

    val extraPrice = product.paperWeights.getOrNull(selectedWeight)?.extraPrice ?: 0.0
    val unitPrice = product.price + extraPrice
    val total = unitPrice * quantity

    Column(modifier = modifier.fillMaxSize()) {
        DetailHeader(category = product.category, onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ProductGallery(
                product = product,
                isFavorite = favoriteIds.contains(product.id),
                onFavoriteClick = { viewModel.toggleFavorite(product.id) },
                onAddClick = { cartViewModel.add(product, quantity) }
            )
            ProductHeading(product = product, price = unitPrice)
            ProductOptions(
                product = product,
                selectedColor = selectedColor,
                selectedWeight = selectedWeight,
                quantity = quantity,
                onColorSelect = { selectedColor = it },
                onWeightSelect = { selectedWeight = it },
                onDecrease = { quantity = (quantity - 1).coerceAtLeast(1) },
                onIncrease = { quantity++ }
            )
            ProductDetailsSection(product = product)
        }

        // --- Barra inferior fija: total + Añadir + Comprar ---
        DetailBottomBar(
            total = total,
            onAdd = { cartViewModel.add(product, quantity) },
            onBuy = onOpenCart
        )
    }
}

// ---------------------------------------------------------------------------

/** Cabecera: "← Volver" a la izquierda y píldora de categoría a la derecha. */
@Composable
private fun DetailHeader(category: String, onBack: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack)
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Volver",
                    tint = InkNavy,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Volver",
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkNavy
                )
            }
            PillBadge(
                text = category,
                containerColor = PeachBg,
                contentColor = PeachText
            )
        }
        HeaderDivider()
    }
}

/** Galería: etiqueta superior, imagen con corazón de favorito y píldora "Añadir". */
@Composable
private fun ProductGallery(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Detalles del Producto",
            style = MaterialTheme.typography.labelSmall,
            color = InkNavy,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)
        )

        Box(modifier = Modifier.fillMaxWidth().height(230.dp)) {
            AppImage(url = product.imageUrl, modifier = Modifier.fillMaxSize())

            // Corazón de favorito flotante
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceWhite)
                    .clickable(onClick = onFavoriteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) Terracota else InkNavy,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Píldora "Añadir" (esquina inferior derecha)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceWhite)
                    .clickable(onClick = onAddClick)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Añadir",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkNavy,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Puntos indicadores de la galería
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(GALLERY_DOTS) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (index == 0) AmberLogo else Border)
                )
            }
        }
    }
}

/** Cintillo de edición, título grande, valoración y precio. */
@Composable
private fun ProductHeading(product: Product, price: Double) {
    Column {
        OverlineText(text = "EDICIÓN LIMITADA • HECHO A MANO")

        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Terracota,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Text(
                    text = "${product.rating} (${product.reviews} reseñas)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            PriceText(
                price = price,
                style = MaterialTheme.typography.headlineMedium,
                color = Terracota
            )
        }
    }
}

/** Tarjeta de opciones: color de tapa, gramaje del papel y cantidad. */
@Composable
private fun ProductOptions(
    product: Product,
    selectedColor: Int,
    selectedWeight: Int,
    quantity: Int,
    onColorSelect: (Int) -> Unit,
    onWeightSelect: (Int) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    SectionCard(containerColor = CreamDeep, cornerRadius = 16, contentPadding = 16) {
        // --- Color de la tapa ---
        if (product.colors.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Color de Tapa:",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = product.colors.getOrNull(selectedColor)?.name.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                product.colors.forEachIndexed { index, color ->
                    ColorSwatch(
                        color = color,
                        selected = index == selectedColor,
                        onClick = { onColorSelect(index) }
                    )
                }
            }
        }

        // --- Gramaje del papel ---
        if (product.paperWeights.isNotEmpty()) {
            if (product.colors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
            }
            Text(
                text = "Gramaje del Papel:",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                product.paperWeights.forEachIndexed { index, weight ->
                    PaperWeightOption(
                        label = weight.label,
                        selected = index == selectedWeight,
                        onClick = { onWeightSelect(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // --- Cantidad ---
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cant:",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            QuantitySelector(
                quantity = quantity,
                onDecrease = onDecrease,
                onIncrease = onIncrease
            )
        }
    }
}

/** Swatch circular de color; el seleccionado lleva un anillo ámbar. */
@Composable
private fun ColorSwatch(color: ProductColor, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(shape)
            .then(
                if (selected) {
                    Modifier
                        .background(AmberLogo)
                        .border(2.dp, AmberLogo, shape)
                        .padding(4.dp)
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(hexToColor(color.hex))
        )
    }
}

/** Opción de gramaje del papel (la elegida lleva borde terracota). */
@Composable
private fun PaperWeightOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(SurfaceWhite)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Terracota else Color.Transparent,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Sección "Detalles del Producto": descripción + rejilla 2x2 de fichas técnicas. */
@Composable
private fun ProductDetailsSection(product: Product) {
    Column {
        SectionHeader(title = "Detalles del Producto")

        if (product.description.isNotBlank()) {
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        if (product.specs.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                product.specs.chunked(2).forEach { rowSpecs ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowSpecs.forEach { spec ->
                            SpecCard(spec = spec, modifier = Modifier.weight(1f))
                        }
                        if (rowSpecs.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/** Ficha técnica individual (Materiales, Dimensiones, Páginas, Origen). */
@Composable
private fun SpecCard(spec: SpecItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CreamDeep)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = spec.label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = spec.value,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Barra inferior fija: total estimado + "Añadir" + "Comprar". */
@Composable
private fun DetailBottomBar(total: Double, onAdd: () -> Unit, onBuy: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(SurfaceWhite)) {
        HeaderDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Total estimado",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                PriceText(
                    price = total,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Terracota
                )
            }
            SecondaryButton(
                text = "Añadir",
                onClick = onAdd,
                icon = Icons.Rounded.ShoppingBag,
                modifier = Modifier.weight(1.3f)
            )
            PrimaryButton(
                text = "Comprar",
                onClick = onBuy,
                modifier = Modifier.weight(1.3f)
            )
        }
    }
}

/** Convierte un color hexadecimal ("#1F5C4A") de los datos al color de Compose. */
private fun hexToColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (error: IllegalArgumentException) {
    Color.Gray
}
