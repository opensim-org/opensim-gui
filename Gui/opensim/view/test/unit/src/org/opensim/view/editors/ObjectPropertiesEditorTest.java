/*
 * ObjectPropertiesEditorTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:53 PM
 */

package org.opensim.view.editors;

import junit.framework.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.OpenSimObject;
import org.opensim.utils.FileUtils;

/**
 *
 * @author Ayman
 */
public class ObjectPropertiesEditorTest extends TestCase {
    
    public ObjectPropertiesEditorTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class org.opensim.view.editors.ObjectPropertiesEditor.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        ObjectPropertiesEditor instance = new ObjectPropertiesEditor();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.editors.ObjectPropertiesEditor.
     */
    public void testGetName() {
        System.out.println("getName");
        
        ObjectPropertiesEditor instance = new ObjectPropertiesEditor();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.editors.ObjectPropertiesEditor.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        ObjectPropertiesEditor instance = new ObjectPropertiesEditor();
        
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
