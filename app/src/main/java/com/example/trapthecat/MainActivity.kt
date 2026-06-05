package com.example.trapthecat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.example.trapthecat.ui.theme.TrapTheCatTheme
import com.example.trapthecat.viewmodel.GameViewModel
import com.example.trapthecat.model.GameStatus
import com.example.trapthecat.ui.theme.CelulaTabuleiro

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrapTheCatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TelaDoJogo(viewModel)
                }
            }
        }
    }
}

@Composable
fun TelaDoJogo(viewModel: GameViewModel) {
    val gameState by viewModel.uiState.collectAsState()

    //pega a largura da tela para n achatar o jogo
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val espacoLivreHorizontal = screenWidth - 32.dp - 20.dp

    val tamanhoCelula = espacoLivreHorizontal / 11.5f //divide por 11 (11x11)
    val deslocamentoImpar = tamanhoCelula / 2f

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //placar
        Text(
            text = "Gato: ${gameState.seGatoVenceu} | Cerca: ${gameState.seCercaVenceu}",
            fontSize = 20.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        //tabuleiro
        for (linha in 0..10) {
            Row(
                modifier = Modifier.padding(bottom = 1.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (linha % 2 != 0) {
                    Spacer(modifier = Modifier.width(deslocamentoImpar))
                }

                for (coluna in 0..10) {
                    val index = linha * 11 + coluna

                    CelulaTabuleiro(
                        estado = gameState.grid[index],
                        tamanho = tamanhoCelula,
                        onClick = { viewModel.onQuadradoClicked(index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (gameState.status) {
            GameStatus.JOGANDO -> {
                Text(text = "Você é o gato, tente escapar para as bordar", fontSize = 16.sp)
            }
            GameStatus.GATO_VENCEU -> {
                Text(text = "Você escapou, Muito bem!", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.comecaJogo() }) {
                    Text("Próxima Partida")
                }
            }
            GameStatus.CERCA_VENCEU -> {
                Text(text = "O Gato foi encurralado!", fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.comecaJogo() }) {
                    Text("Tentar Novamente")
                }
            }
        }
    }
}

//classe para o preview no android studio
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaDoJogoPreview() {
    TrapTheCatTheme {
        val previewViewModel = GameViewModel()
        TelaDoJogo(viewModel = previewViewModel)
    }
}