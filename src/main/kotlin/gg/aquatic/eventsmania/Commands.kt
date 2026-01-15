package gg.aquatic.eventsmania

import gg.aquatic.eventsmania.events.Event
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.kommand.command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Commands {

    fun initialize() {
        command("eventsmania", "em") {
            requires {
                it.sender.hasPermission("eventsmania.admin")
            }

            "start" {
                listArgument("event", { EventManager.events.values }, { it.id }) {
                    execute<CommandSender> {
                        val event = getOrNull<Event>("event")

                        if (event == null) {
                            Messages.EVENT_NOT_FOUND.message().send(sender)
                            return@execute true
                        }

                        if (EventManager.runningEvent != null) {
                            Messages.EVENT_ALREADY_RUNNING.message().send(sender)
                            return@execute true
                        }

                        EventManager.runningEvent = event.start()
                        Messages.EVENT_STARTING.message().send(sender)
                        true
                    }
                }

            }
            "stop" {
                suspendExecute<CommandSender> {
                    val running = EventManager.runningEvent
                    if (running == null) {
                        Messages.EVENT_NOT_RUNNING.message().send(sender)
                        return@suspendExecute
                    }
                    running.stop()
                    Messages.EVENT_STOPPED.message().send(sender)
                }
            }
            "reload" {
                suspendExecute<CommandSender> {
                    Messages.PLUGIN_RELOADING.message().send(sender)
                    EventsMania.reloadPlugin()
                    Messages.PLUGIN_RELOADED.message().send(sender)
                }
            }

            execute<Player> {
                sender.sendMessage("EventsMania help!")
                true
            }
        }
    }

}