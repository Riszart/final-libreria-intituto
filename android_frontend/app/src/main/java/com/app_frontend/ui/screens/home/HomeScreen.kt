package com.app_frontend.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.local.MockData
import com.app_frontend.data.model.AppConfig
import com.app_frontend.data.model.Product
import com.app_frontend.ui.components.AppImage
import com.app_frontend.ui.components.PillBadge
import com.app_frontend.ui.components.PriceText
import com.app_frontend.ui.components.ProductRowImage
import com.app_frontend.ui.components.SearchBar
import com.app_frontend.ui.components.SelectableChip
import com.app_frontend.ui.components.SmallButton
import com.app_frontend.ui.components.WideProductCard
import com.app_frontend.ui.theme.AmberLogo
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.BrownHero
import com.app_frontend.ui.theme.Cream
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.PeachBg
import com.app_frontend.ui.theme.PeachText
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextOnDarkMuted
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.ShopViewModel

/**
 * Pantalla de Inicio.
 * Mockup: `intefase/Html → Body.png`
 */
@Composable
fun HomeScreen(
    onOpenProduct: (String) -> Unit,
    onTrackOrder: () -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val recommendations = state.products.filter {
        it.id in listOf("prd-004", "prd-012", "prd-001", "prd-013")
    }.ifEmpty { MockData.products.take(4) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        GreetingHeader()
        SearchBar(value = state.search, onValueChange = viewModel::onSearchChange, onFilterClick = {})
        OrderTrackingBanner(onTrackOrder = onTrackOrder)
        HeroCollectionCard(onExplore = { onOpenProduct("prd-001") })
        ReadingCornersSection()
        RecommendedSection(
            products = recommendations,
            onOpenProduct = onOpenProduct,
            onToggleFavorite = viewModel::toggleFavorite,
            favoriteIds = state.favorites.map { it.id }.toSet()
        )
        MonthlyQuoteCard()
        ExclusiveSection(onOpenProduct = onOpenProduct)
    }
}

// ---------------------------------------------------------------------------

@Composable
private fun GreetingHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hola, Sofía ✦",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "¿Qué historia te acompañará hoy?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(PeachBg)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Club Nivel II",
                style = MaterialTheme.typography.labelMedium,
                color = PeachText,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun OrderTrackingBanner(onTrackOrder: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(PeachBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalShipping,
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pedido #PT-84920 •",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
                Text(
                    text = "En camino",
                    style = MaterialTheme.typography.labelLarge,
                    color = Terracota,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Entrega estimada: Hoy antes de las 8 PM",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(CreamDeep)
                .clickable(onClick = onTrackOrder)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Rastrear",
                style = MaterialTheme.typography.labelMedium,
                color = InkNavy,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HeroCollectionCard(onExplore: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(InkNavy, Color(0xFF1D3350))
                )
            )
            .padding(20.dp)
    ) {
        // Sello decorativo de fondo
        Icon(
            imageVector = Icons.Rounded.FormatQuote,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.06f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
        )

        Column {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, AmberLogo, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = AmberLogo,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "EDICIÓN ESPECIAL LIMITADA",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberLogo,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Colección de Otoño: Clásicos Ilustrados y Tintas Nobles",
                style = MaterialTheme.typography.headlineMedium,
                color = TextOnDark,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )

            Text(
                text = "Tapas enteladas en oro viejo, encuadernación rústica cosida a mano y tintas pigmentadas con extracto de nogal.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnDarkMuted,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrownHero)
                        .clickable(onClick = onExplore)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Explorar Colección",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextOnDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = TextOnDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "Desde ${AppConfig.CURRENCY} 78.00",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextOnDarkMuted
                )
            }
        }
    }
}

@Composable
private fun ReadingCornersSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Rincones Literarios",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ver todas ›",
            style = MaterialTheme.typography.labelLarge,
            color = Terracota,
            fontWeight = FontWeight.SemiBold
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MockData.readingCorners.forEachIndexed { index, corner ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (index == 0) InkNavy else SurfaceWhite)
                    .then(
                        if (index == 0) Modifier else Modifier.border(1.dp, Border, RoundedCornerShape(50))
                    )
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = if (index == 0) AmberLogo else Terracota,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = corner,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (index == 0) TextOnDark else InkNavy
                )
            }
        }
    }
}

@Composable
private fun RecommendedSection(
    products: List<Product>,
    onOpenProduct: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    favoriteIds: Set<String>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recomendados para ti",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Curaduría personalizada según tus últimas lecturas",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Icon(
                imageVector = Icons.Rounded.Sort,
                contentDescription = "Ordenar",
                tint = InkNavy,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            products.forEach { product ->
                WideProductCard(
                    product = product,
                    onClick = { onOpenProduct(product.id) },
                    modifier = Modifier.width(190.dp),
                    isFavorite = favoriteIds.contains(product.id),
                    onFavoriteClick = { onToggleFavorite(product.id) },
                    onAddClick = { onOpenProduct(product.id) }
                )
            }
        }
    }
}

@Composable
private fun MonthlyQuoteCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CreamDeep)
            .padding(18.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(PeachBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "99",
                    style = MaterialTheme.typography.titleMedium,
                    color = Terracota,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text(
                    text = "LECTURA DEL MES • Club de Lectura",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                Text(
                    text = "“Un libro es un sueño que tienes en la mano.”",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(50))
                            .background(InkNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MV",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextOnDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = "Marco V.",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Librero Mayor de Papel & Tinta",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Próximo debate: 28 de Octubre",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Únete a la tertulia",
                    style = MaterialTheme.typography.labelLarge,
                    color = Terracota,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = Terracota,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun ExclusiveSection(onOpenProduct: (String) -> Unit) {
    val offers = listOf(
        Triple("prd-013", "-20% PREVENTA", "Reservar"),
        Triple("prd-014", "-15% EXCLUSIVO", "Canjear")
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = Terracota,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Exclusivo: Lector Voraz",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            PillBadge(
                text = "48h restantes",
                containerColor = Color(0xFFFCE7F3),
                contentColor = Color(0xFFBE185D)
            )
        }
        Text(
            text = "Beneficios reservados para miembros de la cofradía",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        offers.forEach { (id, badge, action) ->
            val product = MockData.product(id) ?: return@forEach
            ExclusiveRow(
                product = product,
                badge = badge,
                action = action,
                onClick = { onOpenProduct(id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ExclusiveRow(product: Product, badge: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductRowImage(
            url = product.imageUrl,
            modifier = Modifier.size(54.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            PillBadge(
                text = badge,
                containerColor = PeachBg,
                contentColor = Terracota
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PriceText(
                    price = product.price,
                    style = MaterialTheme.typography.titleSmall
                )
                product.originalPrice?.let {
                    Text(
                        text = "${AppConfig.CURRENCY} ${String.format("%.2f", it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
        SmallButton(text = action, onClick = onClick)
    }
}
