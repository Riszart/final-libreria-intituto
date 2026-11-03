/**
 * Pantalla "Mi Perfil" (menú de cuenta) de la app Papel & Tinta.
 *
 * Mockup de referencia: `intefase/Html → Body (1).png`
 */
package com.app_frontend.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.model.AppConfig
import com.app_frontend.data.model.UserProfile
import com.app_frontend.data.model.asPrice
import com.app_frontend.ui.components.AppDivider
import com.app_frontend.ui.components.AppImage
import com.app_frontend.ui.components.ChevronRow
import com.app_frontend.ui.components.OverlineText
import com.app_frontend.ui.components.PillBadge
import com.app_frontend.ui.components.SecondaryButton
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.components.SectionHeader
import com.app_frontend.ui.components.SmallButton
import com.app_frontend.ui.theme.AmberLogo
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.DangerRed
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.theme.WarningBg
import com.app_frontend.ui.viewmodel.ProfileViewModel

/** Nivel del club en números romanos, como en el mockup ("Nivel II"). */
private fun Int.toRoman(): String = when (this) {
    1 -> "I"
    2 -> "II"
    3 -> "III"
    4 -> "IV"
    5 -> "V"
    6 -> "VI"
    7 -> "VII"
    8 -> "VIII"
    9 -> "IX"
    10 -> "X"
    else -> toString()
}

/**
 * Pantalla "Mi Perfil": datos de la cuenta, compras, preferencias y soporte.
 */
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenFavorites: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = state.profile
    var notificationsOn by rememberSaveable(profile.notificationsEnabled) {
        mutableStateOf(profile.notificationsEnabled)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            OverlineText(text = "ESPACIO PERSONAL", color = Terracota)
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gestiona tu cuenta, lecturas y preferencias de librería.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        ProfileSummaryCard(profile = profile)

        // --- Mi Cuenta y Datos ---
        MenuSection(
            title = "Mi Cuenta y Datos",
            trailing = {
                Text(
                    text = "Seguridad activa",
                    style = MaterialTheme.typography.labelLarge,
                    color = Terracota,
                    fontWeight = FontWeight.SemiBold
                )
            }
        ) {
            ProfileMenuRow(
                icon = Icons.Rounded.Person,
                title = "Información Personal",
                subtitle = "Nombre, correo, teléfono y contraseña",
                onClick = onEditProfile
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.Place,
                title = "Direcciones Guardadas",
                subtitle = profile.addressSummary,
                badge = "2",
                onClick = { /* Se abre el listado de direcciones. */ }
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.CreditCard,
                title = "Tarjetas y Métodos de Pago",
                subtitle = profile.paymentSummary,
                onClick = { /* Se abre el listado de medios de pago. */ }
            )
        }

        // --- Mis Compras y Lectura ---
        MenuSection(title = "Mis Compras y Lectura") {
            ProfileMenuRow(
                icon = Icons.Rounded.LocalShipping,
                title = "Historial de Pedidos",
                subtitle = "Llega mañana: Cuaderno Leuchtturm1917 + Novela",
                badge = "1 en camino",
                onClick = onOpenOrders
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.FavoriteBorder,
                title = "Favoritos & Lista de Deseos",
                subtitle = "Plumas estilográficas y clásicos de poesía",
                badge = "4",
                onClick = onOpenFavorites
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.RateReview,
                title = "Mis Reseñas y Club de Lectura",
                subtitle = "3 opiniones publicadas • Próxima tertulia 24 Oct",
                onClick = { /* Se abren las reseñas y el club de lectura. */ }
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.ConfirmationNumber,
                title = "Cupones y Descuentos",
                subtitle = "15% en Editoriales Independientes y 20% Papelería",
                badge = "2 disponibles",
                onClick = { /* Se abren los cupones disponibles. */ }
            )
        }

        // --- Preferencias ---
        MenuSection(title = "Preferencias") {
            NotificationsRow(
                checked = notificationsOn,
                onCheckedChange = { notificationsOn = it }
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.Palette,
                title = "Ambiente y Lectura",
                subtitle = "Papel marfil suave activado • Fuente Clásica",
                onClick = { /* Se abren las opciones de ambiente y tipografía. */ }
            )
        }

        // --- Soporte & Información ---
        MenuSection(title = "Soporte & Información") {
            ProfileMenuRow(
                icon = Icons.Rounded.SupportAgent,
                title = "Centro de Ayuda & Atención",
                subtitle = "WhatsApp directo con libreros o chat en vivo",
                onClick = { /* Se abre el centro de ayuda. */ }
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.Description,
                title = "Términos y Condiciones",
                subtitle = "Políticas de compras, envíos y devoluciones",
                onClick = { /* Se abren los términos. */ }
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.PrivacyTip,
                title = "Política de Privacidad y Cookies",
                subtitle = "Tratamiento de datos personales seguro",
                onClick = { /* Se abre la política de privacidad. */ }
            )
            AppDivider()
            ProfileMenuRow(
                icon = Icons.Rounded.Info,
                title = "Acerca de Papel & Tinta",
                subtitle = "Hecho con amor por la lectura y el buen papel",
                badge = AppConfig.APP_VERSION,
                onClick = { /* Se muestra la ficha de la aplicación. */ }
            )
        }

        SecondaryButton(
            text = "Cerrar Sesión",
            onClick = onLogout,
            icon = Icons.Rounded.Logout,
            containerColor = SurfaceWhite,
            contentColor = DangerRed
        )

        StoreFooter()
    }
}

// ---------------------------------------------------------------------------
// Tarjeta principal
// ---------------------------------------------------------------------------

@Composable
private fun ProfileSummaryCard(profile: UserProfile) {
    SectionCard(cornerRadius = 18, contentPadding = 16) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(profile = profile, size = 76.dp)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                PillBadge(
                    text = "${profile.readingLevel} • Nivel ${profile.levelNumber.toRoman()}",
                    modifier = Modifier.padding(top = 8.dp),
                    containerColor = WarningBg,
                    contentColor = Terracota,
                    icon = Icons.Rounded.AutoStories
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CreamDeep)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AmberLogo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.WorkspacePremium,
                    contentDescription = null,
                    tint = TextOnDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${profile.points} Puntos Acumulados",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Equivalente a ${AppConfig.CURRENCY} ${profile.pointsAsMoney.asPrice()} en beneficios",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            SmallButton(
                text = "Ver beneficios",
                // Los beneficios del club se muestran desde el detalle de recompensas.
                onClick = {}
            )
        }
    }
}

/** Avatar circular con la foto del perfil o las iniciales como respaldo. */
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
                .background(CreamDeep),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profile.initials,
                style = MaterialTheme.typography.headlineSmall,
                color = Terracota,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Secciones del menú
// ---------------------------------------------------------------------------

@Composable
private fun MenuSection(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = title, trailing = trailing)
        SectionCard(containerColor = SurfaceWhite, cornerRadius = 16, contentPadding = 12) {
            content()
        }
    }
}

/** Fila de menú con icono circular + [ChevronRow] (título, detalle y badge). */
@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(50))
                .background(CreamDeep),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = InkNavy,
                modifier = Modifier.size(20.dp)
            )
        }
        ChevronRow(
            title = title,
            subtitle = subtitle,
            modifier = Modifier.weight(1f),
            badge = badge,
            onClick = onClick
        )
    }
}

@Composable
private fun NotificationsRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(50))
                .background(CreamDeep),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = null,
                tint = InkNavy,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Notificaciones y Alertas",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Pedidos, novedades editoriales y promociones",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextOnDark,
                checkedTrackColor = InkNavy,
                checkedBorderColor = InkNavy,
                uncheckedThumbColor = TextOnDark,
                uncheckedTrackColor = Border,
                uncheckedBorderColor = Border
            )
        )
    }
}

// ---------------------------------------------------------------------------
// Pie
// ---------------------------------------------------------------------------

@Composable
private fun StoreFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoStories,
                contentDescription = null,
                tint = AmberLogo,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = AppConfig.STORE_NAME,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Text(
            text = "© 2024 Todos los derechos reservados • ${AppConfig.STORE_LOCATION}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}
