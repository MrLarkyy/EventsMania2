package gg.aquatic.eventsmania

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.kregistry.Registry
import gg.aquatic.waves.statistic.StatisticType
import gg.aquatic.waves.statistic.impl.BlockBreakStatistic
import gg.aquatic.waves.statistic.registerStatistic
import org.bukkit.plugin.java.JavaPlugin

object EventsMania: JavaPlugin() {

    lateinit var settings: PluginSettings

    override fun onEnable() {
        Registry.update {
            replaceRegistry(StatisticType.REGISTRY_KEY) {
                registerStatistic("BLOCK_BREAK", BlockBreakStatistic)
            }
        }

        VirtualsCtx {
            Messages.load()
        }

        println("Registered statistics: ${StatisticType.REGISTRY.getAll().values.sumOf { it.getAll().size }}")
        for ((clazz, reg) in StatisticType.REGISTRY.getAll()) {
            println("Registry for ${clazz.simpleName}:")
            for ((id, type) in reg.getAll()) {
                println("  - ${id}: ${type::class.simpleName}")
            }
        }
        reloadPlugin()

        Commands.initialize()
    }

    internal fun reloadPlugin() {
        settings = Serializer.loadSettings()
        EventManager.initialize()
    }
}