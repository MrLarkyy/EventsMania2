package gg.aquatic.eventsmania

import gg.aquatic.klocale.LocaleManager
import gg.aquatic.klocale.LocaleProvider
import gg.aquatic.klocale.impl.paper.PaperMessage
import gg.aquatic.klocale.impl.paper.handler.CfgMessageHandler
import gg.aquatic.klocale.impl.paper.provider.YamlLocaleProvider
import gg.aquatic.waves.Waves
import java.io.File

enum class Messages(
    override val path: String
): CfgMessageHandler<PaperMessage> {

    HELP("help"),
    PLUGIN_RELOADED("plugin-reloaded"),
    PLUGIN_RELOADING("plugin-reloading"),

    // ERRORS
    NO_PERMISSION("no-permission"),
    EVENT_STARTING("event-starting"),
    EVENT_STOPPED("event-stopped"),
    EVENT_NOT_RUNNING("event-not-running"),
    EVENT_ALREADY_RUNNING("event-already-running"),
    EVENT_NOT_FOUND("event-not-found"),
    ;

    override val manager: LocaleManager<PaperMessage> = Waves.locale

    companion object {
        private val provider: LocaleProvider<PaperMessage> = YamlLocaleProvider(
            File("locale.yml"),
            YamlLocaleProvider.DefaultSerializer
        )

        private var injected = false

        suspend fun load() {
            if (!injected) {
                injected = true
                Waves.locale.injectProvider(provider)
                return
            }

            Waves.locale.invalidate()
        }
    }
}
