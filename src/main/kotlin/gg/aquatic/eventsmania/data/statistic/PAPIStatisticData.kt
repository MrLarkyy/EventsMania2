package gg.aquatic.eventsmania.data.statistic

import gg.aquatic.common.toMMComponent
import gg.aquatic.eventsmania.events.EventHandle
import gg.aquatic.execute.argument.ObjectArguments
import gg.aquatic.stacked.toStackedBuilder
import gg.aquatic.waves.editor.ValueSerializer
import gg.aquatic.waves.editor.handlers.ChatInputHandler
import gg.aquatic.waves.editor.value.SimpleEditorValue
import gg.aquatic.waves.statistic.StatisticHandle
import gg.aquatic.waves.statistic.impl.PlaceholderStatistic
import org.bukkit.Material
import org.bukkit.entity.Player

class PAPIStatisticData(
    placeholder: String = "",
    updateFrequency: Int = 20
) : StatisticData() {

    override val type: SimpleEditorValue<String> = infoString("type","placeholder")

    val placeholder = edit(
        "placeholder", placeholder, ValueSerializer.Str(""), {
            Material.PAPER.toStackedBuilder {
                displayName = "Placeholder".toMMComponent()
                lore += listOf("", "Current value: $it").map { line -> line.toMMComponent() }
            }.getItem()
        },
        ChatInputHandler.forString("Type the PlaceholderAPI placeholder into the chat:")
    )

    val updateFrequency = edit(
        "update", updateFrequency, ValueSerializer.IntSerializer(20), {
            Material.CLOCK.toStackedBuilder {
                displayName = "Update Frequency".toMMComponent()
                lore += listOf("", "Current value: $it").map { line -> line.toMMComponent() }
            }.getItem()
        },
        ChatInputHandler.forInteger("Type the update frequency in ticks (default is 20):")
    )

    override fun copy(): PAPIStatisticData {
        return PAPIStatisticData(placeholder.value, updateFrequency.value)
    }

    override fun createSupplier(): (EventHandle) -> StatisticHandle<Player> {
        return { handle ->
            StatisticHandle(
                PlaceholderStatistic,
                ObjectArguments(
                    mapOf(
                        "placeholder" to placeholder.value,
                        "update" to updateFrequency.value
                    )
                )
            ) { e ->
                val player = e.binder
                handle.increase(player, e)
            }
        }
    }
}