package br.com.ravikyu.barbercontrol.cucumber.steps;

import br.com.ravikyu.barbercontrol.cucumber.ScenarioContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class CommonSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private ObjectMapper objectMapper;

    @After
    public void resetContext() {
        scenarioContext.reset();
    }

    @Then("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int expectedStatus) {
        assertEquals(expectedStatus, scenarioContext.getLastStatusCode(),
                "Status code mismatch. Response body: " + scenarioContext.getLastResponseBody());
    }

    @Then("o campo {string} da resposta deve ser {string}")
    public void oCampoDaRespostaDeveSer(String fieldName, String expectedValue) throws Exception {
        JsonNode root = objectMapper.readTree(scenarioContext.getLastResponseBody());
        JsonNode fieldNode = root.get(fieldName);
        assertNotNull(fieldNode, "Field '" + fieldName + "' not found in response: " + scenarioContext.getLastResponseBody());
        if (fieldNode.isBoolean()) {
            assertEquals(Boolean.parseBoolean(expectedValue), fieldNode.asBoolean(),
                    "Boolean field '" + fieldName + "' value mismatch");
        } else {
            assertEquals(expectedValue, fieldNode.asText(),
                    "Field '" + fieldName + "' value mismatch");
        }
    }

    @Then("a resposta deve ser uma lista")
    public void aRespostaDeveSerUmaLista() {
        String body = scenarioContext.getLastResponseBody();
        assertNotNull(body, "Response body is null");
        assertTrue(body.trim().startsWith("["), "Expected JSON array but got: " + body);
    }

    @Then("a lista de resposta deve ter tamanho {int}")
    public void aListaDeRespostaDeveTerTamanho(int tamanhoEsperado) throws Exception {
        JsonNode root = objectMapper.readTree(scenarioContext.getLastResponseBody());
        assertTrue(root.isArray(), "Expected JSON array but got: " + scenarioContext.getLastResponseBody());
        assertEquals(tamanhoEsperado, root.size(), "Unexpected list size");
    }

    @Then("a resposta deve conter um token JWT")
    public void aRespostaDeveConterUmTokenJWT() throws Exception {
        JsonNode root = objectMapper.readTree(scenarioContext.getLastResponseBody());
        JsonNode tokenNode = root.get("token");
        assertNotNull(tokenNode, "Field 'token' not found in response: " + scenarioContext.getLastResponseBody());
        assertFalse(tokenNode.asText().isBlank(), "JWT token is blank");
    }
}
