package com.example.trapthecat.model

enum class CelulaState {
    VAZIO, CERCA, GATO
}

enum class GameStatus {
    JOGANDO, GATO_VENCEU, CERCA_VENCEU
}

data class GameState(
    val grid: List<CelulaState> = List(121) { CelulaState.VAZIO },
    val posicaoGato: Int = 60,
    val status: GameStatus = GameStatus.JOGANDO,
    val seGatoVenceu: Int = 0,
    val seCercaVenceu: Int = 0,

    //variáveis do multiplayer
    val isMultiplayer: Boolean = false,
    val isTurnoGato: Boolean = true
)
