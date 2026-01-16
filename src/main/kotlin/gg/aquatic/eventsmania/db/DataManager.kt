package gg.aquatic.eventsmania.db

import com.github.benmanes.caffeine.cache.Caffeine
import gg.aquatic.eventsmania.events.LeaderboardPlayer
import java.util.*
import java.util.concurrent.TimeUnit

class DataManager(private val dbHandler: DBHandler) {

    private val winsCache = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<UUID, Int>()

    private val statsCache = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build<UUID, PlayerStats>()

    private var cachedLeaderboard: List<LeaderboardPlayer> = emptyList()
    private var lastLeaderboardUpdate = 0L

    suspend fun getWins(uuid: UUID): Int {
        val cached = winsCache.getIfPresent(uuid)
        if (cached != null) return cached

        val wins = dbHandler.getWins(uuid)
        winsCache.put(uuid, wins)
        return wins
    }

    suspend fun addWin(uuid: UUID, username: String) {
        dbHandler.addWin(uuid, username)
        val currentWins = winsCache.getIfPresent(uuid) ?: dbHandler.getWins(uuid)
        winsCache.put(uuid, currentWins + 1)
        loadStats(uuid)
    }

    suspend fun updateLeaderboard() {
        val now = System.currentTimeMillis()
        if (now - lastLeaderboardUpdate < TimeUnit.MINUTES.toMillis(10)) return

        cachedLeaderboard = dbHandler.getTopPlayers(10)
        cachedLeaderboard.forEach { lbPlayer ->
            statsCache.put(lbPlayer.uuid, PlayerStats(lbPlayer.score, lbPlayer.rank))
        }
        lastLeaderboardUpdate = System.currentTimeMillis()
    }

    fun getLeaderboard(): List<LeaderboardPlayer> = cachedLeaderboard

    suspend fun loadStats(uuid: UUID): PlayerStats {
        val wins = dbHandler.getWins(uuid)
        val rank = dbHandler.getPlayerRank(uuid)
        val stats = PlayerStats(wins, rank)
        statsCache.put(uuid, stats)
        return stats
    }

    fun getCachedStats(uuid: UUID): PlayerStats? {
        return statsCache.getIfPresent(uuid)
    }

    fun getPlayerRankOrNull(uuid: UUID): Int? = getCachedStats(uuid)?.rank
    fun getPlayerWinsOrNull(uuid: UUID): Int? = getCachedStats(uuid)?.wins
}

data class PlayerStats(
    val wins: Int,
    val rank: Int
)