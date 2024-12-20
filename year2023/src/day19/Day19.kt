package day19

import check
import readInput
import splitByEmptyLines

fun main() {
    val testInput = readInput("2023", "Day19_test")
    check(part1(testInput), 19114)
    check(part2(testInput), 167409079868000)

    val input = readInput("2023", "Day19")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (workflows, parts) = input.parseWorkflowsAndParts()
    val workflowsByName = workflows.associateBy { it.name }
    val acceptedParts = mutableListOf<Part>()
    for (part in parts) {
        var workflow = workflowsByName.getValue("in")
        var result = workflow.process(part)
        while (result !is Accept && result !is Reject) {
            if (result is SendToWorkflow) {
                workflow = workflowsByName.getValue(result.name)
                result = workflow.process(part)
            } else error("Unexpected result $result")
        }
        if (result is Accept) {
            acceptedParts += part
        }
    }

    return acceptedParts.sumOf { it.total() }
}

private fun part2(input: List<String>): Long {
    val workflowsByName = input.parseWorkflowsAndParts().first.associateBy { it.name }
    val partRanges = findPartRanges(PartRange(), workflowsByName.getValue("in"), workflowsByName)
    return partRanges.toList().combinations()
}

private data class Part(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
) {
    fun total() = x + m + a + s
}

private data class Workflow(
    val name: String,
    val operations: List<Operation>,
)

private sealed interface Operation {
    fun check(part: Part): Result
}

private sealed interface Result
private data object Continue : Result
private data object Reject : Result
private data object Accept : Result
private class SendToWorkflow(val name: String) : Result

private data object RejectOperation : Operation {
    override fun check(part: Part) = Reject
}

private data object AcceptOperation : Operation {
    override fun check(part: Part) = Accept
}

private class SendToWorkflowOperation(val name: String) : Operation {
    override fun check(part: Part) = SendToWorkflow(name)
}

private enum class Attribute {
    X, M, A, S
}

private sealed interface ComparisonOperation : Operation {
    val limit: Int
    val attribute: Attribute
    val getAttribute: Part.() -> Int
    val result: Result
}

private data class LessThanOperation(
    override val limit: Int,
    override val attribute: Attribute,
    override val getAttribute: Part.() -> Int,
    override val result: Result,
) : ComparisonOperation {
    override fun check(part: Part) = if (part.getAttribute() < limit) result else Continue
}

private data class GreaterThanOperation(
    override val limit: Int,
    override val attribute: Attribute,
    override val getAttribute: Part.() -> Int,
    override val result: Result,
) : ComparisonOperation {
    override fun check(part: Part) = if (part.getAttribute() > limit) result else Continue
}

private fun List<String>.parseWorkflowsAndParts(): Pair<List<Workflow>, List<Part>> {
    val (workflowLines, partLines) = splitByEmptyLines()
    val workflows: List<Workflow> = workflowLines.map { line ->
        val name = line.substringBefore("{")
        val operationTexts = line.substringAfter("{").removeSuffix("}").split(',')
        val operations = operationTexts.map { operationText ->
            if (operationText == "R") RejectOperation
            else if (operationText == "A") AcceptOperation
            else if ("<" in operationText || ">" in operationText) operationText.toComparisonOperation()
            else SendToWorkflowOperation(operationText)
        }
        Workflow(name, operations)
    }
    val parts = partLines.map { line ->
        val (x, m, a, s) = line.removePrefix("{").removeSuffix("}").split(',').map { it.split("=").last().toInt() }
        Part(x = x, m = m, a = a, s = s)
    }
    return workflows to parts
}

private fun String.toComparisonOperation(): Operation {
    val (attributeName, limitText, resultText) = split('<', '>', ':')
    val result = when (resultText) {
        "A" -> Accept
        "R" -> Reject
        else -> SendToWorkflow(resultText)
    }
    val attribute = Attribute.valueOf(attributeName.uppercase())
    val attributeGetter: Part.() -> Int = when (attribute) {
        Attribute.X -> Part::x
        Attribute.M -> Part::m
        Attribute.A -> Part::a
        Attribute.S -> Part::s
    }
    val limit = limitText.toInt()
    return if ("<" in this) LessThanOperation(limit, attribute, attributeGetter, result)
    else GreaterThanOperation(limit, attribute, attributeGetter, result)
}

private fun Workflow.process(part: Part): Result {
    for (operation in operations) {
        val result = operation.check(part)
        if (result is Continue) continue
        else return result
    }
    error("Workflow $this had no outcome for $part")
}

private data class PartRange(
    val xMin: Int = 1,
    val xMax: Int = 4000,
    val mMin: Int = 1,
    val mMax: Int = 4000,
    val aMin: Int = 1,
    val aMax: Int = 4000,
    val sMin: Int = 1,
    val sMax: Int = 4000,
) {
    fun copyWithMin(attribute: Attribute, min: Int) = when (attribute) {
        Attribute.X -> copy(xMin = min)
        Attribute.M -> copy(mMin = min)
        Attribute.A -> copy(aMin = min)
        Attribute.S -> copy(sMin = min)
    }

    fun copyWithMax(attribute: Attribute, max: Int) = when (attribute) {
        Attribute.X -> copy(xMax = max)
        Attribute.M -> copy(mMax = max)
        Attribute.A -> copy(aMax = max)
        Attribute.S -> copy(sMax = max)
    }

    fun isEmpty() = combinations() == 0L
}

private fun findPartRanges(
    partRange: PartRange,
    workflow: Workflow,
    workflowsByName: Map<String, Workflow>,
): List<PartRange> {
    return findPartRangesInOperations(partRange, workflow, 0, workflowsByName)
}

private fun findPartRangesInOperations(
    partRange: PartRange,
    workflow: Workflow,
    operationIndex: Int,
    workflowsByName: Map<String, Workflow>,
): List<PartRange> {
    if (partRange.isEmpty() || operationIndex > workflow.operations.lastIndex) {
        return emptyList()
    }
    return when (val operation = workflow.operations[operationIndex]) {
        AcceptOperation -> listOf(partRange)
        RejectOperation -> emptyList()
        is ComparisonOperation -> {
            val (matchingPartRange, nonMatchingPartRange) = operation.splitPartRanges(partRange)
            val partRangesFromContinuing =
                findPartRangesInOperations(nonMatchingPartRange, workflow, operationIndex + 1, workflowsByName)
            when (val result = operation.result) {
                Accept -> listOf(matchingPartRange) + partRangesFromContinuing
                Reject -> partRangesFromContinuing
                is SendToWorkflow -> findPartRanges(
                    matchingPartRange,
                    workflowsByName.getValue(result.name),
                    workflowsByName
                ) + partRangesFromContinuing

                Continue -> error("Unexpected Continue")
            }
        }

        is SendToWorkflowOperation -> findPartRanges(
            partRange,
            workflowsByName.getValue(operation.name),
            workflowsByName
        )
    }
}

private fun ComparisonOperation.splitPartRanges(partRange: PartRange) = when (this) {
    is GreaterThanOperation -> splitPartRanges(partRange)
    is LessThanOperation -> splitPartRanges(partRange)
}

private fun GreaterThanOperation.splitPartRanges(partRange: PartRange): Pair<PartRange, PartRange> {
    return partRange.copyWithMin(attribute, limit + 1) to partRange.copyWithMax(attribute, limit)
}

private fun LessThanOperation.splitPartRanges(partRange: PartRange): Pair<PartRange, PartRange> {
    return partRange.copyWithMax(attribute, limit - 1) to partRange.copyWithMin(attribute, limit)
}

private fun PartRange.combinations() =
    1L * (xMax - xMin + 1) * (mMax - mMin + 1) * (aMax - aMin + 1) * (sMax - sMin + 1)

private fun List<PartRange>.combinations() = sumOf { it.combinations() }
