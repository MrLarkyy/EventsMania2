package gg.aquatic.eventsmania.events

import gg.aquatic.common.event
import gg.aquatic.common.ticker.Ticker
import gg.aquatic.eventsmania.EventsMania
import gg.aquatic.eventsmania.Serializer
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent

object EventManager {

    var runningEvent: EventHandle? = null

    val events = HashMap<String, Event>()

    private var tickerCount = 0L

    fun loadEvents() {
        events.clear()
        events += Serializer.loadEvents(EventsMania.dataFolder.resolve("events").apply {
            mkdirs()
        })
    }

    fun initialize() {
        loadEvents()

        Ticker {
            runningEvent?.tick()
            tickerCount++
            if (tickerCount < EventsMania.settings.period) {
                return@Ticker
            }
            tickerCount = 0

            if (runningEvent != null) {
                return@Ticker
            }
            if (Bukkit.getOnlinePlayers().size < EventsMania.settings.minPlayers) {
                return@Ticker
            }
            events.values.random().start()
        }.register()

        event<PlayerQuitEvent> {
            val e = runningEvent ?: return@event
            e.statistics -= it.player.uniqueId
        }
    }
}
