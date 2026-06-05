<div align="center">

  <h1>🐱 Trap The Cat (Pegue o Gato)</h1>

  ![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
  ![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
  ![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

</div>

---

## 📖 Sobre o Projeto

O **Trap The Cat** é um jogo de tabuleiro de lógica e estratégia desenvolvido nativamente para Android como projeto da matéria de desenvolvimento de aplicativos móveis. O jogo ocorre em um grid hexagonal de 11x11, onde dois papéis se enfrentam: o **Gato**, cujo objetivo é fugir alcançando qualquer borda do tabuleiro, e a **Cerca**, que deve bloquear todos os caminhos e encurralar o Gato.

O aplicativo conta com dois modos de jogo:

- **Singleplayer (vs CPU):** O jogador controla o Gato e joga contra uma Inteligência Artificial baseada no algoritmo de Busca em Largura (BFS), que calcula a rota de fuga mais curta e tenta cortá-la.
- **Multiplayer Local:** Modo *pass-and-play* onde dois jogadores dividem o mesmo dispositivo, alternando turnos entre mover o Gato e colocar a Cerca.

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| **Linguagem** | Kotlin |
| **Interface** | Jetpack Compose (UI Declarativa) |
| **Arquitetura** | MVVM (Model-View-ViewModel) |
| **Algoritmos** | Busca em Largura (BFS) para cálculo de rotas em Grafos |

---

## 📂 Estrutura do Projeto

O projeto foi estruturado seguindo o padrão de separação de responsabilidades (SRP), garantindo que a regra de negócio esteja isolada da interface visual.

```text
app/src/main/java/com/example/trapthecat
 ┣ /config
 ┃ ┗ GameConfig.kt        # validação de turnos, lógica hexagonal e IA (BFS)
 ┣ /model
 ┃ ┗ GameState.kt         # estrutura de dados imutável que representa o estado da partida
 ┣ /ui.theme
 ┃ ┣ CelulaTabuleiro.kt   # componente visual customizado (Hexágono trigonométrico responsivo)
 ┃ ┣ Color.kt             # Paleta de cores do Material Design
 ┃ ┣ Theme.kt             # Configurações do tema principal do Compose
 ┃ ┗ Type.kt              # Tipografia do aplicativo
 ┣ /viewmodel
 ┃ ┗ GameViewModel.kt     # ponte do padrão MVVM que gerencia o fluxo de estados entre a UI e o GameConfig
 ┗ MainActivity.kt        # ponto de entrada do app, montagem das telas e diálogos
```

---

## 🚀 Como Iniciar

**Pré-requisito:** Certifique-se de ter o [Android Studio](https://developer.android.com/studio) instalado (versão mais recente recomendada).

**1. Clone o repositório:**
```bash
git clone https://github.com/Izukill/TrapTheCat
```

**2. Abra o projeto:**
Inicie o Android Studio, clique em **Open** e selecione a pasta do projeto clonado.

**3. Sincronize o Gradle:**
Aguarde o Android Studio baixar as dependências e indexar o projeto.

**4. Execute o App:**
Conecte seu dispositivo Android via cabo (com a **Depuração USB** ativada) ou inicie um Emulador (AVD) e clique no botão **Run ▶**.

---

## 📝 Padrão de Commits

Este projeto adota a convenção de [Conventional Commits](https://www.conventionalcommits.org/) para manter o histórico limpo e rastreável. Ao contribuir, utilize os seguintes prefixos:

| Prefixo | Uso |
|---|---|
| `feat` | Nova funcionalidade — ex: `feat: adicionado modo multiplayer` |
| `fix` | Correção de bug — ex: `fix: corrigido travamento ao iniciar jogo novo` |
| `refactor` | Refatoração sem adicionar feature ou corrigir bug — ex: `refactor: extraída lógica da CPU para função privada` |
| `style` | Formatação de código, espaçamento, etc — ex: `style: ajustado alinhamento dos hexágonos` |
| `docs` | Alterações na documentação — ex: `docs: atualizado README com instruções de instalação` |
| `chore` | Atualização de tarefas de build, dependências do Gradle, etc |
