/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.ee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 *
 * Entity representing a shopper's basket.
 *
 * @author Dorian Burihabwa
 */
@Entity
public class Basket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_id")
    private long id;

    @Column(name = "bogus")
    private boolean bogus;

    @ManyToMany
    @JoinTable(name = "BASKET_BOOK", joinColumns = {
        @JoinColumn(name = "basket_id")}, inverseJoinColumns = {
        @JoinColumn(name = "book_id")})
    List<Book> books;

    public Basket() {
        books = new ArrayList<>();
    }

    public Basket(final long id, final List<Book> books) {
        this.id = id;
        this.books = books;
    }

    /**
     * Adds a book to the basket.
     *
     * @param book The book that will be added to the list of books
     */
    public void add(final Book book) {
        if (book == null) {
            throw new IllegalArgumentException("book argument cannot be null!");
        }
        books.add(book);
    }

    /**
     * Removes a book from the basket.
     *
     * @param book The book that will be removed from the list of books.
     */
    public void remove(final Book book) {
        if (book == null) {
            throw new IllegalArgumentException("book argument cannot be null!");
        }
        books.remove(book);
    }

    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public List<Book> getBooks() {
        return this.books;
    }

    public void setBooks(final List<Book> books) {
        this.books = books;
    }

    public void setBogus(final boolean bogus) {
        this.bogus = bogus;
    }

    public boolean getBogus() {
        return this.bogus;
    }
}
