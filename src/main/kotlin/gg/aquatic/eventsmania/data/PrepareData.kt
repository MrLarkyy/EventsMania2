package gg.aquatic.eventsmania.data

import gg.aquatic.common.toMMComponent
import gg.aquatic.eventsmania.TicksUtil
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
            val value = ChatInput.createHandle(validator = chatInputValidation {
                validate { str -> TicksUtil.validateTicks(str) }
                onFail { player, _ ->
                    player.sendMessage(
                        "Please enter a valid tick time!\n\n" +
                                "Examples:\n" +
                                "- 5 - Just at 5th tick\n" +
                                "- every-2 - Every 2 ticks\n" +
                                "- every-2-!5 - Every 2 ticks, for 5 times limit\n" +
                                "- every-2->20 - Every 2 ticks, since 20th tick\n" +
                                "- every-2-!5->20 - Every 2 ticks, for 5 times limit, since 20th tick\n" +
                                "- 1;4;7 - List of ticks\n" +
                                "- every-2;5 - Every 2 ticks and at 5th tick"
                    )
                }
            }).await(player)

            keySupplier(value)
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

            TicksUtil.parseTicks(key, prepareTime.value).forEach { tick ->
                actionsFinalMap.getOrPut(tick) { mutableListOf() }.addAll(actions)
            }
        }
        return Event.PrepareSettings(
            prepareTime = prepareTime.value,
            actions = actionsFinalMap.mapValues { it.value.toList() as List<ActionHandle<Player>> }
        )
    }

    override fun copy(): PrepareData {
        TODO("Not yet implemented")
    }
}