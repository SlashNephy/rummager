package blue.starry.rummager

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val logger = KotlinLogging.logger("rummager")

@OptIn(ExperimentalTime::class)
suspend fun main() {
    while (true) {
        val taken = measureTime {
            try {
                Rummager.search()
            } catch (t: Throwable) {
                logger.error(t) { "Error occurred while searching." }
            }
        }
        logger.trace { "Search operation finished in $taken." }

        delay(Env.SEARCH_INTERVAL_SECONDS.seconds)
    }
}
