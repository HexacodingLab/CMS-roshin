package roshin.service;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import roshin.model.Article;
import roshin.model.ArticleRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class ArticleService {
    private final ArticleRepository repository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.repository = articleRepository;
    }

    public UUID save(Article article) {
        return update(article);
    }

    public UUID update(Article article) {
        Optional<Article> oldArticle = repository.findById(article.getId());
        if (oldArticle.isPresent()) {
            oldArticle.get().setName(article.getName());
            oldArticle.get().setContent(article.getContent());
            var save = repository.save(oldArticle.get());
            return save.getId();
        } else {
            var save = repository.save(article);
            return save.getId();
        }
    }

    public Optional<Article> getById(UUID guid) {
        return repository.findById(guid);
    }

    public Optional<Article> getByName(String name) {
        return repository.findByName(name);
    }

    public List<Article> getPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable).getContent();
    }

    public List<Article> getAll() {
        return repository.findAll();
    }

    public long count() {
        return repository.count();
    }

    public void deleteByName(String name) {
        repository.findByName(name).ifPresent(repository::delete);
    }

    public void deleteById(UUID guid) {
        repository.findById(guid).ifPresent(repository::delete);
    }
}
