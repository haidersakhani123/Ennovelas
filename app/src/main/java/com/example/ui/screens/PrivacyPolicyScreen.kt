package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BlueDark
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * PrivacyPolicyScreen:
 * Dedicated screen displaying the complete Privacy Policy conforming strictly
 * to Google Play Console Developer Program Policies and User Data regulations.
 */
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .testTag("privacy_policy_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("privacy_policy_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextPrimary
                )
            }

            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = "Política de Privacidad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Ennovelas • Términos y Protección de Datos",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        HorizontalDivider(color = BorderLight, thickness = 1.dp)

        // Scrollable Document Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Header Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BluePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Aviso de Privacidad y Cookies",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Última actualización: 30 de septiembre de 2026",
                            fontSize = 11.5.sp,
                            color = BlueDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Introduction Section
            PolicySectionCard(
                icon = Icons.Default.Info,
                title = "Introducción",
                content = "En Ennovelas, accesible a través de nuestra aplicación oficial de Android, la privacidad y seguridad de nuestros usuarios es de máxima prioridad. Este documento detalla de manera transparente qué información procesamos, cómo la empleamos y las medidas implementadas conforme a las directrices de Google Play Developer Policy."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 1
            PolicySectionCard(
                icon = Icons.Default.Lock,
                title = "1. Información que Procesamos",
                content = "Nuestra plataforma no exige el registro de datos sensibles para acceder a las series y novelas. Para brindar una experiencia óptima, procesamos:\n\n" +
                        "• Información Técnica del Dispositivo: Versión del sistema operativo Android, modelo de dispositivo y resolución de pantalla para optimizar la visualización de los videos.\n\n" +
                        "• Datos de Diagnóstico: Registros técnicos anónimos de red para resolver fallos de conexión.\n\n" +
                        "• Almacenamiento Local (Zero Cloud Leak): Sus listas de favoritos, historial de capítulos vistos y valoraciones se guardan exclusivamente en el almacenamiento local de su dispositivo mediante la base de datos Room/SQLite."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 2
            PolicySectionCard(
                icon = Icons.Default.Security,
                title = "2. Uso de la Información",
                content = "La información técnica procesada se utiliza exclusivamente para:\n\n" +
                        "• Gestionar y transmitir el catálogo de entretenimiento en streaming.\n" +
                        "• Notificar sobre nuevos lanzamientos de capítulos y episodios.\n" +
                        "• Prevenir abusos de red y proteger la integridad del servicio."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 3
            PolicySectionCard(
                icon = Icons.Default.Gavel,
                title = "3. Publicidad y Google AdMob",
                content = "Ennovelas utiliza Google AdMob para financiar la gratuidad del servicio. Google AdMob puede procesar identificadores publicitarios anónimos conforme a las directrices de privacidad de Google LLC. El usuario puede restablecer o desactivar la personalización de anuncios en cualquier momento desde los Ajustes de Google de su dispositivo Android."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 4
            PolicySectionCard(
                icon = Icons.Default.Info,
                title = "4. Reproductores y Contenido de Terceros",
                content = "La aplicación organiza e indexa reproductores de video distribuidos a través de la API pública de WordPress y proveedores de video de terceros. Ennovelas no aloja archivos de video en servidores propios ni altera el contenido original de los creadores."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 5
            PolicySectionCard(
                icon = Icons.Default.Shield,
                title = "5. Protección de Menores (COPPA)",
                content = "Ennovelas cumple estrictamente con la Ley COPPA y las directrices de Google Play para Familias. No recopilamos conscientemente información de identificación personal de niños menores de 13 años."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureWhite)
                    .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "Contacto de Soporte y Privacidad",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Para consultas sobre privacidad o derechos de autor:\nCorreo: soporte@ennovelas.app\nSitio oficial: ennovelas.app",
                        fontSize = 11.5.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun PolicySectionCard(
    icon: ImageVector,
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                fontSize = 12.5.sp,
                color = TextSecondary,
                lineHeight = 17.5.sp
            )
        }
    }
}
