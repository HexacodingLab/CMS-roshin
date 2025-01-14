package roshin.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.*;
import roshin.model.Article;
import roshin.service.ArticleService;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoshinApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private GraphQlTester graphQlTester;

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
    void articleServiceTest(){
        Article article = new Article();
        String name = "Article";
        article.setName(name);

        articleService.save(article);
        article.setContent("This is my example test");
        articleService.update(article);

        Optional<Article> retrieved = articleService.getByName(name);
        assertTrue(retrieved.isPresent());
        assertEquals(name, retrieved.get().getName());
        assertEquals(article.getContent(), retrieved.get().getContent());
    }

    @Test
    void shouldGetArticlesFromGraphQL(){
        Article article = new Article();
        article.setName("Article");
        articleService.save(article);
        article = articleService.getByName(article.getName()).orElseThrow();

        this.graphQlTester
                .documentName("Article")
                .variable("name", "Article")
                .execute()
                .path("getArticles")
                .matchesJson(String.format("""
                    [{
                        "id": "%s",
                        "name": "Article",
                        "content": null
                    }]
                """, article.getId()));
    }

    String buildUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
