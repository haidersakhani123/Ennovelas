package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppFooter(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceVariant)
            .padding(vertical = 32.dp, horizontal = 20.dp)
            .testTag("app_footer"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ennovelas Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppPlayIcon(size = 28.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "En",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "novelas",
                color = BluePrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tagline
        Text(
            text = "Series Y novelas online gratis en ennovelas.",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = BorderLight, thickness = 1.dp)
        Spacer(modifier = Modifier.height(18.dp))

        // Links
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterLink(text = "Inicio", onClick = { onNavigate("home") })
            FooterLink(text = "Tendencias", onClick = { onNavigate("trending") })
            FooterLink(text = "Categorías", onClick = { onNavigate("categories") })
            FooterLink(text = "Ajustes", onClick = { onNavigate("settings") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterLink(text = "Privacidad", onClick = { onNavigate("privacy_policy") })
            FooterLink(text = "Términos", onClick = { onNavigate("privacy_policy") })
            FooterLink(text = "Contacto", onClick = { onNavigate("settings") })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "© 2026 Ennovelas. Todos los derechos reservados.\nContenido provisto a través de la API oficial de WordPress.",
            color = TextMuted,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FooterLink(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    )
}
