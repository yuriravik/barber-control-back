package br.com.ravikyu.barbercontrol.cucumber.steps;

import br.com.ravikyu.barbercontrol.cucumber.ScenarioContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
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

import static org.junit.jupiter.api.Assertions.*;

public class RelatorioSteps {

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

    @Given("que existe um cliente de relatório com nome {string}, email {string} e telefone {string}")
    public void queExisteUmClienteDeRelatorio(String nome, String email, String telefone) throws Exception {
        String url = "http://localhost:" + port + "/clientes";
        String body = String.format("{\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\"}", nome, email, telefone);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("relClienteId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um barbeiro de relatório com nome {string}, especialidade {string} e comissão {int}")
    public void queExisteUmBarbeiroDeRelatorio(String nome, String especialidade, int comissao) throws Exception {
        String url = "http://localhost:" + port + "/barbeiros";
        String body = String.format(
                "{\"nome\":\"%s\",\"especialidade\":\"%s\",\"percentualComissao\":%s}",
                nome, especialidade, comissao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("relBarbeiroId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um serviço de relatório com nome {string}, preço {int} e duração {int} minutos")
    public void queExisteUmServicoDeRelatorio(String nome, int preco, int duracao) throws Exception {
        String url = "http://localhost:" + port + "/servicos";
        String body = String.format("{\"nome\":\"%s\",\"descricao\":\"\",\"preco\":%s,\"duracaoMinutos\":%d}", nome, preco, duracao);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("relServicoId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um agendamento de relatório criado para o cliente, barbeiro e serviço em {string}")
    public void queExisteUmAgendamentoDeRelatorio(String data) throws Exception {
        String url = "http://localhost:" + port + "/agendamentos";
        String body = String.format(
                "{\"clienteId\":\"%s\",\"barbeiroId\":\"%s\",\"servicoId\":\"%s\",\"dataHora\":\"%s\"}",
                scenarioContext.getId("relClienteId"),
                scenarioContext.getId("relBarbeiroId"),
                scenarioContext.getId("relServicoId"),
                data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        scenarioContext.putId("relAgendId", UUID.fromString(root.get("id").asText()));
    }

    @Given("que existe um pagamento de relatório para o agendamento criado com valor {int} e forma {string}")
    public void queExisteUmPagamentoDeRelatorio(int valor, String formaPagamento) throws Exception {
        String url = "http://localhost:" + port + "/pagamentos";
        String body = String.format(
                "{\"agendamentoId\":\"%s\",\"valor\":%s,\"formaPagamento\":\"%s\"}",
                scenarioContext.getId("relAgendId"),
                valor,
                formaPagamento);
        restTemplate.postForEntity(url, new HttpEntity<>(body, headersWithJwt()), String.class);
    }

    @When("eu gero o relatório de agendamentos sem filtros")
    public void euGeroORelatorioDeAgendamentosSemFiltros() {
        String url = "http://localhost:" + port + "/relatorios/agendamentos";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu gero o relatório de agendamentos com dataInicio {string} e dataFim {string}")
    public void euGeroORelatorioDeAgendamentosComFiltrosDeDatas(String dataInicio, String dataFim) {
        String url = "http://localhost:" + port + "/relatorios/agendamentos?dataInicio=" + dataInicio + "&dataFim=" + dataFim;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu gero o relatório financeiro sem filtros")
    public void euGeroORelatorioFinanceiroSemFiltros() {
        String url = "http://localhost:" + port + "/relatorios/financeiro";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu gero o relatório financeiro com dataInicio {string} e dataFim {string}")
    public void euGeroORelatorioFinanceiroComFiltrosDeDatas(String dataInicio, String dataFim) {
        String url = "http://localhost:" + port + "/relatorios/financeiro?dataInicio=" + dataInicio + "&dataFim=" + dataFim;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headersWithJwt()), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @Then("o campo {string} da resposta deve ser maior que {int}")
    public void oCampoDaRespostaDeveSerMaiorQue(String campo, int valor) throws Exception {
        JsonNode root = objectMapper.readTree(scenarioContext.getLastResponseBody());
        JsonNode fieldNode = root.get(campo);
        assertNotNull(fieldNode, "Campo '" + campo + "' não encontrado na resposta: " + scenarioContext.getLastResponseBody());
        assertTrue(fieldNode.asInt() > valor,
                "Campo '" + campo + "' deveria ser maior que " + valor + " mas foi: " + fieldNode.asInt());
    }

    @Then("a resposta deve conter o campo {string}")
    public void aRespostaDeveConterOCampo(String campo) throws Exception {
        JsonNode root = objectMapper.readTree(scenarioContext.getLastResponseBody());
        assertNotNull(root.get(campo),
                "Campo '" + campo + "' não encontrado na resposta: " + scenarioContext.getLastResponseBody());
    }
}
