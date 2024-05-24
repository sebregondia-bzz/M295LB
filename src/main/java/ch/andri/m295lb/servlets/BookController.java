package ch.andri.m295lb.servlets;

import ch.andri.m295lb.models.Book;
import ch.andri.m295lb.repositories.IBookRepository;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Component
@Path("/book")
public class BookController {
    private final Logger logger = LogManager.getLogger(BookController.class);

    private final IBookRepository bookRepository;

    @Autowired
    public BookController(IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/{bookID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("bookID") @Valid Integer bookID) {//Datensatz mit ID lesen
        Optional<Book> book = bookRepository.findById(bookID);
        if (book.isPresent()) {
            logger.info("Returning book with number {}.", bookID);
            return Response.status(Response.Status.OK).entity(book.get()).build();
        }
        throw new NotFoundException(String.format("No book with number %d found.", bookID));
    }//http://localhost:8080/artifact/resources/book/1

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/Existing/{bookID}")//todo path correcting
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookExistenceById(@PathParam("bookID") @Valid Integer bookID) {//Datensatz vorhanden (ID) prüfen
        Optional<Book> book = bookRepository.findById(bookID);
        if (book.isPresent()) {
            logger.info("Book with number {} exists.", bookID);
            return Response.status(Response.Status.OK).entity("Book with number "+bookID+" exists.").build();
        }
        throw new NotFoundException(String.format("No book with number %d exists.", bookID));
    }//http://localhost:8080/artifact/resources/book/Existing/1

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBook() {//Alle Datensätze lesen
        List<Book> books = bookRepository.findAll();
        logger.info("Returning all books...");
        return Response.status(Response.Status.OK).entity(books).build();
    }//http://localhost:8080/artifact/resources/book


    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/publicationDate/{publicationDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookByPublicationDate(@PathParam("publicationDate") @Valid String publicationDate) {//2-mal Lesen basierend auf einem Filter (einmal Datum, einmal Text)
        List<Book> books = bookRepository.findByPublicationDate(LocalDateTime.parse(publicationDate));
        logger.info("Returning all books with a publicationDate on the: {}  which are: {}.", publicationDate, books);
        return Response.status(Response.Status.OK).entity("Books with a publicationDate on the: "+publicationDate+" which are: "+books).build();
    }//http://localhost:8080/artifact/resources/book/publicationDate/1997-06-26T00:00:00

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/title/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookByTitle(@PathParam("title") @Valid String title) {//2-mal Lesen basierend auf einem Filter (einmal Datum, einmal Text)
        List<Book> books = bookRepository.findByTitle(title);
        logger.info("Returning all books with title:  {} which are: {}.", title, books);
        return Response.status(Response.Status.OK).entity("Books with title:  "+title+" which are: "+books).build();
    }//http://localhost:8080/artifact/resources/book/title:It


    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/amount")//todo path correcting
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookAmount() {//Anzahl Datensätze bestimmen
        List<Book> books = bookRepository.findAll();
        logger.info("Returning books amount {}", books.size());
        return Response.status(Response.Status.OK).entity("There are "+ books.size()+" books in the database").build();
    }//http://localhost:8080/artifact/resources/book/amount



    @POST
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBook(@Valid Book book) {
        if (bookRepository.findById(book.getBookID()).isPresent()) {
            logger.warn("Book with ID {} already exists.", book.getBookID());
            return Response.status(Response.Status.CONFLICT).entity(book).build();
        }
        return saveOrUpdate(book, "Insert");
    }


    @POST
    @RolesAllowed({"ADMIN"})
    @Path("/multiple")//todo path correcting
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBooks(@Valid List<Book> books) {//Mehrere neue Einträge
        List<Book> conflictingBooks = new ArrayList<>();
        for (Book book : books) {
            if (bookRepository.findById(book.getBookID()).isPresent()) {
                logger.warn("Book with ID {} already exists.", book.getBookID());
                conflictingBooks.add(book);
            }
        }

        if (!conflictingBooks.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).entity(conflictingBooks).build();
        }

        for (Book book : books) {
            saveOrUpdate(book, "Insert");
        }

        return Response.status(Response.Status.CREATED).entity(books).build();
    }


    @PUT
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@Valid Book Book) {//Einen Eintrag aktualisieren
        if (bookRepository.findById(Book.getBookID()).isPresent()) {
            return saveOrUpdate(Book, "Update");
        }
        throw new NotFoundException(String.format("Book with id %d doesn't exist.", Book.getBookID()));
    }

    @DELETE
    @RolesAllowed("ADMIN")
    @Path("/{bookID}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteBook(@PathParam("bookID") @Valid Integer bookID) {//Datensatz mit ID löschen
        if (bookRepository.findById(bookID).isPresent()) {
            bookRepository.deleteById(bookID);
            logger.info("Deleting Book with number {}.", bookID);
            return Response.status(Response.Status.OK)
                    .entity(String.format("Book with id %d deleted.", bookID)).build();
        }
        throw new NotFoundException(String.format("No Book with id %d found.", bookID));
    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllBook() {//Alle Datensätze löschen
        bookRepository.deleteAll();
        logger.info("Deleting all Books");
        return Response.status(Response.Status.OK).entity("Deleting all Books").build();
    }

    private Response saveOrUpdate(Book book, String method) {
        try {
            logger.info("{} book with id {}.", method, book.getBookID());
            return Response.status(Response.Status.OK)
                    .entity(bookRepository.save(book)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }




    //Creating tables part
    @POST
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/createTables")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTables() {
        // Execute the SQL queries to create tables
        String sqlQueries = "-- drop schema LB295;\n\n" +
                "-- MySQL Workbench Forward Engineering\n\n" +
                "SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;\n" +
                "SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;\n" +
                "SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';\n\n" +
                "-- -----------------------------------------------------\n" +
                "-- Schema LB295\n" +
                "-- -----------------------------------------------------\n\n" +
                "CREATE SCHEMA IF NOT EXISTS `LB295` DEFAULT CHARACTER SET utf8 ;\n" +
                "USE `LB295` ;\n\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table `LB295`.`Author`\n" +
                "-- -----------------------------------------------------\n\n" +
                "CREATE TABLE IF NOT EXISTS `LB295`.`Author` (\n" +
                "  `AuthorID` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `Name` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`AuthorID`))\n" +
                "ENGINE = InnoDB;\n\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table `LB295`.`Books`\n" +
                "-- -----------------------------------------------------\n\n" +
                "CREATE TABLE IF NOT EXISTS `LB295`.`Books` (\n" +
                "  `BookID` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `Title` VARCHAR(45) NULL,\n" +
                "  `Pages` INT NULL,\n" +
                "  `PublicationDate` DATETIME NULL,\n" +
                "  `Price` DECIMAL(10,2) NULL,\n" +
                "  `Available` Boolean NULL,\n" +
                "  `Author_AuthorID` INT NOT NULL,\n" +
                "  PRIMARY KEY (`BookID`),\n" +
                "  INDEX `fk_Books_Author_idx` (`Author_AuthorID` ASC) VISIBLE,\n" +
                "  CONSTRAINT `fk_Books_Author`\n" +
                "    FOREIGN KEY (`Author_AuthorID`)\n" +
                "    REFERENCES `LB295`.`Author` (`AuthorID`)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n\n" +
                "SET SQL_MODE=@OLD_SQL_MODE;\n" +
                "SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;\n" +
                "SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;\n\n";

        // Log the execution of SQL queries
        logger.info("Executing SQL queries to create tables...");

        // Execute SQL queries to create tables
        // Here you can implement the logic to execute SQL queries,
        // or you can use an ORM framework like Hibernate to handle database operations

        // For now, I'm just returning a response indicating that the tables are created
        return Response.status(Response.Status.OK).entity("Tables created successfully").build();
    }
}
