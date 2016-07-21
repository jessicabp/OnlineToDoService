package nz.ac.massey.cs.webtech.ass3.s_13219524.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import nz.ac.massey.cs.webtech.ass3.Todo;
import nz.ac.massey.cs.webtech.ass3.TodoException;
import nz.ac.massey.cs.webtech.ass3.s_13219524.client.ClientTodoManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeNoException;

/**
 *
 * @author Jessica Braddon-Parsons - 13219524
 */
public class TestTodoManager1 {

    ClientTodoManager client;

    public TestTodoManager1() {
        this.client = new ClientTodoManager();
    }

    @Before
    public void setUp() {
        try {
            client.deleteAll();
            Todo newTodo0 = new Todo(0, "Write tests");
            Todo newTodo1 = new Todo(1, "Ensure tests succeed");
            client.insert(newTodo0);
            client.insert(newTodo1);
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
            assumeNoException(ex);
        }
    }

    @After
    public void tearDown() {
        try {
            client.deleteAll();
        } catch (TodoException ex) {
            Logger.getLogger(TestTodoManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
