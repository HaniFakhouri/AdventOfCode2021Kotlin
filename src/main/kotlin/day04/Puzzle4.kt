package day04

import PuzzleTemplate

class Puzzle4 : PuzzleTemplate(day = 4) {

    private val numbers = inputAsStrings[0].split(",").map { it.toInt() }
    private val boards = mutableListOf<Board>()

    init {
        readInBoards()
    }

    override fun puzzleOne(answer: Int?.() -> Unit) {
        numbers.forEach { number ->
            boards.forEach { board ->
                board.mark(number)
                if (board.bingo()) {
                    return answer(number * board.sumOfUnmarkedNumbers())
                }
            }
        }
    }

    override fun puzzleTwo(answer: Int?.() -> Unit) {
        var lastBoard: Board? = null
        numbers.forEach { number ->
            if (lastBoard == null) {
                for (i in boards.indices) {
                    val board = boards[i]
                    if (!board.skipBoard) {
                        val boardsLeft = boards.filter { !it.skipBoard }.size
                        if (boardsLeft == 1) {
                            lastBoard = board
                            break
                        }
                        board.mark(number)
                        if (board.bingo()) {
                            board.setSkip()
                        }
                    }
                }
            }
            lastBoard?.let { board ->
                board.mark(number)
                if (board.bingo()) {
                    return answer(number * board.sumOfUnmarkedNumbers())
                }
            }
        }
    }

    private fun readInBoards() {
        inputAsStrings.subList(1, inputAsStrings.size).forEach { inputLine ->
            if (inputLine.isEmpty()) {
                boards.add(Board(BOARD_SIZE))
            } else {
                val line = inputLine.replace("  ", " ").trim()
                boards.last().addRow(line.split(' ').mapNotNull { it.toInt() }.toIntArray())
            }
        }
    }

}

private const val BOARD_SIZE = 5

class Board constructor(private val size: Int) {

    private var board = Array(size) { IntArray(size) }
    private var lastAddedRow = 0
    var skipBoard = false
        private set

    fun sumOfUnmarkedNumbers() = board.map { row -> row.filter { it != -1 }.sum() }.sum()

    fun addRow(data: IntArray) {
        board[lastAddedRow++] = data
    }

    fun setSkip() {
        skipBoard = true
    }

    fun mark(num: Int) {
        board.forEachIndexed { i, row ->
            board[i] = row.map { if (it == num) -1 else it }.toIntArray()
        }
    }

    fun bingo(): Boolean {
        if (board.any { it.sum() == -size }) {
            return true
        }
        for (col in 0 until size) {
            if (colAt(col).sum() == -size) {
                return true
            }
        }
        return false
    }

    private fun colAt(col: Int): IntArray {
        var c = IntArray(size)
        var colIndex = 0
        board.forEach { row ->
            c[colIndex++] = row[col]
        }
        return c
    }

}