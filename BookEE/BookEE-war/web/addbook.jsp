<%-- 
    Document   : addbook.jsp
    Created on : 13-Apr-2014, 14:39:37
    Author     : dorian
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Book Shop</h1>
        <form method = "POST" action="./book">

            <label for = "title">Title</label>
            <br/>
            <input id = "title" name = "title" placeholder="title" type = "text" />
            <br/>
            <label for = "year">Year</label>
            <br/>
            <input id = "year" name = "year" placeholder="2014" type ="number" min="-3000" max ="2014" step="1" />
            <br/>
            <label for = "author">Author</label>
            <br/>
            <input id = "author" name = "author" placeholder="author" type="text"/>
            <br/>
            <input type ="submit" value ="ok" />
        </form>
    </body>
</html>
