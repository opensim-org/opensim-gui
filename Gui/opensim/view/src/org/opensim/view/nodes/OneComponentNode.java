/* -------------------------------------------------------------------------- *
 * OpenSim: OneComponentNode.java                                             *
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

import java.beans.PropertyEditorSupport;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.AbstractOutput;
import org.opensim.modeling.AbstractSocket;
import org.opensim.modeling.Component;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.StdVectorString;

/**
 *
 * @author Ayman
 */
public class OneComponentNode extends OpenSimObjectNode {
    protected static ResourceBundle bundle = NbBundle.getBundle(OpenSimObjectNode.class);
    protected Component comp;

    public OneComponentNode(OpenSimObject obj) {
        super(obj);
        comp = Component.safeDownCast(obj);
        setShortDescription(comp.getAbsolutePathString());
    }

    public Action[] getActions(boolean b) {
        return super.getActions(b);
    }

    @Override
    public Sheet createSheet() {
        Sheet parentSheet =  super.createSheet(); 
        if (comp.getNumSockets()>0){
            Sheet.Set socketsSheet = parentSheet.createExpertSet();
            socketsSheet.setDisplayName("Sockets");
            parentSheet.put(socketsSheet);
            StdVectorString cNames = comp.getSocketNames();
            for (int i=0; i< comp.getNumSockets();i++ ){
                AbstractSocket ac = comp.getSocket(cNames.get(i));
                createSocketProperty(ac, socketsSheet);
            }
        }
        if (comp.getNumOutputs()>0){
            Sheet.Set outputSet = Sheet.createPropertiesSet(); 
            outputSet.setName("Outputs");
            outputSet.setDisplayName("Outputs (name:type)");
            outputSet.setValue("tabName", "Outputs");
            StdVectorString outputNames = comp.getOutputNames();
            parentSheet.put(outputSet);
            for (int i=0; i< comp.getNumOutputs();i++ ){
                String oName = outputNames.get(i);
                AbstractOutput ac = comp.getOutput(oName);
                createOutputProperty(ac, outputSet);
            }
            parentSheet.put(outputSet);
        }
        return parentSheet;
    }
    
    private void createSocketProperty(AbstractSocket socket, Sheet.Set sheetSet) {
        try {
            String connecteeType = socket.getConnecteeTypeName();
            String connectionName = socket.getName();
            PropertySupport.Reflection nextNodeProp = 
                    new PropertySupport.Reflection(new ConnectionEditor(socket, this),
                    String.class,
                    "getConnectedToPath",
                    "setConnectedToPath");
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
            nextNodeProp.setName(connecteeType + ":" + connectionName);
            PropertyEditorSupport editor = EditorRegistry.getEditor(connecteeType);
            if (editor != null)
                nextNodeProp.setPropertyEditorClass(editor.getClass());
            sheetSet.put(nextNodeProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void createOutputProperty(AbstractOutput anOutput, Sheet.Set sheetSet) {
        try {
            String outputName = anOutput.getName();
            PropertySupport.Reflection nextNodeProp = 
                    new PropertySupport.Reflection(anOutput,
                    String.class,
                    "getTypeName",null);
            nextNodeProp.setValue("canEditAsText", Boolean.FALSE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
            nextNodeProp.setName(outputName);
            // String to show at bottom of Properties panel 
            nextNodeProp.setShortDescription(anOutput.getPathName());
            sheetSet.put(nextNodeProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * @return the comp
     */
    public Component getComp() {
        return comp;
    }
    
}
