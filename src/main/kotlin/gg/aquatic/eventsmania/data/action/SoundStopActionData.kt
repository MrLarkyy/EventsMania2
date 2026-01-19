package gg.aquatic.eventsmania.data.action

import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.impl.SoundStopAction
import gg.aquatic.execute.argument.ObjectArguments
import gg.aquatic.waves.editor.value.SimpleEditorValue
import org.bukkit.Registry
import org.bukkit.Sound
import org.bukkit.entity.Player

class SoundStopActionData: ActionData() {
    override val type: SimpleEditorValue<String> = infoString("type", "stop-sound")

    val sound = editSound("sound", Sound.ENTITY_PLAYER_LEVELUP, "Select a sound:")

    override fun create(): ActionHandle<Player> {
        return ActionHandle(SoundStopAction, ObjectArguments(mapOf("sound" to Registry.SOUNDS.getKey(sound.value)?.toString())))
    }

    override fun copy(): ActionData {
        TODO("Not yet implemented")
    }
}