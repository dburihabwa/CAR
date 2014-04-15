<%-- 
    Document   : error.jsp
    Created on : 15-Apr-2014, 14:10:27
    Author     : dorian
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp"/>
<jsp:include page="nav.jsp"/>
<h2><c:out value = "${error}"/></h2>
<jsp:include page="footer.jsp"/>
