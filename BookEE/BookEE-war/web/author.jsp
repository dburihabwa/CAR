<%-- 
    Document   : auhtor
    Created on : 13-Apr-2014, 13:51:23
    Author     : dorian
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Book shop</h1>
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
        <h2>Authors</h2>
        <c:if test ="${authors != null}">
            <h2>List of authors found:</h2>
            <ul>
                <c:forEach items="${authors}" var="author">
                    <li>
                        <c:out value="${author}"/>
                    </li>
                </c:forEach>

            </ul>
        </c:if>
        <c:if test="${authors == null}">
            <h2>Could not find any authors in the database</h2>
        </c:if>
    </body>
</html>
