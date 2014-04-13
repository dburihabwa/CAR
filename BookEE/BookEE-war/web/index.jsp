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
        <c:if test="${books != null && books.size() > 0}">
            <table style="border-style: solid">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Title</th>
                        <th>Year</th>
                        <th>Author</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${books}" var="book">
                        <tr>
                            <td>
                                <c:out value="${book.id}"/>
                            </td>
                            <td>
                                <c:out value="${book.title}"/>
                            </td>
                            <td>
                                <c:out value="${book.year}"/>
                            </td>
                            <td>
                                <c:out value="${book.author}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <ul>
            <li>        
                <a href="" />Home</a>
            </li>
            <li>
                <a href ="authors">Authors</a>
            </li>
            <li>
                <a href ="book">Add a new book</a>
            </li>
        </ul>
    </body>
</html>
