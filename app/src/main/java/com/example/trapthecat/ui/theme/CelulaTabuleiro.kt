package com.example.trapthecat.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trapthecat.model.CelulaState

//classe para carregar os hexagonos
class CelulaHexagona : Shape {
    override fun createOutline(tam: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(Path().apply {
            val radius = minOf(tam.width, tam.height) / 2f
            val centerX = tam.width / 2
            val centerY = tam.height / 2f

            //calcula os 6 verticies
            for (i in 0 until 6) {
                //a cada iteração multiplica 60 graus e - 30 no angulo para desenhar
                val angleDeg = 60 * i - 30
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val x = centerX + radius * Math.cos(angleRad).toFloat()
                val y = centerY + radius * Math.sin(angleRad).toFloat()

                if (i == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
            close()
        })
    }
}

@Composable
fun CelulaTabuleiro(
    estado: CelulaState,
    tamanho: Dp,
    onClick: () -> Unit
) {
    val corFundo = when (estado) {
        CelulaState.VAZIO -> Color(0xFFB0BEC5)
        CelulaState.CERCA -> Color(0xFF37474F)
        CelulaState.GATO -> Color(0xFFFF9800)
    }

    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CelulaHexagona())
            .background(corFundo)
            .border(width = 1.dp, color = Color(0xFF78909C), shape = CelulaHexagona())
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (estado) {
            CelulaState.GATO -> Text(text = "🐱", fontSize = (tamanho.value * 0.45).sp)
            CelulaState.CERCA -> Text(text = "🚧", fontSize = (tamanho.value * 0.35).sp)
            CelulaState.VAZIO -> {}
        }
    }
}