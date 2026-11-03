package com.app_frontend.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.ui.components.AppCheckbox
import com.app_frontend.ui.components.LabeledTextField
import com.app_frontend.ui.components.OutlinedInputField
import com.app_frontend.ui.components.PasswordField
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.theme.AmberLogo
import com.app_frontend.ui.theme.DangerRed
import com.app_frontend.ui.theme.PeachBg
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.AuthEvent
import com.app_frontend.ui.viewmodel.AuthViewModel

/**
 * Pantalla "Registrarse".
 * Mockup: `intefase/Registrarse (Clásica) _ Papel & Tinta.png`
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is AuthEvent.Registered) onRegisterSuccess()
        }
    }

    AuthScaffold {
        // Logotipo en círculo durazno (variante de registro)
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(50))
                .background(PeachBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MenuBook,
                contentDescription = null,
                tint = AmberLogo,
                modifier = Modifier.size(28.dp)
            )
        }

        AuthHeader(
            title = "Bienvenido a Papel & Tinta",
            subtitle = "Tu refugio literario y de papelería fina te espera."
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            AuthTabs(
                isLogin = false,
                onLogin = onGoLogin,
                onRegister = { /* ya estamos en registro */ }
            )

            Spacer(modifier = Modifier.height(22.dp))

            LabeledTextField(
                label = "Nombre Completo",
                value = state.fullName,
                onValueChange = viewModel::onFullNameChange,
                placeholder = "Ej. Sofía Martínez",
                leadingIcon = Icons.Rounded.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Correo Electrónico",
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "tucorreo@ejemplo.com",
                leadingIcon = Icons.Rounded.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Contraseña",
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Mínimo 8 caracteres",
                leadingIcon = Icons.Rounded.Lock
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Confirmar Contraseña",
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = "Repite tu contraseña",
                leadingIcon = Icons.Rounded.Lock
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                AppCheckbox(
                    checked = state.acceptTerms,
                    onCheckedChange = viewModel::onAcceptTermsChange
                )
                Text(
                    text = buildAnnotatedString {
                        append("Acepto los ")
                        withStyle(androidx.compose.ui.text.SpanStyle(color = Terracota, fontWeight = FontWeight.SemiBold)) {
                            append("Términos del Servicio")
                        }
                        append(" y la ")
                        withStyle(androidx.compose.ui.text.SpanStyle(color = Terracota, fontWeight = FontWeight.SemiBold)) {
                            append("Política de Privacidad")
                        }
                        append(".")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

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
                text = "Crear Cuenta",
                onClick = viewModel::register,
                loading = state.isLoading
            )

            SocialDivider(text = "o regístrate con")
            SocialRow(onClick = { /* OAuth pendiente del backend */ })
        }

        AuthPromoFooter()
    }
}
