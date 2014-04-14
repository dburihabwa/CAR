/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.ee;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * An entity representing a book.
 *
 * @author Dorian Burihabwa
 */
@Entity
public class Book implements Serializable {

    @Id
    private String title;
    private String author;
    private int publishingYear;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return publishingYear;
    }

    public void setYear(int year) {
        this.publishingYear = year;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("{");
        res.append("title:").append(title).append(", ");
        res.append("year:").append(publishingYear).append(", ");
        res.append("author:").append(author);
        res.append("}");
        return res.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.title);
        hash = 79 * hash + Objects.hashCode(this.author);
        hash = 79 * hash + this.publishingYear;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Book)) {
            return false;
        }
        Book other = (Book) obj;
        return this.title.equals(other.title);
    }

}
