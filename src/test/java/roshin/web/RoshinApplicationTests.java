package roshin.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import roshin.SecurityConfig;
import roshin.model.Article;
import roshin.service.ArticleService;


import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin");

        Article article = new Article();
        String name = "Article";
        article.setName(name);
        article.setContent("This is my example test");

        if (articleService.getByName("Article").isEmpty()) {
            articleService.save(article);
            articleService.update(article);
        }
    }

    @Test
    void smokeTest() {
        String response = restTemplate.getForObject(buildUrl("/api/ping"), String.class);
        assertEquals("pong", response, "Expected response was 'pong'");
    }

    @Test
    void loadHomeTest() {
        String response = restTemplate.getForObject(buildUrl("/"), String.class);
        assertThat(response).contains("Roshin CMS");
    }

    @Test
    void loadLoginPageTest() {
        String response = restTemplate.getForObject(buildUrl("/login"), String.class);
        assertThat(response).contains("login");
        assertThat(response).contains("username");
        assertThat(response).contains("password");
    }

    @Test
    void loginTest() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginRequest, headers);
        var response = restTemplate.exchange(buildUrl("/api/login"), HttpMethod.POST, entity, Map.class);

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

    @Test
    void shouldGetArticlesFromGraphQL() {
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
    public void loadArticlesPageTest(){
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                buildUrl("/articles"),
                HttpMethod.GET,
                request,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("Article");
    }

    @Test
    public void loadArticleEditPageTest(){
        Article article = articleService.getByName("Article").orElseThrow();
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                buildUrl("/articles/" + article.getId().toString()),
                HttpMethod.GET,
                request,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("Article");
    }

    @Test
    public void updateArticleWithFormTest(){
        Article article = articleService.getByName("Article").orElseThrow();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id", article.getId().toString());
        formData.add("name", "Article Changed");
        formData.add("content", "This is my example test changed");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
                formData,
                headers
        );
        ResponseEntity<String> response = restTemplate.exchange(
                buildUrl("/articles/" + article.getId().toString()),
                HttpMethod.POST,
                request,
                String.class
        );
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        ResponseEntity<String> updatedResponse = restTemplate.exchange(
                buildUrl("/articles/" + article.getId().toString()),
                HttpMethod.GET,
                request,
                String.class
        );
        assertThat(updatedResponse.getBody())
                .contains("This is my example test changed")
                .contains("Article Changed");
    }

    @Test
    public void deleteArticleTest(){
        Article article = articleService.getByName("Article").orElseThrow();
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                buildUrl("/articles/" + article.getId().toString()),
                HttpMethod.DELETE,
                request,
                String.class
        );
        boolean isPresent = articleService.getByName("Article").isPresent();
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertFalse(isPresent);
    }

    String buildUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @AfterEach
    void tearDown() {
        articleService.deleteByName("Article");
        articleService.deleteByName("Article Changed");
    }
}


