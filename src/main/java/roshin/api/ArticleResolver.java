package roshin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import roshin.model.Article;
import roshin.service.ArticleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (name.isEmpty()) {
            articles.addAll(service.getAll());
        } else {
            articles.add(service.getByName(name).orElse(null));
        }
        return articles;
    }
}
