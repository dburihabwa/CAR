<%-- 
    Document   : index.jsp
    Created on : 08-Apr-2014, 14:38:44
    Author     : dorian
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>BOOK EE</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h1>Book Shop</h1>
        <ul>
            <li>        
                <a href="${pageContext.request.contextPath}" />Home</a>
            </li>
            <li>
                <a href ="${pageContext.request.contextPath}/authors">Authors</a>
            </li>
            <li>
                <a href ="${pageContext.request.contextPath}/book">Add a new book</a>
            </li>
            <li>
                <a href ="${pageContext.request.contextPath}/basket">Basket</a>
            </li>
        </ul>
        <c:if test="${books != null && books.size() > 0}">
            <form method="POST" action="http://localhost:8080/BookEE-war/basket">
                <table style="border-style: solid">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Year</th>
                            <th>Author</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${books}" var="book">
                            <tr>
                                <td>
                                    <c:out value="${book.title}"/>
                                </td>
                                <td>
                                    <c:out value="${book.year}"/>
                                </td>
                                <td>
                                    <c:out value="${book.author}"/>
                                </td>
                                <td>
                                    <input type = "checkbox" name = "<c:out value = "${book.title}"/>" value = "<c:out value = "${book.title}" />"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <input type = "submit" value = "add"/>
            </form>
        </c:if>
    </body>
</html>
