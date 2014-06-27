/*
 * FileLoadMotionActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 12:01 PM
 */

package org.opensim.view.motions;

import java.io.File;
import java.io.IOException;
import junit.framework.*;
import org.opensim.modeling.Model;
import org.opensim.view.FileOpenOsimModelAction;
import org.opensim.view.TestEnvironment;
import org.opensim.view.motions.MotionsDB.ModelMotionPair;

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
    public void testPerformAction() throws IOException {
        System.out.println("performAction");
        
        Model aModel = new Model(TestEnvironment.getModelPath());
        System.out.println("model path="+TestEnvironment.getModelPath());
        //aModel= new Model(modelPath);
        FileOpenOsimModelAction instance = new FileOpenOsimModelAction();
        boolean expResult = true;
        boolean result = instance.loadModel(aModel, true);
        FileLoadMotionAction instance2 = new FileLoadMotionAction();
        String motionFilePath = new File(TestEnvironment.getModelPath()).getParent()+
                "\\OutputReference\\InverseKinematics\\arm26_InverseKinematics.mot";
        instance2.loadMotion(motionFilePath);
        ModelMotionPair mp = MotionsDB.getInstance().getCurrentMotion(0);
        assertEquals(mp.model, aModel);
    }

    /**
     * Test of getName method, of class org.opensim.view.motions.FileLoadMotionAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileLoadMotionAction instance = new FileLoadMotionAction();
        
        String expResult = "Load Motion...";
        String result = instance.getName();
        assertEquals(expResult, result);
        
    }

}
