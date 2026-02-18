package io.home.app.corckboard;

import io.home.app.corckboard.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void healthEndpointShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk());
    }

    @Test
    void helloShouldReturn401WhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/v1/hello"))
            .andExpect(status().isUnauthorized());
    }
}
