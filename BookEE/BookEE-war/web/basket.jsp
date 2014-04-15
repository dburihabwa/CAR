<%-- 
    Document   : basket.jsp
    Created on : 13-Apr-2014, 17:39:05
    Author     : dorian
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:include page="header.jsp"/>
<jsp:include page="nav.jsp" />
<jsp:include page="state.jsp" />
<c:if test = "${basket != null && basket.books != null}">
    <strong>Size: <c:out value = "${basket.books.size()}"/></strong>

    <form method="POST" action="${pageContext.request.contextPath}/removebasket">
        <table>
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Year</th>
                    <th>Author</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items = "${basket.books}" var= "book">
                    <tr>
                        <td><c:out value="${book.title}"/></td>
                        <td><c:out value="${book.year}" /></td>
                        <td><c:out value="${book.author}" /></td>
                        <td><input type = "checkbox" name = "<c:out value = "${book.title}"/>" value = "<c:out value = "${book.title}" />"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <input type="submit" value ="remove" />
    </form>
    <form method="POST" action="${pageContext.request.contextPath}/placeorder">
        <input type="submit" value="Place order" />
    </form>
</c:if>

<jsp:include page="footer.jsp" />
