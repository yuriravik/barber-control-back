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

public class PagamentoSteps {

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

    @Given("que existe um cliente de pagamento com nome {string}, email {string} e telefone {string}")
    public void queExisteUmClienteDePagamento(String nome, String email, String telefone) throws Exception {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\"}", nome, email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoClienteId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um barbeiro de pagamento com nome {string}, especialidade {string} e comissão {double}")
    public void queExisteUmBarbeiroDePagamento(String nome, String especialidade, double comissao) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoBarbeiroId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um serviço de pagamento com nome {string}, preço {double} e duração {int} minutos")
    public void queExisteUmServicoDePagamento(String nome, double preco, int duracao) throws Exception {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoServicoId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em {string}")
    public void queExisteUmAgendamentoDePagamento(String data) throws Exception {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHoraInicio\":\"%s\"}",
                scenarioContext.getId("pgtoClienteId"),
                scenarioContext.getId("pgtoBarbeiroId"),
                scenarioContext.getId("pgtoServicoId"),
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoAgendId", UUID.fromString(root.get("id").asText()));
    }

    @When("eu registro um pagamento para o agendamento criado com valor {double} e forma de pagamento {string}")
    public void euRegistroUmPagamento(double valor, String formaPagamento) {
        String url = "http://localhost:" + port + "/pagamentos";
        String body = String.format(
                "{\"agendamentoId\":\"%s\",\"valor\":%s,\"formaPagamento\":\"%s\"}",
                scenarioContext.getId("pgtoAgendId"),
                valor,
                formaPagamento);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @Given("que já existe um pagamento registrado para o agendamento criado com valor {double} e forma {string}")
    public void queJaExisteUmPagamentoRegistrado(double valor, String formaPagamento) throws Exception {
        String url = "http://localhost:" + port + "/pagamentos";
        String body = String.format(
                "{\"agendamentoId\":\"%s\",\"valor\":%s,\"formaPagamento\":\"%s\"}",
                scenarioContext.getId("pgtoAgendId"),
                valor,
                formaPagamento);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoId", UUID.fromString(root.get("id").asText()));
    }

    @When("eu registro um pagamento para o agendamentoId {string} com valor {double} e forma de pagamento {string}")
    public void euRegistroUmPagamentoParaAgendamentoId(String agendamentoId, double valor, String formaPagamento) {
        String url = "http://localhost:" + port + "/pagamentos";
        String body = String.format(
                "{\"agendamentoId\":\"%s\",\"valor\":%s,\"formaPagamento\":\"%s\"}",
                agendamentoId,
                valor,
                formaPagamento);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu listo os pagamentos")
    public void euListoOsPagamentos() {
        String url = "http://localhost:" + port + "/pagamentos";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu busco o pagamento pelo ID criado")
    public void euBuscoOPagamentoPeloIdCriado() {
        String url = "http://localhost:" + port + "/pagamentos/" + scenarioContext.getId("pgtoId");
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @When("eu busco o pagamento com ID {string}")
    public void euBuscoOPagamentoComId(String id) {
        String url = "http://localhost:" + port + "/pagamentos/" + id;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }
}
