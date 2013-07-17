package org.opensim.view.nodes;


import java.awt.Color;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.ErrorManager;
import org.opensim.modeling.*;
import org.opensim.utils.Vec3;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;


/**
 *
 * @author ayman
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib, Kevin Xu
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
        //defaultPose = new ModelPose(model.getCoordinateSet(), "_saveDefault", true);
    }
    public PropertyEditorAdaptor(AbstractProperty prop, OpenSimObject obj, Model model, OpenSimObjectNode ownerNode) {
        this.prop = prop;
        this.context = OpenSimDB.getInstance().getContext(model);
        this.model = model;
        this.obj = obj;
        this.node = ownerNode;
        //defaultPose = new ModelPose(model.getCoordinateSet(), "_saveDefault", true);
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
    // Double Properties

    public double getValueDouble() {
        return PropertyHelper.getValueDouble(prop);
    }

    public void setValueDouble(double v) {
        setValueDouble(v, true);
    }
    
    private void handlePropertyChangeCommon() {
        ViewDB.getInstance().updateModelDisplay(model, obj);
        if (node!= null) node.refreshNode();
        SingleModelGuiElements guiElem = ViewDB.getInstance().getModelGuiElements(model);
        guiElem.setUnsavedChangesFlag(true);
    }

    /**
     * set value of property to a double, with optional undo support
     * 
     * @param v
     * @param supportUndo 
     */
    private void setValueDouble(double v, boolean supportUndo) {
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

    private void setValueInt(int v, boolean supportUndo) {
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
        setValueBool(v, true);
    }

    private void setValueBool(boolean v, boolean supportUndo) {
        boolean oldValue = getValueBool();
        handlePropertyChange(oldValue, v, supportUndo);
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

    private void setValueVec3(Vec3 v, boolean supportUndo) {
        Vec3 oldVec3 = new Vec3(PropertyHelper.getValueVec3(prop, 0),
                PropertyHelper.getValueVec3(prop, 1),
                PropertyHelper.getValueVec3(prop, 2));
        handlePropertyChange(oldVec3, v, supportUndo);
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

    private void cacheModelAndState() {

        clonedModel = (Model)model.clone();
        clonedState = context.getCurrentStateCopy();
        
    }
    
    private void restoreStateFromCachedModel() {

        try {
            model.initSystem();
            clonedModel.initSystem();

        } catch (Exception e) {
            // Any issues in initSystem gets thrown
            IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
            throw iae;
        }
        // Find all the state variables
        ArrayStr modelVariableNames = model.getStateVariableNames();
        ArrayStr clonedModelVariableNames = clonedModel.getStateVariableNames();

        for(int i = 0; i < modelVariableNames.getSize(); i++) {

            // Going through the state variables in the model
            String name = modelVariableNames.getitem(i);

            // If finds it in the clonedState, copies value over
            if(clonedModelVariableNames.findIndex(name) >=0 ) {
                double value = clonedModel.getStateVariable(clonedState, name);
                model.setStateVariable(model.getWorkingState(), name, value); 
            }
        }

        context.setState(model.getWorkingState());
        context.realizePosition();
    }
    
    private void handlePropertyChange(final double oldValue, final double v, boolean supportUndo) {
        
        cacheModelAndState();
        PropertyHelper.setValueDouble(v, prop);        
        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            PropertyHelper.setValueDouble(oldValue, prop);
            restoreStateFromCachedModel();  
            throw iae;
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
        cacheModelAndState();
        PropertyHelper.setValueString(v, prop);        
        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            PropertyHelper.setValueString(oldValue, prop);
            restoreStateFromCachedModel();  
            throw iae;
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

    private void handlePropertyChange(final boolean oldValue, final boolean v, boolean supportUndo) {
        cacheModelAndState();
        PropertyHelper.setValueBool(v, prop);        
        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            PropertyHelper.setValueBool(oldValue, prop);
            restoreStateFromCachedModel();  
            throw iae;
        }
        handlePropertyChangeCommon();

        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setValueBool(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+prop.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setValueBool(v, true);
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
        cacheModelAndState();
        PropertyHelper.setValueInt(v, prop);
        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            PropertyHelper.setValueInt(oldValue, prop);
            restoreStateFromCachedModel();  
            throw iae;
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
        cacheModelAndState();
        int sz = v.size();
        for (int i = 0; i < sz; i++) {
            PropertyHelper.setValueDouble(v.getitem(i), prop, i);
        }
        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            for (int i = 0; i < sz; i++) {
                PropertyHelper.setValueDouble(oldValue.getitem(i), prop, i);
            }
            restoreStateFromCachedModel();  
            throw iae;
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
        cacheModelAndState();
        for (int i = 0; i < 3; i++) {
            PropertyHelper.setValueVec3(v.get()[i], prop , i);
        }

        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            for (int i = 0; i < 3; i++) {
                PropertyHelper.setValueVec3(oldValue.get()[i], prop , i);
            }
            restoreStateFromCachedModel();  
            throw iae;
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

    private void handlePropertyChangeTransform(final ArrayDouble oldValue, final ArrayDouble v, boolean supportUndo) {
        cacheModelAndState();
        int sz = prop.size();

        for (int i = 0; i < sz; i++) {
            PropertyHelper.setValueTransform(v.getitem(i), prop, i);
        }

        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            for (int i = 0; i < sz; i++) {
                PropertyHelper.setValueTransform(oldValue.getitem(i), prop, i);
            }
            restoreStateFromCachedModel();  
            throw iae;
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

        //context.setPropertiesFromState();
        //prop.setValueAsObject(v);
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
            newArray.append(splits[i]);
        }
       
        handlePropertyChange(oldArray, newArray, allowUndo);    
    }
    private void handlePropertyChange(final ArrayStr oldValue, final ArrayStr newValue, boolean supportUndo) {
        cacheModelAndState();    
        PropertyHelper.setValueStringArray(prop, newValue);
        try {
            restoreStateFromCachedModel();
        } catch (IllegalArgumentException iae) {
            ErrorManager.getDefault().annotate(iae, ErrorManager.ERROR, null, iae.getMessage(), null, null);
            PropertyHelper.setValueStringArray(prop, oldValue);
            restoreStateFromCachedModel();  
            throw iae;
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
        cacheModelAndState();
        restoreStateFromCachedModel();
    }
            
}
