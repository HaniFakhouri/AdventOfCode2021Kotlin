package day14

import PuzzleTemplate

class Puzzle14 : PuzzleTemplate(day = 14) {

    override fun puzzleOne(answer: Int?.() -> Unit) {
        val chars = HashSet<Char>()
        val template = inputAsStrings[0]
        val rules = inputAsStrings.subList(2, inputAsStrings.size).map {
            val (from, to) = it.split(" -> ")
            chars.apply { add(from[0]); add(from[1]); chars.add(to[0]) }
            from to "${from[0]}$to${from[1]}"
        }.toMap()

        var result = template
        val steps = 10
        for (step in 0 until steps) {
            result = result
                .windowed(2, 1)
                .map { rules[it] }
                .reduce { acc, s -> acc + s?.substring(1) }!!
        }

        val counts = chars.map { c ->
            c to result.count { it == c }
        }.toMap()

        val max = counts.values.maxOrNull() ?: 0
        val min = counts.values.minOrNull() ?: 0

        answer(max - min)
    }

}