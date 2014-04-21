/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.ee;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author dorian
 */
@Stateless
public class BasketDao {

    @PersistenceContext(name = "book-unit")
    private EntityManager em;

    public Basket persist(final Basket basket) {
        em.persist(basket);
        return basket;
    }

    public Basket find(final long id) {
        return em.find(Basket.class, id);
    }

    /**
     * Returns all the baskets in the database.
     *
     * @return All the baskets in the dabatase
     */
    public List<Basket> getAllBaskets() {
        TypedQuery<Basket> query = em.createQuery("SELECT b FROM Basket b", Basket.class);
        return query.getResultList();
    }

}
