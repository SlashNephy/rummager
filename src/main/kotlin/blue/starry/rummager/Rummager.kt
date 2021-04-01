package blue.starry.rummager

import blue.starry.penicillin.endpoints.search
import blue.starry.penicillin.endpoints.search.SearchResultType
import blue.starry.penicillin.endpoints.search.search
import blue.starry.penicillin.endpoints.search.universal
import blue.starry.penicillin.extensions.createdAt
import blue.starry.penicillin.extensions.instant
import blue.starry.penicillin.extensions.models.text
import blue.starry.penicillin.extensions.via
import blue.starry.penicillin.models.Status
import io.ktor.client.request.*
import io.ktor.http.*
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicLong

object Rummager {
    private val lastId = AtomicLong()

    suspend fun search() {
        if (Env.USE_PRIVATE_API) {
            searchWithPrivateApi()
        } else {
            searchWithPublicApi()
        }
    }

    private suspend fun searchWithPrivateApi() {
        val search = RummagerTwitterClient.search.universal(
            query = Env.SEARCH_QUERY,
            modules = "tweet",
            resultType = SearchResultType.Recent
        ).execute()

        if (lastId.get() != 0L) {
            for (status in search.result.statuses) {
                val tweet = status.data

                if (lastId.get() >= tweet.id) {
                    continue
                }

                if (tweet.user.screenName in Env.IGNORE_SCREEN_NAMES || tweet.via.name in Env.IGNORE_SOURCES) {
                    continue
                }

                if (tweet.user.following || tweet.user.followedBy == true) {
                    sendToDiscord(tweet, Env.DISCORD_WEBHOOK_URL_FROM_FOLLOWERS ?: Env.DISCORD_WEBHOOK_URL)
                } else {
                    sendToDiscord(tweet, Env.DISCORD_WEBHOOK_URL)
                }
            }
        }

        lastId.set(search.result.statuses.maxOf { it.data.id })
    }

    private suspend fun searchWithPublicApi() {
        val search = RummagerTwitterClient.search.search(
            query = Env.SEARCH_QUERY,
            resultType = SearchResultType.Recent
        ).execute()

        if (lastId.get() != 0L) {
            for (tweet in search.result.statuses) {
                if (lastId.get() >= tweet.id) {
                    continue
                }

                if (tweet.user.screenName in Env.IGNORE_SCREEN_NAMES || tweet.via.name in Env.IGNORE_SOURCES) {
                    continue
                }

                if (tweet.user.following || tweet.user.followedBy == true) {
                    sendToDiscord(tweet, Env.DISCORD_WEBHOOK_URL_FROM_FOLLOWERS ?: Env.DISCORD_WEBHOOK_URL)
                } else {
                    sendToDiscord(tweet, Env.DISCORD_WEBHOOK_URL)
                }
            }
        }

        lastId.set(search.result.statuses.maxOf { it.id })
    }

    private suspend fun sendToDiscord(tweet: Status, webhookUrl: String) {
        logger.trace { tweet }

        if (Env.DRYRUN) {
            return
        }

        RummagerHttpClient.post<Unit>(webhookUrl) {
            contentType(ContentType.Application.Json)

            body = DiscordWebhookMessage(
                embeds = listOf(
                    DiscordEmbed(
                        color = 1942002,
                        author = DiscordEmbed.Author(
                            name = "${tweet.user.name} (@${tweet.user.screenName})",
                            url = "https://twitter.com/${tweet.user.screenName}",
                            iconUrl = tweet.user.profileImageUrlHttps
                        ),
                        description = tweet.text,
                        fields = listOf(
                            DiscordEmbed.Field(
                                name = "Source",
                                value = "[${tweet.via.name}](https://twitter.com/${tweet.user.screenName}/status/${tweet.id})"
                            ),
                            DiscordEmbed.Field(
                                name = "Link",
                                value = "https://twitter.com/${tweet.user.screenName}/status/${tweet.id}"
                            )
                        ),
                        image = tweet.entities.media.firstOrNull()?.let {
                            DiscordEmbed.Image(
                                url = it.mediaUrlHttps,
                                height = it.sizes?.large?.h,
                                width = it.sizes?.large?.w
                            )
                        },
                        footer = DiscordEmbed.Footer(
                            text = "Twitter",
                            iconUrl = "https://abs.twimg.com/icons/apple-touch-icon-192x192.png"
                        ),
                        timestamp = tweet.createdAt.instant.atOffset(ZoneOffset.UTC)?.toZonedDateTime()
                    )
                )
            )
        }
    }
}
