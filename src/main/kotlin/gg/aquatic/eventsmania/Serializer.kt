package gg.aquatic.eventsmania

import gg.aquatic.common.getSectionList
import gg.aquatic.eventsmania.events.Event
import gg.aquatic.eventsmania.events.EventHandle
import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.ActionSerializer
import gg.aquatic.waves.Config
import gg.aquatic.waves.statistic.StatisticHandle
import gg.aquatic.waves.statistic.StatistikSerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.File

object Serializer {

    fun loadEvents(folder: File): Map<String, Event> {
        val events = mutableMapOf<String, Event>()
        for (file in folder.listFiles()) {
            if (file.isDirectory) {
                events += loadEvents(file)
                continue
            }
            val config = Config(file, EventsMania)
            config.loadSync()
            events += loadEvents(config.getConfiguration())
        }
        return events
    }

    fun loadSettings(): PluginSettings {
        val config = Config(File(EventsMania.dataFolder, "config.yml"), EventsMania)
        config.loadSync()
        val cfg = config.getConfiguration()
        val period = cfg.getLong("period")
        val minPlayers = cfg.getInt("min-players")

        val dbUrl = cfg.getString("database.url") ?: throw Exception("Database URL not set")
        val dbDriver = cfg.getString("database.driver") ?: throw Exception("Database driver not set")
        val dbUser = cfg.getString("database.user") ?: throw Exception("Database user not set")
        val dbPassword = cfg.getString("database.password") ?: throw Exception("Database password not set")
        return PluginSettings(minPlayers, period, dbUrl, dbDriver, dbUser, dbPassword)
    }

    fun loadEvents(config: FileConfiguration): Map<String, Event> {
        val events = mutableMapOf<String, Event>()
        val eventsSection = config.getConfigurationSection("events") ?: return events
        for (key in eventsSection.getKeys(false)) {
            val eventSection = eventsSection.getConfigurationSection(key) ?: continue
            try {
                val statisticHandle: (EventHandle) -> StatisticHandle<Player> = { handle ->
                    StatistikSerializer.fromSection<Player>(eventSection) { e ->
                        val player = e.binder
                        handle.increase(player, e)
                    } ?: throw Exception("Failed to load statistic type for event $key")
                }

                val delayDuration = eventSection.getInt("delayed.duration")
                val delayActions = eventSection.getConfigurationSection("delayed.actions")?.let {
                    loadTimedActions(it, delayDuration)
                } ?: emptyMap()

                val duration = eventSection.getInt("duration")
                val startActions = eventSection.getConfigurationSection("actions")?.let {
                    loadTimedActions(it, duration)
                } ?: emptyMap()
                val endActions = ActionSerializer.fromSections<Player>(eventSection.getSectionList("end-actions"))

                val rewards = loadIndexActions(eventSection.getConfigurationSection("rewards")!!)

                events[key] = Event(
                    key,
                    statisticHandle,
                    duration,
                    Event.PrepareSettings(delayDuration, delayActions),
                    Event.GameActions(
                        startActions,
                        endActions
                    ),
                    rewards
                )
            } catch (e: Exception) {
                EventsMania.logger.warning("Failed to load event $key: ${e.message}")
                e.printStackTrace()
            }
        }
        return events
    }

    fun loadIndexActions(section: ConfigurationSection): Map<Int, Collection<ActionHandle<Player>>> {
        val map = mutableMapOf<Int, Collection<ActionHandle<Player>>>()
        for (key in section.getKeys(false)) {
            val actions = ActionSerializer.fromSections<Player>(section.getSectionList(key))
            map[key.toInt()] = actions
        }
        return map
    }

    fun loadTimedActions(section: ConfigurationSection, duration: Int): Map<Int, Collection<ActionHandle<Player>>> {
        val map = mutableMapOf<Int, Collection<ActionHandle<Player>>>()
        for (key in section.getKeys(false)) {
            val actions = ActionSerializer.fromSections<Player>(section.getSectionList(key))
            if (key.lowercase().startsWith("every-")) {
                val every = key.substringAfter("every-").toInt()
                var currentI = 0
                while (true) {
                    if (currentI >= duration) {
                        break
                    }
                    val list = map.getOrPut(currentI) { mutableListOf() } as MutableList
                    list += actions
                    currentI += every
                }
                continue
            } else if (key.contains(";")) {
                val split = key.split(";").map { it.toInt() }
                for (i in split) {
                    val list = map.getOrPut(i) { mutableListOf() } as MutableList
                    list += actions
                }
                continue
            }
            val list = map.getOrPut(key.toInt()) { mutableListOf() } as MutableList
            list += actions
        }
        return map
    }
}