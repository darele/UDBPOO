<%-- 
    Document   : PaginaDeError
    Created on : May 23, 2026, 1:49:21 PM
    Author     : pdarw
--%>

<%@ page isErrorPage="true" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.logging.Level" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Error</title>
    </head>
    <body>
        <h2>Un error inesperado ha ocurrido!</h2>
        <p><strong>Mensaje:</strong> <%= exception != null ? exception.getMessage() : "Unknown error"%></p>

        <a href="\Biblioteca_Amigos\index.html">Return to Home</a>

        <%
            Logger logger = Logger.getLogger("PaginaDeError.jsp");
            if (exception != null) {
                logger.log(Level.SEVERE, "An uncaught exception occurred in the application:", exception);
            } else {
                logger.warning("The error page was triggered, but the exception object was null.");
            }
        %>
    </body>
</html>
