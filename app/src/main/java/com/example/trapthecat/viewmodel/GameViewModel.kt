package com.example.trapthecat.viewmodel

import androidx.lifecycle.ViewModel
import com.example.trapthecat.config.GameConfig
import com.example.trapthecat.model.GameState
import com.example.trapthecat.model.GameStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//classe para guardar o gameConfig e modifica-lo para intermediar a comunicação (MVVM pattern)

class GameViewModel : ViewModel() {

    private val engine = GameConfig()

    //estado interno que pode ser modificado
    private val _uiState = MutableStateFlow(GameState())

    //estado para a tela
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        comecaJogo()
    }

    fun comecaJogo() {
        val estadoAtual = _uiState.value
        _uiState.value = engine.criarStatusInicial(estadoAtual.seGatoVenceu, estadoAtual.seCercaVenceu)
    }

    fun onCellClicked(index: Int) {
        if (_uiState.value.status != GameStatus.JOGANDO) return

        _uiState.update { currentState ->
            engine.jogada(currentState, index)
        }
    }
}