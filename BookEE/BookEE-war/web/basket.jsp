<%-- 
    Document   : basket.jsp
    Created on : 13-Apr-2014, 17:39:05
    Author     : dorian
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book Shop - In your basket</title>
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
        <h2>In your basket</h2>

        <c:if test = "${basket != null && basket.books != null}">
            <strong>Size: <c:out value = "${basket.books.size()}"/></strong>

            <form method="POST" action="${pageContext.request.contextPath}/removebasket">
                <table>
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Year</th>
                            <th>Author</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items = "${basket.books}" var= "book">
                            <tr>
                                <td><c:out value="${book.title}"/></td>
                                <td><c:out value="${book.year}" /></td>
                                <td><c:out value="${book.author}" /></td>
                                <td><input type = "checkbox" name = "<c:out value = "${book.title}"/>" value = "<c:out value = "${book.title}" />"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <input type="submit" value ="remove" />
            </form>
        </c:if>

    </body>
</html>
