<%-- 
    Document   : orders.jsp
    Created on : 21-Apr-2014, 16:36:50
    Author     : dorian
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"/>
<jsp:include page="nav.jsp" />
<c:if test="${baskets != null && baskets.size() > 0}">
    <c:forEach items="${baskets}" var="basket">
        <h2>#<c:out value="${basket.id}"/></h2>
        <table style="border-style: solid">
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Year</th>
                    <th>Author</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${basket.books}" var="book">
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
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:forEach>
</c:if>
<jsp:include page="footer.jsp" />

