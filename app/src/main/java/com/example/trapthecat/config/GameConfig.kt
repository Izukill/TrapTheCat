package com.example.trapthecat.config

import com.example.trapthecat.model.GameState
import com.example.trapthecat.model.GameStatus
import com.example.trapthecat.model.QuadradoState

class GameConfig {

    fun criarStatusInicial(gatoVenceu: Int, cercaVenceu: Int): GameState {
        val newGrid = MutableList(121) { QuadradoState.VAZIO }
        val posicaoInicialGato = 60
        newGrid[posicaoInicialGato] = QuadradoState.GATO

        gerarCercasIniciais(newGrid, posicaoInicialGato)

        return GameState(
            grid = newGrid,
            posicaoGato = posicaoInicialGato,
            status = GameStatus.JOGANDO,
            seGatoVenceu = gatoVenceu,
            seCercaVenceu = cercaVenceu
        )
    }

    fun jogada(estadoAtualGame: GameState, clickedIndex: Int): GameState {

        //checagem de jogada
        if (estadoAtualGame.status != GameStatus.JOGANDO) return estadoAtualGame
        if (!isMovimentoValido(estadoAtualGame, clickedIndex)) return estadoAtualGame

        //turno do gato e checagem de condição de vitória
        val estadoPosTurnoGato = executarTurnoGato(estadoAtualGame, clickedIndex)
        if (estadoPosTurnoGato.status == GameStatus.GATO_VENCEU) {
            return estadoPosTurnoGato
        }

        //turno da cerca e checagem de vitória
        return executarTurnoCerca (estadoPosTurnoGato)
    }

    private fun gerarCercasIniciais(grid: MutableList<QuadradoState>, posicaoIgnorada: Int) {
        val posicoesDisponiveis = (0 until 121)
            .filter { it != posicaoIgnorada }
            .shuffled()

        val quantidadeCercas = (9..15).random()

        posicoesDisponiveis
            .take(quantidadeCercas)
            .forEach { indice -> grid[indice] = QuadradoState.CERCA }
    }

    private fun isMovimentoValido(estadoAtualGame: GameState, clickedIndex: Int): Boolean {
        val vizinhosPermitidos = getVizinhos(estadoAtualGame.posicaoGato)
        return clickedIndex in vizinhosPermitidos && estadoAtualGame.grid[clickedIndex] == QuadradoState.VAZIO
    }

    private fun executarTurnoGato(estadoAtualGame: GameState, novoIndice: Int): GameState {
        val updatedGrid = estadoAtualGame.grid.toMutableList()
        updatedGrid[estadoAtualGame.posicaoGato] = QuadradoState.VAZIO
        updatedGrid[novoIndice] = QuadradoState.GATO

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
            updatedGrid[indexParaCerca] = QuadradoState.CERCA
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
    private fun calcularJogadaCerca(posicaoGato: Int, grid: List<QuadradoState>): Int {
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
                if (!visitados[vizinho] && grid[vizinho] == QuadradoState.VAZIO) {
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
        val vizinhosVazios = getVizinhos(posicaoGato).filter { grid[it] == QuadradoState.VAZIO }
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

    private fun isGatoPreso(posicaoGato: Int, grid: List<QuadradoState>): Boolean{
        val vizinhos = getVizinhos(posicaoGato)
        return vizinhos.none { grid[it] == QuadradoState.VAZIO }
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