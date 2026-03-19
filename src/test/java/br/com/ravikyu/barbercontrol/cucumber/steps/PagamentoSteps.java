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

public class PagamentoSteps {

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

    @Given("que existe um cliente de pagamento com nome {string}, email {string} e telefone {string}")
    public void queExisteUmClienteDePagamento(String nome, String email, String telefone) throws Exception {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\"}", nome, email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoClienteId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um barbeiro de pagamento com nome {string}, especialidade {string} e comissão {int}")
    public void queExisteUmBarbeiroDePagamento(String nome, String especialidade, int comissao) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoBarbeiroId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um serviço de pagamento com nome {string}, preço {int} e duração {int} minutos")
    public void queExisteUmServicoDePagamento(String nome, int preco, int duracao) throws Exception {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"descricao\":\"\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoServicoId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um agendamento de pagamento criado para o cliente, barbeiro e serviço em {string}")
    public void queExisteUmAgendamentoDePagamento(String data) throws Exception {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHora\":\"%s\"}",
                scenarioContext.getId("pgtoClienteId"),
                scenarioContext.getId("pgtoBarbeiroId"),
                scenarioContext.getId("pgtoServicoId"),
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("pgtoAgendId", UUID.fromString(root.get("id").asText()));
    }

    @When("eu registro um pagamento para o agendamento criado com valor {int} e forma de pagamento {string}")
    public void euRegistroUmPagamento(int valor, String formaPagamento) {
        String url = "http://localhost:" + port + "/pagamentos";
        String body = String.format(
                "{\"agendamentoId\":\"%s\",\"valor\":%s,\"formaPagamento\":\"%s\"}",
                scenarioContext.getId("pgtoAgendId"),
                valor,
                formaPagamento);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @Given("que já existe um pagamento registrado para o agendamento criado com valor {int} e forma {string}")
    public void queJaExisteUmPagamentoRegistrado(int valor, String formaPagamento) throws Exception {
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

    @When("eu registro um pagamento para o agendamentoId {string} com valor {int} e forma de pagamento {string}")
    public void euRegistroUmPagamentoParaAgendamentoId(String agendamentoId, int valor, String formaPagamento) {
        String url = "http://localhost:" + port + "/pagamentos";
        String body = String.format(
                "{\"agendamentoId\":\"%s\",\"valor\":%s,\"formaPagamento\":\"%s\"}",
                agendamentoId,
                valor,
                formaPagamento);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu listo os pagamentos")
    public void euListoOsPagamentos() {
        String url = "http://localhost:" + port + "/pagamentos";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o pagamento pelo ID criado")
    public void euBuscoOPagamentoPeloIdCriado() {
        String url = "http://localhost:" + port + "/pagamentos/" + scenarioContext.getId("pgtoId");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu busco o pagamento com ID {string}")
    public void euBuscoOPagamentoComId(String id) {
        String url = "http://localhost:" + port + "/pagamentos/" + id;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
    }
}
