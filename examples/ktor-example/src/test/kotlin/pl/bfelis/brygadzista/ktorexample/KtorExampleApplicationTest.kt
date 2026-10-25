package pl.bfelis.brygadzista.ktorexample

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorExampleApplicationTest {
    @Test
    fun `greets by name`() = testApplication {
        application { module() }

        val response = client.get("/greetings/Ada")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, Ada!", response.bodyAsText())
    }
}
