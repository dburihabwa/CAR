/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lille1.car.ee;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author dorian
 */
@WebServlet(name = "ValidateServer", urlPatterns = {"/placeorder"})
public class PlaceOrderServlet extends HttpServlet {

    @EJB
    private BasketDao basketDao;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            request.setAttribute("error", "A problem occured with your basket!");
            request.setAttribute("pageTitle", "Error");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        Basket basket = (Basket) session.getAttribute("basket");
        if (basket == null) {
            session.invalidate();
            request.setAttribute("error", "A problem occured with your basket!");
            request.setAttribute("pageTitle", "Error");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        try {
            basket = basketDao.persist(basket);
        } catch (EJBException e) {
            Logger.getLogger(PlaceOrderServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        session.removeAttribute("basket");
        session.invalidate();
        request.setAttribute("orderNumber", basket.getId());
        request.getRequestDispatcher("orderPlaced.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
