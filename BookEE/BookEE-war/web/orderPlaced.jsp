<%-- 
    Document   : orderpassed
    Created on : 15-Apr-2014, 17:58:38
    Author     : dorian
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"/>
<jsp:include page="nav.jsp" />
<h2>Your order has been placed (number <c:out value="${orderNumber}"/>!</h2>
<jsp:include page="footer.jsp" />
