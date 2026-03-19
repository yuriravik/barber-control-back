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

public class AgendamentoSteps {

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

    @Given("que existe um cliente de agendamento com nome {string}, email {string} e telefone {string}")
    public void queExisteUmClienteDeAgendamento(String nome, String email, String telefone) throws Exception {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\"}", nome, email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("agendClienteId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um barbeiro de agendamento com nome {string}, especialidade {string} e comissão {double}")
    public void queExisteUmBarbeiroDeAgendamento(String nome, String especialidade, double comissao) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("agendBarbeiroId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um serviço de agendamento com nome {string}, preço {double} e duração {int} minutos")
    public void queExisteUmServicoDeAgendamento(String nome, double preco, int duracao) throws Exception {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("agendServicoId", UUID.fromString(root.get("id").asText()));
    }

    @When("eu crio um agendamento para o cliente, barbeiro e serviço criados com data {string}")
    public void euCrioUmAgendamento(String data) {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHoraInicio\":\"%s\"}",
                scenarioContext.getId("agendClienteId"),
                scenarioContext.getId("agendBarbeiroId"),
                scenarioContext.getId("agendServicoId"),
                data);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @Given("que existe um agendamento criado para o cliente, barbeiro e serviço em {string}")
    public void queExisteUmAgendamentoCriado(String data) throws Exception {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHoraInicio\":\"%s\"}",
                scenarioContext.getId("agendClienteId"),
                scenarioContext.getId("agendBarbeiroId"),
                scenarioContext.getId("agendServicoId"),
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.setLastCreatedId(UUID.fromString(root.get("id").asText()));
    }

    @When("eu listo os agendamentos")
    public void euListoOsAgendamentos() {
        String url = "http://localhost:" + port + "/agendamentos";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu busco o agendamento pelo ID criado")
    public void euBuscoOAgendamentoPeloIdCriado() {
        String url = "http://localhost:" + port + "/agendamentos/" + scenarioContext.getLastCreatedId();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu busco o agendamento com ID {string}")
    public void euBuscoOAgendamentoComId(String id) {
        String url = "http://localhost:" + port + "/agendamentos/" + id;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu deleto o agendamento pelo ID criado")
    public void euDeletoOAgendamentoPeloIdCriado() {
        String url = "http://localhost:" + port + "/agendamentos/" + scenarioContext.getLastCreatedId();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu crio um agendamento sem clienteId, com o barbeiro e serviço criados e data {string}")
    public void euCrioUmAgendamentoSemClienteId(String data) {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":null,\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHoraInicio\":\"%s\"}",
                scenarioContext.getId("agendBarbeiroId"),
                scenarioContext.getId("agendServicoId"),
                data);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu crio um agendamento para o cliente e barbeiro criados com servicoId {string} e data {string}")
    public void euCrioUmAgendamentoComServicoId(String servicoId, String data) {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHoraInicio\":\"%s\"}",
                scenarioContext.getId("agendClienteId"),
                scenarioContext.getId("agendBarbeiroId"),
                servicoId,
                data);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }
}
