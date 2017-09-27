/* -------------------------------------------------------------------------- *
 * OpenSim: RunLabAction.java                                                 *
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
 * RunLabAction.java
 *
 * Created on July 26, 2010, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

import java.awt.event.ActionEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.AbstractAction;
import org.opensim.tools.serializers.ToolSerializer;

/**
 *
 * @author ayman
 */
public class RunLabAction extends AbstractAction{
    
    File labFile;
    /**
     * Creates a new instance of RunLabAction
     */
    public RunLabAction(File labFile ) {
        this.labFile=labFile;
        this.putValue("Name", labFile.getName());
    }

    public void actionPerformed(ActionEvent e) {
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
                           new FileInputStream(labFile)));
             Lab lab= (Lab)decoder.readObject();
             lab.execute();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

    }
    
}
