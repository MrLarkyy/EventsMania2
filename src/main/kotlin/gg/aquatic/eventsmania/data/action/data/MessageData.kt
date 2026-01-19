package gg.aquatic.eventsmania.data.action.data

import gg.aquatic.common.toMMComponent
import gg.aquatic.klocale.impl.paper.PaperMessage
import gg.aquatic.waves.editor.Configurable
import gg.aquatic.waves.editor.Serializers
import gg.aquatic.waves.editor.handlers.ChatInputHandler
import gg.aquatic.waves.editor.value.ElementBehavior
import gg.aquatic.waves.input.impl.ChatInput
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MessageData(
    initialLines: List<Component> = listOf()
) : Configurable<MessageData>() {

    val lines = editList(
        "lines", initialLines, Serializers.COMPONENT,
        ElementBehavior(
            icon = { line -> ItemStack(Material.PAPER).apply { editMeta { it.displayName(line) } } },
            handler = ChatInputHandler.forComponent("Enter line:")
        ),
        addButtonClick = { player, accept ->
            player.closeInventory()
            player.sendMessage("Enter line:")
            ChatInput.createHandle(listOf("cancel")).await(player).thenAccept {
                accept(it?.toMMComponent())
            }
        },
        listIcon = { list -> ItemStack(Material.BOOK).apply { editMeta { it.displayName(Component.text("Edit Lore (${list.size} lines)")) } } },
        guiHandler = { p, ed, u -> openListMenu(p, ed, ed.addButtonClick, u) })

    fun create(): PaperMessage {
        return PaperMessage.of(lines.value.map { it.value })
    }

    override fun copy(): MessageData {
        return MessageData(lines.value.map { it.clone().value })
    }
}