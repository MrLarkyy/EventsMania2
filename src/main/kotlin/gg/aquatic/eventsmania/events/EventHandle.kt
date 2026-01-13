package gg.aquatic.eventsmania.events

import gg.aquatic.eventsmania.EventsMania
import gg.aquatic.waves.statistic.StatisticAddEvent
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player
import java.util.*

class EventHandle(
    val event: Event
) {

    val statisticHandle = event.statisticHandle(this)
    val statistics = mutableMapOf<UUID, LeaderboardPlayer>()
    var sortedStatistics: List<LeaderboardPlayer> = listOf()
        private set

    var phase = Phase.PREPARE
        private set

    enum class Phase {
        PREPARE,
        START,
        END
    }

    var tick = 0
        private set

    fun start() {
        EventsMania.logger.info("Event started")
        EventManager.runningEvent = this
    }

    suspend fun tick() {
        try {
            when (phase) {
                Phase.PREPARE -> {
                    if (event.prepareSettings.prepareTime <= tick) {
                        phase = Phase.START
                        tick = 0
                        statisticHandle.register()
                        tick()
                        return
                    }
                    event.prepareSettings.execute(this)
                    tick++
                }

                Phase.START -> {
                    if (tick % 10 == 0) {
                        sortStatistics()
                    }
                    if (event.duration <= tick) {
                        finalizeEvent()
                        return
                    }
                    event.gameActions.execute(this)
                    tick++
                }

                else -> {

                }
            }
        } catch (e: Exception) {
            EventsMania.logger.warning("Failed to execute event tick: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun finalizeEvent() {

        phase = Phase.END
        tick = 0

        statisticHandle.unregister()
        EventManager.runningEvent = null
        sortStatistics()
        event.gameActions.executeEnd(this)

        event.giveRewards(sortedStatistics)
    }

    fun sortStatistics() {
        if (statistics.isEmpty()) return
        val sorted = statistics.values
            .sortedWith(
                compareByDescending { it.score }
            )

        // Assign ranks with tie handling (same score => same rank)
        var position = 1
        var lastScore: Int? = null
        var lastRank = 1
        for (player in sorted) {
            if (lastScore == null || player.score != lastScore) {
                lastRank = position
                lastScore = player.score
            }
            player.rank = lastRank
            position++
        }

        sortedStatistics = sorted
    }


    fun updatePlaceholders(player: Player, str: String): String {
        var str = str
        for (i in 1..10) {
            val value = sortedStatistics.getOrNull(i - 1)
            str = str
                .replace("%leaderboard-value-$i%", value?.score?.toString() ?: "0")
                .replace("%leaderboard-name-$i%", value?.username ?: "N/A")
        }
        val self = statistics[player.uniqueId]
        if (self != null) {
            str = str
                .replace("%leaderboard-value-self%", self.score.toString())
                .replace("%leaderboard-name-self%", self.username)
                .replace("%leaderboard-rank%", self.rank.toString())
        }
        str = str
            .replace("%leaderboard-value-self%", self?.score?.toString() ?: "0")
            .replace("%leaderboard-rank%", self?.rank?.toString() ?: (statistics.size + 1).toString())
            .replace("%tick%", tick.toString())
        return str.updatePAPIPlaceholders(player)
    }

    fun increase(player: Player, event: StatisticAddEvent<Player>) {
        val amt = event.increasedAmount.toInt()
        val lbPlayer = statistics.getOrPut(player.uniqueId) { LeaderboardPlayer(player.uniqueId, player.name,0) }
        lbPlayer.score += amt
    }

    suspend fun stop() {
        finalizeEvent()
    }
}