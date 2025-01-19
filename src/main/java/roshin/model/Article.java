package roshin.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.UUID;

@Entity
public class Article implements Serializable {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();;

    @Column(unique = true)
    private String name;

    private String content;

    // I hate this. This is stupid and it should be berried deep.
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

