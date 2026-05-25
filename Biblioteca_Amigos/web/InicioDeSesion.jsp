<%-- 
    Document   : InicioDeSesion
    Created on : May 22, 2026, 9:50:57 PM
    Author     : pdarw
--%>

<%@page import="utils.Init"%>
<%@page import="utils.TipoUsuario"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    String action = request.getParameter("action");
    
    if ("cancel".equals(action)) {
        // Execute Java code here
        session.removeAttribute("tempData");
        session.removeAttribute("formDraft");
        session.removeAttribute("carnet");
        System.out.println("User cancelled operation - cleaned up session");
    }
%>

<%  
    Init.initDB();
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String errorMsg = "";

    if ("POST".equalsIgnoreCase(request.getMethod()) && username != null) {
        
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            errorMsg = "Los campos no pueden estar vacios";
        } else {
            
            Map<String, String> valido = Init.validarUserPassword(username, password);
            String pagina;
            if (valido.containsKey("rol")) {
                switch (valido.get("rol")) {
                    case "administrador":
                        session.setAttribute("rol", TipoUsuario.ADMINISTRADOR);
                        pagina = "MenuAdmin.jsp";
                        break;
                    case "profesor":
                        session.setAttribute("rol", TipoUsuario.PROFESOR);
                        pagina = "MenuUsuario.jsp";
                        break;
                    default:
                        session.setAttribute("rol", TipoUsuario.ESTUDIANTE);
                        pagina = "MenuUsuario.jsp";
                        break;
                }
                session.setAttribute("carnet", valido.get("carnet"));
                response.sendRedirect(pagina);
            } else {
                errorMsg = valido.getOrDefault("errores1", "error");
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="./estilo.css">
</head>
<body>

    <div class="login-container">
        <h1>Login</h1>

        <form action="InicioDeSesion.jsp" method="POST">
            
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <p><%= !errorMsg.isEmpty() ? errorMsg : "" %><p>
            </div>
            
            <button type="submit" class="btn-login">Login</button>

        </form>
    </div>

</body>
</html>