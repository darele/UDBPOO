<%-- 
    Document   : addMaterial
    Created on : May 25, 2026, 8:52:01 AM
    Author     : pdarw
--%>

<%@page import="materiales.Material"%>
<%@page import="utils.Init"%>
<%@page import="java.util.logging.Level"%>
<%@page import="materiales.TipoCampo"%>
<%--<%@page import="materiales.Tesis"%>
<%@page import="materiales.DVD"%>
<%@page import="materiales.CD"%>
<%@page import="materiales.Revista"%>--%>
<%@page import="materiales.Libro"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>

<%
    System.out.println("Kika");
    Map<String, List<String>> materialDBFields = new HashMap<>();
    Map<String, List<String>> materialFields = new HashMap<>();
    Map<String, List<TipoCampo>> materialTypes = new HashMap<>();

    materialFields.put("libro", Libro.getCampos());
    materialTypes.put("libro", Libro.getTipoCampos());
    materialDBFields.put("libro", Libro.getDBFields());
//    materialFields.put("revista", Revista.getCampos());
//    materialTypes.put("revista", Revista.getTipoCampos());
//    materialFields.put("cd", CD.getCampos());
//    materialTypes.put("cd", CD.getTipoCampos());
//    materialFields.put("dvd", DVD.getCampos());
//    materialTypes.put("dvd", DVD.getTipoCampos());
//    materialFields.put("tesis", Tesis.getCampos());
//    materialTypes.put("tesis", Tesis.getTipoCampos());

    String tipo = request.getParameter("tipo");
    String step = request.getParameter("step");
    String paso = request.getParameter("paso");
    String errorMsg = "";

    if ("select".equals(step)) {
        if (tipo == null || tipo.isEmpty()) {
            errorMsg = "Seleccione un tipo de material";
        } else {
            response.sendRedirect("materialAdd.jsp?tipo=" + tipo + "&step=form");
            return;
        }
    }

    if ("guardar".equals(paso)) {
        System.out.println("Kika3");
        tipo = request.getParameter("tipo");
        List<String> fields = materialFields.get(tipo);

        List<String> problems = new ArrayList();
        String[] input = new String[10];

        boolean valid = true;
        int index = 0;
        for (int i = 0; i < fields.size(); i++) {
            String fieldName = fields.get(i);
            String fieldId = fieldName.toLowerCase().replace(" ", "_");
            String value = request.getParameter(fieldId);
            System.out.println(fieldId);
            System.out.println(value);
            if ("insert".equals(value)) {
                value = request.getParameter(fieldId + "_otro");
            }

            if (value == null || value.trim().isEmpty()) {
                valid = false;
                break;
            }
            input[index++] = value;
        }

        switch (tipo) {
            case "libro":
                valid = Libro.validarDatos(input, problems, Init.conexion);
                break;
            default:
                throw new AssertionError();
        }
        if (!valid) {
            errorMsg = String.join(",", problems);
        } else {
            Material material;
            switch (tipo) {
                case "libro":
                    material = new Libro(input);
                    break;
                default:
                    throw new AssertionError();
            }
            material.writeSelfToDB(Init.conexion);
            int unidades = Integer.parseInt(request.getParameter("numeroUnidades"));
            material.setUnidades(unidades, Init.conexion);

            response.sendRedirect("../MenuAdmin.jsp?msg=Material agregado");
            return;
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Agregar Material</title>
        <link rel="stylesheet" href="../estilo.css">
    </head>
    <body>

        <div class="login-container">
            <h1>Agregar Material</h1>


            <p>
                <%= !errorMsg.isEmpty() ? errorMsg : ""%>
            </p>


            <% if (tipo == null || tipo.isEmpty() || "select".equals(step)) { %>

            <form method="POST" action="">
                <input type="hidden" name="step" value="select">

                <div class="form-group">
                    <label for="tipo">Tipo de Material</label>
                    <select name="tipo" id="tipo" required>
                        <option value="">-- Seleccione --</option>
                        <option value="libro">Libro</option>
                        <option value="revista">Revista</option>
                        <option value="cd">CD</option>
                        <option value="dvd">DVD</option>
                        <option value="tesis">Tesis</option>
                    </select>
                </div>

                <div class="button-group">
                    <button type="submit" class="btn-login">Continuar</button>
                    <a href="../MenuAdmin.jsp" class="btn-cancel">Cancelar</a>
                </div>
            </form>

            <% } else {
                List<String> fields = materialFields.get(tipo);
                List<TipoCampo> types = materialTypes.get(tipo);
                List<String> dbFields = materialDBFields.get(tipo);
            %>

            <form method="POST" action="">
                <input type="hidden" name="step" value="guardar">
                <input type="hidden" name="paso" value="guardar">
                <input type="hidden" name="tipo" value="<%= tipo%>">

                <div class="form-group">
                    <label>Tipo</label>
                    <input type="text" value="<%= tipo.toUpperCase()%>" disabled>
                </div>

                <% for (int i = 0; i < fields.size(); i++) {
                        String fieldName = fields.get(i);
                        TipoCampo fieldType = types.get(i);
                        String dbField = dbFields.get(i);
                        String fieldId = fieldName.toLowerCase().replace(" ", "_");
                %>
                <div class="form-group">
                    <label for="<%= fieldId%>"><%= fieldName%></label>

                    <% if (fieldType == TipoCampo.TEXTO) {%>
                    <input type="text" id="<%= fieldId%>" name="<%= fieldId%>" required>

                    <% } else if (fieldType == TipoCampo.NUMERO) {%>
                    <input type="number" id="<%= fieldId%>" name="<%= fieldId%>" min="1" required>

                    <% } else if (fieldType == TipoCampo.OPCION) {

                    %>
                    <select id="<%= fieldId%>" name="<%= fieldId%>" required 
                            onchange="toggleOtro(this, '<%= fieldId%>_otro')">
                        <option value="">-- Seleccione --</option>
                        <% try {
                                java.sql.ResultSet rs = Init.conexion.ejecutarInstruccion(
                                        "SELECT " + dbField + " FROM " + tipo
                                );
                                while (rs != null && rs.next()) {
                                    String temp = rs.getString(dbField);
                        %>
                        <option value="<%=temp%>"><%= temp%></option>
                        <%  }
                                rs.close();
                            } catch (java.sql.SQLException e) {
                                Init.logger.log(Level.WARNING, "error al cargar los datos de la bd", e);
                            }%>
                        <option value="insert">Otro(Escribir)</option>
                    </select>

                    <div id="<%= fieldId%>_otro" style="display: none; margin-top: 8px;">
                        <input type="text" name="<%= fieldId%>_otro" placeholder="Especifique...">
                    </div>
                    <% } %>


                </div>
                <% } %>

                <div class="form-group">
                    <label for="numeroUnidades">Numero de unidades a agregar</label>
                    <input type="number" id="numeroUnidades" name="numeroUnidades" min="1" required>
                </div>

                <div class="button-group">
                    <button type="submit" class="btn-login">Guardar</button>
                    <a href="materialAdd.jsp" class="btn-cancel">Volver</a>
                </div>
            </form>

            <% }%>

        </div>

        <script>
            function toggleOtro(selectElement, otroDivId) {
                var otroDiv = document.getElementById(otroDivId);
                if (selectElement.value === 'insert') {
                    otroDiv.style.display = 'block';
                } else {
                    otroDiv.style.display = 'none';
                }
            }
        </script>
    </body>
</html>