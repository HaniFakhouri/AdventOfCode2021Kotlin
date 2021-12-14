package day13

import PuzzleTemplate
import kotlin.math.max

private sealed class Fold(val v: Int) {
    data class X(val x: Int) : Fold(x)
    data class Y(val x: Int) : Fold(x)
}

class Puzzle13 : PuzzleTemplate(day = 13) {

    private val regexXFold = Regex("(.*x=)(\\d+)")
    private val regexYFold = Regex("(.*y=)(\\d+)")

    private val grid = mutableListOf<ArrayList<Boolean>>()
    private val foldingInstructions = ArrayList<Fold>()

    init {
        var maxX = Integer.MIN_VALUE
        var maxY = Integer.MIN_VALUE
        inputAsStrings.forEach { line ->
            if (line.isNotEmpty()) {
                if (!line.startsWith("fold")) {
                    val (x, y) = line.split(",").map { it.toInt() }
                    maxX = max(maxX, x)
                    maxY = max(maxY, y)
                } else {
                    regexXFold.matchEntire(line)?.groupValues?.get(2)?.let {
                        foldingInstructions.add(Fold.X(it.toInt()))
                    }
                    regexYFold.matchEntire(line)?.groupValues?.get(2)?.let {
                        foldingInstructions.add(Fold.Y(it.toInt()))
                    }
                }
            }
        }
        for (i in 0 until maxY + 1) {
            grid.add(ArrayList())
            for (j in 0 until maxX + 1) {
                grid[i].add(false)
            }
        }
        inputAsStrings.forEach { line ->
            if (line.isNotEmpty() && !line.startsWith("fold")) {
                val (x, y) = line.split(",").map { it.toInt() }
                grid[y][x] = true
            }
        }
    }

    override fun puzzleOne(answer: Int?.() -> Unit) {
        val res = solve(partOne = true)
        answer(res.map { row -> row.count { it } }.sum())
    }

    override fun puzzleTwo(answer: Int?.() -> Unit) {
        val res = solve(partOne = false)
        res.print()
        println("Puzzle 13.2: Answer: ECFHLHZF")
        answer(-1)
    }

    private fun solve(partOne: Boolean): MutableList<ArrayList<Boolean>> {
        var grid = this.grid.toMutableList()
        for (fold in foldingInstructions) {
            when (fold) {
                is Fold.Y -> {
                    val y = fold.v
                    val s1 = grid.subList(0, y)
                    val s2 = grid.subList(y + 1, grid.size)
                    val res = s1.zip(s2.reversed()) { l1, l2 ->
                        l1.zip(l2) { e1, e2 ->
                            e1 || e2
                        }
                    }
                    grid.clear()
                    res.forEach { grid.add(ArrayList(it)) }
                }
                is Fold.X -> {
                    val x = fold.v
                    var xTemp = mutableListOf<ArrayList<Boolean>>()
                    grid.forEach { row ->
                        val s1 = row.subList(0, x)
                        val s2 = row.subList(x + 1, row.size)
                        val res = s1.zip(s2.reversed()) { e1, e2 -> e1 || e2 }
                        xTemp.add(ArrayList(res))
                    }
                    grid.clear()
                    grid.addAll(xTemp)
                }
            }
            if (partOne) break
        }
        return grid
    }

}

private fun List<List<Boolean>>.print() {
    this.forEach { r ->
        r.forEach { print("${if (it) "#" else "."}") }
        println()
    }
}