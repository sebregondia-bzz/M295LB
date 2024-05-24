package ch.andri.m295lb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Books", schema = "LB295")
public class Book {

    @Id
    private Integer bookID;

    private String title;

    @Min(value = 1, message = "Book must have at least 1 page")
    private Integer pages;

    @Temporal(TemporalType.TIMESTAMP)
    @PastOrPresent(message = "Publication date must be in the past or present.")
    private Date publicationDate;

    @Digits(integer = 8, fraction = 2, message = "Price can't have more than 2 decimal places and 8 normal places")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Boolean available;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Author_AuthorID")
    private Author author;
}
