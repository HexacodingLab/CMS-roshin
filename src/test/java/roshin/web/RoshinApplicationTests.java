package roshin.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoshinApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void smokeTest() {
        String response = restTemplate.getForObject(buildUrl("/ping"), String.class);
        assertEquals("pong", response, "Expected response was 'pong'");
    }

    @Test
    void loginTest() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "roshin");
        loginRequest.put("password", "mySecretPassword123");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginRequest, headers);
        var response = restTemplate.exchange(buildUrl("/login"), HttpMethod.POST, entity, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    String buildUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
