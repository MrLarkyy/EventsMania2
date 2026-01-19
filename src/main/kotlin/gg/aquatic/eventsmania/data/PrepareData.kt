package gg.aquatic.eventsmania.data

import gg.aquatic.common.toMMComponent
import gg.aquatic.eventsmania.data.action.ActionData
import gg.aquatic.eventsmania.events.Event
import gg.aquatic.execute.ActionHandle
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.editor.Configurable
import gg.aquatic.waves.input.impl.ChatInput
import gg.aquatic.waves.input.impl.chatInputValidation
import org.bukkit.Material
import org.bukkit.entity.Player

class PrepareData(
    initialPrepareTime: Int = 0,
    initialActions: Map<String, List<ActionData>> = mapOf()
) : Configurable<PrepareData>() {

    val prepareTime = editInt("prepare-time", initialPrepareTime, "Type the prepare time in ticks:")

    val actions = editString2PolymorphicListConfigurableMap(
        "actions", initialActions, ActionData.ALL_TYPES,
        { player, keySupplier ->
            player.sendMessage(
                "Examples:\n" +
                        "- 5 - Just at 5th tick\n" +
                        "- every-2 - Every 2 ticks\n" +
                        "- every-2-!5 - Every 2 ticks, for 5 times limit\n" +
                        "- every-2->20 - Every 2 ticks, since 20th tick\n" +
                        "- every-2-!5->20 - Every 2 ticks, for 5 times limit, since 20th tick\n" +
                        "- 1;4;7 - List of ticks\n" +
                        "- every-2;5 - Every 2 ticks and at 5th tick\n\n" +
                        "Type the prepare time in ticks:"
            )
            player.closeInventory()
            ChatInput.createHandle(validator = chatInputValidation {
                validate { str -> str.toIntOrNull() != null }
                onFail { player, string ->
                    player.sendMessage(
                        "Please enter a valid tick time!\n\n" +
                                "Examples:\n" +
                                "- 5 - Just at 5th tick\n" +
                                "- every-2 - Every 2 ticks\n" +
                                "- every-2-!5 - Every 2 ticks, for 5 times limit\n" +
                                "- every-2->20 - Every 2 ticks, since 20th tick" +
                                "- every-2-!5->20 - Every 2 ticks, for 5 times limit, since 20th tick\n" +
                                "- 1;4;7 - List of ticks\n" +
                                "- every-2;5 - Every 2 ticks and at 5th tick"
                    )
                }
            }).await(player).thenAccept { value ->
                keySupplier(value)
            }
        },
        { value ->
            stackedItem(Material.DIAMOND) {
                displayName = "Actions".toMMComponent()
            }.getItem()
        },
        { entry, value ->
            stackedItem(Material.COMMAND_BLOCK) {
                displayName = "#$entry Tick Actions".toMMComponent()
            }.getItem()
        },
        { action ->
            stackedItem(Material.PAPER) {
                displayName = "Action: ${action.type.value}".toMMComponent()
            }.getItem()
        }
    )

    @Suppress("UNCHECKED_CAST")
    fun toPrepareSettings(): Event.PrepareSettings {
        val actionsFinalMap = HashMap<Int, MutableList<ActionHandle<*>>>()

        for (entry in actions.value) {
            val key = entry.key
            val actions = entry.value.map { it.value.create() }

            parseTicks(key, prepareTime.value).forEach { tick ->
                actionsFinalMap.getOrPut(tick) { mutableListOf() }.addAll(actions)
            }
        }
        return Event.PrepareSettings(
            prepareTime = prepareTime.value,
            actions = actionsFinalMap.mapValues { it.value.toList() as List<ActionHandle<Player>> }
        )
    }

    private fun parseTicks(input: String, maxTime: Int): Set<Int> {
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

    override fun copy(): PrepareData {
        TODO("Not yet implemented")
    }
}