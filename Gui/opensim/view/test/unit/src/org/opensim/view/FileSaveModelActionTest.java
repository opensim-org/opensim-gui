/*
 * FileSaveModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:48 PM
 */

package org.opensim.view;

import java.io.IOException;
import junit.framework.*;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.pub.OpenSimDB;
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
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        instance.loadModel(TestEnvironment.getModelPath(), true);
        System.out.println("Loading model is done.");
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of saveOrSaveAsModel method, of class org.opensim.view.FileSaveModelAction.
     */
    @org.junit.Test
    public void testSaveOrSaveAsModel() throws IOException {
        Model aModel = null;
        
        System.out.println("testSaveOrSaveAsModel()");
        
        aModel= new Model(OpenSimDB.getInstance().getCurrentModel());
        boolean expResult = true;
        boolean result = FileSaveModelAction.saveOrSaveAsModel(aModel, false);
        FileSaveModelAction instance = new FileSaveModelAction();
        assertEquals(expResult, result);
    }

    /**
     * Test of performAction method, of class org.opensim.view.FileSaveModelAction.
     */
    @org.junit.Test
    public void testPerformAction() {
        System.out.println("testPerformAction()");        
        FileSaveModelAction instance = new FileSaveModelAction();      
        instance.performAction();
        assert(true);
    }

    /**
     * Test of getName method, of class org.opensim.view.FileSaveModelAction.
     */
    @org.junit.Test
    public void testGetName() {
        System.out.println("testGetName()");
        FileSaveModelAction instance = new FileSaveModelAction();        
        String expResult = "Save Model...";
        String result = instance.getName();
        System.out.println("getName()="+result);
        assertEquals(expResult, result);
    }
}