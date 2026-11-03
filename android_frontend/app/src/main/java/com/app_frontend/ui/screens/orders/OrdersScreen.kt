package com.app_frontend.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app_frontend.ui.viewmodel.OrderViewModel

@Composable
fun OrdersScreen(
    onOpenOrder: (String) -> Unit,
    viewModel: OrderViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Historial de Pedidos", style = MaterialTheme.typography.titleLarge)
    }
}