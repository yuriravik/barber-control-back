package br.com.ravikyu.barbercontrol.cucumber;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ScenarioContext {

    private String jwtToken;
    private int lastStatusCode;
    private String lastResponseBody;
    private UUID lastCreatedId;
    private final Map<String, UUID> ids = new HashMap<>();

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public void setLastStatusCode(int lastStatusCode) {
        this.lastStatusCode = lastStatusCode;
    }

    public String getLastResponseBody() {
        return lastResponseBody;
    }

    public void setLastResponseBody(String lastResponseBody) {
        this.lastResponseBody = lastResponseBody;
    }

    public UUID getLastCreatedId() {
        return lastCreatedId;
    }

    public void setLastCreatedId(UUID lastCreatedId) {
        this.lastCreatedId = lastCreatedId;
    }

    public void putId(String key, UUID id) {
        ids.put(key, id);
    }

    public UUID getId(String key) {
        return ids.get(key);
    }

    public void reset() {
        jwtToken = null;
        lastStatusCode = 0;
        lastResponseBody = null;
        lastCreatedId = null;
        ids.clear();
    }
}
