<%-- 
    Document   : addbook.jsp
    Created on : 13-Apr-2014, 14:39:37
    Author     : dorian
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:include page="header.jsp"/>
<jsp:include page="nav.jsp" />
<jsp:include page="state.jsp" />
<form method = "POST" action="${pageContext.request.contextPath}/book">

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

<jsp:include page="footer.jsp" />