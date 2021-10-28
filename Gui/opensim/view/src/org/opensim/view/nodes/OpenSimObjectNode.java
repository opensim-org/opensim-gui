/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimObjectNode.java                                            *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Paul Mitiguy                                       *
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
 *
 * OpenSimObjectNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.nodes;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.Reflection;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import static org.openide.nodes.Sheet.createExpertSet;
import static org.openide.nodes.Sheet.createPropertiesSet;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Function;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Model;
import org.opensim.modeling.Component;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.XYFunctionInterface;
import org.opensim.view.ObjectDisplayMenuAction;
import org.opensim.view.ObjectDisplayShowAction;
import org.opensim.view.OpenSimBaseObjectProperty;
import org.opensim.view.OpenSimFunctionProperty;
import org.opensim.view.OpenSimGeometryPathProperty;
import org.opensim.view.OpenSimObjectProperty;
import org.opensim.view.actions.ObjectDisplaySelectAction;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman. A node backed by an OpenSim Object 
 * (or a Set as a set is also an Object)
 */
public class OpenSimObjectNode extends OpenSimNode {

    private OpenSimObject openSimObject;

    protected String getPropertyComment(String string) {
            return getOpenSimObject().getPropertyByName(string).getComment();
   }

    private Reflection createNodePropForOpenSimNoListProperty(OpenSimObject obj, int p, Model model) throws NoSuchMethodException {
        PropertySupport.Reflection nextNodeProp=null;
        AbstractProperty ap = obj.getPropertyByIndex(p);
        String apType = ap.getTypeName();
        if (apType.equalsIgnoreCase("double")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                getClassForTypeName(ap.getTypeName()),
                "getValueDouble",
                "setValueDouble");
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       }
       else if (apType.equalsIgnoreCase("int")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                getClassForTypeName(ap.getTypeName()),
                "getValueInt",
                "setValueInt");
             nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
             nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       }
       else if (apType.equalsIgnoreCase("bool")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                getClassForTypeName(ap.getTypeName()),
                "getValueBool",//mapPropertyEnumToGetters.get(currentPropType),
                "setValueBool");//mapPropertyEnumToSetters.get(currentPropType));
            //nextNodeProp.setValue("canEditAsText", Boolean.FALSE);
            //nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       }
       else if (apType.equalsIgnoreCase("string")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                getClassForTypeName(ap.getTypeName()),
                "getValueString",//mapPropertyEnumToGetters.get(currentPropType),
                "setValueString");//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.FALSE);
       }
       else if (apType.equalsIgnoreCase("Vec3")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                String.class,
                "getPropertyAsString",//mapPropertyEnumToGetters.get(currentPropType),
                "setValueVec3FromString");//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       }
       else if (apType.equalsIgnoreCase("Vec6")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                String.class,
                "getPropertyAsString",//mapPropertyEnumToGetters.get(currentPropType),
                "setValueVec6FromString");//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       }
       else { // fall through, unsupported for now
           nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                String.class,
                "getPropertyAsString",//mapPropertyEnumToGetters.get(currentPropType),
                null);//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.FALSE);
       }
       return nextNodeProp;
    }

    private Reflection createCustomNodePropertyAndEditor(AbstractProperty ap, OpenSimObject obj, Model model, Reflection nextNodeProp) throws NoSuchMethodException {
        // already know its not list, could be optional though
        // Custom editor for geometry
        if (ap.getTypeName().toLowerCase().equalsIgnoreCase("GeometryPath")){
          nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
             GeometryPath.class,
             "getValueObjAsGeometryPath",//mapPropertyEnumToGetters.get(currentPropType),
             "setValueObjFromGeometryPath");//mapPropertyEnumToSetters.get(currentPropType));              
          nextNodeProp.setPropertyEditorClass(OpenSimGeometryPathEditor.class);
        }
        else if (ap.getTypeName().toLowerCase().equalsIgnoreCase("Function")){
          nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
             Function.class,
             "getValueObjAsFunction",//mapPropertyEnumToGetters.get(currentPropType),
             null);//mapPropertyEnumToSetters.get(currentPropType));              
          nextNodeProp.setPropertyEditorClass(OpenSimFunctionEditor.class);
        }
        else 
         nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
             OpenSimObject.class,
             "getValueObj",//mapPropertyEnumToGetters.get(currentPropType),
             "setValueObj");//mapPropertyEnumToSetters.get(currentPropType));
        nextNodeProp.setValue("canEditAsText", Boolean.FALSE);
        nextNodeProp.setValue("suppressCustomEditor", Boolean.FALSE);
        return nextNodeProp;
    }

    private Reflection createNodePropForOpenSimListProperty(OpenSimObject obj, int p, Model model) throws NoSuchMethodException {
       PropertySupport.Reflection nextNodeProp=null;
        AbstractProperty ap = obj.getPropertyByIndex(p);
        String apType = ap.getTypeName();
        if (apType.equalsIgnoreCase("double")){
            if (ap.getName().toLowerCase().contains("color")){
                nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                    Color.class,
                    "getPropertyDoubleListAsColor",//mapPropertyEnumToGetters.get(currentPropType),
                    "setValueDoubleListFromColor");
            }
            else
                nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                    String.class,
                    "getPropertyAsString",//mapPropertyEnumToGetters.get(currentPropType),
                    "setValueDoubleListFromString");//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.FALSE);
       }
       else if (apType.equalsIgnoreCase("Transform")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                String.class,
                "getPropertyAsString",//mapPropertyEnumToGetters.get(currentPropType),
                "setValueTransformFromString");//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.FALSE);
       }
       else if (apType.equalsIgnoreCase("string")){
            nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                String.class,
                "getPropertyAsString",//mapPropertyEnumToGetters.get(currentPropType),
                "setValueStringListFromString");//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.FALSE);
       }
       else { // fall through, unexpected, should handle arrays of strings
           nextNodeProp = new PropertySupport.Reflection(new PropertyEditorAdaptor(ap, this),
                String.class,
                "getPropertyAsString",//mapPropertyEnumToGetters.get(currentPropType),
                null);//mapPropertyEnumToSetters.get(currentPropType));
            nextNodeProp.setValue("canEditAsText", Boolean.FALSE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       }
       return nextNodeProp;
   }

    protected Reflection createNodePropForObjectName(OpenSimObject obj, Model model, boolean isSettable) throws NoSuchMethodException {
       PropertySupport.Reflection nextNodeProp = new PropertySupport.Reflection(new ObjectNameEditor(obj, model, this),
            String.class,
            "getName",
            (isSettable?"setName":null));
       nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
       nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       return nextNodeProp;
   }
    
    private Reflection createNodePropForObjectType(OpenSimObject obj) throws NoSuchMethodException {
       PropertySupport.Reflection nextNodeProp = new PropertySupport.Reflection(obj,
            String.class,
            "getConcreteClassName",
            null);
       nextNodeProp.setValue("canEditAsText", Boolean.FALSE);
       nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
       return nextNodeProp;
   }
    /**
     * Method for the node to update its children by addition/deletion/rename
     * Model delegates the call to sets and sets down to objects after handling add/delete from set
     * If an Object's node creates children it's resposnible for maintaining them by addition/deletion/rename
     * Object is repsonsible for enabling/disabling
     */
    public void updateSelfFromObject() {
        setDisplayName(openSimObject.getName());
    };
    // Showable: Show/Hide
    // Isolatable: ShowOnly
    // Colorable: Color, Opacity, Representation
    public enum displayOption{Showable, Isolatable, Colorable};
    private ArrayList<displayOption> validDisplayOptions = new ArrayList<displayOption>();
    
    /** Creates a new instance of OpenSimObjectNode */
    public OpenSimObjectNode(OpenSimObject obj) {
       this.openSimObject = obj;
       setDisplayName(obj.getName());
       //super(Children.LEAF, Lookups.fixed( new Object[] {obj} ) );
    }
    
    public OpenSimObjectNode( OpenSimObject obj, boolean leaf ) {
      super( Children.LEAF, Lookups.fixed( new Object[] {obj} ) );
      this.openSimObject = obj;
      setDisplayName( obj.getName() );
    }
    
    /**
     * Display name 
     */
    @Override
    public String getHtmlDisplayName()  { return getOpenSimObject().getName(); }

    /**
     * Action to be invoked on double clicking.
     */
    @Override
    public Action getPreferredAction() {
         if( getValidDisplayOptions().isEmpty() ) return null;  // Nothing to show or hide.
        
         OpenSimObject obj = getOpenSimObject();
         
         int currentStatus = 0;
         if (obj.hasProperty("display_preference")){
             int pref = PropertyHelper.getValueInt(obj.getPropertyByName("display_preference"));
             // 0 hidden
             // 1 wireframe
             // 3 flat
             // 4 surface
             if (pref==0) 
                 currentStatus=0; 
             else 
                 currentStatus=1;
         }
         else
             currentStatus = ViewDB.getInstance().getDisplayStatus( obj );
         try {
             // 2 for mixed, some shown some hidden, hide all now
               return ((ObjectDisplaySelectAction) ObjectDisplaySelectAction.findObject( (Class)Class.forName("org.opensim.view.actions.ObjectDisplaySelectAction"), true));
            
         } catch( ClassNotFoundException ex ) {
            ex.printStackTrace();
         }
            
         return null;
    }
       
    /**
     * Return the list of available actions.
     * Subclasses should user super.getActions() to use this
     */
    @Override
    public Action[] getActions(boolean b) {
      Action[] objectNodeActions;
      try {
         boolean showDisplayMenu = validDisplayOptions.size()>0;
         if (showDisplayMenu)
            objectNodeActions = new Action[]  { 
                                          (ObjectDisplayMenuAction) ObjectDisplayMenuAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectDisplayMenuAction"), true)};
         else
            objectNodeActions = new Action[]  {};
         
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
         objectNodeActions = new Action[] {null};
      }
      return objectNodeActions;
    }
    /**
     * return the Object presented by this node
     */
    public OpenSimObject getOpenSimObject()  { return openSimObject; }

    public Component getOwnerComponent() {
        OpenSimObject obj = getOpenSimObject();
        
        if (Component.safeDownCast(obj)!= null) 
            return Component.safeDownCast(obj);
        else {
            if (this instanceof OneModelNode)
            return ((OneModelNode)this).getModel();
        else 
            return ((OpenSimObjectNode)getParentNode()).getOwnerComponent();
        }
    }
   final void addDisplayOption(displayOption newOption)
   {
      if( !getValidDisplayOptions().contains(newOption) )
         getValidDisplayOptions().add(newOption);
   }

    public ArrayList<displayOption> getValidDisplayOptions()  { return validDisplayOptions; }

    @Override
    public Sheet createSheet() {
        Sheet retValue;

        retValue = super.createSheet();
        Sheet.Set set = retValue.get(Sheet.PROPERTIES);
        OpenSimObject obj = ((OpenSimObjectNode) (this)).getOpenSimObject();
        Model theModel = getModelForNode();
        if (!obj.getName().equalsIgnoreCase("")){
            try {
                Reflection nextNodeProp = createNodePropForObjectName(obj, theModel, false);
                if (nextNodeProp != null) {
                    nextNodeProp.setName("name");
                    nextNodeProp.setShortDescription("Name of the Object");
                    nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
                    set.put(nextNodeProp);
                }
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        // Repeat for type
        try {
            Reflection nextNodeProp = createNodePropForObjectType(obj);
            if (nextNodeProp != null) {
                nextNodeProp.setName("type");
                nextNodeProp.setShortDescription("Type of the Object");
                nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
                set.put(nextNodeProp);
            }
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        // Skip connections and add them in another group or Sheet
        for (int p = 0; p < obj.getNumProperties(); ++p) {
            try {
                AbstractProperty ap = obj.getPropertyByIndex(p);  
                String propname = ap.getName();
                // Skip over sockets and inputs per github issue
                if (propname.matches("socket_(.*)") || 
                        propname.matches("input_(.*)"))
                    continue;
                if (!ap.isListProperty()) {
                    if (ap.isObjectProperty() && ap.size() == 1) {
                        
                        OpenSimObject objectFromProperty = ap.isOptionalProperty()?ap.getValueAsObject(0):ap.getValueAsObject();
                        OpenSimBaseObjectProperty customProp=null;
                        if (GeometryPath.safeDownCast(objectFromProperty)!= null){
                            customProp  = new OpenSimGeometryPathProperty(ap, this);
                        }
                        else if (Function.safeDownCast(objectFromProperty)!= null &&
                                XYFunctionInterface.isXYFunction(Function.safeDownCast(objectFromProperty))){
                            customProp  = new OpenSimFunctionProperty(ap, this);
                        }
                        else
                           customProp  = new OpenSimObjectProperty(ap, this);
                        if (customProp!=null)
                            set.put(customProp);
                    } else {
                        // This could be optional if so treat as  a list, else noList
                        if (ap.isOptionalProperty()){
                            Reflection nextNodeProp = createNodePropForOpenSimListProperty(obj, p, theModel);
                            if (nextNodeProp != null) {
                                nextNodeProp.setName(ap.getName());
                                nextNodeProp.setShortDescription(ap.getComment());
                                set.put(nextNodeProp);
                            }
                        }
                        else {
                        Reflection nextNodeProp = createNodePropForOpenSimNoListProperty(obj, p, theModel);
                        if (nextNodeProp != null) {
                            nextNodeProp.setName(ap.getName());
                            nextNodeProp.setShortDescription(ap.getComment());
                            set.put(nextNodeProp);
                        }
                        }

                    }
                }
                else { 
                    if (ap.getName().equalsIgnoreCase("connectors")){
                        ;//createConnectorProperties(ap, retValue);
                    }
                    else {
                        Reflection nextNodeProp = createNodePropForOpenSimListProperty(obj, p, theModel);
                        if (nextNodeProp != null) {
                            nextNodeProp.setName(ap.getName());
                            nextNodeProp.setShortDescription(ap.getComment());
                            set.put(nextNodeProp);
                        }
                    }
                }
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return retValue;
    }
}