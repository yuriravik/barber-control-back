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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class UsuarioSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @When("eu cadastro um usuário com email {string}, senha {string} e role {string}")
    public void euCadastroUmUsuario(String email, String senha, String role) {
        String url = "http://localhost:" + port + "/usuarios/cadastrar";
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"%s\"}", email, senha, role);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @Given("que existe um usuário cadastrado com email {string} e senha {string} e role {string}")
    public void queExisteUmUsuarioCadastrado(String email, String senha, String role) {
        euCadastroUmUsuario(email, senha, role);
    }

    @Given("que existe um admin cadastrado com email {string} e senha {string} salvo como {string}")
    public void queExisteUmAdminCadastradoSalvoComoChave(String email, String senha, String chave) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Register admin (ignore if already exists)
        String registerUrl = "http://localhost:" + port + "/usuarios/cadastrar";
        String registerBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"ADMIN\"}", email, senha);
        restTemplate.postForEntity(registerUrl, new HttpEntity<>(registerBody, headers), String.class);

        // Login to get token
        String loginUrl = "http://localhost:" + port + "/usuarios/login";
        String loginBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, new HttpEntity<>(loginBody, headers), String.class);
        JsonNode loginRoot = objectMapper.readTree(loginResponse.getBody());
        String token = loginRoot.get("token").asText();

        // Fetch current user info to get admin ID
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);
        String meUrl = "http://localhost:" + port + "/usuarios/me";
        ResponseEntity<String> meResponse = restTemplate.exchange(meUrl, org.springframework.http.HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);
        JsonNode meRoot = objectMapper.readTree(meResponse.getBody());
        UUID adminId = UUID.fromString(meRoot.get("id").asText());
        scenarioContext.putId(chave, adminId);
    }

    @When("eu cadastro um barbeiro com email {string}, senha {string} e adminId {string}")
    public void euCadastroUmBarbeiroComAdminId(String email, String senha, String adminIdKey) {
        String url = "http://localhost:" + port + "/usuarios/cadastrar";
        String adminId = scenarioContext.getId(adminIdKey).toString();
        String body = String.format(
                "{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"BARBEIRO\",\"adminId\":\"%s\"}",
                email, senha, adminId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu cadastro um barbeiro com email {string}, senha {string} sem adminId")
    public void euCadastroUmBarbeiroSemAdminId(String email, String senha) {
        String url = "http://localhost:" + port + "/usuarios/cadastrar";
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"BARBEIRO\"}", email, senha);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu cadastro uma secretaria com email {string}, senha {string} e adminId {string}")
    public void euCadastroUmaSecretariaComAdminId(String email, String senha, String adminIdKey) {
        String url = "http://localhost:" + port + "/usuarios/cadastrar";
        String adminId = scenarioContext.getId(adminIdKey).toString();
        String body = String.format(
                "{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"SECRETARIA\",\"adminId\":\"%s\"}",
                email, senha, adminId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu cadastro uma secretaria com email {string}, senha {string} sem adminId")
    public void euCadastroUmaSecretariaSemAdminId(String email, String senha) {
        String url = "http://localhost:" + port + "/usuarios/cadastrar";
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"SECRETARIA\"}", email, senha);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }

    @When("eu faço login com email {string} e senha {string}")
    public void euFacoLogin(String email, String senha) {
        String url = "http://localhost:" + port + "/usuarios/login";
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        scenarioContext.setLastStatusCode(response.getStatusCode().value());
        scenarioContext.setLastResponseBody(response.getBody());
    }
}
