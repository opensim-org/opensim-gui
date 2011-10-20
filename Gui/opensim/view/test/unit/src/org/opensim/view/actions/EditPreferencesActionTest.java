/*
 * EditPreferencesActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:52 PM
 */

package org.opensim.view.actions;

import junit.framework.*;
import java.util.prefs.BackingStoreException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author Ayman
 */
public class EditPreferencesActionTest extends TestCase {
    
    public EditPreferencesActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class org.opensim.view.actions.EditPreferencesAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        EditPreferencesAction instance = new EditPreferencesAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.actions.EditPreferencesAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        EditPreferencesAction instance = new EditPreferencesAction();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.actions.EditPreferencesAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        EditPreferencesAction instance = new EditPreferencesAction();
        
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
