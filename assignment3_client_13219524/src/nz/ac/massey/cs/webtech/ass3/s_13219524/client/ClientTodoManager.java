package nz.ac.massey.cs.webtech.ass3.s_13219524.client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import nz.ac.massey.cs.webtech.ass3.*;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.*;

/**
 * This class uses http to send the actual requests to insert, delete or fetch
 * todos on the server
 *
 * @author Jessica Braddon-Parsons - 13219524
 */
public class ClientTodoManager implements TodoManager {

    private final String USER_AGENT = "Mozilla/5.0";

    /**
     * Generates the HTTP GET method to get a todo or list of todos
     *
     * @param id of todo to get, null to get all todos
     * @return HttpResponse from server
     */
    private HttpResponse sendGet(String id) {
        try {
            URI uri;
            if (id == null) {
                uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath("/assignment3_server_13219524/todos").build();
            } else {
                uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath("/assignment3_server_13219524/todos/" + id).build();
            }
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(uri);
            request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response = client.execute(request);
            return response;
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(ClientTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Generates the HTTP POST method to add a new todo
     *
     * @param todo to add to server
     * @return HttpResponse from server
     */
    private HttpResponse sendPost(Todo todo) {
        try {
            URI uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath("/assignment3_server_13219524/todos").build();
            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(uri);
            post.setHeader("User-Agent", USER_AGENT);

            // encode given todo in json and add to post request to send
            JSONObject json = new JSONObject(todo);
            StringEntity todoEntity = new StringEntity(json.toString());
            post.setHeader("Content-Type", "application/json");
            post.setEntity(todoEntity);

            HttpResponse response = client.execute(post);
            return response;
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(ClientTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Generates the HTTP DELETE method to delete one or all todos
     *
     * @param id of todo to be deleted, null to delete all todos
     * @return HttpResponse from server
     */
    private HttpResponse sendDelete(String id) {
        try {
            URI uri;
            if (id == null) {
                uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath("/assignment3_server_13219524/todos").build();
            } else {
                uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath("/assignment3_server_13219524/todos/" + id).build();
            }
            HttpClient client = HttpClients.createDefault();
            HttpDelete request = new HttpDelete(uri);
            request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response = client.execute(request);
            return response;
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(ClientTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Get a list of all stored todos
     *
     * @return a list of all todos
     * @throws TodoException
     */
    @Override
    public List<Todo> getAll() throws TodoException {
        try {
            HttpResponse response = sendGet(null);

            // read json encoded todos from the servlet response
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String result;
            String output = "";
            while ((result = br.readLine()) != null) {
                output += result;
            }
            JSONArray json = new JSONArray(output);

            // create and return list of todos from json encoded todos
            List<Todo> listOfTodos = new ArrayList<>();
            int len = json.length();
            for (int i = 0; i < len; i++) {
                Todo nextTodo = new Todo();
                JSONObject todoJson = (JSONObject) json.get(i);
                nextTodo.setId(todoJson.getInt("id"));
                nextTodo.setText(todoJson.getString("text"));
                nextTodo.setTimeCreated(todoJson.getLong("timeCreated"));
                listOfTodos.add(nextTodo);
            }
            return listOfTodos;
        } catch (IOException | UnsupportedOperationException | JSONException ex) {
            Logger.getLogger(ClientTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Insert a todo
     *
     * @param todo to add to the server
     * @throws TodoException
     */
    @Override
    public void insert(Todo todo) throws TodoException {
        try {
            // call function to send POST to server
            sendPost(todo);
        } catch (Exception ex) {
            Logger.getLogger(ClientTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new TodoException();
        }
    }

    /**
     * Delete a todo
     *
     * @param todo to delete from the server
     * @throws TodoException thrown if deleting the todo fails
     */
    @Override
    public void delete(Todo todo) throws TodoException {
        String id = Integer.toString(todo.getId());
        // call function to send DELETE to server, and throw TodoException if returned status code is 404
        HttpResponse response = sendDelete(id);
        if (response.getStatusLine().getStatusCode() == 404) {
            throw new TodoException();
        }
    }

    /**
     * Get a todo with this id, return null if it does not exist
     *
     * @param id of todo to return
     * @return todo with matching id, null if todo not found
     * @throws TodoException
     */
    @Override
    public Todo getById(int id) throws TodoException {
        try {
            HttpResponse response = sendGet(Integer.toString(id));
            // return null if HttpResponse has status code 404 (indicating todo with given id was not found)
            if (response.getStatusLine().getStatusCode() == 404) {
                return null;
            }

            // read json encoded todo from the servlet response
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String result;
            String output = "";
            while ((result = br.readLine()) != null) {
                output += result;
            }
            JSONObject json = new JSONObject(output);

            // create and return todo from json encoding
            Todo resultTodo = new Todo();
            resultTodo.setId(json.getInt("id"));
            resultTodo.setText(json.getString("text"));
            resultTodo.setTimeCreated(json.getLong("timeCreated"));
            return resultTodo;
        } catch (IOException | JSONException ex) {
            Logger.getLogger(ClientTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new TodoException();
        }
    }

    /**
     * Delete all todos
     * 
     * @throws TodoException 
     */
    public void deleteAll() throws TodoException {
        // call function to send DELETE to server, and throw TodoException if returned status code is 404
        HttpResponse response = sendDelete(null);
        if (response.getStatusLine().getStatusCode() == 404) {
            throw new TodoException();
        }
    }
}
