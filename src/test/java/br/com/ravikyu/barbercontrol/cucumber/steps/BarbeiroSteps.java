package br.com.ravikyu.barbercontrol.cucumber.steps;

import br.com.ravikyu.barbercontrol.cucumber.ScenarioContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import java.util.UUID;

public class BarbeiroSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headersWithJwt() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(scenarioContext.getJwtToken());
        return headers;
    }

    @When("eu crio um barbeiro com nome {string}, especialidade {string} e percentual de comissão {int}")
    public void euCrioUmBarbeiro(String nome, String especialidade, int percentualComissao) {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, percentualComissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu crio um barbeiro com nome {string} e percentual de comissão {int} sem especialidade")
    public void euCrioUmBarbeiroSemEspecialidade(String nome, int percentualComissao) {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format("{\"nome\":\"%s\",\"percentualComissao\":%s}", nome, percentualComissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu crio um barbeiro sem nome com percentual de comissão {int}")
    public void euCrioUmBarbeiroSemNome(int percentualComissao) {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format("{\"percentualComissao\":%s}", percentualComissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @Given("que existe um barbeiro criado com nome {string}, especialidade {string} e comissão {int}")
    public void queExisteUmBarbeiroCriado(String nome, String especialidade, int comissao) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.setLastCreatedId(UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um barbeiro criado com nome {string}, especialidade {string} e comissão {int} salvo como {string}")
    public void queExisteUmBarbeiroCriadoSalvoComChave(String nome, String especialidade, int comissao, String chave) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        UUID barbeiroId = UUID.fromString(root.get("id").asText());
        scenarioContext.setLastCreatedId(barbeiroId);
        scenarioContext.putId(chave, barbeiroId);
    }

    @When("eu listo os barbeiros")
    public void euListoOsBarbeiros() {
        String url = "http://localhost:" + port + "/barbeiros";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu desativo o barbeiro pelo ID criado")
    public void euDesativoOBarbeiroPeloIdCriado() {
        String url = "http://localhost:" + port + "/barbeiros/" + scenarioContext.getLastCreatedId() + "/desativar";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }
}
