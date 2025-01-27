package roshin.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import roshin.model.Article;
import roshin.service.ArticleService;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private GraphQlTester graphQlTester;

    private GraphQlTester authGraphQlTester;

    private Util util;

    @BeforeEach
    void setUp() {
        util = Util.setUp(port, articleService);

        WebTestClient.Builder webTestClientBuilder = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port + "/api/graphql");

        authGraphQlTester = HttpGraphQlTester
                .builder(webTestClientBuilder)
                .headers(
                    (headers) -> headers.setBasicAuth("admin", "admin")
                )
                .build();
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
    void shouldGetArticles() {
        Article article = articleService.getByName("Article").orElseThrow();

        this.graphQlTester
                .documentName("Article")
                .variable("name", "Article")
                .execute()
                .path("getArticles")
                .matchesJson(String.format("""
                    [{
                        "id": "%s",
                        "name": "Article",
                        "content": "This is my example test"
                    }]
                """, article.getId()));
    }

    @Test
    void shouldCreateNewArticle() {
        this.authGraphQlTester
                .documentName("createArticle")
                .execute()
                .path("createArticle")
                .matchesJson("""
                    {
                        "name": "Article2",
                        "content": "This is my example test2"
                    }
                """);
    }

    @Test
    void shouldDeleteArticle() {
        String id = articleService
                .getByName("Article")
                .orElseThrow(() -> new RuntimeException("Article not found"))
                .getId()
                .toString();
        this.authGraphQlTester
                .documentName("deleteArticle")
                .variable("id", id)
                .execute()
                .path("deleteArticle")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void shouldNotDeleteNotAuth() {
        String id = articleService
                .getByName("Article")
                .orElseThrow(() -> new RuntimeException("Article not found"))
                .getId()
                .toString();
        this.graphQlTester
                .documentName("deleteArticle")
                .variable("id", id)
                .execute()
                .errors()
                .expect(e ->
                    Objects.requireNonNull(e.getMessage()).contains("Unauthorized")
                );
    }

    @AfterEach
    void tearDown() {
        util.tearDown(articleService);
        articleService.deleteByName("Article2");
    }
}


