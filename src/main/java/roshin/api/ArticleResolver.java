package roshin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import roshin.model.Article;
import roshin.service.ArticleService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleResolver {

    private final ArticleService service;

    @Autowired
    public ArticleResolver(ArticleService service) {
        this.service = service;
    }

    @QueryMapping
    public List<Article> getArticles(@Argument String name) {
        List<Article> articles = new ArrayList<>();
        if (name == null) {
            articles.addAll(service.getAll());
        } else if (service.getByName(name).isPresent()) {
            articles.add(service.getByName(name).get());
        }
        return articles;
    }
}
