package gg.aquatic.eventsmania.data

import gg.aquatic.common.toMMComponent
import gg.aquatic.eventsmania.data.action.ActionData
import gg.aquatic.eventsmania.data.action.MessageActionData
import gg.aquatic.eventsmania.data.statistic.PAPIStatisticData
import gg.aquatic.eventsmania.data.statistic.StatisticData
import gg.aquatic.eventsmania.events.Event
import gg.aquatic.stacked.stackedItem
import gg.aquatic.stacked.toStackedBuilder
import gg.aquatic.waves.editor.Configurable
import gg.aquatic.waves.editor.ValueSerializer
import gg.aquatic.waves.editor.handlers.ChatInputHandler
import gg.aquatic.waves.input.impl.ChatInput
import gg.aquatic.waves.input.impl.chatInputValidation
import net.kyori.adventure.text.Component
import org.bukkit.Material

class EventData(
    statistic: StatisticData,
    duration: Int,
    rewards: Map<Int, List<ActionData>> = mapOf(),
    initialActions: Map<Int, List<ActionData>> = mapOf(),
    initialEndActions: List<ActionData> = listOf()
) : Configurable<EventData>() {

    val statistic = editPolymorphicConfigurable(
        "statistic",
        statistic,
        mapOf("placeholder" to { PAPIStatisticData("%vault_balance%") }),
        { data ->
            Material.SPRUCE_SAPLING.toStackedBuilder {
                displayName = "Statistic Type".toMMComponent()
                lore += listOf("", "Current value: ${data.type.value}").map { line -> line.toMMComponent() }
            }.getItem()
        })

    val duration = edit(
        "duration", duration, ValueSerializer.IntSerializer(20), {
            Material.CLOCK.toStackedBuilder {
                displayName = "Duration".toMMComponent()
                lore += listOf("", "Current value: $it").map { line -> line.toMMComponent() }
            }.getItem()
        },
        ChatInputHandler.forInteger("Type the duration in ticks (default is 20):")
    )

    val rewards = editInt2PolymorphicListConfigurableMap(
        "rewards", rewards, ActionData.ALL_TYPES,
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
                displayName = "Rewards".toMMComponent()
            }.getItem()
        },
        { entry, value ->
            stackedItem(Material.COMMAND_BLOCK) {
                displayName = "Actions - #$entry Place".toMMComponent()
            }.getItem()
        },
        { action ->
            stackedItem(Material.PAPER) {
                displayName = "Action: ${action.type.value}".toMMComponent()
            }.getItem()
        }
    )

    val prepareData = editConfigurable("delayed", PrepareData(), {
        stackedItem(Material.COMPARATOR) {
            displayName = Component.text("Game Preparation")
            lore.addAll(
                listOf(
                    "",
                    "Using game preparation you can setup",
                    "actions that are executed before",
                    "the actual game starts"
                ).map { it.toMMComponent() })
        }.getItem()
    })

    val gameActions = editInt2PolymorphicListConfigurableMap(
        "actions", initialActions, ActionData.ALL_TYPES,
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
            stackedItem(Material.CHAIN_COMMAND_BLOCK) {
                displayName = "Game Actions".toMMComponent()
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

    val gameEndActions =
        editPolymorphicConfigurableList(
            "end-actions", initialEndActions, ActionData.ALL_TYPES,
            { value ->
                stackedItem(Material.CHAIN_COMMAND_BLOCK) {
                    displayName = "Game End Actions".toMMComponent()
                }.getItem()
            },
            { action ->
                stackedItem(Material.PAPER) {
                    displayName = "Action: ${action.type.value}".toMMComponent()
                }.getItem()
            }
        )

    fun toEvent(id: String): Event {
        val prepare = Event.PrepareSettings(
            prepareData.value.prepareTime.value,
            prepareData.value.actions.value.associate { it.key.toInt() to it.value.map { it.value.create() } })
        val actions = gameActions.value.associate { it.key.toInt() to it.value.map { it.value.create() } }
        val endActions = gameEndActions.value.map { it.value.create() }
        return Event(
            id,
            statistic.value.createSupplier(),
            duration.value,
            prepare,
            Event.GameActions(actions, endActions),
            rewards.value.associate { it.key.toInt() to it.value.map { it.value.create() } }
        )
    }

    override fun copy(): EventData {
        return EventData(
            statistic.clone().value,
            duration.value,
            rewards.value.associate { it.key.toInt() to it.value.map { it.clone().value } },
            gameActions.value.associate { it.key.toInt() to it.value.map { it.clone().value } },
            gameEndActions.value.map { it.clone().value }
        )
    }
}