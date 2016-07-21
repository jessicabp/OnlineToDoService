<%-- 
    Document   : index.jsp
    Created    : May 2016
    Author     : Jessica Braddon-Parsons - 13219524
--%>

<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="nz.ac.massey.cs.webtech.ass3.s_13219524.server.ServerTodoManager"%>
<%@page import="java.util.List"%>
<%@page import="nz.ac.massey.cs.webtech.ass3.Todo"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Todo Viewer</title>
        <script type="text/javascript" src="jquery-1.12.4.min.js"></script>
        <script>
            function colourSchemeChange() {
                // change the colour scheme to match the preference selected in the drop down
                var colourScheme = $('#colourSchemeSelector').val();
                if (colourScheme == 'light') {
                    $('body, h1, table, th, td').css({"color": "black", "background-color": "white"});
                } else {
                    $('body, h1, table, th, td').css({"color": "white", "background-color": "black"});
                }
            }

            function filterTodos() {
                var rows = document.getElementsByClassName("todo");
                var searchterm = document.getElementById("search").value.toLowerCase();

                // for every row (containing one todo), if the text column contains the searchterm, make it visible, otherwise make it invisible
                for (var i = 0; i < rows.length; i++) {
                    var rowData = rows[i].getElementsByTagName("td");
                    rowData = rowData[1].innerHTML.toLowerCase();
                    if (rowData) {
                        if (searchterm.length == 0 || rowData.indexOf(searchterm) > -1) {
                            rows[i].style.display = "";
                        } else {
                            rows[i].style.display = "none";
                        }
                    }
                }
            }

            function reset() {
                // make all todo items visible and set the search value to be nothing
                document.getElementById("search").value = "";
                var rows = document.getElementsByClassName("todo");
                for (var i = 0; i < rows.length; i++) {
                    rows[i].style.display = "";
                }
            }
        </script>
        <style>
            body, h1, table, th, td {
                color: black;
                background-color: white;
            }

            table {
                border-collapse: collapse;
                width: 100%;
            }

            th, td {
                padding: 8px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }
        </style>
    </head>
    <body>
        <h1>Todos</h1>

        <hr>
        <p>Colour Scheme:</p>
        <select id='colourSchemeSelector' name='colourSchemeSelector' onchange='colourSchemeChange()'>
            <option value='light' title='light'>Default</option>
            <option value='dark' title='dark'>Energy Saver</option>
        </select>

        <br><br>

        <p>Search Todos:</p>
        <input type="text" size="30" name="search" id="search" />
        <button type="button" onclick="filterTodos()">Submit</button>

        <button type="button" onclick="reset()">Reset</button>

        <hr>

        <table id="table">
            <tr>
                <th>ID</th>
                <th>Text</th>
                <th>Time Created</th>
            </tr>
            <%
                ServerTodoManager manager = new ServerTodoManager(this.getServletContext());
                List<Todo> listOfTodos = manager.getAll();
                if (listOfTodos != null) {
                    for (Todo nextTodo : listOfTodos) {
                        out.println("<tr class=\"todo\" id=\"" + nextTodo.getId() + "\" >");
                        out.println("<td>" + nextTodo.getId() + "</td>");
                        out.println("<td>" + nextTodo.getText() + "</td>");
                        out.println("<td>" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Timestamp(nextTodo.getTimeCreated())) + "</td>");
                        out.println("</tr>");
                    }
                }
            %>
        </table>
    </body>
</html>
