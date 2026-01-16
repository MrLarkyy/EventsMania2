package gg.aquatic.eventsmania.db

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.eventsmania.events.LeaderboardPlayer
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.upsert
import java.util.*

class DBHandler(private val database: Database) {

    suspend fun addWin(uuid: UUID, username: String) = newSuspendedTransaction(VirtualsCtx, database) {
        EMTable.upsert(onUpdate = {
            it[EMTable.wins] = EMTable.wins plus 1
            it[EMTable.username] = username
        }) {
            it[EMTable.uuid] = uuid
            it[EMTable.username] = username
            it[wins] = 1
        }
    }

    suspend fun getPlayerRank(uuid: UUID): Int = newSuspendedTransaction(VirtualsCtx, database) {
        val playerWins = EMTable.select(EMTable.wins)
            .where { EMTable.uuid eq uuid }
            .singleOrNull()?.get(EMTable.wins) ?: return@newSuspendedTransaction -1

        (EMTable.select(EMTable.uuid.count())
            .where { EMTable.wins greater playerWins }
            .single()[EMTable.uuid.count()] + 1).toInt()
    }

    suspend fun getPlayerRank(userName: String): Int = newSuspendedTransaction(VirtualsCtx, database) {
        val playerWins = EMTable.select(EMTable.wins)
            .where { EMTable.username eq userName }
            .singleOrNull()?.get(EMTable.wins) ?: return@newSuspendedTransaction -1

        (EMTable.select(EMTable.uuid.count())
            .where { EMTable.wins greater playerWins }
            .single()[EMTable.uuid.count()] + 1).toInt()
    }

    suspend fun getWins(uuid: UUID): Int = newSuspendedTransaction(VirtualsCtx, database) {
        EMTable.select(EMTable.wins)
            .where { EMTable.uuid eq uuid }
            .singleOrNull()?.get(EMTable.wins) ?: 0
    }


    suspend fun getTopPlayers(limit: Int): List<LeaderboardPlayer> = newSuspendedTransaction(VirtualsCtx, database) {
        EMTable.selectAll()
            .orderBy(EMTable.wins to SortOrder.DESC)
            .limit(limit)
            .mapIndexed { index, row ->
                LeaderboardPlayer(
                    row[EMTable.uuid],
                    row[EMTable.username],
                    row[EMTable.wins]
                ).apply { rank = index + 1 }
            }
    }
}