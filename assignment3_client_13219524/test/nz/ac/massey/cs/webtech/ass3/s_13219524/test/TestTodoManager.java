package nz.ac.massey.cs.webtech.ass3.s_13219524.test;

import java.util.*;
import java.util.logging.*;
import nz.ac.massey.cs.webtech.ass3.*;
import nz.ac.massey.cs.webtech.ass3.s_13219524.client.ClientTodoManager;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *
 * @author Jessica Braddon-Parsons - 13219524
 */
public class TestTodoManager {

    ClientTodoManager client;
    Todo newTodo0 = new Todo(0, "Write tests");
    Todo newTodo1 = new Todo(1, "Ensure tests succeed");

    public TestTodoManager() {
        this.client = new ClientTodoManager();
    }

    /**
     * Runs before every test
     * Deletes all todos, inserts newTodo0 and newTodo1
     * Assumes no exceptions thrown
     */
    @Before
    public void setUp() {
        try {
            client.deleteAll();
            client.insert(newTodo0);
            client.insert(newTodo1);
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            assumeNoException(ex);
        }
    }

    /**
     * Runs after every test
     * Deletes all todos
     * Assumes no exceptions thrown
     */
    @After
    public void tearDown() {
        try {
            client.deleteAll();
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            assumeNoException(ex);
        }
    }

    /**
     * Tests getAll()
     * Assumes no exceptions thrown
     */
    @Test
    public void testGetAll() {
        // add todos to the expected result
        List<Todo> expectedTodos = new ArrayList<>();
        expectedTodos.add(newTodo0);
        expectedTodos.add(newTodo1);

        //get list of todos from server
        List<Todo> listOfTodos = new ArrayList<>();
        try {
            listOfTodos = client.getAll();
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            assumeNoException(ex);
        }

        // check expected and actual lists are the same
        assertEquals(expectedTodos, listOfTodos);
    }

    /**
     * Tests getById() by requesting a todo which exists on the server
     * Assumes no exceptions thrown
     */
    @Test
    public void testValidGetById() {
        Todo expectedTodo = newTodo0;
        Todo returnedTodo = null;

        //get todos from server
        try {
            returnedTodo = client.getById(expectedTodo.getId());
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            assumeNoException(ex);
        }

        // check expected and actual todos are the same
        assertEquals(expectedTodo, returnedTodo);
    }

    /**
     * Tests getById() by requesting a todo which does not exist on the server
     * 
     * @throws TodoException 
     */
    @Test
    public void testInvalidGetById() throws TodoException {
        Todo invalidTodo = new Todo(2, "Delete all work");
        // check a null object is returned when requesting the invalid todo
        assertNull(client.getById(invalidTodo.getId()));
    }

    /**
     * Tests insert()
     * Assumes no exceptions thrown
     */
    @Test
    public void testInsert() {
        // create new todo
        Todo addedTodo = new Todo(2, "Write jsp");
        List<Todo> listOfTodos = null;

        try {
            // send the new todo to be added to the server
            client.insert(addedTodo);
            listOfTodos = client.getAll();
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            assumeNoException(ex);
        }
        
        // assert the list of todos from the server contains the new todo
        assertTrue(listOfTodos.contains(addedTodo));
    }

    /**
     * Tests delete() by deleting a todo which exists on the server
     * Assumes no exceptions thrown
     * 
     * @throws TodoException 
     */
    @Test
    public void testValidDelete() throws TodoException {
        Todo deletedTodo = newTodo0;

        // delete todo from server
        try {
            client.delete(deletedTodo);
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            assumeNoException(ex);
        }

        // assert the deleted todo cannot be fetched from the server
        assertNull(client.getById(deletedTodo.getId()));
    }

    /**
     * Tests delete() by deleting a todo which does not exist on the server
     * 
     * @throws TodoException 
     */
    @Test(expected = TodoException.class)
    public void testInvalidDelete() throws TodoException {
        Todo invalidTodo = new Todo(2, "Delete all work");
        // delete the invalid todo, which should cause a TodoException to be thrown
        client.delete(invalidTodo);
    }
}
