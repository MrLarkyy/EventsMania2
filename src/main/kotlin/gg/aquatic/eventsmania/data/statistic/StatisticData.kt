package gg.aquatic.eventsmania.data.statistic

import gg.aquatic.eventsmania.events.EventHandle
import gg.aquatic.waves.editor.Configurable
import gg.aquatic.waves.editor.value.SimpleEditorValue
import gg.aquatic.waves.statistic.StatisticHandle
import org.bukkit.entity.Player

abstract class StatisticData: Configurable<StatisticData>() {

    abstract val type: SimpleEditorValue<String>

    abstract fun createSupplier(): (EventHandle) -> StatisticHandle<Player>

}