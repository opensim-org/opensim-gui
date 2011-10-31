/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opensim.modeling.Model;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Jingjing
 */
public class FileSaveAsModelActionTest {

    public FileSaveAsModelActionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        instance.loadModel(TestEnvironment.getModelPath(), true);
        System.out.println("Loading model is done.");
    }

    @After
    public void tearDown() {
        OpenSimDB.getInstance().removeModel(OpenSimDB.getInstance().getCurrentModel());
    }

     /**
     * Test of performAction method, of class org.opensim.view.FileSaveAsModelAction.
     */
    public void testPerformAction() {
        System.out.println("testPerformAction()");
        FileSaveAsModelAction instance = new FileSaveAsModelAction();
        instance.performAction();
    }

    /**
     * Test of saveAsModel method, of class FileSaveAsModelAction.
     */
    @Test
    public void testSaveModel() throws IOException {
        System.out.println("testSaveModel()");
        String fileName = "saveAsModelTo.osim";
        if (OpenSimDB.getInstance().getCurrentModel()== null) fail("No model to save");
        FileSaveAsModelAction.saveModel(OpenSimDB.getInstance().getCurrentModel(), fileName);
        Model reconstructed = new Model(fileName);
        assert(reconstructed.isEqualTo(OpenSimDB.getInstance().getCurrentModel()));
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
    }   
}