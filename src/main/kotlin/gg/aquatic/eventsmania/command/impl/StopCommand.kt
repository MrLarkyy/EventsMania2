package gg.aquatic.eventsmania.command.impl

import gg.aquatic.eventsmania.Messages
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.kommand.CommandBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
internal fun CommandBuilder<CommandSourceStack>.stopCommand() =
    "stop" {
        suspendExecute<CommandSender> {
            val running = EventManager.runningEvent.load()
            if (running == null) {
                Messages.EVENT_NOT_RUNNING.message().send(sender)
                return@suspendExecute
            }
            running.stop()
            Messages.EVENT_STOPPED.message().send(sender)
        }
    }