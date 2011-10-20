/*
 * FileLoadMotionActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 12:01 PM
 */

package org.opensim.view.motions;

import junit.framework.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class FileLoadMotionActionTest extends TestCase {
    
    public FileLoadMotionActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class org.opensim.view.motions.FileLoadMotionAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileLoadMotionAction instance = new FileLoadMotionAction();
        
        instance.performAction();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.motions.FileLoadMotionAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileLoadMotionAction instance = new FileLoadMotionAction();
        
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
