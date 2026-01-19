package gg.aquatic.eventsmania.data.action

import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.editor.Configurable
import gg.aquatic.waves.editor.value.SimpleEditorValue
import org.bukkit.entity.Player

abstract class ActionData: Configurable<ActionData>() {

    abstract val type: SimpleEditorValue<String>

    abstract fun create(): ActionHandle<Player>

}