package year2015.day07

import readInput
import java.util.function.Supplier

fun main() {
    val input = readInput("2015", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = parseSupplierByWire(input).getValue("a").get()

private fun part2(input: List<String>) = parseSupplierByWire(input).let {
    it["b"] = Supplier { parseSupplierByWire(input).getValue("a").get() }
    it.getValue("a").get()
}

private fun parseSupplierByWire(input: List<String>): HashMap<String, Supplier<Int>> {
    val map = hashMapOf<String, Supplier<Int>>()
    val cache = hashMapOf<String, Int>()
    val pattern = "(NOT)? ?(\\d+|[a-z]+) ?(AND|OR|LSHIFT|RSHIFT)? ?(\\d+|[a-z]+)? -> (\\w+)".toRegex()
    input.forEach { instruction ->
        val groups = pattern.matchEntire(instruction)?.groupValues?.drop(2)
            ?: error("Instruction not matching pattern: $instruction")
        val (p1, _, p2, to) = groups
        when {
            "NOT" in instruction -> map[to] = Supplier {
                cache.getOrPut(to) { (p1.toIntOrNull() ?: map.getValue(p1).get()).inv() }
            }

            "AND" in instruction -> map[to] = Supplier {
                cache.getOrPut(to) {
                    (p1.toIntOrNull() ?: map.getValue(p1).get()) and (p2.toIntOrNull() ?: map.getValue(p2).get())
                }
            }

            "OR" in instruction -> map[to] = Supplier {
                cache.getOrPut(to) {
                    (p1.toIntOrNull() ?: map.getValue(p1).get()) or (p2.toIntOrNull() ?: map.getValue(p2).get())
                }
            }

            "LSHIFT" in instruction -> map[to] = Supplier {
                cache.getOrPut(to) {
                    (p1.toIntOrNull() ?: map.getValue(p1).get()) shl (p2.toIntOrNull() ?: (p2.toIntOrNull()
                        ?: map.getValue(p2).get()))
                }
            }

            "RSHIFT" in instruction -> map[to] = Supplier {
                cache.getOrPut(to) {
                    (p1.toIntOrNull() ?: map.getValue(p1).get()) shr (p2.toIntOrNull() ?: (p2.toIntOrNull()
                        ?: map.getValue(p2).get()))
                }
            }

            else -> map[to] = Supplier { cache.getOrPut(to) { p1.toIntOrNull() ?: map.getValue(p1).get() } }
        }
    }
    return map
}