package gg.aquatic.eventsmania.data.action

import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.impl.SoundAction
import gg.aquatic.execute.argument.ObjectArguments
import gg.aquatic.waves.editor.value.SimpleEditorValue
import org.bukkit.Registry
import org.bukkit.Sound
import org.bukkit.entity.Player

class SoundActionData: ActionData() {
    override val type: SimpleEditorValue<String> = infoString("type", "sound")

    val sound = editSound("sound", Sound.ENTITY_PLAYER_LEVELUP, "Select a sound:")
    val pitch = editFloat("pitch", 1f, "Sound pitch (0.0 - 2.0):")
    val volume = editFloat("volume", 1f, "Sound volume (0.0 - 1.0):")

    override fun create(): ActionHandle<Player> {
        return ActionHandle(SoundAction, ObjectArguments(mapOf("sound" to Registry.SOUNDS.getKey(sound.value)?.toString(), "pitch" to pitch.value, "volume" to volume.value)))
    }

    override fun copy(): ActionData {
        TODO("Not yet implemented")
    }
}