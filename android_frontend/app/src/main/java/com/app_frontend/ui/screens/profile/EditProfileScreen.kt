/**
 * Pantalla "Editar Perfil" de la app Papel & Tinta.
 *
 * Mockup de referencia: `intefase/Main.png`
 */
package com.app_frontend.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.model.UserProfile
import com.app_frontend.ui.components.AppCheckbox
import com.app_frontend.ui.components.AppImage
import com.app_frontend.ui.components.IconCircleButton
import com.app_frontend.ui.components.LabeledTextField
import com.app_frontend.ui.components.PasswordField
import com.app_frontend.ui.components.PrimaryButton
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.components.SectionHeader
import com.app_frontend.ui.components.TextActionButton
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.DangerRed
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.PeachBg
import com.app_frontend.ui.theme.SuccessGreen
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.viewmodel.ProfileViewModel

/**
 * Pantalla de edición del perfil: datos personales, dirección de envíos,
 * preferencias de notificación y seguridad de la cuenta.
 */
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = state.profile

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackRow(onBack = onBack)

        EditProfileHeaderCard(profile = profile)

        // --- Información Personal ---
        SectionCard {
            SectionHeader(title = "Información Personal", icon = Icons.Rounded.Person)
            Spacer(modifier = Modifier.height(12.dp))
            LabeledTextField(
                label = "Nombre Completo",
                value = profile.fullName,
                onValueChange = viewModel::onFullNameChange
            )
            Spacer(modifier = Modifier.height(14.dp))
            LabeledTextField(
                label = "Correo Electrónico",
                value = profile.email,
                onValueChange = viewModel::onEmailChange,
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(14.dp))
            LabeledTextField(
                label = "Teléfono / Celular",
                value = profile.phone,
                onValueChange = viewModel::onPhoneChange,
                keyboardType = KeyboardType.Phone
            )
        }

        // --- Dirección de Envíos ---
        SectionCard {
            SectionHeader(title = "Dirección de Envíos", icon = Icons.Rounded.LocalShipping)
            Spacer(modifier = Modifier.height(12.dp))
            LabeledTextField(
                label = "Calle y Número",
                value = profile.address.street,
                onValueChange = viewModel::onStreetChange
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LabeledTextField(
                    label = "Ciudad",
                    value = profile.address.city,
                    onValueChange = viewModel::onCityChange,
                    modifier = Modifier.weight(1f)
                )
                LabeledTextField(
                    label = "Código Postal",
                    value = profile.address.zip,
                    onValueChange = viewModel::onZipChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- Preferencias de Notificaciones ---
        SectionCard {
            SectionHeader(
                title = "Preferencias de Notificaciones",
                icon = Icons.Rounded.Notifications
            )
            Spacer(modifier = Modifier.height(8.dp))
            NotificationOptionRow(
                title = "Newsletter de libros y novedades",
                description = "Recibe recomendaciones semanales y preventas exclusivas.",
                checked = profile.newsletter,
                onCheckedChange = viewModel::onNewsletterChange
            )
            NotificationOptionRow(
                title = "Ofertas de útiles de oficina y papelería",
                description = "Entérate de descuentos en cuadernos, plumas y accesorios.",
                checked = profile.offers,
                onCheckedChange = viewModel::onOffersChange
            )
        }

        // --- Seguridad y Contraseña ---
        SectionCard {
            SectionHeader(title = "Seguridad y Contraseña", icon = Icons.Rounded.Security)
            Spacer(modifier = Modifier.height(12.dp))
            PasswordField(
                label = "Contraseña Actual",
                value = state.currentPassword,
                onValueChange = viewModel::onCurrentPasswordChange,
                placeholder = "••••••••"
            )
            Spacer(modifier = Modifier.height(14.dp))
            PasswordField(
                label = "Nueva Contraseña",
                value = state.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                placeholder = "Mínimo 8 caracteres"
            )
            Spacer(modifier = Modifier.height(14.dp))
            PasswordField(
                label = "Confirmar Nueva Contraseña",
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = "Repite la nueva contraseña"
            )
        }

        state.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = SuccessGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        state.error?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = DangerRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        PrimaryButton(
            text = "Guardar Cambios",
            onClick = viewModel::save,
            loading = state.isSaving,
            icon = Icons.Rounded.Save
        )

        TextActionButton(text = "Cancelar", onClick = onBack)
    }
}

// ---------------------------------------------------------------------------
// Cabecera
// ---------------------------------------------------------------------------

@Composable
private fun BackRow(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onBack)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(CreamDeep),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Volver",
                tint = InkNavy,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = "volver a Inicio",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun EditProfileHeaderCard(profile: UserProfile) {
    SectionCard(containerColor = CreamDeep, cornerRadius = 18, contentPadding = 20) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Sello durazno decorativo del mockup (se recorta contra la tarjeta).
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 36.dp)
                    .size(110.dp)
                    .clip(RoundedCornerShape(50))
                    .background(PeachBg.copy(alpha = 0.75f))
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    ProfileAvatar(profile = profile, size = 92.dp)
                    IconCircleButton(
                        onClick = { /* Cambio de foto: pendiente del permiso de cámara. */ },
                        icon = Icons.Rounded.PhotoCamera,
                        contentDescription = "Cambiar foto de perfil",
                        containerColor = InkNavy,
                        contentColor = TextOnDark,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
                Text(
                    text = profile.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Lector ávido desde ${profile.memberSince}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Avatar grande con foto o iniciales como respaldo. */
@Composable
private fun ProfileAvatar(profile: UserProfile, size: Dp) {
    if (!profile.avatarUrl.isNullOrBlank()) {
        AppImage(
            url = profile.avatarUrl,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(50))
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(50))
                .background(SurfaceWhite),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profile.initials,
                style = MaterialTheme.typography.headlineMedium,
                color = Terracota,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Preferencias de notificación
// ---------------------------------------------------------------------------

@Composable
private fun NotificationOptionRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        AppCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(top = 2.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
