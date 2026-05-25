<%-- 
    Document   : UserAdd
    Created on : May 24, 2026, 10:23:21 AM
    Author     : pdarw
--%>

<%@page import="utils.Init"%>
<%@page import="Usuario.Usuario"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% 
    if (session.getAttribute("carnet") == null) {
        response.sendRedirect("InicioDeSesion.jsp");
    }
%>

<%
    String errorMsg = "";
    String nombre = "";
    String rol = "";
    String username = "";
    String password = "";

    if ("POST".equalsIgnoreCase(request.getMethod())) {

        nombre = request.getParameter("nombre");
        rol = request.getParameter("rol");
        username = request.getParameter("username");
        password = request.getParameter("password");

        String[] input = new String[]{nombre, rol, username, password};

        List<String> problems = new ArrayList();
        boolean usuarioValido = Usuario.validarDatos(input, problems, Init.conexion);
        Usuario usuario;
        if (usuarioValido) {
            usuario = new Usuario(input);
            usuario.writeSelftoDB(Init.conexion);
            response.sendRedirect("MenuAdmin.jsp");
        } else {
            errorMsg = String.join(" ", problems);
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Agregar Usuario</title>
        <link rel="stylesheet" href="../estilo.css">
    </head>
    <body>

        <div class="login-container">
            <h1>Agregar Usuario</h1>

            <form method="POST" action="">

                <div class="form-group">
                    <label for="nombre">Nombre Completo</label>
                    <input type="text" id="nombre" name="nombre" required
                           value="<%= nombre%>">
                </div>

                <div class="form-group">
                    <label for="rol">Rol</label>
                    <select name="rol" id="rol" required>
                        <option value="">-- Seleccione un rol --</option>
                        <option value="estudiante" <%= "estudiante".equals(rol) ? "selected" : ""%>>Estudiante</option>
                        <option value="profesor" <%= "profesor".equals(rol) ? "selected" : ""%>>Profesor</option>
                        <option value="administrador" <%= "administrador".equals(rol) ? "selected" : ""%>>Administrador</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="username">Nombre de usuario</label>
                    <input type="text" id="username" name="username" required
                           value="<%= username%>">
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <div class="form-group">
                    <%= !errorMsg.isEmpty() ? errorMsg : ""%>
                </div>

                <div class="button-group">
                    <button type="submit" class="btn-login">Registrar</button>
                    <button type="button" class="btn-cancel" onclick="window.location.href='MenuAdmin.jsp'">Cancelar</button>
                </div>

            </form>
        </div>

    </body>
</html>