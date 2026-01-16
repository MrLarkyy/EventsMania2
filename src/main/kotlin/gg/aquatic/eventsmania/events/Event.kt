package gg.aquatic.eventsmania.events

import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.executeActions
import gg.aquatic.treepapi.updatePAPIPlaceholders
import gg.aquatic.waves.statistic.StatisticHandle
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Event(
    val id: String,
    val statisticHandle: (EventHandle) -> StatisticHandle<Player>,
    val duration: Int,
    val prepareSettings: PrepareSettings,
    val gameActions: GameActions,
    val rewards: Map<Int, Collection<ActionHandle<Player>>>
) {

    class PrepareSettings(
        val prepareTime: Int,
        val actions: Map<Int, Collection<ActionHandle<Player>>>
    ) {
        suspend fun execute(handle: EventHandle) {
            val tick = handle.tick
            val delayed = actions[tick] ?: emptyList()

            for (player in Bukkit.getOnlinePlayers()) {
                delayed.executeActions(player) { p, str ->
                    handle.updatePlaceholders(p, str)
                }
            }
        }
    }

    class GameActions(
        val actions: Map<Int, Collection<ActionHandle<Player>>>,
        val endActions: Collection<ActionHandle<Player>>
    ) {

        suspend fun execute(handle: EventHandle) {
            val tick = handle.tick
            val timed = actions[tick] ?: return
            for (player in Bukkit.getOnlinePlayers()) {
                timed.executeActions(player) { p, str ->
                    handle.updatePlaceholders(p, str)
                }
            }
        }

        suspend fun executeEnd(handle: EventHandle) {
            for (player in Bukkit.getOnlinePlayers()) {
                endActions.executeActions(player) { p, str ->
                    handle.updatePlaceholders(p, str)
                }
            }
        }
    }

    suspend fun giveRewards(leaderboard: List<LeaderboardPlayer>) {
        for ((index, reward) in rewards) {
            val lbPlayer = leaderboard.getOrNull(index-1) ?: break
            val player = Bukkit.getPlayer(lbPlayer.uuid) ?: continue
            reward.executeActions(player) { p, string ->
                string.updatePAPIPlaceholders(p).replace("%rank%", lbPlayer.rank.toString())
            }
        }
    }

    fun start(): EventHandle {
        return EventHandle(this).apply {
            start()
        }
    }
}
