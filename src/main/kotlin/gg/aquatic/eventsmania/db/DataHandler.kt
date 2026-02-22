package gg.aquatic.eventsmania.db

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.common.event
import gg.aquatic.common.ticker.GlobalTicker
import gg.aquatic.eventsmania.EventsMania
import org.bukkit.event.player.PlayerJoinEvent

object DataHandler {

    fun initialize() {
        event<PlayerJoinEvent> {
            VirtualsCtx {
                EventsMania.dataManager.loadStats(it.player.uniqueId)
            }
        }

        GlobalTicker.runRepeatFixedRate(60_000L) {
            EventsMania.dataManager.updateLeaderboard()
        }
    }

}