package gg.aquatic.eventsmania.data.action

import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.impl.ActionbarAction
import gg.aquatic.waves.editor.value.SimpleEditorValue
import org.bukkit.entity.Player

class ActionbarActionData : ActionData() {
    override val type: SimpleEditorValue<String> = infoString("type", "actionbar")

    val message = editString("message", "", "Type the message:")

    override fun create(): ActionHandle<Player> {
        return ActionHandle(ActionbarAction, ObjectArguments(mapOf("message" to message.value)))
    }

    override fun copy(): ActionData {
        TODO("Not yet implemented")
    }
}