package ch.andri.m295lb.repositories;

import ch.andri.m295lb.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthorRepository extends JpaRepository<Author, Integer> {
}
