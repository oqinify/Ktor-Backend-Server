package id.oqinify

import id.oqinify.data.user.MongoUserDataSource
import id.oqinify.plugins.configureMonitoring
import id.oqinify.plugins.configureRouting
import id.oqinify.plugins.configureSecurity
import id.oqinify.plugins.configureSerialization
import id.oqinify.security.hashing.SHA256HashingService
import id.oqinify.security.token.JWTTokenService
import id.oqinify.security.token.TokenConfig
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongoPw = System.getenv("MONGO_PW")
    val dbName = "ktor-auth-db"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://oqinify:$mongoPw@cluster0.7bvhe3z.mongodb.net/$dbName?retryWrites=true&w=majority"
    ).coroutine
        .getDatabase(dbName)

    val dataSource = MongoUserDataSource(db)
    val tokenService = JWTTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    configureRouting(dataSource, hashingService, tokenService, tokenConfig)
    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
}
