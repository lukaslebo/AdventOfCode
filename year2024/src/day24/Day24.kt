package day24

import check
import readInput
import util.splitByEmptyLines

fun main() {
    val testInput = readInput("2024", "Day24_test")
    check(part1(testInput), 2024)

    val input = readInput("2024", "Day24")
    println(part1(input))
    println(part2(input))
}

private var debug = false

private fun part1(input: List<String>) = input.parseMainboard().computeOutput().binaryToLong()

private fun part2(input: List<String>): String {
    val mainboard = input.parseMainboard()
    val x = mainboard.wires.getBinarySignal("x").binaryToLong()
    val y = mainboard.wires.getBinarySignal("y").binaryToLong()
    val correctOutput = java.lang.Long.toBinaryString(x + y)

    if (debug) {
        val manualSwaps = listOf(
            "dbp" to "fdv",
            "rpp" to "z39",
            "kdf" to "z23",
            "ckj" to "z15",
        )
        val fixedMainboard = mainboard.swap(manualSwaps)
        fixedMainboard.gates
            .filter { it.outputWire.name.startsWith("z") }
            .sortedBy { it.outputWire.name }
            .forEach { it.prettyPrint(fixedMainboard.gates) }
    }

    val swaps = findSwaps(mainboard.gates)
    val fixedMainboard = mainboard.swap(swaps)

    if (fixedMainboard.computeOutput() == correctOutput) {
        return swaps.flatMap { listOf(it.first, it.second) }
            .sorted()
            .joinToString(",")
    }
    error("mainboard is still broken")
}

private fun Mainboard.computeOutput(): String {
    val gatesByInputWire = buildMap<Wire, MutableList<Gate>> {
        for (gate in gates) {
            getOrPut(gate.inputWireA) { mutableListOf() } += gate
            getOrPut(gate.inputWireB) { mutableListOf() } += gate
        }
    }
    wires.filter { it.name.first() !in "xy" }.forEach { it.signal = null }
    val queue = ArrayDeque<Wire>()
    queue += gatesByInputWire.keys.filter { it.signal != null }
    while (queue.isNotEmpty()) {
        val wire = queue.removeFirst()
        val gatesToUpdate = gatesByInputWire.getOrElse(wire) { emptyList() }
        for (gate in gatesToUpdate) {
            val outputWire = gate.outputWire
            if (outputWire.signal != null) continue
            queue += outputWire
            val signalA = gate.inputWireA.signal
            val signalB = gate.inputWireB.signal
            if (signalA != null && signalB != null) {
                outputWire.signal = gate.operation.execute(signalA, signalB)
            }
        }
    }
    return wires.asSequence()
        .filter { it.name.startsWith("z") }
        .sortedByDescending { it.name }
        .map { it.signal }
        .joinToString("")
}

private fun List<Wire>.getBinarySignal(wirePrefix: String) = filter { it.name.startsWith(wirePrefix) }
    .sortedByDescending { it.name.drop(1).toInt() }
    .map { it.signal }
    .joinToString("")

private fun String.binaryToLong() = java.lang.Long.parseLong(this, 2)

private fun Mainboard.swap(swaps: Iterable<Pair<String, String>>): Mainboard {
    val wireByName = wires.map { Wire(it.name, it.signal) }.associateBy { it.name }
    val swappedGates = gates.map { gate ->
        val swap = swaps.find { gate.outputWire.name == it.first || gate.outputWire.name == it.second }
        if (swap?.first == gate.outputWire.name) gate.copy(
            inputWireA = wireByName.getValue(gate.inputWireA.name),
            inputWireB = wireByName.getValue(gate.inputWireB.name),
            outputWire = wireByName.getValue(swap.second),
        )
        else if (swap?.second == gate.outputWire.name) gate.copy(
            inputWireA = wireByName.getValue(gate.inputWireA.name),
            inputWireB = wireByName.getValue(gate.inputWireB.name),
            outputWire = wireByName.getValue(swap.first),
        )
        else gate.copy(
            inputWireA = wireByName.getValue(gate.inputWireA.name),
            inputWireB = wireByName.getValue(gate.inputWireB.name),
            outputWire = wireByName.getValue(gate.outputWire.name),
        )
    }
    return copy(gates = swappedGates, wires = wireByName.values.toList())
}

private fun List<String>.parseMainboard(): Mainboard {
    val (inputLines, gateLines) = splitByEmptyLines()
    val inputWires = inputLines.map {
        val (name, signal) = it.split(": ")
        Wire(
            name = name,
            signal = signal.toInt(),
        )
    }
    val wiresByName = inputWires.associateBy { it.name }.toMutableMap()
    fun findOrCreateWire(name: String) = wiresByName.getOrPut(name) { Wire(name) }
    val gates = gateLines.map {
        val (inputWireA, operation, inputWireB, _, outputWire) = it.split(" ")
        Gate(
            operation = Operation.valueOf(operation),
            inputWireA = findOrCreateWire(inputWireA),
            inputWireB = findOrCreateWire(inputWireB),
            outputWire = findOrCreateWire(outputWire),
        )
    }
    return Mainboard(gates = gates, wires = wiresByName.values.toList())
}

private data class Mainboard(val gates: List<Gate>, val wires: List<Wire>)

private data class Wire(
    val name: String,
) {
    var signal: Int? = null

    constructor(name: String, signal: Int?) : this(name) {
        this.signal = signal
    }
}

private data class Gate(
    val operation: Operation,
    val inputWireA: Wire,
    val inputWireB: Wire,
    val outputWire: Wire,
) {
    val inputWireNames = setOf(inputWireA.name, inputWireB.name)
    val inputWires = setOf(inputWireA, inputWireB)
}

private enum class Operation {

    AND, OR, XOR;

    fun execute(a: Int, b: Int) = when (this) {
        AND -> a and b
        OR -> a or b
        XOR -> a xor b
    }
}

private fun findSwaps(gates: List<Gate>): List<Pair<String, String>> {
    val gateByOutputWire = gates.associateBy { it.outputWire }
    val swaps = mutableSetOf<Set<Gate>>()

    fun findGateByOperationAndNumber(operation: Operation, inputNumber: Int): Gate? {
        val padded = inputNumber.toString().padStart(2, '0')
        val inputs = setOf("x$padded", "y$padded")
        return gates.singleOrNull { it.operation == operation && inputs == it.inputWireNames }
    }

    fun findByOperationAndInput(operation: Operation, wire: Wire) =
        gates.find { it.operation == operation && wire in it.inputWires }

    fun checkGate(gate: Gate, n: Int) {
        // Assumption is that gates z00 and z01 are correct...
        if (n < 2) return

        val children = gate.inputWires.mapNotNull { gateByOutputWire[it] }
        if (children.isEmpty()) return

        val expectedXorGate = findGateByOperationAndNumber(Operation.XOR, n) ?: return
        val orChild = children.first { it !== expectedXorGate }
        val xorChild = children.first { it !== orChild }
        val andInsideOr = findGateByOperationAndNumber(Operation.AND, n - 1) ?: return
        val expectedOr = findByOperationAndInput(Operation.OR, andInsideOr.outputWire)

        if (orChild != expectedOr && xorChild != expectedXorGate) return
        if (expectedOr != null && orChild !== expectedOr) {
            swaps += setOf(orChild, expectedOr)
        }
        if (xorChild != expectedXorGate) {
            swaps += setOf(xorChild, expectedXorGate)
        }
        val orChildChildren = orChild.takeIf { it.operation == Operation.OR }
            ?.inputWires
            ?.map { gateByOutputWire.getValue(it) }
            ?: emptyList()
        val expectedAndGate1 = findGateByOperationAndNumber(Operation.AND, n - 1)!!
        val xorInsideAnd = findGateByOperationAndNumber(Operation.XOR, n - 1) ?: return
        val expectedAndGate2 = findByOperationAndInput(Operation.AND, xorInsideAnd.outputWire)
        val actualAnd2 = orChildChildren.find { it !== expectedAndGate1 }
        val actualAnd1 = orChildChildren.find { it !== actualAnd2 }
        if (actualAnd1 != null && actualAnd1 !== expectedAndGate1) {
            swaps += setOf(actualAnd1, expectedAndGate1)
        }
        if (actualAnd2 != null && expectedAndGate2 != null && actualAnd2 !== expectedAndGate2) {
            swaps += setOf(actualAnd2, expectedAndGate2)
        }

        val expectedAndGate = findGateByOperationAndNumber(Operation.AND, n - 1) ?: return
        val actualAndGate = orChildChildren.singleOrNull { it.inputWireA.name.first() in "xy" }
        if (actualAndGate != null && actualAndGate != expectedAndGate) {
            swaps += setOf(actualAndGate, expectedAndGate)
        }

        val otherAndGate = orChildChildren.singleOrNull { it.inputWireA.name.first() !in "xy" }
        if (otherAndGate != null) {
            checkGate(otherAndGate, n - 1)
        }
    }

    val zGates = gates.filter { it.outputWire.name.startsWith("z") }
    for (zGate in zGates) {
        checkGate(zGate, zGate.outputWire.name.drop(1).toInt())
    }
    return swaps.map { it.first().outputWire.name to it.last().outputWire.name }
}

private fun Gate.prettyPrint(gates: List<Gate>) {
    val gateByOutputWire = gates.associateBy { it.outputWire }
    fun Gate.toPrettyString(depth: Int = 0): String {
        val indent = " ".repeat(depth)
        if (inputWireA.name.first() in "xy") {
            return if (depth == 0) "${outputWire.name}=$operation(${inputWireA.name},${inputWireB.name})" else "$indent$operation(${inputWireA.name},${inputWireB.name}) (${outputWire.name})"
        }
        val childA = gateByOutputWire.getValue(inputWireA)
        val childB = gateByOutputWire.getValue(inputWireB)
        val treeA = childA.toPrettyString(depth = depth + 1)
        val treeB = childB.toPrettyString(depth = depth + 1)
        val (first, second) = listOf(treeA, treeB).sortedBy { it.length }
        return if (depth == 0) "${outputWire.name}=$operation\n$first\n$second" else "$indent$operation (${outputWire.name})\n$first\n$second"
    }
    println(toPrettyString())
}
