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

public class ServicoSteps {

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

    @When("eu crio um serviço com nome {string}, descrição {string}, preço {int} e duração {int} minutos")
    public void euCrioUmServico(String nome, String descricao, int preco, int duracao) {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format(
                "{\"nome\":\"%s\",\"descricao\":\"%s\",\"preco\":%s,\"duracaoMinutos\":%d}",
                nome, descricao, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu crio um serviço com nome {string}, preço {int} e duração {int} minutos sem descrição")
    public void euCrioUmServicoSemDescricao(String nome, int preco, int duracao) {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"descricao\":\"\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu crio um serviço sem nome com preço {int} e duração {int} minutos")
    public void euCrioUmServicoSemNome(int preco, int duracao) {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"preco\":%s,\"duracaoMinutos\":%d}", preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @Given("que existe um serviço criado com nome {string}, preço {int} e duração {int} minutos")
    public void queExisteUmServicoCriado(String nome, int preco, int duracao) throws Exception {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"descricao\":\"\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.setLastCreatedId(UUID.fromString(root.get("id").asText()));
    }

    @When("eu listo os serviços")
    public void euListoOsServicos() {
        String url = "http://localhost:" + port + "/servicos";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o serviço pelo ID criado")
    public void euBuscoOServicoPeloIdCriado() {
        String url = "http://localhost:" + port + "/servicos/" + scenarioContext.getLastCreatedId();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o serviço com ID {string}")
    public void euBuscoOServicoComId(String id) {
        String url = "http://localhost:" + port + "/servicos/" + id;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu deleto o serviço pelo ID criado")
    public void euDeletoOServicoPeloIdCriado() {
        String url = "http://localhost:" + port + "/servicos/" + scenarioContext.getLastCreatedId();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }
}
