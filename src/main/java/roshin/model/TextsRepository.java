package roshin.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TextsRepository extends JpaRepository<Article, UUID> {
    Optional<Article> findByName(String name);
    void deleteByName(String name);
}
