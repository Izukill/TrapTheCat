package com.example.trapthecat.model

enum class QuadradoState {
    VAZIO, CERCA, GATO
}

enum class GameStatus {
    JOGANDO, GATO_VENCEU, CERCA_VENCEU
}

data class GameState(
    val grid: List<QuadradoState> = List(121) { QuadradoState.VAZIO },
    val posicaoGato: Int = 60,
    val status: GameStatus = GameStatus.JOGANDO,
    val seGatoVenceu: Int = 0,
    val seCercaVenceu: Int = 0
)
