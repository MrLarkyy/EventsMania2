package gg.aquatic.eventsmania.command.impl

import gg.aquatic.eventsmania.Messages
import gg.aquatic.eventsmania.events.Event
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.kommand.CommandBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
internal fun CommandBuilder<CommandSourceStack>.startCommand() =
    "start" {
        listArgument("event", { EventManager.events.values }, { it.id }) {
            execute<CommandSender> {
                val event = getOrNull<Event>("event")

                if (event == null) {
                    Messages.EVENT_NOT_FOUND.message().send(sender)
                    return@execute true
                }

                if (EventManager.runningEvent.load() != null) {
                    Messages.EVENT_ALREADY_RUNNING.message().send(sender)
                    return@execute true
                }

                val newValue = event.start()
                while (true) {
                    if (EventManager.runningEvent.compareAndSet(null, newValue)) {
                        Messages.EVENT_STARTING.message().send(sender)
                        break
                    }
                }
                true
            }
        }
    }