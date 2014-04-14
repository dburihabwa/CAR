/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.ee;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * A DAO allowing interaction with the books in the database.
 *
 * @author Dorian Burihabwa
 */
@Stateless
public class BookDao {

    @PersistenceContext(name = "book-unit")
    private EntityManager em;

    /**
     * Populates the database with sample books.
     *
     * @return The list of books added to the database
     */
    public List<Book> populate() {
        List<Book> books = new ArrayList<>();
        Book oldMan = new Book();
        oldMan.setAuthor("Ernest Heminwgay");
        oldMan.setTitle("The old man and the sea");
        oldMan.setYear(1952);
        em.persist(oldMan);
        books.add(oldMan);

        Book tomSayer = new Book();
        tomSayer.setAuthor("Mark Twain");
        tomSayer.setTitle("The adventures of Tom Sawyer");
        tomSayer.setYear(1876);
        em.persist(tomSayer);
        books.add(tomSayer);

        return books;
    }

    /**
     * Returns the list of authors in the database.
     *
     * @return the list of authors in the database
     */
    public List<String> getAuthors() {
        TypedQuery<String> query = em.createQuery("SELECT DISTINCT(b.author) FROM Book b ORDER BY b.author", String.class);
        List<String> authors = query.getResultList();
        return authors;
    }

    /**
     * Saves a book in the database
     *
     * @param book The book to save
     * @return The bpok with its new id
     */
    public Book persist(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("book argument cannot be null!");
        }
        this.em.persist(book);
        return book;
    }

    /**
     * Returns the list of books in the database.
     *
     * @return the list of books in the database
     */
    public List<Book> getAllBooks() {
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
        return query.getResultList();
    }

    public Book find(final String name) {
        Book book = em.find(Book.class, name);
        return book;
    }

}
