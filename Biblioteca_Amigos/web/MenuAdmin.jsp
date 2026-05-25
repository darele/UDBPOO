<%-- 
    Document   : MenuAdmin
    Created on : May 23, 2026, 10:04:03 PM
    Author     : pdarw
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% 
    if (session.getAttribute("carnet") == null) {
        response.sendRedirect("InicioDeSesion.jsp");
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Management Menu</title>
    <link rel="stylesheet" href="estilo.css">
</head>
<body>

    <div class="menu-container">
        <h1>Menu de Administrador</h1>

        <section class="menu-section">
            <h2>Usuarios</h2>
            <ul class="menu-list">
                <li><a href="userPages/userAdd.jsp">Agregar Usuario</a></li>
                <li><a href="userPages/userModify.jsp">Modificar Usuario</a></li>
                <li><a href="userPages/userList.jsp">Listar Usuarios</a></li>
                <li><a href="userPages/userSearch.jsp">Buscar Usuario</a></li>
                <li><a href="userPages/userDelete.jsp">Eliminar Usuario</a></li>
            </ul>
        </section>

        <section class="menu-section">
            <h2>Materiales</h2>
            <ul class="menu-list">
                <li><a href="materialAdd.jsp">Agregar Material</a></li>
                <li><a href="materialModify.jsp">Modificar Material</a></li>
                <li><a href="materialList.jsp">Listar Materiales</a></li>
                <li><a href="materialSearch.jsp">Buscar Material</a></li>
                <li><a href="materialDelete.jsp">Eliminar Material</a></li>
            </ul>
        </section>
        
        <section class="menu-section">
            <h2>
                Salir
            </h2>
            <ul class="menu-list">
                <li><a href="InicioDeSesion.jsp?action=cancel">Cerrar Sesion</a>
            </ul>
        </section>

    </div>

</body>
</html>
