package day16

import PuzzleTemplate
import java.math.BigInteger

class Puzzle16 : PuzzleTemplate(day = 16) {

    // Global working index
    private var index = 0

    private var hex = ""

    override fun puzzleOne(answer: Int?.() -> Unit) {
        answer(decode(inputAsStrings[0]).getVersionSum())
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

    private fun decode(hex: String): Packet {
        index = 0
        val binaryStr = hexStrToBinaryStr(hex)
        this.hex = binaryStr
        return parsePacket(binaryStr)
    }

    private fun parsePacket(binaryStr: String): Packet {
        val versionBits = binaryStr.substring(index, index + 3)
        val version = binaryStrToBigInt(versionBits).toInt()
        index += 3

        val typeBits = binaryStr.substring(index, index + 3)
        val typeId = binaryStrToBigInt(typeBits).toInt()
        index += 3

        return when (typeId) {
            4 -> parseLiteralValuePacket(version, hex)
            else -> parseOperatorPacket(version, typeId, hex)
        }
    }

    private fun parseLiteralValuePacket(
        version: Int,
        hex: String,
    ): Packet.LiteralValuePacket {
        var res = ""
        var firstBit = -1
        while (firstBit != 0) {
            firstBit = hex[index].digitToInt()
            val v = hex.substring(index + 1, index + 5)
            res += v
            index += 5
        }
        val value = binaryStrToBigInt(res)

        return Packet.LiteralValuePacket(
            version = version,
            value = value
        )
    }

    private fun parseOperatorPacket(
        version: Int,
        typeId: Int,
        hex: String,
    ): Packet.OperatorPacket {
        val lengthTypeId = hex[index].digitToInt()
        index++
        var subPackets = ArrayList<Packet>()

        if (lengthTypeId == 0) {
            // then the next 15 bits are a number that represents the total length in bits of the sub-packets
            // contained by this packet.
            val v = hex.substring(index, index + 15)
            val totalLength = binaryStrToBigInt(v)
            index += 15
            val indexToStop = index + totalLength.toInt()
            while (index < indexToStop) {
                subPackets.add(parsePacket(hex))
            }
        } else {
            // then the next 11 bits are a number that represents the number of sub-packets immediately contained
            // by this packet.
            val v = hex.substring(index, index + 11)
            val numberSubPackets = binaryStrToBigInt(v)
            index += 11
            repeat(numberSubPackets.toInt()) {
                subPackets.add(parsePacket(hex))
            }
        }

        return Packet.OperatorPacket(
            version = version,
            typeId = typeId,
            subPackets = subPackets
        )
    }

}

private sealed class Packet(
    open val version: Int = 1,
    open val typeId: Int = -1,
) {

    data class LiteralValuePacket(
        override val version: Int = 1,
        val value: BigInteger,
    ) : Packet(version, typeId = 4) {
        override fun toString(): String {
            return value.toString()
        }
    }

    data class OperatorPacket(
        override val version: Int = 1,
        override val typeId: Int = -1,
        val op: Op = Op.parse(typeId),
        val subPackets: ArrayList<Packet> = ArrayList()
    ) : Packet(version, typeId) {

        override fun toString(): String {
            return "$op: $subPackets"
        }
    }

    fun getVersionSum(): Int {
        return when (this) {
            is LiteralValuePacket -> this.version
            is OperatorPacket -> this.version + this.subPackets.map { it.getVersionSum() }.sum()
        }
    }

}

enum class Op(val typeId: Int) {
    SUM(0), MUL(1), MIN(2), MAX(3), GRT(5), LES(6), EQU(7);

    companion object {
        fun parse(t: Int) = values().first { it.typeId == t }
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