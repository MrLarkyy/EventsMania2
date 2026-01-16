package gg.aquatic.eventsmania.hook

import gg.aquatic.eventsmania.EventsMania.dataManager
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.treepapi.papiPlaceholder

object PAPIHook {

    fun initialize() = papiPlaceholder("Larkyy","eventsmania") {
        "leaderboard" {
            "rank" {
                handle {
                    dataManager.getPlayerRankOrNull(binder.uniqueId)?.toString() ?: ""
                }
            }
            "wins" {
                intArgument("place") {
                    handle {
                        val place = getOrNull<Int>("place") ?: return@handle ""
                        dataManager.getLeaderboard().getOrNull(place - 1)?.score?.toString() ?: ""
                    }
                }

                handle {
                    dataManager.getPlayerWinsOrNull(binder.uniqueId)?.toString() ?: ""
                }
            }
            "username" {
                intArgument("place") {
                    handle {
                        val place = getOrNull<Int>("place") ?: return@handle ""
                        dataManager.getLeaderboard().getOrNull(place - 1)?.username ?: ""
                    }
                }
            }
        }
        "isrunning" {
            handle {
                return@handle (EventManager.runningEvent != null).toString()
            }
        }
    }
}