/*
 * FileCloseActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 3:45 PM
 */

package org.opensim.view.actions;

import java.io.IOException;
import junit.framework.*;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.opensim.modeling.Model;
import org.opensim.view.FileOpenOsimModelAction;
import org.opensim.view.TestEnvironment;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class FileCloseActionTest extends TestCase {
    
    public FileCloseActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of closeModel method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testCloseModel() {
        try {
             System.out.println("closeModel");
            Model aModel = new Model(TestEnvironment.getModelPath());
            System.out.println("model path="+TestEnvironment.getModelPath());
            //aModel= new Model(modelPath);
            FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
            boolean expResult = true;
            boolean result = instance.loadModel(aModel, true);
            Model mdl = OpenSimDB.getInstance().getCurrentModel();
            
            FileCloseAction.closeModel(mdl);
            assertEquals(OpenSimDB.getInstance().getCurrentModel(), null);
            
            // TODO review the generated test code and remove the default call to fail.
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            fail("Exception is thrown.");
        }
    }

    /**
     * Test of performAction method, of class org.opensim.view.actions.FileCloseAction.
     *
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileCloseAction instance = new FileCloseAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getName method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileCloseAction instance = new FileCloseAction();
        
        String expResult = "Close Model";
        String result = instance.getName();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileCloseAction instance = new FileCloseAction();
        
        HelpCtx expResult = HelpCtx.DEFAULT_HELP;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of isEnabled method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testIsEnabled() {
        System.out.println("isEnabled");
        
        FileCloseAction instance = new FileCloseAction();
        
        boolean expResult = false;
        boolean result = instance.isEnabled();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        
    }
    
}
