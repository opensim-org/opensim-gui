/* -------------------------------------------------------------------------- *
 * OpenSim: AppearanceHelper.java                                             *
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

import java.awt.Color;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Appearance;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.Vec3;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman-NMBL
 */
public class AppearanceHelper {

    Appearance appearance = null;
    OneComponentNode compNode = null;
    Model model;
    
    public AppearanceHelper(Model model, OneComponentNode node, Appearance dAppearance){
        this.model = model;
        this.compNode = node;
        this.appearance = dAppearance;
    }
    
    void setAppearanceDisplayPrefProperty(int pref) {
        final AbstractProperty ap = appearance.upd_SurfaceProperties().updPropertyByName("representation");
        final DecorativeGeometry.Representation oldRep = appearance.get_representation();
        final int newRep = pref;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance.upd_SurfaceProperties(), ap, compNode);
        ap.setValueIsDefault(false);
        pea.setValueInt(newRep, false);
        // Delay update display till end
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueInt(oldRep.swigValue(), false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo representation change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueInt(newRep, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo representation change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    void setAppearanceVisilibityProperty(Boolean newValue) {
        final AbstractProperty ap = appearance.getPropertyByName("visible");
        final boolean oldVis = appearance.get_visible();
        final boolean newVis = newValue;
        final Model model = this.model;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance, ap, compNode);
        ap.setValueIsDefault(false);
        pea.setValueBool(newVis, false, false);
        // Delay update display till end
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueBool(oldVis, false, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo visibility change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueBool(newVis, false, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo visibility change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    void setAppearanceColorProperty(Color newColor) {
        float[] colorComp = new float[3];
        newColor.getColorComponents(colorComp);
        final AbstractProperty ap = appearance.updPropertyByName("color");
        final Model model = this.model;
        final Vec3 oldValue = new Vec3(appearance.get_color());
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance, ap, compNode);
        ap.setValueIsDefault(false);
        final Vec3 newColorVec3 = new Vec3(colorComp[0], colorComp[1], colorComp[2]);
        pea.setValueVec3(newColorVec3, false);
        // Delay update display till end
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        //System.out.println("p, c"+ap.toString()+compNode.comp.dump());
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueVec3(oldValue, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo color change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueVec3(newColorVec3, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo color change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    void setAppearanceOpacityProperty(double opacity) {
        final AbstractProperty ap = appearance.updPropertyByName("opacity");
        final double oldOpacity = appearance.get_opacity();
        final double newOpacity = opacity;
        final Model model = this.model;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance, ap, compNode);
        ap.setValueIsDefault(false);
        pea.setValueDouble(newOpacity, false);
        // Delay update display till end
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueDouble(oldOpacity, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo opacity change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueDouble(newOpacity, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo opacity change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }
    
}
