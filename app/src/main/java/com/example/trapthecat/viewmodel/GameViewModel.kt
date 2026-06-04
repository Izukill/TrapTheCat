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

    private val config = GameConfig()

    //estado interno que pode ser modificado
    private val _uiState = MutableStateFlow(config.criarStatusInicial(0, 0))

    //estado para a tela
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    fun onQuadradoClicked(index: Int) {
        _uiState.update { estadoAtual -> config.jogada(estadoAtual, index) }
    }

    fun comecaJogo() {
        //placares anteriores mantidos
        val placarGato = _uiState.value.seGatoVenceu
        val placarCerca = _uiState.value.seCercaVenceu

        _uiState.value = config.criarStatusInicial(gatoVenceu = placarGato, cercaVenceu = placarCerca)
    }

}