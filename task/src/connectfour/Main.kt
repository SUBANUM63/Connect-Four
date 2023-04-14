package connectfour
const val WINNING_SIZE = 4

class Player(val name: String, val tokenType: Char, var score: Int = 0)

class GameStage(val playerOne: Player, val playerTwo: Player, val gameAmount: Int)

fun main() {
    println("Connect Four")


    println("First player's name:")
    val playerOne = readLine()!!

    println("Second player's name:")
    val playerTwo = readLine()!!


    val player1 = Player(playerOne, 'o')
    val player2 = Player(playerTwo, '*')

    val (rows, cols) = validBoardDimension()

    val numberOfGames = validNumberOfGames()

    val gameStage = GameStage(player1, player2, numberOfGames)

    println("${player1.name} VS ${player2.name}")
    println("$rows X $cols board")

    if (numberOfGames == 1) {
        println("Single game")
    } else {
        println("Total $numberOfGames games")
    }

    repeat (numberOfGames) {
        playTheGame((it + 1), gameStage, rows, cols)
    }

    println("Game over!")
}

fun playTheGame(gameNumber: Int, gameStage: GameStage, rows: Int, cols: Int) {

    val board = List(cols) { MutableList(rows) { ' ' } }

    if (gameStage.gameAmount > 1) {
        println("Game #$gameNumber")
    }
    printGameBoard(board)

    var currentPlayer = if (gameNumber % 2 == 1) gameStage.playerOne else gameStage.playerTwo

    while (true) {
        println("${currentPlayer.name}'s turn:")

        val selectedColumn = readLine()!!
        if (selectedColumn == "end") {
            println("Game over!")
            return
        } else if (!isValidNumber(selectedColumn)) {
            println("Incorrect column number")
            continue
        } else {
            if (selectedColumn.toInt() !in 1..cols) {
                println("The column number is out of range (1 - $cols)")
                continue
            }
            if (getEmptyRowIndexInColumn(board, selectedColumn.toInt() - 1)  < 0) { // b/se -1 is return value for no
                println("Column ${selectedColumn.toInt()} is full")
                continue
            }

            board[selectedColumn.toInt() - 1][ getEmptyRowIndexInColumn(board, selectedColumn.toInt() - 1)] = currentPlayer.tokenType

            printGameBoard(board)

            if (checkBoardForWinningCondition(board, currentPlayer.tokenType)) {
                currentPlayer.score += 2

                println("Player ${currentPlayer.name} won")
                scoreResult(gameStage)
                return
            }

            if (checkForDraw(board)) {
                gameStage.playerOne.score++
                gameStage.playerTwo.score++

                println("It is a draw")
                scoreResult(gameStage)
                return
            }

            // continue game
            currentPlayer = toggleActivePlayer(currentPlayer, gameStage)
        }
    }
}

// check all cells are filled with either of the token types
fun checkForDraw(board : List<List<Char>>) = board.all { chars -> chars.all { c -> c != ' ' } }

fun scoreResult(gameStage: GameStage) {
    println("Score")
    println("${gameStage.playerOne.name}: ${gameStage.playerOne.score} ${gameStage.playerTwo.name}: ${gameStage.playerTwo.score}")
}

fun validNumberOfGames(): Int {
    while (true) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")

        val numberOfGame = readLine()!!
        if (numberOfGame.isEmpty()) {
            return 1
        }
        if (!isValidNumber(numberOfGame)) {
            println("Invalid input")
            continue
        }

        if (numberOfGame.toInt() < 1) {
            println("Invalid input")
            continue
        }

        return numberOfGame.toInt()
    }
}


fun getEmptyRowIndexInColumn(board: List<List<Char>>, col: Int) = board[col].indexOfFirst { it == ' ' }

fun checkBoardForWinningCondition(board: List<List<Char>>, tokenType: Char): Boolean {
    return if (checkColumnsForWinningCondition(board, tokenType)) {
        true
    } else if (checkRowsForWinningCondition(board, tokenType)) {
        true
    } else if (checkUpWardDiagonalsForWinningCondition(board, tokenType)) {
        true
    } else checkDownWardDiagonalsForWinningCondition(board, tokenType)

}

fun checkColumnsForWinningCondition(board: List<List<Char>>, tokenType: Char): Boolean {
    for (col in board.indices) {
        val column = board[col]
        for (row in 0..column.size - WINNING_SIZE) {
            val cells = listOf(
                board[col][row],
                board[col][row + 1],
                board[col][row + 2],
                board[col][row + 3]
                )

            if (cells.all { it == tokenType }) {
                return true
            }
        }
    }
    return false
}

fun checkRowsForWinningCondition(board: List<List<Char>>, tokenType: Char): Boolean {
    // check rows
    for (col in 0 .. board.size - WINNING_SIZE) {
        val column = board[col]
        for (row in column.indices) {
            val cells = listOf(
                board[col][row],
                board[col + 1][row],
                board[col + 2][row],
                board[col + 3][row]
                )

            if (cells.all { it == tokenType }) {
                return true
            }
        }
    }
    return false
}

fun checkUpWardDiagonalsForWinningCondition(board: List<List<Char>>, tokenType: Char): Boolean {
    for (col in 0 .. board.size - WINNING_SIZE) {
        val column = board[col]
        for (row in 0 .. column.size - WINNING_SIZE) {
            val cells = listOf(
                board[col][row],
                board[col + 1][row + 1],
                board[col + 2][row + 2],
                board[col + 3][row + 3]
                )

            if (cells.all { it == tokenType }) {
                return true
            }
        }
    }
    return false
}

fun checkDownWardDiagonalsForWinningCondition(board: List<List<Char>>, tokenType: Char): Boolean {
    for (col in 0 .. board.size - WINNING_SIZE) {
        val column = board[col]
        for (row in WINNING_SIZE - 1 until column.size) {
            val cells = listOf(
                board[col][row],
                board[col + 1][row - 1],
                board[col + 2][row - 2],
                board[col + 3][row - 3]
            )

            if (cells.all { it == tokenType }) {
                return true
            }
        }
    }
    return false
}


fun isValidNumber(input: String) = Regex("\\d+").matches(input)

fun toggleActivePlayer(player: Player, gameStage: GameStage) = if (player == gameStage.playerOne) gameStage.playerTwo else gameStage.playerOne

fun validBoardDimension(): Pair<Int, Int> {
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")

    val regex = """\s*(\d)+\s*[xX]\s*(\d)+\s*""".toRegex()
    val boardDimensions = readLine()!!

    val (row, col) = when {
        regex.matches(boardDimensions) -> regex.find(boardDimensions)!!.destructured.toList().map { s -> s.toInt() }
        boardDimensions.isEmpty() -> listOf(6, 7)
        else -> {
            println("Invalid input")
            return validBoardDimension()
        }
    }

    // validate row boundary
    if (row !in 5..9) {
        println("Board rows should be from 5 to 9")
        return validBoardDimension()
    }

    //validate col boundary
    if (col !in 5..9) {
        println("Board columns should be from 5 to 9")
        return validBoardDimension()
    }

    return Pair(row, col)
}

fun printGameBoard(board: List<List<Char>>) {
    val noOfCols = board.size
    val noOfRows = board[0].size

    // header with numbers
    println(" " + (1..noOfCols).joinToString(" "))

    // columns
    repeat (noOfRows) { row ->
        print("║")
        repeat (noOfCols) { col ->
            print("" + board[col][noOfRows - row - 1] + "║")
        }
        println()
    }

    val bottomRow = CharArray(noOfCols) { '═' }.joinToString("╩")
    println("╚$bottomRow╝")
}
