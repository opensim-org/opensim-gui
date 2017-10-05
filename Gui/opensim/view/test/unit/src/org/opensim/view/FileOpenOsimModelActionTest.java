/* -------------------------------------------------------------------------- *
 * OpenSim: FileOpenOsimModelActionTest.java                                  *
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
