/*
 * FileOpenOsimModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 11:03 AM
 */

package org.opensim.view;

import java.util.prefs.Preferences;
import junit.framework.*;
import org.opensim.modeling.Model;
import org.opensim.utils.TheApp;

/**
 *
 * @author Ayman
 */
public class FileOpenOsimModelActionTest extends TestCase {
    String defaultDir;
    public FileOpenOsimModelActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        defaultDir = Preferences.userNodeForPackage(TheApp.class).get("WorkDirectory", "testing");
        Preferences.userNodeForPackage(TheApp.class).put("WorkDirectory", "testing");
    }

    protected void tearDown() throws Exception {
        Preferences.userNodeForPackage(TheApp.class).put("WorkDirectory", defaultDir);
    }

    /**
     * Test of performAction method, of class org.opensim.view.FileOpenOsimModelAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        assert(true);
    }

    /**
     * Test of loadModel method, of class org.opensim.view.FileOpenOsimModelAction.
     */
    public void testLoadModel() throws Exception {
        System.out.println("loadModel");
        
        Model aModel = null;
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        
        boolean expResult = true;
        boolean result = instance.loadModel(aModel);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getName method, of class org.opensim.view.FileOpenOsimModelAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        
        String expResult = "Open Model...";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
