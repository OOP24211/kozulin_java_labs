package com.aiarbiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "ai.providers.worker-a.api-key=test",
        "ai.providers.worker-b.api-key=test",
        "ai.providers.judge.api-key=test",
        "spring.datasource.url=jdbc:postgresql://localhost:5432/cliai",
        "spring.datasource.username=cliai",
        "spring.datasource.password=cliai"
})
class AiArbiterApplicationTests {

    @Test
    void contextLoads() {
    }
}
