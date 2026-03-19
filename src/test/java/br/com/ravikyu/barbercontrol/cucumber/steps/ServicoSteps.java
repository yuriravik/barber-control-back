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

public class ServicoSteps {

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

    @When("eu crio um serviço com nome {string}, descrição {string}, preço {double} e duração {int} minutos")
    public void euCrioUmServico(String nome, String descricao, double preco, int duracao) {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format(
                "{\"nome\":\"%s\",\"descricao\":\"%s\",\"preco\":%s,\"duracaoMinutos\":%d}",
                nome, descricao, preco, duracao);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu crio um serviço com nome {string}, preço {double} e duração {int} minutos sem descrição")
    public void euCrioUmServicoSemDescricao(String nome, double preco, int duracao) {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu crio um serviço sem nome com preço {double} e duração {int} minutos")
    public void euCrioUmServicoSemNome(double preco, int duracao) {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"preco\":%s,\"duracaoMinutos\":%d}", preco, duracao);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @Given("que existe um serviço criado com nome {string}, preço {double} e duração {int} minutos")
    public void queExisteUmServicoCriado(String nome, double preco, int duracao) throws Exception {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.setLastCreatedId(UUID.fromString(root.get("id").asText()));
    }

    @When("eu listo os serviços")
    public void euListoOsServicos() {
        String url = "http://localhost:" + port + "/servicos";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu busco o serviço pelo ID criado")
    public void euBuscoOServicoPeloIdCriado() {
        String url = "http://localhost:" + port + "/servicos/" + scenarioContext.getLastCreatedId();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu busco o serviço com ID {string}")
    public void euBuscoOServicoComId(String id) {
        String url = "http://localhost:" + port + "/servicos/" + id;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu deleto o serviço pelo ID criado")
    public void euDeletoOServicoPeloIdCriado() {
        String url = "http://localhost:" + port + "/servicos/" + scenarioContext.getLastCreatedId();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }
}
