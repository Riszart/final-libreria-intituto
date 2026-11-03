package com.app_frontend.ui.screens.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app_frontend.ui.viewmodel.OrderViewModel

@Composable
fun SuccessScreen(
    orderId: String,
    onBack: () -> Unit,
    onViewOrders: () -> Unit,
    onBackHome: () -> Unit,
    viewModel: OrderViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("¡Pedido realizado con éxito!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Número de pedido: $orderId")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onViewOrders, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mis pedidos")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al inicio")
        }
    }
}