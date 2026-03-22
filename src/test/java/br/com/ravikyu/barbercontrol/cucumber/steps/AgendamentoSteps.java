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

public class AgendamentoSteps {

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

    @Given("que existe um cliente de agendamento com nome {string}, email {string} e telefone {string}")
    public void queExisteUmClienteDeAgendamento(String nome, String email, String telefone) throws Exception {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\"}", nome, email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("agendClienteId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um barbeiro de agendamento com nome {string}, especialidade {string} e comissão {int}")
    public void queExisteUmBarbeiroDeAgendamento(String nome, String especialidade, int comissao) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("agendBarbeiroId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um serviço de agendamento com nome {string}, preço {int} e duração {int} minutos")
    public void queExisteUmServicoDeAgendamento(String nome, int preco, int duracao) throws Exception {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"descricao\":\"\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("agendServicoId", UUID.fromString(root.get("id").asText()));
    }

    @When("eu crio um agendamento para o cliente, barbeiro e serviço criados com data {string}")
    public void euCrioUmAgendamento(String data) {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHora\":\"%s\"}",
                scenarioContext.getId("agendClienteId"),
                scenarioContext.getId("agendBarbeiroId"),
                scenarioContext.getId("agendServicoId"),
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @Given("que existe um agendamento criado para o cliente, barbeiro e serviço em {string}")
    public void queExisteUmAgendamentoCriado(String data) throws Exception {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHora\":\"%s\"}",
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
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o agendamento pelo ID criado")
    public void euBuscoOAgendamentoPeloIdCriado() {
        String url = "http://localhost:" + port + "/agendamentos/" + scenarioContext.getLastCreatedId();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o agendamento com ID {string}")
    public void euBuscoOAgendamentoComId(String id) {
        String url = "http://localhost:" + port + "/agendamentos/" + id;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu deleto o agendamento pelo ID criado")
    public void euDeletoOAgendamentoPeloIdCriado() {
        String url = "http://localhost:" + port + "/agendamentos/" + scenarioContext.getLastCreatedId();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @Given("que existe um agendamento para o barbeiro {string} criado para o cliente e serviço de agendamento em {string}")
    public void queExisteUmAgendamentoParaBarbeiroEspecifico(String barbeiroPerfilKey, String data) throws Exception {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHora\":\"%s\"}",
                scenarioContext.getId("agendClienteId"),
                scenarioContext.getId(barbeiroPerfilKey),
                scenarioContext.getId("agendServicoId"),
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.setLastCreatedId(UUID.fromString(root.get("id").asText()));
    }

    @When("eu tento criar um agendamento sem dados válidos")
    public void euTentoCriarUmAgendamentoSemDadosValidos() {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = "{}";
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu crio um agendamento sem clienteId, com o barbeiro e serviço criados e data {string}")
    public void euCrioUmAgendamentoSemClienteId(String data) {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":null,\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHora\":\"%s\"}",
                scenarioContext.getId("agendBarbeiroId"),
                scenarioContext.getId("agendServicoId"),
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu crio um agendamento para o cliente e barbeiro criados com servicoId {string} e data {string}")
    public void euCrioUmAgendamentoComServicoId(String servicoId, String data) {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHora\":\"%s\"}",
                scenarioContext.getId("agendClienteId"),
                scenarioContext.getId("agendBarbeiroId"),
                servicoId,
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }
}
