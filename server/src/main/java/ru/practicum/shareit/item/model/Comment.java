package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    User author;

    @Column(nullable = false)
    String text;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        if (comment.id != null && id != null) {
            return id.equals(comment.id);
        }
        return Objects.equals(item, comment.item) &&
                Objects.equals(author, comment.author) &&
                Objects.equals(text, comment.text) &&
                Objects.equals(createdAt, comment.createdAt);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hash(item, author, text, createdAt);
    }
}
