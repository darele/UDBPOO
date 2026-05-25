<%-- 
    Document   : userSearch
    Created on : May 24, 2026, 7:47:03 PM
    Author     : pdarw
--%>

<%@page import="utils.Init"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="Usuario.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    String username = request.getParameter("username");
    String errorMsg = "";
    Usuario usuario = null;
    boolean found = false;

    if (username != null) {
        username = username.trim();
        
        if (username.isEmpty()) {
            errorMsg = "Ingrese un nombre de usuario";
        } else if (Usuario.estaEnBD(username)) {
            
            Map<String, String> ans = Init.conexion.select(
                "usuarios", 
                "WHERE username='" + username + "'",
                List.of("nombre", "rol", "username")
            );
            
            usuario = new Usuario(
                new String[]{
                    ans.get("nombre"), 
                    ans.get("rol"), 
                    ans.get("username"), 
                    ""
                }
            );
            found = true;
        } else {
            errorMsg = "Usuario no encontrado";
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Buscar usuarios</title>
    <link rel="stylesheet" href="../estilo.css">
</head>
<body>

    <div class="login-container">
        <h1>Buscar Usuarios</h1>

        <% if (!found) { %>

            <form method="POST" action="">

                <div class="form-group">
                    <label for="username">Nombre de usuario</label>
                    <input type="text" id="username" name="username" required autofocus
                           value="<%= username != null ? username : "" %>">
                </div>

                <div class="error-message">
                    <%= !errorMsg.isEmpty() ? errorMsg : "" %>
                </div>

                <div class="button-group">
                    <button type="submit" class="btn-login">Buscar</button>
                    <a href="../MenuAdmin.jsp" class="btn-cancel">Cancelar</a>
                </div>
            </form>

        <% } else { %>

            <table class="user-table">
                <thead>
                    <tr>
                        <th>Usuario</th>
                        <th>Nombre Completo</th>
                        <th>Rol</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><%= usuario.getUsername() %></td>
                        <td><%= usuario.getNombre() %></td>
                        <td><%= usuario.getRol() %></td>
                    </tr>
                </tbody>
            </table>

            <div class="button-group">
                <a href="userSearch.jsp" class="btn-login">Buscar Otro</a>
                <a href="../MenuAdmin.jsp" class="btn-cancel">Volver</a>
            </div>

        <% } %>

    </div>

</body>
</html>