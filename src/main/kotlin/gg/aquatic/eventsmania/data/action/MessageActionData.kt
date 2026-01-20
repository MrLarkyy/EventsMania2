package gg.aquatic.eventsmania.data.action

import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.eventsmania.data.action.data.MessageData
import gg.aquatic.execute.ActionHandle
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.editor.value.SimpleEditorValue
import gg.aquatic.waves.util.action.MessageAction
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

class MessageActionData(
    initialMessage: MessageData = MessageData()
): ActionData() {
    override val type: SimpleEditorValue<String> = infoString("type","message")

    val message = editConfigurable("message", initialMessage, {
        stackedItem(Material.PAPER) {
            displayName = Component.text("Message")
        }.getItem()
    })

    override fun create(): ActionHandle<Player> {
        val action = MessageAction

        val args = hashMapOf("message" to message.value)

        return ActionHandle(
            action,
            ObjectArguments(args)
        )
    }

    override fun copy(): ActionData {
        return MessageActionData(message.clone().value)
    }
}