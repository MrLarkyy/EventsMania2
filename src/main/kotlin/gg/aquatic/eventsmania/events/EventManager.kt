package gg.aquatic.eventsmania.events

import gg.aquatic.common.event
import gg.aquatic.common.ticker.GlobalTicker
import gg.aquatic.eventsmania.EventsMania
import gg.aquatic.eventsmania.Serializer
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

object EventManager {

    @OptIn(ExperimentalAtomicApi::class)
    val runningEvent: AtomicReference<EventHandle?> = AtomicReference(null)

    val events = HashMap<String, Event>()

    fun loadEvents() {
        events.clear()
        events += Serializer.loadEvents(EventsMania.dataFolder.resolve("events").apply {
            mkdirs()
        })
    }

    @OptIn(ExperimentalAtomicApi::class)
    fun initialize() {
        loadEvents()

        GlobalTicker.runRepeatFixedRate(50L) {
            runningEvent.load()?.tick()
        }

        val repeatPeriod = EventsMania.settings.period * 50L

        GlobalTicker.runRepeatFixedRate(repeatPeriod) {
            if (runningEvent.load() != null) {
                return@runRepeatFixedRate
            }
            if (Bukkit.getOnlinePlayers().size < EventsMania.settings.minPlayers) {
                return@runRepeatFixedRate
            }
            events.values.random().start()
        }

        event<PlayerQuitEvent> {
            val e = runningEvent.load() ?: return@event
            e.statistics -= it.player.uniqueId
        }
    }
}
