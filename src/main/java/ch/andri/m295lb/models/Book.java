package ch.andri.m295lb.models;

import ch.andri.m295lb.models.Author;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "book")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookID;
    @Length(max = 255)
    private String title;

    @Length(min = 1)
    private Integer pages;

    @Column(name = "publicationDate")
    @PastOrPresent(message = "Publication date must be in the past or present.")
    private LocalDateTime publicationDate;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Boolean available;

    @JsonIgnore
    @NotNull
    @Column(name = "Author_AuthorID")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Author authorID;
}
