package roshin.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import roshin.model.Article;
import roshin.service.ArticleService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserInterfaceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArticleService articleService;

    private Util util;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        util = Util.setUp(port, articleService);
        headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin");
    }

    @Test
    void loadHomeTest() {
        String response = restTemplate.getForObject(util.buildUrl("/"), String.class);
        assertThat(response).contains("Roshin CMS");
    }

    @Test
    void loadLoginPageTest() {
        String response = restTemplate.getForObject(util.buildUrl("/login"), String.class);
        assertThat(response).contains("login");
        assertThat(response).contains("username");
        assertThat(response).contains("password");
    }

    @Test
    public void loadArticlesPageTest(){
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                util.buildUrl("/articles"),
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
                util.buildUrl("/articles/" + article.getId().toString()),
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
                util.buildUrl("/articles/" + article.getId().toString()),
                HttpMethod.POST,
                request,
                String.class
        );
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        ResponseEntity<String> updatedResponse = restTemplate.exchange(
                util.buildUrl("/articles/" + article.getId().toString()),
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
                util.buildUrl("/articles/" + article.getId().toString()),
                HttpMethod.DELETE,
                request,
                String.class
        );
        boolean isPresent = articleService.getByName("Article").isPresent();
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertFalse(isPresent);
    }

    @AfterEach
    void tearDown() {
        util.tearDown(articleService);
    }
}
