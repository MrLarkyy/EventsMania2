package gg.aquatic.eventsmania.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID

object EMTable : Table("events_mania_stats") {
    val uuid = javaUUID("uuid")
    val username = varchar("username", 16)
    val wins = integer("wins").default(0)

    override val primaryKey = PrimaryKey(uuid)
}