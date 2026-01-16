package gg.aquatic.eventsmania.hook

import gg.aquatic.eventsmania.EventsMania.dataManager
import gg.aquatic.waves.util.PAPIUtil
import org.bukkit.entity.Player

object PAPIHook {

    fun initialize() {
        PAPIUtil.registerExtension("larkyy", "eventsmania") { p, params ->
            val args = params.split("_")
            if (args.isEmpty()) return@registerExtension ""
            when (args[0].lowercase()) {
                // %eventsmania_leaderboard_rank%
                // %eventsmania_leaderboard_wins%
                // %eventsmania_leaderboard_wins_<place>%
                // %eventsmania_leaderboard_username_<place>%
                "leaderboard" -> {
                    if (args.size < 2) return@registerExtension ""
                    when (args[1].lowercase()) {
                        "rank" -> {
                            val player = p as? Player ?: return@registerExtension ""
                            return@registerExtension dataManager.getPlayerRankOrNull(player.uniqueId)?.toString() ?: ""
                        }

                        "wins" -> {
                            if (args.size < 2) {
                                val player = p as? Player ?: return@registerExtension ""
                                return@registerExtension dataManager.getPlayerWinsOrNull(player.uniqueId)?.toString() ?: ""
                            }
                            val place = args[2].toIntOrNull() ?: return@registerExtension ""
                            return@registerExtension dataManager.getLeaderboard()
                                .getOrNull(place - 1)?.score?.toString() ?: ""
                        }

                        "username" -> {
                            if (args.size < 3) return@registerExtension ""
                            val place = args[2].toIntOrNull() ?: return@registerExtension ""
                            return@registerExtension dataManager.getLeaderboard()
                                .getOrNull(place - 1)?.username ?: ""
                        }
                    }
                }
            }
            return@registerExtension ""
        }
    }

}