package day12

import PuzzleTemplate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Puzzle12 : PuzzleTemplate(day = 12) {

    private val graph = Graph()

    init {
        inputAsStrings.forEach { line ->
            val (src, dest) = line.split("-")
            graph.addEdge(src, dest)
        }
    }

    override fun puzzleOne(answer: Int?.() -> Unit) {
        answer(getNumberPaths(
            isCaveInPathFunction = { cave, path ->
                !cave.isBigCave() && path.caves.contains(cave)
            }
        ))
    }

    override fun puzzleTwo(answer: Int?.() -> Unit) {
        answer(getNumberPaths(
            isCaveInPathFunction = { cave, path ->
                when {
                    cave.isBigCave() -> false
                    !path.caves.contains(cave) -> false
                    else -> {
                        path.caves.filter { !it.isBigCave() }.any { v ->
                            path.caves.filter { it == v }.size >= 2
                        }
                    }
                }
            }
        ))
    }

    private fun getNumberPaths(isCaveInPathFunction: (Cave, Path) -> Boolean): Int {
        // Do a BFS and maintain a queue of all paths
        val allPaths = LinkedList<Path>()
        val initialPath = Path(caves = arrayListOf(graph.getVertex("start")))
        allPaths.push(initialPath)

        var nrPaths = 0

        while (allPaths.isNotEmpty()) {
            val path = allPaths.poll()
            val lastVisitedCave = path.getLastCave()
            if (lastVisitedCave.name == "end")
                nrPaths++

            lastVisitedCave.adj.forEach { n ->
                val v = n.dest
                if (!isCaveInPathFunction.invoke(v, path)) {
                    val newPath = Path(caves = ArrayList(path.caves))
                    newPath.caves.add(v)
                    allPaths.offer(newPath)
                }
            }
        }
        return nrPaths
    }

}

private typealias Cave = Vertex

private data class Path(val caves: ArrayList<Cave> = ArrayList())
private data class Edge(val dest: Vertex)
private data class Vertex(val name: String, val adj: ArrayList<Edge> = ArrayList())

private fun Cave.isBigCave() = this.name.isUpperCase()
private fun Path.getLastCave() = this.caves.last()
private fun String.isUpperCase() = this.uppercase() == this

private class Graph {
    private val vertexMap = HashMap<String, Vertex>()

    fun addEdge(source: String, dest: String) {
        val u = getVertex(source)
        val v = getVertex(dest)
        if (source != "start" && dest != "end")
            v.adj.add(Edge(u)) // Multigraph i.e. A <--> B as compared to A --> B
        u.adj.add(Edge(v))
    }

    fun getVertex(name: String): Vertex {
        var v = vertexMap[name]
        if (v == null) {
            v = Vertex(name = name)
            vertexMap[name] = v
        }
        return v
    }
}