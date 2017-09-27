/* -------------------------------------------------------------------------- *
 * OpenSim: FileSaveModelActionTest.java                                      *
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