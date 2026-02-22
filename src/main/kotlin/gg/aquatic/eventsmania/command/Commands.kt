package gg.aquatic.eventsmania.command

import gg.aquatic.eventsmania.Messages
import gg.aquatic.eventsmania.command.impl.editCommand
import gg.aquatic.eventsmania.command.impl.reloadCommand
import gg.aquatic.eventsmania.command.impl.startCommand
import gg.aquatic.eventsmania.command.impl.stopCommand
import gg.aquatic.kommand.command
import org.bukkit.entity.Player
import kotlin.concurrent.atomics.ExperimentalAtomicApi

object Commands {

    @OptIn(ExperimentalAtomicApi::class)
    fun initialize() {
        command("eventsmania", "em") {
            requires {
                it.sender.hasPermission("eventsmania.admin")
            }

            startCommand()
            stopCommand()
            reloadCommand()
            editCommand()

            execute<Player> {
                Messages.HELP.message().send(sender)
                true
            }
        }
    }
}