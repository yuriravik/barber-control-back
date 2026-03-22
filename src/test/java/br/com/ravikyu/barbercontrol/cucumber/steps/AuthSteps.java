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

        // Use current admin JWT to create the barbeiro user via the new authenticated endpoint
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(scenarioContext.getJwtToken());

        String registerUrl = "http://localhost:" + port + "/usuarios/cadastrar-funcionario";
        String registerBody = String.format(
                "{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"BARBEIRO\"}",
                email, senha);
        restTemplate.postForEntity(registerUrl, new HttpEntity<>(registerBody, adminHeaders), String.class);

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

        // Use current admin JWT to create the secretaria user via the new authenticated endpoint
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(scenarioContext.getJwtToken());

        String registerUrl = "http://localhost:" + port + "/usuarios/cadastrar-funcionario";
        String registerBody = String.format(
                "{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"SECRETARIA\"}",
                email, senha);
        restTemplate.postForEntity(registerUrl, new HttpEntity<>(registerBody, adminHeaders), String.class);

        String loginUrl = "http://localhost:" + port + "/usuarios/login";
        String loginBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, new HttpEntity<>(loginBody, headers), String.class);

        JsonNode root = objectMapper.readTree(loginResponse.getBody());
        String token = root.get("token").asText();
        scenarioContext.setJwtToken(token);
    }

    @Given("que estou autenticado como barbeiro {string} com senha {string} vinculado ao barbeiro com id {string} e admin com id {string}")
    public void queEstouAutenticadoComoBarbeiroComPerfilId(String email, String senha, String barbeiroPerfilKey, String adminIdKey) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String barbeiroPerfilId = scenarioContext.getId(barbeiroPerfilKey).toString();

        // Use current admin JWT to create the barbeiro user linked to their barbeiro profile
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(scenarioContext.getJwtToken());

        String registerUrl = "http://localhost:" + port + "/usuarios/cadastrar-funcionario";
        String registerBody = String.format(
                "{\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"BARBEIRO\",\"barbeiroId\":\"%s\"}",
                email, senha, barbeiroPerfilId);
        restTemplate.postForEntity(registerUrl, new HttpEntity<>(registerBody, adminHeaders), String.class);

        String loginUrl = "http://localhost:" + port + "/usuarios/login";
        String loginBody = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, new HttpEntity<>(loginBody, headers), String.class);

        JsonNode root = objectMapper.readTree(loginResponse.getBody());
        String token = root.get("token").asText();
        scenarioContext.setJwtToken(token);
    }
}
