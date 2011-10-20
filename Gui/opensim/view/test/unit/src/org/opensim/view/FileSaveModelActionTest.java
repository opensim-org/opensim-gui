/*
 * FileSaveModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:48 PM
 */

package org.opensim.view;

import junit.framework.*;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class FileSaveModelActionTest extends TestCase {
    
    public FileSaveModelActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of saveOrSaveAsModel method, of class org.opensim.view.FileSaveModelAction.
     */
    public void testSaveOrSaveAsModel() {
        System.out.println("saveOrSaveAsModel");
        
        Model model = null;
        
        boolean expResult = true;
        boolean result = FileSaveModelAction.saveOrSaveAsModel(model);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of performAction method, of class org.opensim.view.FileSaveModelAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileSaveModelAction instance = new FileSaveModelAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.FileSaveModelAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileSaveModelAction instance = new FileSaveModelAction();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.FileSaveModelAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileSaveModelAction instance = new FileSaveModelAction();
        
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isEnabled method, of class org.opensim.view.FileSaveModelAction.
     */
    public void testIsEnabled() {
        System.out.println("isEnabled");
        
        FileSaveModelAction instance = new FileSaveModelAction();
        
        boolean expResult = true;
        boolean result = instance.isEnabled();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
