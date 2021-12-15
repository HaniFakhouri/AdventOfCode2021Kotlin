import day01.Puzzle1
import day10.Puzzle10
import day11.Puzzle11
import day02.Puzzle2
import day03.Puzzle3
import day04.Puzzle4
import day05.Puzzle5
import day06.Puzzle6
import day07.Puzzle7
import day08.Puzzle8
import day09.Puzzle9
import day12.Puzzle12
import day13.Puzzle13
import day14.Puzzle14
import day15.Puzzle15

fun main() {
    listOf(
        Puzzle1(),
        Puzzle2(),
        Puzzle3(),
        Puzzle4(),
        Puzzle5(),
        Puzzle6(),
        Puzzle7(),
        Puzzle8(),
        Puzzle9(),
        Puzzle10(),
        Puzzle11(),
        Puzzle12(),
        Puzzle13(),
        Puzzle14(),
        Puzzle15(),
    ).forEach {
        it.runPuzzle()
    }
}