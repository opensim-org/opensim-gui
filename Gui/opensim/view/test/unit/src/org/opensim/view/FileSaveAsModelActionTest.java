/*
 * FileSaveAsModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:48 PM
 */

package org.opensim.view;

import junit.framework.*;
import java.io.File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class FileSaveAsModelActionTest extends TestCase {
    
    public FileSaveAsModelActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        
        instance.loadModel("C:\\test\\wrist_0.osim");    
    }

    protected void tearDown() throws Exception {
        OpenSimDB.getInstance().removeModel(OpenSimDB.getInstance().getCurrentModel());
    }

    /**
     * Test of saveModel method, of class org.opensim.view.FileSaveAsModelAction.
     */
    public void testSaveModel() {
        System.out.println("saveModel");
        
        
        String fileName = "saveModelTo.osim";
        if (OpenSimDB.getInstance().getCurrentModel()==null) fail("No model to save");
        FileSaveAsModelAction.saveModel(OpenSimDB.getInstance().getCurrentModel(), fileName);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of performAction method, of class org.opensim.view.FileSaveAsModelAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileSaveAsModelAction instance = new FileSaveAsModelAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.FileSaveAsModelAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileSaveAsModelAction instance = new FileSaveAsModelAction();
        
        String expResult = "Save Model As...";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of isEnabled method, of class org.opensim.view.FileSaveAsModelAction.
     */
    public void testIsEnabled() {
        System.out.println("isEnabled");
        
        FileSaveAsModelAction instance = new FileSaveAsModelAction();
        
        boolean expResult = true;
        boolean result = instance.isEnabled();
        assertEquals(expResult, result);
        
        OpenSimDB.getInstance().removeModel(OpenSimDB.getInstance().getCurrentModel());
        // TODO review the generated test code and remove the default call to fail.
        expResult = false;
        result = instance.isEnabled();
        assertEquals(expResult, result);
    }
    
}
