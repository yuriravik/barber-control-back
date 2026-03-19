package br.com.ravikyu.barbercontrol.cucumber.steps;

import br.com.ravikyu.barbercontrol.cucumber.ScenarioContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AuthSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Given("que estou autenticado como {string} com senha {string} e role {string}")
    public void queEstouAutenticado(String email, String senha, String role) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String registerUrl = "http://localhost:" + port + "/usuarios/cadastrar";
        String registerBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"%s\"}", email, senha, role);
        restTemplate.postForEntity(registerUrl, new HttpEntity<>(registerBody, headers), String.class);

        String loginUrl = "http://localhost:" + port + "/usuarios/login";
        String loginBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, new HttpEntity<>(loginBody, headers), String.class);

        JsonNode root = objectMapper.readTree(loginResponse.getBody());
        String token = root.get("token").asText();
        scenarioContext.setJwtToken(token);
    }

    @Given("que estou autenticado como barbeiro {string} com senha {string} vinculado ao admin com id {string}")
    public void queEstouAutenticadoComoBarbeiro(String email, String senha, String adminIdKey) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String adminId = scenarioContext.getId(adminIdKey).toString();

        String registerUrl = "http://localhost:" + port + "/usuarios/cadastrar";
        String registerBody = String.format(
                "{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"BARBEIRO\",\"adminId\":\"%s\"}",
                email, senha, adminId);
        restTemplate.postForEntity(registerUrl, new HttpEntity<>(registerBody, headers), String.class);

        String loginUrl = "http://localhost:" + port + "/usuarios/login";
        String loginBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, new HttpEntity<>(loginBody, headers), String.class);

        JsonNode root = objectMapper.readTree(loginResponse.getBody());
        String token = root.get("token").asText();
        scenarioContext.setJwtToken(token);
    }

    @Given("que estou autenticada como secretaria {string} com senha {string} vinculada ao admin com id {string}")
    public void queEstouAutenticadaComoSecretaria(String email, String senha, String adminIdKey) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String adminId = scenarioContext.getId(adminIdKey).toString();

        String registerUrl = "http://localhost:" + port + "/usuarios/cadastrar";
        String registerBody = String.format(
                "{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"SECRETARIA\",\"adminId\":\"%s\"}",
                email, senha, adminId);
        restTemplate.postForEntity(registerUrl, new HttpEntity<>(registerBody, headers), String.class);

        String loginUrl = "http://localhost:" + port + "/usuarios/login";
        String loginBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, new HttpEntity<>(loginBody, headers), String.class);

        JsonNode root = objectMapper.readTree(loginResponse.getBody());
        String token = root.get("token").asText();
        scenarioContext.setJwtToken(token);
    }
}
