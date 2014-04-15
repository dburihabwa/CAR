<%-- 
    Document   : header.jsp
    Created on : 15-Apr-2014, 14:21:39
    Author     : dorian
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><c:out value = "BookEE"/> - <c:out value = "${pageTitle}"/></title>
        <link rel="stylesheet" href="bs/css/bootstrap.min.css">
        <link rel="stylesheet" href="bs/css/bootstrap-theme.min.css">
        <script type="text/javascript" src="bs/js/bootstrap.min.js"></script>
    </head>
    <body>
        
        <header>
            <h1><c:out value = "${pageTitle}"/></h1>
        </header>
