package day16

import PuzzleTemplate

private val hexToIntMap = mapOf(
    'A' to 10,
    'B' to 11,
    'C' to 12,
    'D' to 13,
    'E' to 14,
    'F' to 15,
)

class Puzzle16 : PuzzleTemplate(day = 16) {

    private val binary = inputAsStrings[0].hexToBinaryString()

    // Global working index
    private var index = 0

    // Global version number sum
    private var versionSum = 0

    override fun puzzleOne(answer: Int?.() -> Unit) {
        index = 0
        val packet = parsePacket(binary)
        answer(packet.version + versionSum)
    }

    override fun puzzleTwoLong(answer: Long?.() -> Unit) {
        index = 0
        val packet = parsePacket(binary)
        answer(packet.applyOp().value)
    }

    private fun parsePacket(binaryStr: String): Packet {
        val versionBits = binaryStr.readNextBits(bits = 3)
        val version = versionBits.binaryStringToLong().toInt()

        val typeBits = binaryStr.readNextBits(bits = 3)
        val typeId = typeBits.binaryStringToLong().toInt()

        return when (typeId) {
            4 -> parseLiteralValuePacket(version, binary)
            else -> parseOperatorPacket(version, typeId, binary)
        }
    }

    private fun parseLiteralValuePacket(
        version: Int,
        binary: String,
    ): Packet.LiteralValuePacket {
        var res = ""
        var firstBit = -1
        while (firstBit != 0) {
            firstBit = binary[index].digitToInt()
            res += binary.readNextBits(start = 1, bits = 5)
        }
        return Packet.LiteralValuePacket(
            version = version,
            value = res.binaryStringToLong()
        )
    }

    private fun parseOperatorPacket(
        version: Int,
        typeId: Int,
        binary: String,
    ): Packet.OperatorPacket {
        val lengthTypeId = binary[index].digitToInt()
        index++
        var subPackets = ArrayList<Packet>()

        if (lengthTypeId == 0) {
            // then the next 15 bits are a number that represents the total length in bits of the sub-packets
            // contained by this packet.
            val totalLength = binary.readNextBits(bits = 15).binaryStringToLong()
            val indexToStop = index + totalLength.toInt()
            while (index < indexToStop) {
                val packet = parsePacket(binary)
                versionSum += packet.version
                subPackets.add(reducePacketIfPossible(packet))
            }
        } else {
            // then the next 11 bits are a number that represents the number of sub-packets immediately contained
            // by this packet.
            val numberSubPackets = binary.readNextBits(bits = 11).binaryStringToLong()
            repeat(numberSubPackets.toInt()) {
                val packet = parsePacket(binary)
                versionSum += packet.version
                subPackets.add(reducePacketIfPossible(packet))
            }
        }

        return Packet.OperatorPacket(
            version = version,
            typeId = typeId,
            subPackets = subPackets
        )
    }

    private fun reducePacketIfPossible(packet: Packet): Packet {
        return if (packet.isPureOpPacket()) {
            packet.applyOp()
        } else {
            packet
        }
    }

    private fun String.readNextBits(start: Int = 0, bits: Int): String {
        return this.substring(index + start, index + bits).also {
            index += bits
        }
    }

}

enum class Op(val typeId: Int) {
    SUM(0), MUL(1), MIN(2), MAX(3), GRT(5), LES(6), EQU(7);

    companion object {
        fun parse(t: Int) = values().first { it.typeId == t }
    }
}

private sealed class Packet(
    open val version: Int = 1,
    open val typeId: Int = -1,
) {

    data class LiteralValuePacket(
        override val version: Int = 1,
        val value: Long,
    ) : Packet(version, typeId = 4)

    data class OperatorPacket(
        override val version: Int = 1,
        override val typeId: Int = -1,
        val op: Op = Op.parse(typeId),
        val subPackets: ArrayList<Packet> = ArrayList()
    ) : Packet(version, typeId)

    // returns true if this packet is an operator packet with only literal sub packets i.e. an operator packet that does
    // not contain other operator sub packets
    fun isPureOpPacket(): Boolean {
        if (this !is OperatorPacket) {
            return false
        }
        return subPackets.filterIsInstance(OperatorPacket::class.java).isEmpty()
    }

    fun applyOp(): LiteralValuePacket {
        val value = ((this as OperatorPacket).subPackets as List<LiteralValuePacket>).applyOp(this.op)
        return LiteralValuePacket(value = value)
    }

}

// "100" -> 4
private fun String.binaryStringToLong() = this.toLong(2)

// "2F" -> "00101111"
private fun String.hexToBinaryString(): String {
    val binaryStr = StringBuilder()
    this.forEach { digit ->
        val decimal = hexToIntMap[digit] ?: digit.digitToInt()
        binaryStr.append(decimal.toLong().toBinaryString())
    }
    return binaryStr.toString()
}

// 4 -> "0100"
private fun Long.toBinaryString(): String {
    val binaryStr = Integer.toBinaryString(this.toInt())
    val remainder = binaryStr.length % 4
    return if (remainder == 0) binaryStr else
        "0".repeat(4 - remainder) + binaryStr // prepend with zeros
}

private fun List<Packet.LiteralValuePacket>.applyOp(op: Op): Long {
    return when (op) {
        Op.SUM -> this.sumOf { it.value }
        Op.MUL -> this.map { it.value }.reduce { acc, i -> acc * i }
        Op.MIN -> this.minOf { it.value }
        Op.MAX -> this.maxOf { it.value }
        Op.GRT -> if (this[0].value > this[1].value) 1 else 0
        Op.LES -> if (this[0].value < this[1].value) 1 else 0
        Op.EQU -> if (this[0].value == this[1].value) 1 else 0
    }
}