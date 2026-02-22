package gg.aquatic.eventsmania.command.impl

import gg.aquatic.eventsmania.EventsMania
import gg.aquatic.eventsmania.Messages
import gg.aquatic.kommand.CommandBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender

internal fun CommandBuilder<CommandSourceStack>.reloadCommand() =
    "reload" {
        suspendExecute<CommandSender> {
            Messages.PLUGIN_RELOADING.message().send(sender)
            EventsMania.reloadPlugin()
            Messages.PLUGIN_RELOADED.message().send(sender)
        }
    }