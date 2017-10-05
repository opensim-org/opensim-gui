/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimCustomEditor.java                                          *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class OpenSimCustomEditor extends PropertyEditorSupport
        implements PropertyChangeListener {

    public OpenSimCustomEditor(AbstractProperty ap, OpenSimObjectNode osNode) {
        this.ap = ap;
        opensimNode = osNode;
        model = opensimNode.getModelForNode();
        viewdb = ViewDB.getInstance();
        context = OpenSimDB.getInstance().getContext(model);

    }

    @Override
    public String getJavaInitializationString() {
        return ""; //super.getJavaInitializationString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        //super.setAsText(text);
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
//        System.out.println("PropertyChanged");
//        System.out.println("New:"+((OpenSimObject)pce.getNewValue()).dump());
//        System.out.println("Old:"+((OpenSimObject)pce.getOldValue()).dump());
        PropertyEditorAdaptor pea = new PropertyEditorAdaptor(ap, opensimNode);
        pea.setValueObj((OpenSimObject) pce.getNewValue(), ((OpenSimObject) pce.getOldValue()));
//        context.recreateSystemKeepStage();
//        viewdb.updateModelDisplay(model, objectToEdit);
//        if (opensimNode!= null) opensimNode.refreshNode();
//        SingleModelGuiElements guiElem = viewdb.getModelGuiElements(model);
//        guiElem.setUnsavedChangesFlag(true);

    }
    
    AbstractProperty ap;
    OpenSimContext context;
    Model model;
    OpenSimObjectNode opensimNode;
    ViewDB viewdb;
}
