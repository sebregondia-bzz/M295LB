package ch.andri.m295lb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "author")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer autorID;
    @Length(max = 45)
    private String Name;


    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Book> books;
}