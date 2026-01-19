package gg.aquatic.eventsmania

object TicksUtil {

    fun parseTicks(input: String, maxTime: Int): Set<Int> {
        val ticks = mutableSetOf<Int>()
        val parts = input.split(";")

        for (part in parts) {
            if (part.startsWith("every-")) {
                val segments = part.split("-")
                // every-2-!5->20
                var interval = 1
                var limit = Int.MAX_VALUE
                var startAt = 0

                for (segment in segments) {
                    when {
                        segment.all { it.isDigit() } -> interval = segment.toInt()
                        segment.startsWith("!") -> limit = segment.substring(1).toInt()
                        segment.startsWith(">") -> startAt = segment.substring(1).toInt()
                    }
                }

                var count = 0
                var current = startAt
                while (current <= maxTime && count < limit) {
                    ticks.add(current)
                    current += interval
                    count++
                }
            } else {
                part.toIntOrNull()?.let {
                    if (it <= maxTime) ticks.add(it)
                }
            }
        }
        return ticks
    }

    fun validateTicks(input: String): Boolean {
        if (input.isEmpty()) return false
        val parts = input.split(";")
        val everyRegex = Regex("^every-\\d+(-![1-9]\\d*)?(->(0|[1-9]\\d*))?$")

        for (part in parts) {
            val trimmed = part.trim()
            if (trimmed.isEmpty()) return false

            if (trimmed.startsWith("every-")) {
                if (!everyRegex.matches(trimmed)) return false
            } else {
                if (trimmed.toIntOrNull() == null) return false
            }
        }
        return true
    }

}