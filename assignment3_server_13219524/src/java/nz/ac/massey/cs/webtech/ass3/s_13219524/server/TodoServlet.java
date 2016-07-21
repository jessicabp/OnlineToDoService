package nz.ac.massey.cs.webtech.ass3.s_13219524.server;

import java.io.*;
import java.util.List;
import java.util.logging.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import nz.ac.massey.cs.webtech.ass3.*;
import org.json.*;

/**
 * Servlet mapped to the URL /todos
 *
 * @author Jessica Braddon-Parsons - 13219524
 */
@WebServlet(name = "TodoServlet", urlPatterns = {"/todos/*"})
public class TodoServlet extends HttpServlet {

    /**
     * Handles the HTTP GET method to get a todo or list of todos
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String givenID = request.getPathInfo();
        if (givenID == null) {
            // if url ends with "/todos" call function to put all todos in response
            showAllTodos(request, response);
        } else {
            // if url ends with "/<id>" call function to put todo with matching id in response
            int givenIDNumber = Integer.parseInt(givenID.substring(1));
            getSingleTodo(request, response, givenIDNumber);
        }
    }

    /**
     * Fetch todo with the given id and return encoded in json, return status
     * code 404 if such a resource does not exist
     *
     * @param request
     * @param response
     * @param givenID of todo to return in response
     */
    protected void getSingleTodo(HttpServletRequest request, HttpServletResponse response, int givenID) {
        ServerTodoManager manager = new ServerTodoManager(this.getServletContext());
        Todo todo = manager.getById(givenID);
        try {
            if (todo == null) {
                // if there is no todo with matching id, return status code 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                // write json encoded todo with matching id to the response
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                JSONObject json = new JSONObject(todo);
                PrintWriter out = response.getWriter();
                out.print(json.toString());
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (IOException ex) {
            Logger.getLogger(TodoServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch all objects and return in list encoded in json
     *
     * @param request
     * @param response
     */
    void showAllTodos(HttpServletRequest request, HttpServletResponse response) {
        ServerTodoManager manager = new ServerTodoManager(this.getServletContext());
        try {
            // write json encoded list of all todos to the response
            List<Todo> listOfTodos = manager.getAll();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JSONArray json = new JSONArray(listOfTodos);
            PrintWriter out = response.getWriter();
            out.print(json.toString());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException | TodoException ex) {
            Logger.getLogger(TodoServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles the HTTP POST method to create a new todo
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            ServerTodoManager manager = new ServerTodoManager(this.getServletContext());

            // read json encoded todo from the servlet request
            BufferedReader br = request.getReader();
            String result;
            String output = "";
            while ((result = br.readLine()) != null) {
                output += result;
            }
            JSONObject json = new JSONObject(output);

            // create new todo and fill with information from json encoded todo
            Todo newTodo = new Todo();
            newTodo.setId(json.getInt("id"));
            newTodo.setText(json.getString("text"));
            newTodo.setTimeCreated(json.getLong("timeCreated"));

            // if a todo with the given id already exists, increment the id until an unused id is found
            Todo todoAtId = manager.getById(newTodo.getId());
            while (todoAtId != null) {
                int id = newTodo.getId() + 1;
                newTodo.setId(id);
                todoAtId = manager.getById(id);
            }

            // add the todo to the xml file
            manager.insert(newTodo);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (JSONException | TodoException | IOException ex) {
            Logger.getLogger(TodoServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles the HTTP DELETE method to delete all todos or a single todo and
     * return status code 404 if such a todo doesn't exist
     *
     * @param request
     * @param response
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        ServerTodoManager manager = new ServerTodoManager(this.getServletContext());
        String givenID = request.getPathInfo();
        try {
            if (givenID == null) {
                // if url ends with "/todos" delete all todos from xml file
                manager.deleteAll();
            } else {
                // if url ends with "/<id>" delete todo with matching id
                int id = Integer.parseInt(givenID.substring(1));
                Todo todoToRemove = manager.getById(id);
                // if no todo has a matching id return status code 404
                if (todoToRemove == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                manager.delete(todoToRemove);
            }
        } catch (TodoException | IOException ex) {
            Logger.getLogger(TodoServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet to receive HTTP Requests and interface with ServerTodoManager";
    }// </editor-fold>

}
