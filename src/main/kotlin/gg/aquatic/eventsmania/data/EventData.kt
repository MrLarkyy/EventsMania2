package gg.aquatic.eventsmania.data

import gg.aquatic.common.toMMComponent
import gg.aquatic.eventsmania.data.statistic.PAPIStatisticData
import gg.aquatic.eventsmania.data.statistic.StatisticData
import gg.aquatic.eventsmania.events.Event
import gg.aquatic.stacked.toStackedBuilder
import gg.aquatic.waves.editor.Configurable
import gg.aquatic.waves.editor.ValueSerializer
import gg.aquatic.waves.editor.handlers.ChatInputHandler
import org.bukkit.Material

class EventData(
    statistic: StatisticData,
    duration: Int
) : Configurable<EventData>() {

    val statistic = editPolymorphicConfigurable(
        "statistic",
        statistic,
        mapOf("placeholder" to { PAPIStatisticData("%vault_balance%" )}),
        { data ->
            Material.SPRUCE_SAPLING.toStackedBuilder {
                displayName = "Statistic Type".toMMComponent()
                lore += listOf("", "Current value: ${data.type.value}").map { line -> line.toMMComponent() }
            }.getItem()
        })

    val duration = edit(
        "update", duration, ValueSerializer.IntSerializer(20), {
            Material.CLOCK.toStackedBuilder {
                displayName = "Duration".toMMComponent()
                lore += listOf("", "Current value: $it").map { line -> line.toMMComponent() }
            }.getItem()
        },
        ChatInputHandler.forInteger("Type the duration in ticks (default is 20):")
    )

    fun toEvent(id: String): Event {
        return Event(
            id,
            statistic.value.createSupplier(),
            duration.value,
            Event.PrepareSettings(0, mapOf()),
            Event.GameActions(mapOf(), listOf()),
            mapOf()
        )
    }

    override fun copy(): EventData {
        return EventData(statistic.clone().value, duration.value)
    }
}