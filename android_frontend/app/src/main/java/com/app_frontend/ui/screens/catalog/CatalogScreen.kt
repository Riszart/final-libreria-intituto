package com.app_frontend.ui.screens.catalog

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.local.MockData
import com.app_frontend.data.model.Product
import com.app_frontend.ui.components.CatalogCard
import com.app_frontend.ui.components.ChipRow
import com.app_frontend.ui.components.EmptyState
import com.app_frontend.ui.components.FilterChip
import com.app_frontend.ui.components.SearchBar
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.viewmodel.ShopUiState
import com.app_frontend.ui.viewmodel.ShopViewModel

/**
 * Pantalla de Catálogo de Productos.
 * Mockup: `intefase/Catálogo de Productos _ Papel & Tinta.png`
 */
@Composable
fun CatalogScreen(
    onOpenProduct: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val products = viewModel.visibleProducts(state)
    val categories = MockData.categories

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Cabecera: búsqueda + chips de categoría + filtros secundarios ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            CatalogHeader(
                state = state,
                categories = categories,
                onSearchChange = viewModel::onSearchChange,
                onCategoryChange = viewModel::onCategoryChange,
                onFilterChange = viewModel::onFilterChange
            )
        }

        when {
            state.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                CatalogLoading()
            }

            products.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    icon = Icons.Rounded.Search,
                    title = "Sin resultados",
                    message = "No encontramos productos que coincidan con tu búsqueda.",
                    actionText = "Limpiar filtros",
                    onAction = {
                        viewModel.onSearchChange("")
                        viewModel.onFilterChange(null)
                        viewModel.onCategoryChange("Todos")
                    }
                )
            }

            // --- Rejilla de 2 columnas con las tarjetas del catálogo ---
            else -> items(products, key = { it.id }) { product ->
                CatalogCard(
                    product = product,
                    onClick = { onOpenProduct(product.id) },
                    modifier = Modifier.fillMaxWidth(),
                    isFavorite = favoriteIds.contains(product.id),
                    onFavoriteClick = { viewModel.toggleFavorite(product.id) },
                    onAddClick = { onAddToCart(product) }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------

/** Barra de búsqueda, chips de categoría y fila desplazable de filtros. */
@Composable
private fun CatalogHeader(
    state: ShopUiState,
    categories: List<String>,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onFilterChange: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SearchBar(
            value = state.search,
            onValueChange = onSearchChange,
            onFilterClick = { /* Los filtros del mockup se muestran en los chips de abajo */ }
        )

        // Chips de categoría: Todos / Libros / Cuadernos / Plumas / Papelería / Oficina
        ChipRow(
            options = categories,
            selectedIndex = categories.indexOf(state.category),
            onSelect = { onCategoryChange(categories[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        // Filtros secundarios en fila desplazable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                text = "Disponible",
                icon = Icons.Rounded.CheckCircle,
                selected = state.filter == "Disponible",
                onClick = { onFilterChange("Disponible") }
            )
            FilterChip(
                text = "Novedades",
                icon = Icons.Rounded.LocalFireDepartment,
                selected = state.filter == "Novedades",
                onClick = { onFilterChange("Novedades") }
            )
            FilterChip(
                text = "Menos de S/ 20",
                icon = Icons.Rounded.Sell,
                selected = state.filter == "Menos de S/ 20",
                onClick = { onFilterChange("Menos de S/ 20") }
            )
            FilterChip(
                text = "Favoritos",
                icon = Icons.Rounded.Star,
                selected = state.filter == "Favoritos",
                onClick = { onFilterChange("Favoritos") }
            )
        }
    }
}

/** Indicador de carga mientras el repositorio trae los productos. */
@Composable
private fun CatalogLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = InkNavy, strokeWidth = 3.dp)
    }
}
