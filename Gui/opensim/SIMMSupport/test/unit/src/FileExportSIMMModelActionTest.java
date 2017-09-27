/* -------------------------------------------------------------------------- *
 * OpenSim: FileExportSIMMModelActionTest.java                                *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
/*
 * FileExportSIMMModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:49 PM
 */



import java.io.File;
import java.io.IOException;
import org.openide.util.Exceptions;
import org.opensim.simmsupport.FileExportSIMMModelAction;
import junit.framework.*;
import org.openide.util.HelpCtx;
import org.opensim.modeling.Model;
import org.opensim.modeling.SimmFileWriter;
import org.opensim.view.FileOpenOsimModelAction;
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
        
        Model aModel=null;
        try {
            aModel = new Model(TestEnvironment.getModelPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.out.println("model path="+TestEnvironment.getModelPath());
        //aModel= new Model(modelPath);
        FileOpenOsimModelAction instanceOpen = new FileOpenOsimModelAction();
        try {
            boolean result = instanceOpen.loadModel(aModel, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        SimmFileWriter modelWriter=new SimmFileWriter(OpenSimDB.getInstance().getCurrentModel());
        File f = new File(TestEnvironment.getModelPath());
        modelWriter.writeJointFile(f.getParent()+"//jntfileName.jnt");        
        modelWriter.writeMuscleFile(f.getParent()+"//musclefileName.msl");        
        OpenSimDB.getInstance().removeModel(aModel);
        // Compare output to std
    }

    /**
     * Test of getName method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        String expResult = "Export SIMM Model...";
        String result = instance.getName();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        HelpCtx expResult = HelpCtx.DEFAULT_HELP;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of isEnabled method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testIsEnabled() {
        System.out.println("isEnabled");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        boolean expResult = false;
        boolean result = instance.isEnabled();
        assertEquals(expResult, result);
        
        
    }
    
}
