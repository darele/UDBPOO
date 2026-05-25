<%-- 
    Document   : userDelete
    Created on : May 24, 2026, 8:40:32 PM
    Author     : pdarw
--%>

<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="utils.Init"%>
<%@page import="Usuario.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    String step = request.getParameter("step");
    String username = "";
    String errorMsg = "";
    Usuario usuario;
    boolean found = false;

    if ("check".equals(step)) {
        username = request.getParameter("username");

        if (username == null || username.trim().isEmpty()) {
            errorMsg = "Ingrese un nombre de usuario";
        } else {
            username = username.trim();

            if (Usuario.estaEnBD(username)) {
                found = true;
            } else {
                errorMsg = "Usuario no encontrado";
            }
        }
    }

    if ("update".equals(step)) {
        username = request.getParameter("username");
        Map<String, String> ans = Init.conexion.select(
                "usuarios", 
                "WHERE username='" + username + "'",
                List.of("username")
            );
        usuario = new Usuario(new String[]{"", "", ans.get("username"), ""});
        usuario.deleteSelfFromDB(Init.conexion);
        response.sendRedirect("../MenuAdmin.jsp");
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Eliminar Usuario</title>
        <link rel="stylesheet" href="../estilo.css">
    </head>
    <body>

        <div class="login-container">
            <h1>Eliminar Usuario</h1>

            <% if (!found) {%>

            <form method="POST" action="">
                <input type="hidden" name="step" value="check">

                <div class="form-group">
                    <label for="username">Nombre de usuario</label>
                    <input type="text" id="username" name="username" required autofocus
                           value="<%= username%>">
                </div>

                <div class="form-group">
                    <p><%= !errorMsg.isEmpty() ? errorMsg : ""%><p>
                </div>

                <div class="button-group">
                    <button type="submit" class="btn-login">Buscar</button>
                    <a href="../MenuAdmin.jsp" class="btn-cancel">Cancelar</a>
                </div>
            </form>

            <% } else {%>

            <form method="POST" action="">
                <input type="hidden" name="step" value="update">
                <input type="hidden" name="username" value="<%= username%>">

                <div class="form-group">
                    <label>Esta seguro que desea eliminar al usuario 
                        <strong><%= username %></strong>, esta accion no se puede deshacer</label>
                </div>
                
                <div class="button-group">
                    <button type="submit" class="btn-login">Eliminar</button>
                    <a href="../MenuAdmin.jsp" class="btn-cancel">Cancelar</a>
                </div>
            </form>

            <% }%>

        </div>

    </body>
</html>