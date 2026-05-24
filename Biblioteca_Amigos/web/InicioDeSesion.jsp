<%-- 
    Document   : InicioDeSesion
    Created on : May 22, 2026, 9:50:57 PM
    Author     : pdarw
--%>

<%@page import="utils.TipoUsuario"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="conexion.Conexion"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.security.NoSuchAlgorithmException"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="java.security.MessageDigest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="PaginaDeError.jsp" %>

<%! 
    Logger logger = Logger.getLogger("MyJSPLogger");
    Conexion conexion = new Conexion();
%>

<%!
    String salt = "Rafael Torres ";
    String encriptar(String contrasena) {
        byte[] contrasenaCifradaBytes = null;
        try {
            contrasenaCifradaBytes = MessageDigest.getInstance("SHA-256").digest(
                    (salt + contrasena).getBytes(StandardCharsets.UTF_8)
            );
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.WARNING, "Error al encriptar la contrasena ", e);
            return "";
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : contrasenaCifradaBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    Map<String, String> validarUserPassword(String username, String password) {
        Map<String, String> usuario = conexion.select("usuarios",
                "WHERE username='" + username + "'",
                List.of("username", "password", "rol"));
        Map<String, String> ans = new HashMap<>();
        if (usuario.isEmpty()) {
            ans.put("errores1", "Usuario o contrasena incorrectos");
            return ans;
        }
        if (usuario.containsKey("errores")) {
            ans.put("errores2", usuario.get("errores"));
            return ans;
        }

        String contrasena = password;
        String contrasenaCifrada = encriptar(contrasena);
        String contrasenaReal = usuario.get("password");
        if (contrasenaCifrada.equals(contrasenaReal)) {
            ans = usuario;
            return ans;
        }
        ans.put("errores1", "Usuario o contrasena incorrectos");
        return ans;
    }
%>

<%  
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String errorMsg = "";
    

    if ("POST".equalsIgnoreCase(request.getMethod()) && username != null) {
        
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            errorMsg = "Los campos no pueden estar vacios";
        } else {
            
            Map<String, String> valido = validarUserPassword(username, password);
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
                response.sendRedirect(pagina);
            } else {
                errorMsg = valido.getOrDefault("errores1", "error");
                errorMsg += valido.getOrDefault("errores2", " otro error");
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

        <div class="error-message">
            <%= !errorMsg.isEmpty() ? errorMsg : "" %>
        </div>

        <form action="InicioDeSesion.jsp" method="POST">
            
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <button type="submit" class="btn-login">Login</button>

        </form>
    </div>

</body>
</html>