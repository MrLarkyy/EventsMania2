package gg.aquatic.eventsmania.db

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.common.event
import gg.aquatic.common.ticker.Ticker
import gg.aquatic.eventsmania.EventsMania
import org.bukkit.event.player.PlayerJoinEvent

object DataHandler {

    private var currentTick = 0

    fun initialize() {
        event<PlayerJoinEvent> {
            VirtualsCtx {
                EventsMania.dataManager.loadStats(it.player.uniqueId)
            }
        }

        Ticker {
            currentTick++
            if (currentTick >= 20*600) {
                currentTick = 0
                EventsMania.dataManager.updateLeaderboard()
            }
        }
    }

}