package year2023.day20

import check
import lcm
import readInput

/** enable debug output to display in which order pulses are sent */
private var debug = false

fun main() {
    val testInput1a = readInput("2023", "Day20_test_part1a")
    val testInput1b = readInput("2023", "Day20_test_part1b")
    check(part1(testInput1a), 32000000)
    check(part1(testInput1b), 11687500)

    val input = readInput("2023", "Day20")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val pulseRecorder = PulseRecorder()
    val modulesByName = input.parseModulesByName(pulseRecorder)
    val broadcasterModule = modulesByName.getValue("broadcaster")
    val buttonPressed = Pulse("button", broadcasterModule.name, false)
    repeat(1000) {
        broadcasterModule.sendPulse(buttonPressed)
        broadcasterModule.process()
    }
    val highPulses = pulseRecorder.recordedPulses.count { it.isHigh }
    val lowPulses = pulseRecorder.recordedPulses.size - highPulses
    return highPulses * lowPulses
}

private fun part2(input: List<String>): Long {
    val pulseRecorder = PulseRecorder()
    val modulesByName = input.parseModulesByName(pulseRecorder)
    val broadcasterModule = modulesByName.getValue("broadcaster")
    val buttonPressed = Pulse("button", broadcasterModule.name, false)
    var buttonPressedCounter = 0L
    val moduleBeforeRx = modulesByName.values.single { "rx" in it.destinationNames }
    require(moduleBeforeRx is ConjunctionModule)
    val connectedModuleNames =
        modulesByName.values.filter { moduleBeforeRx.name in it.destinationNames }.map { it.name }
    val cycleByConnectedModule = mutableMapOf<String, Long>()
    while (true) {
        pulseRecorder.recordedPulses.clear()
        broadcasterModule.sendPulse(buttonPressed)
        broadcasterModule.process()
        buttonPressedCounter++
        pulseRecorder.recordedPulses
            .filter { it.receiverName in moduleBeforeRx.name && it.isHigh }
            .forEach {
                if (cycleByConnectedModule[it.senderName] == null) {
                    cycleByConnectedModule[it.senderName] = buttonPressedCounter
                }
            }
        if (cycleByConnectedModule.size == connectedModuleNames.size) {
            return cycleByConnectedModule.values.lcm()
        }
    }
}

private data class Pulse(
    val senderName: String,
    val receiverName: String,
    val isHigh: Boolean,
) {
    fun log() {
        if (debug) println("$senderName -${if (isHigh) "high" else "low"}-> $receiverName")
    }
}

private sealed class Module(
    val name: String,
    val destinationNames: List<String>,
    val pulseRecorder: PulseRecorder,
) {
    val inbox = mutableListOf<Pulse>()
    val destinations = mutableListOf<Module>()

    open fun initialize(modulesByName: Map<String, Module>) {
        destinations += destinationNames.map { modulesByName.getOrDefault(it, NoopModule(it, pulseRecorder)) }
    }

    fun sendPulse(pulse: Pulse) {
        inbox += pulse
        pulse.log()
    }

    fun process() {
        if (inbox.isEmpty()) return
        for (pulse in inbox) {
            pulseRecorder.recordedPulses += pulse
            processPulse(pulse)
        }
        inbox.clear()
        destinations.forEach { it.process() }
    }

    abstract fun processPulse(pulse: Pulse)
}

private class FlipFlopModule(
    name: String,
    destinationNames: List<String>,
    pulseRecorder: PulseRecorder,
    var active: Boolean = false,
) : Module(name, destinationNames, pulseRecorder) {
    override fun processPulse(pulse: Pulse) {
        if (!pulse.isHigh) {
            active = !active
            destinations.forEach { it.sendPulse(Pulse(name, it.name, active)) }
        }
    }
}

private class NoopModule(
    name: String,
    pulseRecorder: PulseRecorder,
) : Module(name, emptyList(), pulseRecorder) {
    override fun initialize(modulesByName: Map<String, Module>) {}
    override fun processPulse(pulse: Pulse) {}
}

private class ConjunctionModule(
    name: String,
    destinationNames: List<String>,
    pulseRecorder: PulseRecorder,
) : Module(name, destinationNames, pulseRecorder) {
    private val memory = mutableMapOf<String, Boolean>()
    override fun initialize(modulesByName: Map<String, Module>) {
        super.initialize(modulesByName)
        memory += modulesByName.values.filter { name in it.destinationNames }.map { it.name }.associateWith { false }
    }

    override fun processPulse(pulse: Pulse) {
        memory[pulse.senderName] = pulse.isHigh
        val sendHigh = !memory.values.all { it }
        destinations.forEach { it.sendPulse(Pulse(name, it.name, sendHigh)) }
    }
}

private class BroadcasterModule(
    name: String,
    destinationNames: List<String>,
    pulseRecorder: PulseRecorder,
) : Module(name, destinationNames, pulseRecorder) {
    override fun processPulse(pulse: Pulse) {
        destinations.forEach { it.sendPulse(pulse.copy(senderName = name, receiverName = it.name)) }
    }
}

private fun List<String>.parseModulesByName(pulseRecorder: PulseRecorder): Map<String, Module> {
    val modulesByName = map { it.parseModule(pulseRecorder) }.associateBy { it.name }
    modulesByName.values.forEach { it.initialize(modulesByName) }
    return modulesByName
}

private fun String.parseModule(pulseRecorder: PulseRecorder): Module {
    val (moduleText, destinationsText) = split(" -> ")
    val destinationNames = destinationsText.split(", ")
    return when {
        moduleText == "broadcaster" -> BroadcasterModule("broadcaster", destinationNames, pulseRecorder)
        moduleText.startsWith("%") -> FlipFlopModule(moduleText.drop(1), destinationNames, pulseRecorder)
        moduleText.startsWith("&") -> ConjunctionModule(moduleText.drop(1), destinationNames, pulseRecorder)
        else -> error("unknown module $moduleText -> $destinationsText")
    }
}

private class PulseRecorder {
    val recordedPulses = mutableListOf<Pulse>()
}
