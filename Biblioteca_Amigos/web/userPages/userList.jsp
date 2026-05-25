<%-- 
    Document   : userList
    Created on : May 24, 2026, 4:37:13 PM
    Author     : pdarw
--%>

<%@page import="java.util.Map"%>
<%@page import="utils.Init"%>
<%@page import="Usuario.Usuario"%>
<%@page import="java.util.logging.Level"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%
    List<List<String>> datos = new ArrayList<>();
    try {
        java.sql.ResultSet rs = Init.conexion.ejecutarInstruccion(
                "SELECT nombre, rol, username "
                + "FROM usuarios"
        );
        while (rs.next()) {
            datos.add(new ArrayList<>());
            datos.get(0).add(rs.getString("nombre"));
            datos.add(new ArrayList<>());
            datos.get(1).add(rs.getString("rol"));
            datos.add(new ArrayList<>());
            datos.get(2).add(rs.getString("username"));
        }
    } catch (java.sql.SQLException ex) {
        Init.logger.log(Level.WARNING, "error al cargar los datos de la bd", ex);
    }
    
    int n = datos.get(0).size();

    List<Usuario> usuarios = new ArrayList<Usuario>();

    for (int fila = 0; fila < n; fila++) {
        usuarios.add(
                new Usuario(
                        new String[]{
                            datos.get(0).get(fila), 
                            datos.get(1).get(fila), 
                            datos.get(2).get(fila), ""}));
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Listado de Usuarios</title>
        <link rel="stylesheet" href="../estilo.css">
    </head>
    <body>

        <div class="list-container">
            <h1>Listado de Usuarios</h1>

            <table class="user-table">
                <thead>
                    <tr>
                        <th>Usuario</th>
                        <th>Nombre Completo</th>
                        <th>Rol</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Usuario u : usuarios) {%>
                    <tr>
                        <td><%= u.getUsername()%></td>
                        <td><%= u.getNombre()%></td>
                        <td><%= u.getRol()%></td>
                    </tr>
                    <% }%>
                </tbody>
            </table>

            <div class="button-group single">
                <a href="../MenuAdmin.jsp" class="btn-cancel">Volver al Menu</a>
            </div>
        </div>

    </body>
</html>