/* -------------------------------------------------------------------------- *
 * OpenSim: PropertyEditorAdaptor.java                                        *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
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
package org.opensim.view.nodes;


import java.awt.Color;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.util.Exceptions;
import org.opensim.modeling.*;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;


/**
 *
 * @author ayman
 */
/**
 *  PropertyEditorAdaptor is an adaptor class to implement the expected signatures for methods to be used
 * by built in property editors. The signatures are along the form:
 * setValueX( X valueToSet)
 * X getValueX()
 * 
 * These signature patterns originate in JavaBeans
 * The actual functionality is implemented by the native side class PropertyHelper that can leverage RTTI
 * @author ayman
 */
public class PropertyEditorAdaptor {

    OpenSimObject obj; // Object being edited or selected in navigator window
    AbstractProperty prop; // Property being edited
    OpenSimContext context = null; // Context object needed to recreate the system as needed, cached for speed
    Model model; // model to which obj belongs
    OpenSimObjectNode node;

    Model clonedModel;
    State clonedState;
    
    public PropertyEditorAdaptor(String propertyName, OpenSimObjectNode ownerNode) {
        this.model = ownerNode.getModelForNode();
        this.context = OpenSimDB.getInstance().getContext(model);
        this.obj = ownerNode.getOpenSimObject();
        this.prop = obj.getPropertyByName(propertyName);
        this.node = ownerNode;
        //defaultPose = new ModelPose(model.getCoordinateSet(), "_saveDefault", true);
    }
    public PropertyEditorAdaptor(AbstractProperty prop, OpenSimObjectNode ownerNode) {
        this.prop = prop;
        this.model = ownerNode.getModelForNode();
        this.context = OpenSimDB.getInstance().getContext(model);
        this.obj = ownerNode.getOpenSimObject();
        this.node = ownerNode;
    }
    /**
     * Constructor that only takes a model. This's NOY to be used for specific Property editing
     * it just leverages the code written to support recreating the system under the model after an edit
     * 
     * @param aModel 
     */
    public PropertyEditorAdaptor(Model aModel) {
        this.model = aModel;
        this.context = OpenSimDB.getInstance().getContext(model);
        this.obj = null;
        this.prop = null;
        this.node = null;
        //defaultPose = new ModelPose(model.getCoordinateSet(), "_saveDefault", true);
    }
    public PropertyEditorAdaptor(Model aModel, OpenSimObject obj, AbstractProperty prop, OpenSimObjectNode node) {
        this.model = aModel;
        this.context = OpenSimDB.getInstance().getContext(model);
        this.obj = obj;
        this.prop = prop;
        this.node = node;
        //defaultPose = new ModelPose(model.getCoordinateSet(), "_saveDefault", true);
    }
    // Double Properties

    public double getValueDouble() {
        return PropertyHelper.getValueDouble(prop);
    }

    public void setValueDouble(double v) {
        setValueDouble(v, true);
    }
    
    public void handlePropertyChangeCommon() {
        if (Geometry.safeDownCast(obj)!= null){
            Component mc = Component.safeDownCast(obj);
            ViewDB.getInstance().updateComponentDisplay(model, mc, prop);
        }
        else 
            ViewDB.getInstance().updateModelDisplay(model);
        if (node!= null) node.refreshNode();
        SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(model);
        guiElem.setUnsavedChangesFlag(true);
    }

    /**
     * set value of property to a double, with optional undo support
     * 
     * @param v
     * @param supportUndo 
     */
    public void setValueDouble(double v, boolean supportUndo) {
        double oldValue = PropertyHelper.getValueDouble(prop);
        handlePropertyChange(oldValue, v, supportUndo);
    }

    public void setValueDouble(Double v) {
        setValueDouble(v.doubleValue());
    }

    // Int Properties
    public int getValueInt() {
        return PropertyHelper.getValueInt(prop);
    }

    public void setValueInt(int v) {
        setValueInt(v, true);
    }

    public void setValueInt(int v, boolean supportUndo) {
        int oldValue = getValueInt();
        handlePropertyChange(oldValue, v, supportUndo);
    }

    public void setValueInt(Integer v) {
        setValueInt(v.intValue());
    }

    // Bool Properties
    public boolean getValueBool() {
        return PropertyHelper.getValueBool(prop);
    }

    public void setValueBool(boolean v) {
        setValueBool(v, true, true);
    }

    public void setValueBool(boolean v, boolean supportUndo, boolean recreateSystem) {
        boolean oldValue = getValueBool();
        handlePropertyChange(oldValue, v, supportUndo, recreateSystem);
    }

    public void setValueBool(Boolean v) {
        setValueBool(v.booleanValue());
    }

    // String Properties
    public String getValueString() {
        return PropertyHelper.getValueString(prop);
    }

    public void setValueString(String v) {
        setValueString(v, true);
    }

    private void setValueString(String v, boolean supportUndo) {
        String oldValue = PropertyHelper.getValueString(prop);
        handlePropertyChange(oldValue, v, supportUndo);
    }

    // Vec3 Properties
    public Vec3 getValueVec3() {
        Vec3 dVec3 = new Vec3(PropertyHelper.getValueVec3(prop, 0),
                PropertyHelper.getValueVec3(prop, 1),
                PropertyHelper.getValueVec3(prop, 2));
        return dVec3;
    }

    public void setValueVec3FromString(String vString) {
        ArrayDouble d = new ArrayDouble();
        d.fromString(vString);
        if (d.getSize() == 3) {
            setValueVec3(new Vec3(d.getitem(0), d.getitem(1), d.getitem(2)));
        }
    }

    public void setValueVec3(Vec3 v) {
        setValueVec3(v, true);
    }

    public void setValueVec3(Vec3 v, boolean supportUndo) {
        Vec3 oldVec3 = new Vec3(PropertyHelper.getValueVec3(prop, 0),
                PropertyHelper.getValueVec3(prop, 1),
                PropertyHelper.getValueVec3(prop, 2));
        handlePropertyChange(oldVec3, v, supportUndo);
    }
    // Vec6
    public Vec6 getValueVec6() {
        Vec6 dVec6 = new Vec6();
        for (int i=0; i< 6; i++)
            dVec6.set(i, PropertyHelper.getValueVec6(prop, i));
        return dVec6;
    }

    public void setValueVec6FromString(String vString) {
        ArrayDouble d = new ArrayDouble();
        d.fromString(vString);
        if (d.getSize() == 6) {
            setValueVec6(new Vec6(d.getitem(0), d.getitem(1), d.getitem(2),
            d.getitem(3), d.getitem(4), d.getitem(5)));
        }
    }

    public void setValueVec6(Vec6 v) {
        setValueVec6(v, true);
    }

    public void setValueVec6(Vec6 v, boolean supportUndo) {
        Vec6 oldVec6 = new Vec6();
        for (int i=0; i< 6; i++)
            oldVec6.set(i, PropertyHelper.getValueVec6(prop, i));
        handlePropertyChange(oldVec6, v, supportUndo);
    }

    public OpenSimObject getValueObj() {
        if (prop.isOneObjectProperty()) {
            return prop.getValueAsObject();
        }
        if (prop.isOptionalProperty() && prop.size() == 1) {
            return prop.getValueAsObject(0);
        }
        return prop.getValueAsObject();
    }

    public void setValueObj(OpenSimObject v, OpenSimObject oldV) {
        setValueObj(v, oldV, true);
    }
    
    public void setValueObj(OpenSimObject v, OpenSimObject oldV, boolean allowUndo) {
        v.updPropertyByIndex(0); // this line is intended to turn on the flag that object is not in sync. wih properties
        handlePropertyChange(oldV, v, allowUndo);
    }

    public GeometryPath getValueObjAsGeometryPath() {
        OpenSimObject obj = getValueObj();
        return GeometryPath.safeDownCast(obj);
    }
    
    public void setValueObjFromGeometryPath(GeometryPath geometryPath) {
        setValueObj(geometryPath, geometryPath, true);
    }
    
    public Function getValueObjAsFunction() {
        OpenSimObject obj = getValueObj();
        return Function.safeDownCast(obj);
    }
    
    public void setValueObjFromFunction(Function function) {
        setValueObj(function, function, true);
    }
    private void setValueDoubleListFromString(String aString, boolean allowUndo) {
        // Parse String into an array of doubles, check that it's the right size for prop then assign
        ArrayDouble d = new ArrayDouble();
        d.fromString(aString);
        if (prop.size() == d.getSize()) {
            assignValueArrayDouble(d, allowUndo);
        }
    }

    public String getPropertyAsString() {
        return prop.toString();
    }
    
    public void setValueStringListFromString(String aString) {
        setValueStringListFromString(aString, true);
    }
    public void setValueStringListFromArrayStr(ArrayStr oldValue, boolean b) {

         String stringStr = new String("(");
         for(int i=0;i<oldValue.getSize(); i++) {
             stringStr = stringStr.concat(oldValue.getitem(i));
             stringStr = stringStr.concat(" ");
         }
         stringStr = stringStr.concat(")");
         setValueStringListFromString(stringStr);
    }

    private void setValueStringListFromString(String aString, boolean allowUndo) {        
        System.out.println("Property type: " + prop.getTypeName());
        
        // Parse String into an array of doubles, check that it's the right size for prop then assign
        ArrayStr d = new ArrayStr();
        // Remove open and close parenth if any
        String workString= new String(aString);
        int liveStart = workString.indexOf("(");
        int liveEnd = workString.indexOf(")");
        if (liveStart!=-1 && liveEnd!=-1){
            workString = workString.substring(liveStart+1, liveEnd);
        }
        else if (liveStart!=liveEnd){
          //throw new ParseException("Illegal format: Expect space separated values, optionally between matched parentheses", liveEnd);
          return;
        }
        String[] splits = workString.split(" ");
        assignValueArrayString(splits, allowUndo);
        
    }
    
    public void setValueDoubleListFromString(String aString) {
        setValueDoubleListFromString(aString, true);
    }

    public Color getPropertyDoubleListAsColor() {
        return new Color((float) PropertyHelper.getValueDouble(prop, 0),
                (float)PropertyHelper.getValueDouble(prop, 1),
                (float)PropertyHelper.getValueDouble(prop, 2));
    }

    public void setValueDoubleListFromColor(Color aColor) {
        ArrayDouble colorAsDoubleArray = new ArrayDouble();
        float[] colorComp  = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        aColor.getComponents(colorComp);
        for(int i=0;i<3;i++) colorAsDoubleArray.append((double)colorComp[i]);
        assignValueArrayDouble(colorAsDoubleArray, true);
    }
    private void assignValueArrayDouble(ArrayDouble d, boolean allowUndo) {
        int sz = prop.size();
        final ArrayDouble oldArray = new ArrayDouble();
        for (int i = 0; i < sz; i++) {
            oldArray.append(PropertyHelper.getValueDouble(prop, i));
        }
        handlePropertyChange(oldArray, d, allowUndo);
    }

    // Transform Property
    private void setValueTransformFromString(String aString, boolean allowUndo) {
        // Parse String into an array of doubles, check that it's the right size for prop then assign
        ArrayDouble d = new ArrayDouble();
        d.fromString(aString);
        if (prop.size() == d.getSize()) {
            assignValueTransform(d, allowUndo);
        }
    }

    public void setValueTransformFromString(String aString) {
        setValueTransformFromString(aString, true);
    }

    private void assignValueTransform(ArrayDouble d, boolean allowUndo) {
        final ArrayDouble oldArray = new ArrayDouble();
        int sz = prop.size();
        for (int i = 0; i < sz; i++) {
            oldArray.append(PropertyHelper.getValueDouble(prop, i));
            //PropertyHelper.setValueTransform(d.getitem(i), prop, i);
        }
        handlePropertyChangeTransform(oldArray, d, allowUndo);
    }
        
    private void handlePropertyChange(final double oldValue, final double v, boolean supportUndo) {
        
        context.cacheModelAndState();
        PropertyHelper.setValueDouble(v, prop);        
        try {
            context.restoreStateFromCachedModel();
        } catch (IOException iae) {
            try {
                 new JOptionPane(iae.getMessage(), 
				JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);
                
            PropertyHelper.setValueDouble(oldValue, prop);
                context.restoreStateFromCachedModel();  
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
        }
        }
        handlePropertyChangeCommon();
        
        if (supportUndo) {  
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueDouble(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueDouble(v, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void handlePropertyChange(final String oldValue, final String v, boolean supportUndo) {
        context.cacheModelAndState();
        PropertyHelper.setValueString(v, prop);        
        try {
            context.restoreStateFromCachedModel();
         } catch (IOException iae) {
            try {
                 new JOptionPane(iae.getMessage(), 
				JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);
                
            PropertyHelper.setValueString(oldValue, prop);
                context.restoreStateFromCachedModel();  
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
        }
        }
        handlePropertyChangeCommon();
        
        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueString(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueString(v, true);
                }
                 @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
           };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void handlePropertyChange(final boolean oldValue, final boolean v, boolean supportUndo, boolean recreateSystem) {
        if (recreateSystem)
            context.cacheModelAndState();
        PropertyHelper.setValueBool(v, prop); 
        if (recreateSystem) {
            try {
                context.restoreStateFromCachedModel();
            } catch (IOException iae) {
                try {
                    new JOptionPane(iae.getMessage(),
                            JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);

                    PropertyHelper.setValueBool(oldValue, prop);
                    context.restoreStateFromCachedModel();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        handlePropertyChangeCommon();

        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueBool(oldValue, false, true);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueBool(v, true, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
           };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void handlePropertyChange(final int oldValue, final int v, boolean supportUndo) {
        context.cacheModelAndState();
        PropertyHelper.setValueInt(v, prop);
        try {
            context.restoreStateFromCachedModel();
        } catch (IOException iae) {
            try {
                new JOptionPane(iae.getMessage(), 
				JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);
                
            PropertyHelper.setValueInt(oldValue, prop);
                context.restoreStateFromCachedModel();  
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
        }
        }
        handlePropertyChangeCommon();

        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueInt(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueInt(v, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void handlePropertyChange(final ArrayDouble oldValue, final ArrayDouble v, boolean supportUndo) {
        context.cacheModelAndState();
        int sz = v.size();
        for (int i = 0; i < sz; i++) {
            PropertyHelper.setValueDouble(v.getitem(i), prop, i);
        }
        try {
            context.restoreStateFromCachedModel();
        } catch (IOException iae) {
            try {
                new JOptionPane(iae.getMessage(),
                        JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);
                int oldSize = oldValue.size();
                for (int i = 0; i < oldSize; i++) {
                PropertyHelper.setValueDouble(oldValue.getitem(i), prop, i);
            }
                context.restoreStateFromCachedModel();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
        }
        }
        handlePropertyChangeCommon();

        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    assignValueArrayDouble(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    assignValueArrayDouble(v, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void handlePropertyChange(final Vec3 oldValue, final Vec3 v, boolean supportUndo) {
        if (supportUndo) context.cacheModelAndState();
        for (int i = 0; i < 3; i++) {
            PropertyHelper.setValueVec3(v.get(i), prop , i);
        }

        try {
            if (supportUndo) context.restoreStateFromCachedModel();
        } catch (IOException iae) {
            try {
                new JOptionPane(iae.getMessage(),
                        JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);

            for (int i = 0; i < 3; i++) {
                    PropertyHelper.setValueVec3(oldValue.get(i), prop, i);
            }
                if (supportUndo) context.restoreStateFromCachedModel();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
        }
        }
        
        handlePropertyChangeCommon();
  
        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueVec3(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueVec3(v, true);
                }

                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
                
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

        private void handlePropertyChange(final Vec6 oldValue, final Vec6 v, boolean supportUndo) {
        if (supportUndo) context.cacheModelAndState();
        for (int i = 0; i < 6; i++) {
            PropertyHelper.setValueVec6(v.get(i), prop , i);
        }

        try {
            if (supportUndo) context.restoreStateFromCachedModel();
        } catch (IOException iae) {
            try {
                new JOptionPane(iae.getMessage(),
                        JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);

            for (int i = 0; i < 6; i++) {
                    PropertyHelper.setValueVec6(oldValue.get(i), prop, i);
            }
                if (supportUndo) context.restoreStateFromCachedModel();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
        }
        }
        
        handlePropertyChangeCommon();
  
        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueVec6(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueVec6(v, true);
                }

                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
                
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void handlePropertyChangeTransform(final ArrayDouble oldValue, final ArrayDouble v, boolean supportUndo) {
        context.cacheModelAndState();
        int sz = prop.size();

        for (int i = 0; i < sz; i++) {
            PropertyHelper.setValueTransform(v.getitem(i), prop, i);
        }

        try {
            context.restoreStateFromCachedModel();
        } catch (IOException iae) {
            try {
                new JOptionPane(iae.getMessage(),
                        JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);

            for (int i = 0; i < sz; i++) {
                PropertyHelper.setValueTransform(oldValue.getitem(i), prop, i);
            }
                context.restoreStateFromCachedModel();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        handlePropertyChangeCommon();

        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    assignValueTransform(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    assignValueTransform(v, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }
    
    private void handlePropertyChange(final OpenSimObject oldObject, final OpenSimObject v, boolean supportUndo) {

        context.cacheModelAndState();
        //prop.setValueAsObject(v);
        try {
            context.restoreStateFromCachedModel();
        } catch (IOException iae) {
            try {
                new JOptionPane(iae.getMessage(), 
				JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);
                
                 prop.setValueAsObject(oldObject);
                context.restoreStateFromCachedModel();  
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        handlePropertyChangeCommon();
        if (false) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueObj(oldObject, v, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueObj(v, oldObject, false);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void assignValueArrayString(String[] splits, boolean allowUndo) {
        int sz = prop.size();
        final ArrayStr oldArray = new ArrayStr();
        for (int i = 0; i < sz; i++) {
            oldArray.append(PropertyHelper.getValueString(prop, i));
        }
        final ArrayStr newArray = new ArrayStr();
        for (int i = 0; i < splits.length; i++) {
            System.out.println("Splits: " + splits[i]);
            newArray.append(splits[i]);
        }
       
        handlePropertyChange(oldArray, newArray, allowUndo);    
    }
    private void handlePropertyChange(final ArrayStr oldValue, final ArrayStr newValue, boolean supportUndo) {
        System.out.println("Property: " + prop.getTypeName());
        System.out.println("Editing string");
        context.cacheModelAndState();    
        System.out.println("Got here though");
        try {
        PropertyHelper.setValueStringArray(prop, newValue);
            System.out.println("Set up string");

            System.out.println("Exception?");
            context.restoreStateFromCachedModel();
         } catch (Exception iae) {
             System.out.println("Caught.");
        try {
                System.out.println("Exception again?");
                new JOptionPane(iae.getMessage(), 
				JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);
                
            PropertyHelper.setValueStringArray(prop, oldValue);
                context.restoreStateFromCachedModel();  
            } catch (Exception ex) {
                System.out.println("Caught again.");
                Exceptions.printStackTrace(ex);
        }
        }
        handlePropertyChangeCommon();

        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueStringListFromArrayStr(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }
 
                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueStringListFromArrayStr(newValue, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo "+prop.getName()+" change";
                }

            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }
    /**
     * Need model and context only. 
     */
    public void handleModelChange()
    {
        try {
            context.cacheModelAndState();
            context.restoreStateFromCachedModel();
            ViewDB.getInstance().updateModelDisplay(model);
            ViewDB.renderAll();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
    }
    }
            
}
