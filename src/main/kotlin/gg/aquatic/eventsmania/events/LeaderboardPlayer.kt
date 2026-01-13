package gg.aquatic.eventsmania.events

import java.util.UUID

class LeaderboardPlayer(
    val uuid: UUID,
    val username: String,
    var score: Int,
) {

    var rank = 0
}