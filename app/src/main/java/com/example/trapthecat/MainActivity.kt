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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val tamanhoCelula = espacoLivreHorizontal / 11.5f //divide por 11.5 (11x11)
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

        Text(
            text = "Você é o gato, corra para as bordas e não deixe a cerca te fechar.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        //checagem de vitória para mostrar o AlertDialog com a mensagem
        if (gameState.status != GameStatus.JOGANDO) {

            val titulo = if (gameState.status == GameStatus.GATO_VENCEU) "Você venceu! :3" else "Você perdeu :("
            val mensagem = if (gameState.status == GameStatus.GATO_VENCEU) {
                "Parabéns! Você conseguiu escapar para as bordas do tabuleiro."
            } else {
                "Fim de jogo! A Cerca encurralou você te deixando sem rota de fuga."
            }
            val emoji = if (gameState.status == GameStatus.GATO_VENCEU) "🐱" else "🚧"

            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = mensagem,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = emoji,
                            fontSize = 64.sp
                        )
                    }
                },
                //coloquei os 2 botões num row para ficar um ao lado do outro, o comportamento nativo é de empilhamento
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp), //espaço para os botões não colarem nas bordas
                        horizontalArrangement = Arrangement.spacedBy(12.dp) //espaço entre os botões
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.reiniciarPlacar() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Zerar\nPlacar",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                        Button(
                            onClick = { viewModel.comecaJogo() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Jogar\nNovamente",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            )
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