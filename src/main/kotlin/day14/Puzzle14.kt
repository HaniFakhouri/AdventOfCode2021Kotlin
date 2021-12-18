package day14

import PuzzleTemplate


class Puzzle14 : PuzzleTemplate(day = 14) {

    override fun puzzleOne(answer: Int?.() -> Unit) {
        val chars = HashSet<Char>()
        val template = inputAsStrings[0]
        var rules = inputAsStrings.subList(2, inputAsStrings.size).map {
            val (from, to) = it.split(" -> ")
            chars.apply { add(from[0]); add(from[1]); chars.add(to[0]) }
            from to "${from[0]}$to${from[1]}"
        }.toMap().toMutableMap()


        var generatedSequences = ArrayList<String>()

        var result = template
        var start = 0
        var end = 2
        var length = result.length
        var seq = result.substring(start, end)


        /*
        val steps = 40
        for (step in 0 until steps) {

            //println("Step ${step+1} start")

            while (true) {
                //println(seq)
                while (rules.containsKey(seq)) {
                    end++
                    if (end > length) break
                    seq += result[end - 1]
                }
                //println(seq)
                var res = seq
                    .windowed(2, 1)
                    .map { rules[it] }
                    .reduce { acc, s -> acc + s?.substring(1) }!!
                rules[seq] = res
                generatedSequences.add(res)
                //println(">> $res")

                start = end - 1
                end += 1
                //end += 1
                //println("s:$start, e:$end, l:$length")
                if (start >= length || end > length) {
                    break
                }
                seq = result.substring(start, end)
            }

            val res = generatedSequences.reduce { acc, s -> acc + s.substring(1) }
            rules[result] = res

            //println("Step ${step + 1}: $res")

            result = res
            start = 0
            end = 2
            length = result.length
            seq = result.substring(start, end)
            generatedSequences.clear()

            //println("Step ${step+1} done. ${result.length}")

            when {
                length < 150 -> println("Step: $res")
                else -> println("Step: ${res.substring(0, 150)} ... ${res.substring(res.length - 150, res.length - 1)}")
            }
        }

        val counts = chars.map { c ->
            c to result.count { it == c }
        }.toMap()

        val max = counts.values.maxOrNull() ?: 0
        val min = counts.values.minOrNull() ?: 0
        */





        /*
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


        val seed = chars.joinToString("")
        println(permute(seed))

        val perms = permute(seed)

        perms.forEach { input ->
            val res = input.windowed(2, 1)
                .map { rules[it] }
                .reduce { acc, s -> acc + s?.substring(1) }!!
            println("$input -> $res")
            rules[input] = res
        }
        */

        //answer(max - min)
    }

    fun permute(s: String, answer: String = ""): List<String> {
        var r = ArrayList<String>()
        if (s.isEmpty()) {
            r.add(answer)
            return r
        }
        for (i in s.indices) {
            val ch = s[i]
            val left_substr = s.substring(0, i)
            val right_substr = s.substring(i + 1)
            val rest = left_substr + right_substr
            r.addAll(permute(rest, answer + ch))
        }
        return r
    }

}