package com.app_frontend.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.ui.components.PasswordField
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.theme.DangerRed
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.AuthEvent
import com.app_frontend.ui.viewmodel.AuthViewModel

/**
 * Pantalla "Iniciar Sesión".
 * Mockup: `intefase/Iniciar Sesión _ Papel & Tinta.png`
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is AuthEvent.LoggedIn) onLoginSuccess()
        }
    }

    AuthScaffold {
        AuthLogo(size = 64)
        AuthHeader(
            title = "Papel & Tinta",
            subtitle = "Tu espacio acogedor de libros y útiles de oficina."
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            AuthTabs(
                isLogin = true,
                onLogin = { /* ya estamos en inicio de sesión */ },
                onRegister = onGoRegister
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Correo Electrónico",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            com.app_frontend.ui.components.OutlinedInputField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "tucorreo@ejemplo.com",
                leadingIcon = Icons.Rounded.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Terracota,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            PasswordField(
                label = "",
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "••••••••",
                leadingIcon = Icons.Rounded.Lock
            )

            state.error?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = DangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Iniciar Sesión",
                onClick = viewModel::login,
                loading = state.isLoading
            )

            SocialDivider(text = "o continúa con")
            SocialRow(onClick = { /* OAuth pendiente del backend */ })
        }

        AuthPromoFooter()
    }
}
