package roshin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import roshin.model.Article;
import roshin.service.ArticleService;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ArticleView {
    ArticleService articleService;

    @Autowired
    public ArticleView(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/articles")
    public String renderArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model view
    ) {
        view.addAttribute("articles", articleService.getPaginated(page, size));
        view.addAttribute("totalArticles", articleService.count());
        view.addAttribute("currentPage", page);
        view.addAttribute("totalPages", (int)(articleService.count() / size));
        view.addAttribute("pageSize", size);
        return "articles";
    }

    @GetMapping("/articles/{uuid}")
    public String renderArticle(@PathVariable("uuid") String uuid, Model view) {
        Optional<Article> article = articleService.getById(UUID.fromString(uuid));
        view.addAttribute(
                "article",
                article.orElseThrow()
        );
        return "article";
    }

    @PostMapping("/articles/{uuid}")
    public String acceptArticle(
            @PathVariable("uuid") String uuid,
            @ModelAttribute("article") Article article
    ) {
        assert article.getId().toString().equals(uuid);
        articleService.update(article);
        return "redirect:/articles/" + uuid;
    }

    @GetMapping("/articles/new")
    public String newArticle(Model view) {
        Article article = new Article();
        UUID uuid = articleService.save(article);
        return "redirect:/articles/" + uuid;
    }

    @DeleteMapping("/articles/{uuid}")
    public ResponseEntity<String> deleteArticle(
            @PathVariable("uuid") String uuid
    ) {
        articleService.deleteById(UUID.fromString(uuid));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
