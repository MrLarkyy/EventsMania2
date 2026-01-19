package gg.aquatic.eventsmania.data

import gg.aquatic.common.toMMComponent
import gg.aquatic.eventsmania.data.action.ActionData
import gg.aquatic.eventsmania.data.action.MessageActionData
import gg.aquatic.eventsmania.events.Event
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.editor.Configurable
import gg.aquatic.waves.input.impl.ChatInput
import gg.aquatic.waves.input.impl.chatInputValidation
import org.bukkit.Material

class PrepareData(
    initialPrepareTime: Int = 0,
    initialActions: Map<Int, List<ActionData>> = mapOf()
): Configurable<PrepareData>() {

    val prepareTime = editInt("prepare-time", initialPrepareTime,"Type the prepare time in ticks:")

    val actions = editInt2PolymorphicListConfigurableMap(
        "actions", initialActions, mapOf("message" to { MessageActionData() }),
        { player, keySupplier ->
            player.closeInventory()
            ChatInput.createHandle(validator = chatInputValidation {
                validate { str -> str.toIntOrNull() != null }
                onFail { player, string ->
                    player.sendMessage("Please enter a valid integer.")
                }
            }).await(player).thenAccept { value ->
                keySupplier(value?.toIntOrNull())
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

    fun toPrepareSettings(): Event.PrepareSettings {
        val actionsMap = actions.value.associate { it.key.toInt() to it.value.map { it.value.create() } }

        return Event.PrepareSettings(
            prepareTime = prepareTime.value,
            actions = actionsMap
        )
    }

    override fun copy(): PrepareData {
        TODO("Not yet implemented")
    }
}