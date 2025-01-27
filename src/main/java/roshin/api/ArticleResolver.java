package roshin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import roshin.model.Article;
import roshin.service.ArticleService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Article createArticle(@Argument Article article) {
        UUID uuid = service.save(article);
        return service.getById(uuid).orElseThrow();
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deleteArticle(@Argument String id) {
        service.deleteById(UUID.fromString(id));
        return true;
    }
}
