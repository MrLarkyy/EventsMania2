package gg.aquatic.eventsmania.data.action

import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.impl.CommandAction
import gg.aquatic.execute.argument.ObjectArguments
import gg.aquatic.waves.editor.value.SimpleEditorValue
import org.bukkit.entity.Player

class CommandActionData: ActionData() {
    override val type: SimpleEditorValue<String> = infoString("type", "command")

    val command = editString("command", "", "Type the command:")
    val playerExecutor = editBoolean("player-executor", false)

    override fun create(): ActionHandle<Player> {
        return ActionHandle(CommandAction,
            ObjectArguments(mapOf("command" to command.value, "player-executor" to playerExecutor.value))
        )
    }

    override fun copy(): ActionData {
        TODO("Not yet implemented")
    }
}