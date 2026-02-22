package gg.aquatic.eventsmania.command.impl

import gg.aquatic.common.toMMComponent
import gg.aquatic.eventsmania.data.EventData
import gg.aquatic.eventsmania.data.statistic.PAPIStatisticData
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.kommand.CommandBuilder
import gg.aquatic.waves.editor.EditorHandler
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

internal fun CommandBuilder<CommandSourceStack>.editCommand() =
    "edit" {
        listArgument("event", { EventManager.events.values }, { it.id }) {
            suspendExecute<Player> {
                val data = EventData(PAPIStatisticData("%vault_balance%", 20), 500)
                EditorHandler.startEditing(sender, "Test".toMMComponent(), data) {
                    val cfg = YamlConfiguration()
                    it.serialize(cfg)
                    sender.sendMessage("Saved!")
                    sender.sendMessage(cfg.saveToString())
                }
            }
        }
    }