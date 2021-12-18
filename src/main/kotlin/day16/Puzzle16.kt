package day16

import PuzzleTemplate
import java.lang.StringBuilder
import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.log2

class Puzzle16 : PuzzleTemplate(day = 16) {

    var versionNumbersSum = 0

    override fun puzzleOne(answer: Int?.() -> Unit) {
        println(inputAsStrings[0])
        decode(inputAsStrings[0])

        answer(versionNumbersSum)
    }

    private fun hexStrToBinaryStr(hex: String): String {
        val binaryStr = StringBuilder()
        hex.forEach { digit ->
            val decimal = hexToIntMap[digit] ?: digit.digitToInt()
            binaryStr.append(bigIntToBinaryStr(BigInteger.valueOf(decimal.toLong())))
        }
        return binaryStr.toString()
    }

    // "100" -> 4
    private fun binaryStrToBigInt(input: String) = input.toBigInteger(2)

    // 4 -> "0100"
    private fun bigIntToBinaryStr(input: BigInteger): String {
        val binaryStr = Integer.toBinaryString(input.toInt())
        val remainder = binaryStr.length % 4

        return if (remainder == 0) binaryStr else
            "0".repeat(4 - remainder) + binaryStr // prepend with zeros
    }

    private fun decode(hex: String) {
        val binaryStr = hexStrToBinaryStr(hex) //bigIntToBinaryStr(hexStrToBigInt(hex))
        println("hex:$hex")
        decodeBinaryStr(binaryStr)
    }

    private fun decodeBinaryStr(binaryStr: String) {
        var index = 0
        //var indexToStop = Int.MAX_VALUE
        val len = binaryStr.length

        println("Processing: $binaryStr")
        println("Len: ${binaryStr.length}")
        while (true) {
            println("-----------------------")
            println("index: $index")
            if (index >= len) {
                break
            }

            if (binaryStr.substring(index).map { it.digitToInt() }.sum() == 0) {
                // few extra 0 bits at the end
                break
            }

            val versionBits = binaryStr.substring(index, index + 3)
            val version = binaryStrToBigInt(versionBits)
            versionNumbersSum += version.toInt()
            println("versionBits: $versionBits, version: $version")
            index += 3

            val typeBits = binaryStr.substring(index, index + 3)
            val typeId = binaryStrToBigInt(typeBits)
            println("typeBits: $typeBits, type: $typeId")
            index += 3

            if (typeId == BigInteger.valueOf(4L)) {
                println("*Literal value packet*")
                var res = ""
                var firstBit = -1
                //var index = 0
                while (firstBit != 0) {
                    firstBit = binaryStr[index].digitToInt()
                    val v = binaryStr.substring(index + 1, index + 5)
                    res += v
                    index += 5
                }
                val value = binaryStrToBigInt(res)
                println("Literal value: $res: $value")
            } else {
                println("*Operator packet*")
                val lengthTypeId = binaryStr[index].digitToInt()
                println("Length type id: $lengthTypeId")
                index++
                if (lengthTypeId == 0) {
                    // then the next 15 bits are a number that represents the total length in bits of the sub-packets contained by this packet.
                    val v = binaryStr.substring(index, index + 15)
                    val totalLength = binaryStrToBigInt(v)
                    println("v:$v, Length: $totalLength")
                    index += 15
                    val indexToStop = index + totalLength.toInt()
                    println("Decode from $index to $indexToStop")
                    decodeBinaryStr(binaryStr.substring(index, indexToStop))
                    index = indexToStop
                    println("Continue at index: $index")
                } else {
                    val v = binaryStr.substring(index, index + 11)
                    val numberSubPackets = binaryStrToBigInt(v)
                    println("numberSubPackets: $numberSubPackets")
                    index += 11
                }
            }
        }
    }

}

val hexToIntMap = mapOf(
    'A' to 10,
    'B' to 11,
    'C' to 12,
    'D' to 13,
    'E' to 14,
    'F' to 15,
)