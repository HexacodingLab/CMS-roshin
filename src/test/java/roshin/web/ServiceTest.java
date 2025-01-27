package roshin.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import roshin.model.Article;
import roshin.service.ArticleService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArticleService articleService;

    private Util util;

    @BeforeEach
    void setUp() {
        util = Util.setUp(port, articleService);
    }

    @Test
    void smokeTest() {
        String response = restTemplate.getForObject(util.buildUrl("/api/ping"), String.class);
        assertEquals("pong", response, "Expected response was 'pong'");
    }

    @Test
    void loginTest() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginRequest, headers);
        var response = restTemplate.exchange(util.buildUrl("/api/login"), HttpMethod.POST, entity, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void articleEntityTest(){
        Article article = new Article();
        assertNotNull(article.getId());

        String name = "Article";
        article.setName(name);
        assertEquals(name, article.getName());

        String payload = "This is my example test";
        article.setContent(payload);
        assertEquals(payload, article.getContent());
    }

    @Test
    void articleServiceTest() {
        Article article = articleService.getByName("Article").orElseThrow();
        article.setContent("This is my example test");
        UUID uuid = articleService.update(article);

        Optional<Article> retrieved = articleService.getByName("Article");
        assertTrue(retrieved.isPresent());
        assertEquals("Article", retrieved.get().getName());
        assertEquals(article.getContent(), retrieved.get().getContent());
        assertEquals(uuid, retrieved.get().getId());
    }

    @Test
    void articlePaginationTest() {
        List<Article> articles = articleService.getPaginated(0, 10);
        assertFalse(articles.isEmpty());
        var count = articleService.count();
        assertTrue(count > 0);
    }

    @AfterEach
    void tearDown() {
        util.tearDown(articleService);
    }
}
