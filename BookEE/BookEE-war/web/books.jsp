<%-- 
    Document   : books.jsp
    Created on : 15-Apr-2014, 14:47:59
    Author     : dorian
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                            <c:out value="${book.title}"
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
