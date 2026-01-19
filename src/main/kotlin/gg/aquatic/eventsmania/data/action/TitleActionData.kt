package gg.aquatic.eventsmania.data.action

import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.impl.TitleAction
import gg.aquatic.execute.argument.ObjectArguments
import gg.aquatic.waves.editor.value.SimpleEditorValue
import org.bukkit.entity.Player

class TitleActionData: ActionData() {
    override val type: SimpleEditorValue<String> = infoString("type", "title")

    val title = editString("title", "", "Type the title:")
    val subtitle = editString("subtitle", "", "Type the subtitle:")
    val fadeIn = editInt("fade-in", 20, "Fade in time (in ticks):")
    val stay = editInt("stay", 60, "Stay time (in ticks):")
    val fadeOut = editInt("fade-out", 20, "Fade out time (in ticks):")

    override fun create(): ActionHandle<Player> {
        return ActionHandle(TitleAction, ObjectArguments(
            mapOf(
                "title" to title.value,
                "subtitle" to subtitle.value,
                "fade-in" to fadeIn.value,
                "stay" to stay.value,
                "fade-out" to fadeOut.value
            )
        )
        )
    }

    override fun copy(): ActionData {
        TODO("Not yet implemented")
    }
}