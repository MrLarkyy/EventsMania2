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
                            sender.sendMessage("Event not found!")
                            return@execute true
                        }
                        event.start()
                        sender.sendMessage("Event started!")
                        true
                    }
                }

            }
            "stop" {
                suspendExecute<CommandSender> {
                    val running = EventManager.runningEvent
                    if (running == null) {
                        sender.sendMessage("No event is running!")
                        return@suspendExecute
                    }
                    running.stop()
                    sender.sendMessage("Event stopped!")
                }
            }
            "reload" {
                suspendExecute<CommandSender> {
                    sender.sendMessage("Reloading events...")
                    EventsMania.reloadPlugin()
                    sender.sendMessage("Events reloaded!")
                }
            }

            execute<Player> {
                sender.sendMessage("EventsMania help!")
                true
            }
        }
    }

}