package blue.starry.rummager

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.emulation.EmulationMode
import blue.starry.penicillin.core.session.config.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.http.*

val RummagerHttpClient = HttpClient {
    defaultRequest {
        userAgent("rummager (+https://github.com/SlashNephy/rummager)")
    }
}

val RummagerTwitterClient = PenicillinClient {
    account {
        application(Env.TWITTER_CK, Env.TWITTER_CS)
        token(Env.TWITTER_AT, Env.TWITTER_ATS)
    }
    api {
        if (Env.USE_PRIVATE_API) {
            emulationMode = EmulationMode.TwitterForiPhone
        }
    }
    httpClient(RummagerHttpClient)
}
