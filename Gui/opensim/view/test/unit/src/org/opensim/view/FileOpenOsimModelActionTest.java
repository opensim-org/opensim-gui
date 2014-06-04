/*
 * FileOpenOsimModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 11:03 AM
 */

package org.opensim.view;

import java.io.File;
import junit.framework.*;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.opensim.modeling.Model;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class FileOpenOsimModelActionTest extends TestCase {
    public FileOpenOsimModelActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {    
        
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class FileOpenOsimModelAction.
     */
    /*
    @org.junit.Test
    public void testPerformAction() {
        System.out.println("testPerformAction()");
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        instance.performAction();
        assert(true);
    }*/

    /**
     * Test of loadModel method, of class FileOpenOsimModelAction.
     */
    @org.junit.Test
    public void testLoadModel_Model() throws Exception {
        System.out.println("testLoadModel_Model()");
        Model aModel = new Model(TestEnvironment.getModelPath());
        System.out.println("model path="+TestEnvironment.getModelPath());
        //aModel= new Model(modelPath);
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        boolean expResult = true;
        boolean result = instance.loadModel(aModel, true);
        Model mdl = OpenSimDB.getInstance().getCurrentModel();
        assert(mdl.isEqualTo(aModel));
    }

    /**
     * Test of openModelFile method, of class FileOpenOsimModelAction.
     */
    @org.junit.Test
    public void testOpenModelFile() throws Exception {
        System.out.println("testOpenModelFile()");
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        instance.openModelFile(TestEnvironment.getModelPath());
        assert(true);
    }

    /**
     * Test of getName method, of class FileOpenOsimModelAction.
     */
    @org.junit.Test
    public void testGetName() {
        
            System.out.println("testGetName()");
            FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
            String expResult = "Open Model...";
            String result = instance.getName();
            assertEquals(expResult, result);
    }
}
