/*
 * FileCloseActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 3:45 PM
 */

package org.opensim.view.actions;

import junit.framework.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.FileSaveModelAction;
import org.opensim.view.ModelSettingsSerializer;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

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
        System.out.println("closeModel");
        
        Model model = null;
        
        boolean expResult = true;
        boolean result = FileCloseAction.closeModel(model);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of performAction method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileCloseAction instance = new FileCloseAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileCloseAction instance = new FileCloseAction();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileCloseAction instance = new FileCloseAction();
        
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isEnabled method, of class org.opensim.view.actions.FileCloseAction.
     */
    public void testIsEnabled() {
        System.out.println("isEnabled");
        
        FileCloseAction instance = new FileCloseAction();
        
        boolean expResult = true;
        boolean result = instance.isEnabled();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
