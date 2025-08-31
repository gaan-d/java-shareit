package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    Long id;

    @Column(nullable = false)
    String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "requester_id")
    User requester;

    @Column(nullable = false)
    LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        if (request.id != null && id != null) {
            return id.equals(request.id);
        }
        return Objects.equals(description, request.description) &&
                Objects.equals(requester, request.requester);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hash(description, requester);
    }
}
