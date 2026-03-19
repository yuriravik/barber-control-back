package br.com.ravikyu.barbercontrol.cucumber.steps;

import br.com.ravikyu.barbercontrol.cucumber.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class UsuarioSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @When("eu cadastro um usuário com email {string}, senha {string} e role {string}")
    public void euCadastroUmUsuario(String email, String senha, String role) {
        String url = "http://localhost:" + port + "/usuarios/cadastrar";
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"%s\"}", email, senha, role);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }

    @Given("que existe um usuário cadastrado com email {string} e senha {string} e role {string}")
    public void queExisteUmUsuarioCadastrado(String email, String senha, String role) {
        euCadastroUmUsuario(email, senha, role);
    }

    @When("eu faço login com email {string} e senha {string}")
    public void euFacoLogin(String email, String senha) {
        String url = "http://localhost:" + port + "/usuarios/login";
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            scenarioContext.setLastStatusCode(response.getStatusCode().value());
            scenarioContext.setLastResponseBody(response.getBody());
        } catch (HttpStatusCodeException e) {
            scenarioContext.setLastStatusCode(e.getStatusCode().value());
            scenarioContext.setLastResponseBody(e.getResponseBodyAsString());
        }
    }
}
