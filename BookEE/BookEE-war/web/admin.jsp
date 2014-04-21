<%-- 
    Document   : admin
    Created on : 21-Apr-2014, 16:17:34
    Author     : dorian
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"/>
<jsp:include page="nav.jsp" />
<ul>
    <li><a href ="${pageContext.request.contextPath}/book">Add a new book</a></li>
    <li><a href = "${pageContext.request.contextPath}/orders">List orders</a></li>
    <li><a href = "${pageContext.request.contextPath}/prepare">Add default books</a></li>
</ul>
<jsp:include page="footer.jsp" />

