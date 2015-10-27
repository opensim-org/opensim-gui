/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import junit.framework.TestCase;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author Ayman
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testServer() {
        try {
            Server server = new Server(8080);
            server.start();
            server.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
