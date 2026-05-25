<%-- 
    Document   : userModify
    Created on : May 24, 2026, 3:45:21 PM
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
    String nombre = "";
    String rol = "";
    String password = "";
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
        nombre = request.getParameter("nombre");
        rol = request.getParameter("rol");
        password = request.getParameter("password");

        String[] input = new String[]{nombre, rol, username, password};
        List<String> problems = new ArrayList();
        boolean ans = Usuario.validarDatos(input, problems, Init.conexion);
        if (ans) {
            usuario = new Usuario(input);
            usuario.updateSelftoDB(Init.conexion);

            response.sendRedirect("../MenuAdmin.jsp");
        } else {
            errorMsg = String.join(", ", problems);
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Modificar Usuario</title>
        <link rel="stylesheet" href="../estilo.css">
    </head>
    <body>

        <div class="login-container">
            <h1>Modificar Usuario</h1>

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
                    <label>Usuario</label>
                    <input type="text" value="<%= username%>" disabled>
                </div>

                <div class="form-group">
                    <label for="nombre">Nombre Completo</label>
                    <input type="text" id="nombre" name="nombre" required>
                </div>

                <div class="form-group">
                    <label for="rol">Rol</label>
                    <select name="rol" id="rol" required>
                        <option value="estudiante">Estudiante</option>
                        <option value="profesor">Profesor</option>
                        <option value="administrador">Administrador</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="password">Contraseña</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <div class="button-group">
                    <button type="submit" class="btn-login">Guardar</button>
                    <a href="userModify.jsp" class="btn-cancel">Cancelar</a>
                </div>
            </form>

            <% }%>

        </div>

    </body>
</html>