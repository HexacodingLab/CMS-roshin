package roshin.service;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import roshin.model.Article;
import roshin.model.TextsRepository;

import java.util.List;
import java.util.Optional;


@Service
public class ArticleService {
    private final TextsRepository repository;

    @Autowired
    public ArticleService(TextsRepository textsRepository) {
        this.repository = textsRepository;
    }

    public void save(Article article) {
        update(article);
    }

    public void update(Article article) {
        Optional<Article> oldArticle = repository.findByName(article.getName());
        if (oldArticle.isPresent()) {
            oldArticle.get().setName(article.getName());
            oldArticle.get().setContent(article.getContent());
            repository.save(oldArticle.get());
        } else {
            repository.save(article);
        }
    }

    public Optional<Article> getByName(String name) {
        return repository.findByName(name);
    }

    public List<Article> getAll() {
        return repository.findAll();
    }

    public void deleteByName(String name) {
        repository.findByName(name).ifPresent(repository::delete);
    }
}
