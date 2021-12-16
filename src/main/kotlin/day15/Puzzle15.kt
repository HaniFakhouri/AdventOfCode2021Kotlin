package day15

import PuzzleTemplate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class Puzzle15 : PuzzleTemplate(day = 15) {

    private val map = ArrayList<ArrayList<Int>>()

    init {
        inputAsStrings.forEach {
            map.add(ArrayList(it.map { c -> c.digitToInt() }))
        }
    }

    override fun puzzleOne(answer: Int?.() -> Unit) {
        val graph = buildGraph(map)
        val start = graph.getVertex(graph.startVertexName)
        val end = graph.getVertex(graph.destinationVertexName)

        graph.dijkstraShortestPath(start)

        answer(graph.getVertex(end.name).dist)
    }

    override fun puzzleTwo(answer: Int?.() -> Unit) {
        val rows = map.size
        val cols = map[0].size

        // Make a deep copy
        val copyMap = ArrayList<ArrayList<Int>>()
        for (i in 0 until rows) {
            copyMap.add(ArrayList())
            for (j in 0 until cols) {
                copyMap[i].add(map[i][j])
            }
        }

        // Add to the right
        for (step in 1 until 5) {
            for (row in 0 until rows) {
                copyMap[row].addAll(map[row].map {
                    val inc = it + step
                    if (inc > 9) inc % 9 else inc
                })
            }
        }

        // Add downward
        for (step in 1 until 5) {
            for (row in 0 until rows) {
                copyMap.add(ArrayList(copyMap[row].map {
                    val inc = it + step
                    if (inc > 9) inc % 9 else inc
                }))
            }
        }

        val graph = buildGraph(copyMap)
        val start = graph.getVertex(graph.startVertexName)
        val end = graph.getVertex(graph.destinationVertexName)

        graph.dijkstraShortestPath(start)

        answer(graph.getVertex(end.name).dist)
    }

    private fun buildGraph(map: ArrayList<ArrayList<Int>>): Graph {
        val graph = Graph()
        val rows = map.size
        val cols = map[0].size
        for (row in 0 until map.size) {
            for (col in map[0].indices) {
                val upperIndex = row - 1
                val lowerIndex = row + 1
                val leftIndex = col - 1
                val rightIndex = col + 1

                val srcName = if (row == 0 && col == 0) graph.startVertexName else "$row:$col"

                if (upperIndex >= 0) {
                    val destName = "$upperIndex:$col"
                    graph.addEdge(srcName, destName, map[upperIndex][col])
                }
                if (lowerIndex < rows) {
                    val destName = "$lowerIndex:$col"
                    graph.addEdge(srcName, destName, map[lowerIndex][col])
                }
                if (leftIndex >= 0) {
                    val destName = "$row:$leftIndex"
                    graph.addEdge(srcName, destName, map[row][leftIndex])
                }
                if (rightIndex < cols) {
                    val destName = "$row:$rightIndex"
                    graph.addEdge(srcName, destName, map[row][rightIndex])
                }
            }
        }
        graph.destinationVertexName = "${rows - 1}:${cols - 1}"
        return graph
    }
}

private data class Path(
    val dest: Vertex,
    val cost: Int,
) : Comparable<Path> {
    override fun compareTo(other: Path): Int {
        val otherCost: Int = other.cost
        return if (cost < otherCost) -1 else if (cost > otherCost) 1 else 0
    }
}

private data class Edge(
    val dest: Vertex,
    val weight: Int,
)

private data class Vertex(
    val name: String,
    val adj: ArrayList<Edge> = ArrayList(),
    var dist: Int = Int.MAX_VALUE,
    var visited: Boolean = false,
)

private class Graph {

    lateinit var destinationVertexName: String
    val startVertexName = "start"

    private val vertexMap = HashMap<String, Vertex>()

    fun size(): Int {
        return vertexMap.size
    }

    fun addEdge(source: String, dest: String, weight: Int) {
        val u = getVertex(source)
        val v = getVertex(dest)
        u.adj.add(Edge(v, weight))
    }

    fun getVertex(name: String): Vertex {
        var v = vertexMap[name]
        if (v == null) {
            v = Vertex(name = name)
            vertexMap[name] = v
        }
        return v
    }

    fun dijkstraShortestPath(start: Vertex) {
        val pq = PriorityQueue<Path>()
        pq.add(Path(start, 0))
        start.dist = 0

        var nodesSeen = 0
        while (pq.isNotEmpty() && nodesSeen < this.size()) {
            val vpath = pq.remove()
            val v = vpath.dest
            if (v.visited) {
                continue
            }
            v.visited = true
            nodesSeen++

            for (e in v.adj) {
                val w = e.dest
                val cvw = e.weight
                if (w.dist > cvw + v.dist) {
                    w.dist = cvw + v.dist
                    pq.add(Path(w, w.dist))
                }
            }
        }
    }
}