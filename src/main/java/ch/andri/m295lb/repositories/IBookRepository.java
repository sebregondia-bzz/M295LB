package ch.andri.m295lb.repositories;

import ch.andri.m295lb.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IBookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByPublicationDate(LocalDateTime publicationDate);
    List<Book> findByTitle(String title);
}