package nz.ac.massey.cs.webtech.ass3.s_13219524.server;

import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.servlet.ServletContext;
import nz.ac.massey.cs.webtech.ass3.*;

/**
 * The purpose of this class is to manage the persistency of todo data submitted
 * over the network.
 *
 * @author Jessica Braddon-Parsons - 13219524
 */
public class ServerTodoManager implements TodoManager {

    File dataFile;

    /**
     * Constructor sets up access to xml file holding todo data using relative
     * path
     *
     * @param context
     */
    public ServerTodoManager(ServletContext context) {
        File webInf = new File(context.getRealPath("/WEB-INF/"));
        dataFile = new File(webInf, "data.xml");
    }

    /**
     * Get a list of all stored todos
     *
     * @return List of todos from the xml file, or empty list of file is empty
     * @throws TodoException
     */
    @Override
    public List<Todo> getAll() throws TodoException {
        XMLDecoder decoder;
        try {
            decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(dataFile)));
            List<Todo> listOfTodos = (List<Todo>) decoder.readObject();
            decoder.close();
            return listOfTodos;
        } catch (Exception ex) {
            Logger.getLogger(ServerTodoManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // return empty list if a list of todos is not found
        return new ArrayList<>();
    }

    /**
     * Insert (save) a todo
     *
     * @param todo object to add to xml file
     * @throws TodoException
     */
    @Override
    public void insert(Todo todo) throws TodoException {
        XMLEncoder encoder;
        try {
            // Get list of todos from xml file, add new todo, write back to file
            List<Todo> listOfTodos = getAll();
            listOfTodos.add(todo);
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(dataFile)));
            encoder.writeObject(listOfTodos);
            encoder.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new TodoException();
        }
    }

    /**
     * Delete a todo
     *
     * @param todo to delete from xml file
     * @throws TodoException
     */
    @Override
    public void delete(Todo todo) throws TodoException {
        XMLEncoder encoder;
        try {
            // fetch all todos from xml file
            List<Todo> listOfTodos = getAll();
            for (Todo nextTodo : listOfTodos) {
                if (nextTodo.getId() == todo.getId()) {
                    // if todo to be deleted matches todo in list, remove it
                    listOfTodos.remove(nextTodo);
                    break;
                }
            }
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(dataFile)));
            // write list of remaining todos back to xml
            encoder.writeObject(listOfTodos);
            encoder.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new TodoException();
        }
    }

    /**
     * Get a todo with this id, return null if it does not exist
     *
     * @param id of todo to return
     * @return todo object with given id
     */
    @Override
    public Todo getById(int id) {
        try {
            // fetch list of all todos from xml
            List<Todo> listOfTodos = getAll();
            if (listOfTodos != null) {
                for (Todo nextTodo : listOfTodos) {
                    if (nextTodo.getId() == id) {
                        // if id of todo matches provided it, return the todo
                        return nextTodo;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerTodoManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // return null if a todo with matching id is not found
        return null;
    }

    /**
     * Delete all todos in the xml document
     *
     * @throws TodoException
     */
    void deleteAll() throws TodoException {
        XMLEncoder encoder;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(dataFile)));
            List<Todo> listOfTodos = getAll();
            for (Todo nextTodo : listOfTodos) {
                // for every todo fetched from the xml file, remove it from the xml file
                encoder.remove(nextTodo);
            }
            encoder.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new TodoException();
        }
    }
}
