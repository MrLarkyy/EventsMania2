package gg.aquatic.eventsmania

import gg.aquatic.common.HikariDBFactory
import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.eventsmania.command.Commands
import gg.aquatic.eventsmania.db.DBHandler
import gg.aquatic.eventsmania.db.DataHandler
import gg.aquatic.eventsmania.db.DataManager
import gg.aquatic.eventsmania.db.EMTable
import gg.aquatic.eventsmania.events.EventManager
import gg.aquatic.eventsmania.hook.PAPIHook
import gg.aquatic.statistik.StatisticType
import gg.aquatic.statistik.impl.BlockBreakStatistic
import gg.aquatic.statistik.impl.BlockPlaceStatistic
import gg.aquatic.statistik.impl.DamageDealtStatistic
import gg.aquatic.statistik.impl.DeathStatistic
import gg.aquatic.statistik.impl.ItemCraftStatistic
import gg.aquatic.statistik.impl.KillStatistic
import gg.aquatic.statistik.impl.PlaceholderStatistic
import gg.aquatic.statistik.impl.TravelStatistic
import gg.aquatic.waves.Waves
import org.bukkit.plugin.java.JavaPlugin

object EventsMania : JavaPlugin() {

    lateinit var settings: PluginSettings
    lateinit var dataManager: DataManager

    override fun onEnable() {
        Waves.registryBootstrap(Waves) {
            registry(StatisticType.REGISTRY_KEY) {
                add("BLOCK_BREAK", BlockBreakStatistic)
                add("BLOCK_PLACE", BlockPlaceStatistic)
                add("DAMAGE_DEALT", DamageDealtStatistic)
                add("DEATH", DeathStatistic)
                add("ITEM_CRAFT", ItemCraftStatistic)
                add("KILL", KillStatistic)
                add("PLACEHOLDER", PlaceholderStatistic)
                add("TRAVEL", TravelStatistic)
            }
        }

        VirtualsCtx {
            Messages.load()
        }
        reloadPlugin()

        val database = HikariDBFactory.init(
            settings.dbUrl,
            settings.dbDriver,
            settings.dbUser,
            settings.dbPassword,
            EMTable
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