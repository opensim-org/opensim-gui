/*
 * FileLoadDataActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:51 PM
 */

package org.opensim.view.motions;

import junit.framework.*;
import java.io.File;
import java.io.IOException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.MarkerData;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Units;
import org.opensim.utils.FileUtils;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class FileLoadDataActionTest extends TestCase {
    
    public FileLoadDataActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class org.opensim.view.motions.FileLoadDataAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileLoadDataAction instance = new FileLoadDataAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.motions.FileLoadDataAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileLoadDataAction instance = new FileLoadDataAction();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.motions.FileLoadDataAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileLoadDataAction instance = new FileLoadDataAction();
        
        HelpCtx expResult = null;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
