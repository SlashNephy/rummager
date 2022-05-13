package blue.starry.rummager

import java.util.*
import kotlin.properties.ReadOnlyProperty

object Env {
    val TWITTER_CK by string
    val TWITTER_CS by string
    val TWITTER_AT by string
    val TWITTER_ATS by string
    val USE_PRIVATE_API by boolean

    val SEARCH_QUERY by string
    val SEARCH_INTERVAL_SECONDS by long { 30 }
    val IGNORE_SOURCES by stringList
    val IGNORE_SCREEN_NAMES by stringList
    val DISCORD_WEBHOOK_URL by string
    val DISCORD_WEBHOOK_URL_FROM_FOLLOWERS by stringOrNull

    val DRYRUN by boolean
}

private val string: ReadOnlyProperty<Env, String>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name) ?: error("Env: ${property.name} is not present.")
    }

private val stringOrNull: ReadOnlyProperty<Env, String?>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name)
    }

private fun long(default: () -> Long) = ReadOnlyProperty<Env, Long> { _, property ->
    System.getenv(property.name)?.toLongOrNull() ?: default()
}

private val stringList: ReadOnlyProperty<Env, List<String>>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name)?.split(",").orEmpty()
    }

private fun String?.toBooleanFazzy(): Boolean {
    return when (this) {
        null -> false
        "1", "yes" -> true
        else -> lowercase(Locale.getDefault()).toBoolean()
    }
}

private val boolean: ReadOnlyProperty<Env, Boolean>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name).toBooleanFazzy()
    }
