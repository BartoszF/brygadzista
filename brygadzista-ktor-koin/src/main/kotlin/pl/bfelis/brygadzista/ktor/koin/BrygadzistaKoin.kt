package pl.bfelis.brygadzista.ktor.koin

import io.ktor.server.application.Application
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.install
import io.ktor.util.AttributeKey
import org.koin.ktor.ext.getKoin
import pl.bfelis.brygadzista.SuspendingActionInterceptor
import pl.bfelis.brygadzista.ktor.Brygadzista
import pl.bfelis.brygadzista.ktor.KtorActionContextFactory
import pl.bfelis.brygadzista.ktor.KtorActionDispatcher
import pl.bfelis.brygadzista.ktor.KtorActionHandler

/** Installs Brygadzista using handlers and extensions registered in this application's Koin context. */
object BrygadzistaKoin : BaseApplicationPlugin<Application, Unit, KtorActionDispatcher> {
    override val key = AttributeKey<KtorActionDispatcher>("BrygadzistaKoin")

    override fun install(
        pipeline: Application,
        configure: Unit.() -> Unit,
    ): KtorActionDispatcher {
        configure(Unit)
        val koin = pipeline.getKoin()
        return pipeline.install(Brygadzista) {
            koin.getAll<KtorActionHandler>().forEach { handler(it) }
            koin.getAll<KtorActionContextFactory>().forEach(::contextFactory)
            koin.getAll<SuspendingActionInterceptor>().forEach(::interceptor)
        }
    }
}
