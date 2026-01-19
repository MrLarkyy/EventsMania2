package gg.aquatic.eventsmania.data.action

import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.impl.CloseInventory
import gg.aquatic.execute.argument.ObjectArguments
import gg.aquatic.waves.editor.value.SimpleEditorValue
import org.bukkit.entity.Player

class CloseInventoryActionData: ActionData() {
    override val type: SimpleEditorValue<String> = infoString("type", "close-inventory")

    override fun create(): ActionHandle<Player> {
        return ActionHandle(CloseInventory, ObjectArguments(emptyMap()))
    }

    override fun copy(): ActionData {
        TODO("Not yet implemented")
    }
}