package gg.aquatic.eventsmania.db

import gg.aquatic.eventsmania.db.EMTable.wins
import gg.aquatic.eventsmania.events.LeaderboardPlayer
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.upsert
import java.util.*

class DBHandler(private val database: Database) {

    suspend fun addWin(uuid: UUID, username: String) = suspendTransaction(database) {
        EMTable.upsert(onUpdate = {
            it[wins] = wins plus 1
            it[EMTable.username] = username
        }) {
            it[EMTable.uuid] = uuid
            it[EMTable.username] = username
            it[wins] = 1
        }
    }

    suspend fun getPlayerRank(uuid: UUID): Int = suspendTransaction(database) {
        val playerWins = EMTable.select(wins)
            .where { EMTable.uuid eq uuid }
            .singleOrNull()?.get(wins) ?: return@suspendTransaction -1

        (EMTable.select(EMTable.uuid.count())
            .where { wins greater playerWins }
            .single()[EMTable.uuid.count()] + 1).toInt()
    }

    suspend fun getPlayerRank(userName: String): Int = suspendTransaction(database) {
        val playerWins = EMTable.select(wins)
            .where { EMTable.username eq userName }
            .singleOrNull()?.get(wins) ?: return@suspendTransaction -1

        (EMTable.select(EMTable.uuid.count())
            .where { wins greater playerWins }
            .single()[EMTable.uuid.count()] + 1).toInt()
    }

    suspend fun getWins(uuid: UUID): Int = suspendTransaction(database) {
        EMTable.select(wins)
            .where { EMTable.uuid eq uuid }
            .singleOrNull()?.get(wins) ?: 0
    }


    suspend fun getTopPlayers(limit: Int): List<LeaderboardPlayer> = suspendTransaction(database) {
        EMTable.selectAll()
            .orderBy(wins to SortOrder.DESC)
            .limit(limit)
            .mapIndexed { index, row ->
                LeaderboardPlayer(
                    row[EMTable.uuid],
                    row[EMTable.username],
                    row[wins]
                ).apply { rank = index + 1 }
            }
    }
}