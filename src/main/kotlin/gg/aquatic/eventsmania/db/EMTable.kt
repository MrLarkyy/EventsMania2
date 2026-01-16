package gg.aquatic.eventsmania.db

import org.jetbrains.exposed.sql.Table

object EMTable : Table("events_mania_stats") {
    val uuid = uuid("uuid")
    val username = varchar("username", 16)
    val wins = integer("wins").default(0)

    override val primaryKey = PrimaryKey(uuid)
}