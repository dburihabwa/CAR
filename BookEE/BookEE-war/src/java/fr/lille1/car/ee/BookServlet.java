package fr.lille1.car.ee;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author dorian
 */
@WebServlet(urlPatterns = {"/BookHandler"})
public class BookServlet extends HttpServlet {

    @EJB
    private BookDao bookDao;

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
        Book book = new Book();
        String author = request.getParameter("author");
        String year = request.getParameter("year");
        String title = request.getParameter("title");
        if (author == null || author.isEmpty()) {
            request.setAttribute("pageTitle", "Add a new book");
            request.setAttribute("status", "The author of the book must be given!");
            request.getRequestDispatcher("addbook.jsp").forward(request, response);
            return;
        }
        if (year == null || year.isEmpty()) {
            request.setAttribute("pageTitle", "Add a new book");
            request.setAttribute("status", "The year of the book must be given!");
            request.getRequestDispatcher("addbook.jsp").forward(request, response);
            return;
        }

        if (title == null || title.isEmpty()) {
            request.setAttribute("pageTitle", "Add a new book");
            request.setAttribute("status", "The title of the book must be given!");
            request.getRequestDispatcher("addbook.jsp").forward(request, response);
            return;
        }

        try {
            book.setAuthor(author);
            book.setYear(Integer.parseInt(year));
            book.setTitle(title);
            book = bookDao.persist(book);
        } catch (NumberFormatException e) {
            Logger.getLogger(BookServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            request.setAttribute("pageTitle", "Add a new book");
            request.setAttribute("status", "The year of the book must be an integer!");
            request.getRequestDispatcher("addbook.jsp").forward(request, response);
            return;
        } catch (EJBException e) {
            Logger.getLogger(BookServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            request.setAttribute("pageTitle", "Error");
            request.setAttribute("error", "An error occured while saving the new book!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            Logger.getLogger(BookServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            request.setAttribute("pageTitle", "Error");
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("");
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
        request.setAttribute("pageTitle", "Add a new book");
        request.getRequestDispatcher("addbook.jsp").forward(request, response);
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
