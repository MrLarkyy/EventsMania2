package gg.aquatic.eventsmania

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.eventsmania.db.DBHandler
import gg.aquatic.eventsmania.db.DataHandler
import gg.aquatic.eventsmania.db.DataManager
import gg.aquatic.eventsmania.db.EMDatabaseFactory
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.eventsmania.hook.PAPIHook
import gg.aquatic.kregistry.Registry
import gg.aquatic.kurrency.db.BalancesTable
import gg.aquatic.waves.statistic.StatisticType
import gg.aquatic.waves.statistic.impl.BlockBreakStatistic
import gg.aquatic.waves.statistic.registerStatistic
import org.bukkit.plugin.java.JavaPlugin

object EventsMania : JavaPlugin() {

    lateinit var settings: PluginSettings
    lateinit var dataManager: DataManager

    override fun onEnable() {
        Registry.update {
            replaceRegistry(StatisticType.REGISTRY_KEY) {
                registerStatistic("BLOCK_BREAK", BlockBreakStatistic)
            }
        }

        VirtualsCtx {
            Messages.load()
        }
        reloadPlugin()

        val database = EMDatabaseFactory.init(
            settings.dbUrl,
            settings.dbDriver,
            settings.dbUser,
            settings.dbPassword,
            BalancesTable
        )
        dataManager = DataManager(DBHandler(database))
        VirtualsCtx {
            dataManager.updateLeaderboard()
        }

        DataHandler.initialize()
        PAPIHook.initialize()
        Commands.initialize()
    }

    internal fun reloadPlugin() {
        settings = Serializer.loadSettings()
        EventManager.initialize()
    }
}