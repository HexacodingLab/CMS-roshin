package roshin.web;

import roshin.model.Article;
import roshin.service.ArticleService;


public class Util {
    private final int port;

    public Util(int port) {
        this.port = port;
    }

    public String buildUrl(String path) {
        return "http://localhost:" + port + path;
    }

    static Util setUp(int port, ArticleService articleService) {
        Article article = new Article();
        String name = "Article";
        article.setName(name);
        article.setContent("This is my example test");

        if (articleService.getByName("Article").isEmpty()) {
            articleService.save(article);
            articleService.update(article);
        }

        return new Util(port);
    }

    public void tearDown(ArticleService articleService) {
        articleService.deleteByName("Article");
        articleService.deleteByName("Article Changed");
    }
}
