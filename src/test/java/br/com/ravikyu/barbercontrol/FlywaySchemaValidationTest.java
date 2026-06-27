package br.com.ravikyu.barbercontrol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("migration")
class FlywaySchemaValidationTest {

    @Test
    void contextoComFlywayDeveSubir() {
    }
}
