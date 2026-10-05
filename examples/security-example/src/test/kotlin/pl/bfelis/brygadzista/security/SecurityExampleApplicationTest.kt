package pl.bfelis.brygadzista.security

import java.nio.charset.StandardCharsets
import java.util.Base64
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SecurityExampleApplicationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `action markers enforce public authenticated and admin access`() {
        mockMvc.perform(get("/public"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Public action"))

        mockMvc.perform(get("/authorized"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(get("/authorized").header(HttpHeaders.AUTHORIZATION, basic("user", "user")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("user"))
            .andExpect(jsonPath("$.role").value("USER"))

        mockMvc.perform(get("/admin").header(HttpHeaders.AUTHORIZATION, basic("user", "user")))
            .andExpect(status().isForbidden)

        mockMvc.perform(get("/admin").header(HttpHeaders.AUTHORIZATION, basic("admin", "admin")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
    }

    private fun basic(username: String, password: String): String {
        val credentials = "$username:$password".toByteArray(StandardCharsets.UTF_8)
        return "Basic ${Base64.getEncoder().encodeToString(credentials)}"
    }
}
