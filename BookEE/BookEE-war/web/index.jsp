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
        <h1>BookEE</h1>
        <c:if test="${book != null}">
            <h2>A new book has been added!</h2>
        </c:if>
        <form method = "POST" action="BookHandler">

            <label for = "title">Title</label>
            <br/>
            <input id = "title" name = "title" placeholder="title" type = "text"
                   <c:if test="${book != null}">
                       value = "<c:out value="${book.title}"/>"
                   </c:if> 
                   />
            <br/>
            <label for = "year">Year</label>
            <br/>
            <input id = "year" name = "year" placeholder="2014" type ="number" min="-3000" max ="2014" step="1"
                   <c:if test="${book != null}">
                       value = "<c:out value="${book.year}"/>"
                   </c:if>
                   />
            <br/>
            <label for = "author">Author</label>
            <br/>
            <input id = "author" name = "author" placeholder="author" type="text"
                   <c:if test="${book != null}">
                       value = "<c:out value="${book.author}"/>"
                   </c:if>
                   />
            <br/>
            <input type ="submit" value ="ok" />
        </form>
    </body>
</html>
