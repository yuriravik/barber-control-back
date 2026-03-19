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

public class ClienteSteps {

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

    @When("eu crio um cliente com nome {string}, email {string} e telefone {string}")
    public void euCrioUmCliente(String nome, String email, String telefone) {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\"}", nome, email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu crio um cliente sem nome, com email {string} e telefone {string}")
    public void euCrioUmClienteSemNome(String email, String telefone) {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"email\":\"%s\",\"telefone\":\"%s\"}", email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @Given("que existe um cliente criado com nome {string}, email {string} e telefone {string}")
    public void queExisteUmClienteCriado(String nome, String email, String telefone) throws Exception {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\"}", nome, email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.setLastCreatedId(UUID.fromString(root.get("id").asText()));
    }

    @When("eu listo os clientes")
    public void euListoOsClientes() {
        String url = "http://localhost:" + port + "/clientes";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o cliente pelo ID criado")
    public void euBuscoOClientePeloIdCriado() {
        String url = "http://localhost:" + port + "/clientes/" + scenarioContext.getLastCreatedId();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o cliente com ID {string}")
    public void euBuscoOClienteComId(String id) {
        String url = "http://localhost:" + port + "/clientes/" + id;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu deleto o cliente pelo ID criado")
    public void euDeletoOClientePeloIdCriado() {
        String url = "http://localhost:" + port + "/clientes/" + scenarioContext.getLastCreatedId();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }
}
