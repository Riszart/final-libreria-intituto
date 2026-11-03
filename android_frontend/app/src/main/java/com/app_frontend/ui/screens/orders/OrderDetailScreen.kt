package com.app_frontend.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app_frontend.ui.viewmodel.OrderViewModel

@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    onContinueShopping: () -> Unit,
    viewModel: OrderViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Detalle del Pedido: $orderId", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onContinueShopping) {
            Text("Continuar comprando")
        }
    }
}