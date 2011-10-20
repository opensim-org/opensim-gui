/*
 * FileExportSIMMModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:49 PM
 */

package org.opensim.view;

import junit.framework.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.modeling.SimmFileWriter;
import org.opensim.view.actions.OpenSimToSIMMOptionsJPanel;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class FileExportSIMMModelActionTest extends TestCase {
    
    public FileExportSIMMModelActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isEnabled method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testIsEnabled() {
        System.out.println("isEnabled");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        boolean expResult = true;
        boolean result = instance.isEnabled();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
