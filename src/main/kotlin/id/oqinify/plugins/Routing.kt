package id.oqinify.plugins

import id.oqinify.authenticate
import id.oqinify.data.user.UserDataSource
import id.oqinify.getSecretInfo
import id.oqinify.security.hashing.HashingService
import id.oqinify.security.token.TokenConfig
import id.oqinify.security.token.TokenService
import id.oqinify.signIn
import id.oqinify.signUp
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signUp(hashingService, userDataSource)
        signIn(hashingService, userDataSource, tokenService, tokenConfig)
        authenticate()
        getSecretInfo()
    }
}
