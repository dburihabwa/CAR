<%-- 
    Document   : Book
    Created on : 01-Apr-2014, 17:37:21
    Author     : dorian
--%>

<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book</title>
    </head>
    <body>
        <h1>New book!</h1>
        <table style="border-style: solid">
            <thead>
                <tr>
                    <c:forEach items="${sessionScope.parameters}" var="parameter">
                        <th>
                            <c:out value="${parameter.key}"/>
                        </th>
                    </c:forEach>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <c:forEach items="${sessionScope.parameters}" var="parameter">
                        <td>
                            <c:out value="${parameter.value[0]}"/>
                        </td>
                    </c:forEach>
                </tr>
            </tbody>
        </table>

        <ul>
            <li>        
                <a href="./" />Home</a>
            </li>
            <li>
                <a href ="./authors">Auhtors</a>
            </li>
        </ul>
    </body>
</html>
