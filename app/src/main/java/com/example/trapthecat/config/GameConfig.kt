package com.example.trapthecat.config

import com.example.trapthecat.model.GameState
import com.example.trapthecat.model.GameStatus
import com.example.trapthecat.model.CelulaState

class GameConfig {

    fun criarStatusInicial(gatoVenceu: Int, cercaVenceu: Int, isMultiplayer: Boolean = false): GameState {
        val newGrid = MutableList(121) { CelulaState.VAZIO }
        val posicaoInicialGato = 60
        newGrid[posicaoInicialGato] = CelulaState.GATO

        gerarCercasIniciais(newGrid, posicaoInicialGato)

        return GameState(
            grid = newGrid,
            posicaoGato = posicaoInicialGato,
            status = GameStatus.JOGANDO,
            seGatoVenceu = gatoVenceu,
            seCercaVenceu = cercaVenceu,
            isMultiplayer = isMultiplayer,
            isTurnoGato = true
        )
    }

    fun jogada(estadoAtualGame: GameState, clickedIndex: Int): GameState {

        //checagem de jogada
        if (estadoAtualGame.status != GameStatus.JOGANDO) return estadoAtualGame

        //checagem de modo de jogo
        return if (estadoAtualGame.isMultiplayer) {
            jogadaMultiplayer(estadoAtualGame, clickedIndex)
        } else {
            jogadaContraCPU(estadoAtualGame, clickedIndex)
        }
    }

    //lógica de cerca sendo cpu
    private fun jogadaContraCPU(estado: GameState, clickedIndex: Int): GameState {
        if (!isMovimentoValido(estado, clickedIndex)) return estado

        val estadoPosTurnoGato = executarTurnoGato(estado, clickedIndex)
        if (estadoPosTurnoGato.status == GameStatus.GATO_VENCEU) {
            return estadoPosTurnoGato
        }

        return executarTurnoCerca(estadoPosTurnoGato)
    }

    //lógica de multiplayer, alterna entre gato e cerca para jogada
    private fun jogadaMultiplayer(estado: GameState, clickedIndex: Int): GameState {
        val updatedGrid = estado.grid.toMutableList()

        if (estado.isTurnoGato) {
            //turno do gato
            if (!isMovimentoValido(estado, clickedIndex)) return estado

            val novoEstadoGato = executarTurnoGato(estado, clickedIndex)
            //se n venceu então retorna o estado
            return if (novoEstadoGato.status == GameStatus.GATO_VENCEU) {
                novoEstadoGato
            } else {
                novoEstadoGato.copy(isTurnoGato = false) //passa o turno
            }
        } else {
            //turno da cerca
            //checagem de quadrados vazios
            if (updatedGrid[clickedIndex] != CelulaState.VAZIO) return estado

            updatedGrid[clickedIndex] = CelulaState.CERCA

            //checa se a jogada prendeu o gato
            if (isGatoPreso(estado.posicaoGato, updatedGrid)) {
                return estado.copy(
                    grid = updatedGrid,
                    status = GameStatus.CERCA_VENCEU,
                    seCercaVenceu = estado.seCercaVenceu + 1
                )
            }

            return estado.copy(grid = updatedGrid, isTurnoGato = true)
        }
    }

    private fun gerarCercasIniciais(grid: MutableList<CelulaState>, posicaoIgnorada: Int) {
        val posicoesDisponiveis = (0 until 121)
            .filter { it != posicaoIgnorada }
            .shuffled()

        val quantidadeCercas = (9..15).random()

        posicoesDisponiveis
            .take(quantidadeCercas)
            .forEach { indice -> grid[indice] = CelulaState.CERCA }
    }

    private fun isMovimentoValido(estadoAtualGame: GameState, clickedIndex: Int): Boolean {
        val vizinhosPermitidos = getVizinhos(estadoAtualGame.posicaoGato)
        return clickedIndex in vizinhosPermitidos && estadoAtualGame.grid[clickedIndex] == CelulaState.VAZIO
    }

    private fun executarTurnoGato(estadoAtualGame: GameState, novoIndice: Int): GameState {
        val updatedGrid = estadoAtualGame.grid.toMutableList()
        updatedGrid[estadoAtualGame.posicaoGato] = CelulaState.VAZIO
        updatedGrid[novoIndice] = CelulaState.GATO

        return if (isNaBorda(novoIndice)) {
            estadoAtualGame.copy(
                grid = updatedGrid,
                posicaoGato = novoIndice,
                status = GameStatus.GATO_VENCEU,
                seGatoVenceu = estadoAtualGame.seGatoVenceu + 1
            )
        } else {
            estadoAtualGame.copy(
                grid = updatedGrid,
                posicaoGato = novoIndice
            )
        }
    }

    private fun executarTurnoCerca(estadoAtualGame: GameState): GameState {
        val updatedGrid = estadoAtualGame.grid.toMutableList()
        val indexParaCerca = calcularJogadaCerca(estadoAtualGame.posicaoGato, updatedGrid)

        if (indexParaCerca != -1) {
            updatedGrid[indexParaCerca] = CelulaState.CERCA
        }

        return if (isGatoPreso(estadoAtualGame.posicaoGato, updatedGrid)) {
            estadoAtualGame.copy(
                grid = updatedGrid,
                status = GameStatus.CERCA_VENCEU,
                seCercaVenceu = estadoAtualGame.seCercaVenceu + 1
            )
        } else {
            estadoAtualGame.copy(grid = updatedGrid)
        }
    }



    //lógica pra jogada da cerca com BFS para achar a menor rota do gato e tentar impedir
    private fun calcularJogadaCerca(posicaoGato: Int, grid: List<CelulaState>): Int {
        val fila = ArrayDeque<Int>()
        val parent = mutableMapOf<Int, Int>()
        val visitados = BooleanArray(121)

        fila.addLast(posicaoGato)
        visitados[posicaoGato] = true

        var bordaAlcancada = -1

        //roda o BFS para achar a borda mais próxima
        while (fila.isNotEmpty()) {
            val atual = fila.removeFirst()

            if (isNaBorda(atual) && atual != posicaoGato) {
                bordaAlcancada = atual
                break
            }

            for (vizinho in getVizinhos(atual)) {
                if (!visitados[vizinho] && grid[vizinho] == CelulaState.VAZIO) {
                    visitados[vizinho] = true
                    parent[vizinho] = atual
                    fila.addLast(vizinho)
                }
            }
        }

        //se achou uma rota, faz um backtrack para descobrir qual é o primeiro passo
        if (bordaAlcancada != -1) {
            var passo = bordaAlcancada
            while (parent[passo] != posicaoGato && parent[passo] != null) {
                passo = parent[passo]!!
            }
            return passo //coloca a cerca no caminho imediato do gato
        }

        //se checou aqui é porque o gato está cercado então é apenas preencher até ele ficar sem rota de fuga
        val vizinhosVazios = getVizinhos(posicaoGato).filter { grid[it] == CelulaState.VAZIO }
        if (vizinhosVazios.isNotEmpty()) {
            return vizinhosVazios.random()
        }

        return -1
    }

    private fun isNaBorda(index: Int): Boolean{
        val lin = index/11
        val col = index%11

        return lin == 0 || lin == 10 || col == 0 || col == 10
    }

    private fun isGatoPreso(posicaoGato: Int, grid: List<CelulaState>): Boolean{
        val vizinhos = getVizinhos(posicaoGato)
        return vizinhos.none { grid[it] == CelulaState.VAZIO }
    }

    //calcula os 6 vizinhos no array
    fun getVizinhos(index: Int): List<Int> {
        val vizinhos = mutableListOf<Int>()
        val lin = index / 11
        val col = index % 11

        //em um grid hexagonal, o deslocamento das colunas vizinhas acima e abaixo depende se for par ou impar
        val isLinhaPar = lin % 2 == 0

        //lista para calcular os offsets (linha,coluna)
        val offsets = if (isLinhaPar) {
            listOf(
                Pair(0, -1), Pair(0, 1), Pair(-1, -1),
                Pair(-1, 0), Pair(1, -1), Pair(1, 0)
            )
        } else {
            listOf(
                Pair(0, -1), Pair(0, 1), Pair(-1, 0),
                Pair(-1, 1), Pair(1, 0), Pair(1, 1)
            )
        }

        //calcula a coordenada com base nos offsets
        for (offset in offsets) {
            val vizinhoLin = lin + offset.first
            val vizinhoCol = col + offset.second

            //só existe se não sair para fora do tabuleiro 11x11.
            if (vizinhoLin in 0..10 && vizinhoCol in 0..10) {

                //converte a coordenada (linha, coluna) de volta para o índice 1D e adiciona na lista
                val vizinhoIndex = vizinhoLin * 11 + vizinhoCol
                vizinhos.add(vizinhoIndex)
            }
        }

        return vizinhos
    }

}