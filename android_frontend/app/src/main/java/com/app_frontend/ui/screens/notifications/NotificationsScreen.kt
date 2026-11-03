/**
 * Pantalla "Centro de Notificaciones" de la app Papel & Tinta.
 */
package com.app_frontend.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app_frontend.data.model.AppNotification
import com.app_frontend.data.model.NotificationTab
import com.app_frontend.data.model.NotificationType
import com.app_frontend.ui.components.EmptyState
import com.app_frontend.ui.components.ProductRowImage
import com.app_frontend.ui.components.SecondaryButton
import com.app_frontend.ui.components.SectionCard
import com.app_frontend.ui.theme.AmberLogo
import com.app_frontend.ui.theme.Border
import com.app_frontend.ui.theme.CreamDeep
import com.app_frontend.ui.theme.InfoBlue
import com.app_frontend.ui.theme.InfoBlueBg
import com.app_frontend.ui.theme.InkNavy
import com.app_frontend.ui.theme.PeachBg
import com.app_frontend.ui.theme.SurfaceWhite
import com.app_frontend.ui.theme.Terracota
import com.app_frontend.ui.theme.TextMuted
import com.app_frontend.ui.theme.TextOnDark
import com.app_frontend.ui.theme.TextPrimary
import com.app_frontend.ui.theme.TextSecondary
import com.app_frontend.ui.theme.WarningBg
import com.app_frontend.ui.viewmodel.NotificationViewModel

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val visible = viewModel.visible(notifications)
    val unreadTotal = viewModel.unreadCount(notifications)
    val unreadByTab = notifications.filter { !it.isRead }
        .groupingBy { it.tab }
        .eachCount()
    val groups = visible.groupBy { it.group }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NotificationsHeader(onMarkAllRead = viewModel::markAllRead)

        NotificationTabs(
            current = uiState.tab,
            unreadTotal = unreadTotal,
            unreadByTab = unreadByTab,
            onTabChange = viewModel::onTabChange
        )

        if (visible.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Notifications,
                title = if (notifications.isEmpty()) {
                    "No tienes notificaciones"
                } else {
                    "No hay notificaciones aquí"
                },
                message = if (notifications.isEmpty()) {
                    "Aviso de pedidos, puntos y preventas aparecerán en este centro."
                } else {
                    "Prueba con otra pestaña o revisa tu historial completo."
                }
            )
        } else {
            groups.forEach { (group, items) ->
                GroupHeader(
                    group = group,
                    unread = items.count { !it.isRead }
                )
                items.forEach { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = { viewModel.markRead(notification.id) }
                    )
                }
            }
        }

        HistoryCard()
        PreferencesLink()
    }
}

// ---------------------------------------------------------------------------
// Cabecera
// ---------------------------------------------------------------------------

@Composable
private fun NotificationsHeader(onMarkAllRead: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Mantente al tanto de tus lecturas y pedidos",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .widthIn(max = 118.dp)
                .clip(RoundedCornerShape(50))
                .background(CreamDeep)
                .clickable(onClick = onMarkAllRead)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Marcar leídas",
                style = MaterialTheme.typography.labelLarge,
                color = Terracota,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Pestañas
// ---------------------------------------------------------------------------

@Composable
private fun NotificationTabs(
    current: NotificationTab,
    unreadTotal: Int,
    unreadByTab: Map<NotificationTab, Int>,
    onTabChange: (NotificationTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotificationTab.entries.forEach { tab ->
            NotificationTabChip(
                text = tab.label,
                selected = tab == current,
                count = unreadByTab[tab] ?: 0,
                showDot = tab == NotificationTab.ALL && unreadTotal > 0,
                onClick = { onTabChange(tab) }
            )
        }
    }
}

@Composable
private fun NotificationTabChip(
    text: String,
    selected: Boolean,
    count: Int,
    showDot: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) InkNavy else SurfaceWhite)
            .then(
                if (selected) Modifier else Modifier.border(1.dp, Border, shape)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) TextOnDark else InkNavy,
            maxLines = 1
        )
        if (count > 0) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Terracota),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextOnDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AmberLogo)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Agrupador por día
// ---------------------------------------------------------------------------

@Composable
private fun GroupHeader(group: String, unread: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = group,
            style = MaterialTheme.typography.labelLarge,
            color = Terracota,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Text(
            text = if (unread > 0) "$unread nuevas" else "Leídas",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

// ---------------------------------------------------------------------------
// Tarjeta de notificación
// ---------------------------------------------------------------------------

private data class NotificationVisual(
    val icon: ImageVector,
    val background: Color,
    val tint: Color
)

private fun visualFor(type: NotificationType): NotificationVisual = when (type) {
    NotificationType.ORDER -> NotificationVisual(
        icon = Icons.Rounded.LocalShipping,
        background = PeachBg,
        tint = Terracota
    )
    NotificationType.REWARD -> NotificationVisual(
        icon = Icons.Rounded.Star,
        background = WarningBg,
        tint = AmberLogo
    )
    NotificationType.PREORDER -> NotificationVisual(
        icon = Icons.Rounded.MenuBook,
        background = InfoBlueBg,
        tint = InfoBlue
    )
    NotificationType.SYSTEM -> NotificationVisual(
        icon = Icons.Rounded.Info,
        background = CreamDeep,
        tint = InkNavy
    )
}

@Composable
private fun NotificationCard(notification: AppNotification, onClick: () -> Unit) {
    val visual = visualFor(notification.type)
    val current = notification.progressCurrent
    val total = notification.progressTotal
    val hasHighlight = !notification.highlight.isNullOrBlank()

    SectionCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        containerColor = SurfaceWhite,
        cornerRadius = 16,
        contentPadding = 14
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(visual.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.tint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = notification.timeLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            maxLines = 1
                        )
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(AmberLogo)
                            )
                        }
                    }
                }

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                if (hasHighlight || (current != null && total != null)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CreamDeep)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        notification.highlight?.let { highlight ->
                            HighlightRow(notification = notification, highlight = highlight)
                        }
                        if (current != null && total != null) {
                            PointsProgress(current = current, total = total)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HighlightRow(notification: AppNotification, highlight: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when (notification.type) {
            NotificationType.PREORDER -> ProductRowImage(
                url = null,
                modifier = Modifier.size(40.dp),
                cornerRadius = 8
            )
            NotificationType.ORDER -> Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = null,
                    tint = Terracota,
                    modifier = Modifier.size(17.dp)
                )
            }
            else -> Unit
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = highlight,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            if (notification.actionLabel == null && notification.type == NotificationType.PREORDER) {
                Text(
                    text = "Cupos limitados de preventa",
                    style = MaterialTheme.typography.labelMedium,
                    color = Terracota
                )
            }
        }

        notification.actionLabel?.let { label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Terracota,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = Terracota,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun PointsProgress(current: Int, total: Int) {
    val fraction = if (total > 0) {
        (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nivel Lector Voraz",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$current / $total pts",
                style = MaterialTheme.typography.labelLarge,
                color = Terracota,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(Border)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Terracota)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Historial y preferencias
// ---------------------------------------------------------------------------

@Composable
private fun HistoryCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CreamDeep)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = InkNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Buscas notificaciones anteriores?",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Conservamos tus avisos y comprobantes de lectura de los últimos 30 días en tu archivo personal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            SecondaryButton(
                text = "Ver historial completo ›",
                onClick = {},
                modifier = Modifier.width(200.dp),
                containerColor = SurfaceWhite
            )
        }
    }
}

@Composable
private fun PreferencesLink() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Conectar pantalla de preferencias */ }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Tune,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Configurar preferencias de notificación",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}