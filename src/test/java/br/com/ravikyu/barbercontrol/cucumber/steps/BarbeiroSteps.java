package br.com.ravikyu.barbercontrol.cucumber.steps;

import br.com.ravikyu.barbercontrol.cucumber.ScenarioContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class BarbeiroSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders headersWithJwt() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(scenarioContext.getJwtToken());
        return headers;
    }

    @When("eu crio um barbeiro com nome {string}, especialidade {string} e percentual de comissão {double}")
    public void euCrioUmBarbeiro(String nome, String especialidade, double percentualComissao) {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, percentualComissao);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu crio um barbeiro com nome {string} e percentual de comissão {double} sem especialidade")
    public void euCrioUmBarbeiroSemEspecialidade(String nome, double percentualComissao) {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format("{\"nome\":\"%s\",\"percentualComissao\":%s}", nome, percentualComissao);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu crio um barbeiro sem nome com percentual de comissão {double}")
    public void euCrioUmBarbeiroSemNome(double percentualComissao) {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format("{\"percentualComissao\":%s}", percentualComissao);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @Given("que existe um barbeiro criado com nome {string}, especialidade {string} e comissão {double}")
    public void queExisteUmBarbeiroCriado(String nome, String especialidade, double comissao) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.setLastCreatedId(UUID.fromString(root.get("id").asText()));
    }

    @When("eu listo os barbeiros")
    public void euListoOsBarbeiros() {
        String url = "http://localhost:" + port + "/barbeiros";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu desativo o barbeiro pelo ID criado")
    public void euDesativoOBarbeiroPeloIdCriado() {
        String url = "http://localhost:" + port + "/barbeiros/" + scenarioContext.getLastCreatedId() + "/desativar";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }
}
