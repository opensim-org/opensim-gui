/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimObjectModel.java                                           *
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

package org.opensim.view.editors;

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.MouseInputAdapter;
import org.opensim.modeling.IO;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayDouble;

//=========================================================================
// OpenSimObjectModel
//=========================================================================
public class OpenSimObjectModel extends AbstractTreeTableModel {
  // Names of the columns.
  static protected String[] cNames = { "Name", "", "Value", "", "Description" };

  // Types of the columns.
  static protected Class[] cTypes = { TreeTableModel.class, JButton.class, String.class, String.class, String.class };

  // Column header tool tips
  static final String[] toolTipStr = { "Property name in xml file", "Controls for array properties", "Current property value", null, "Description"};

  // Whether columns are editable
  // Column 0 is the tree, needs to be "editable" in order for expansion/collapse of nodes
  static protected boolean[] editableColumns = { true, false, true, false, false };

  // Whether the table as a whole is editable (false if we're just viewing properties)
  protected boolean isEditable;

  static protected PropertyNode[] EMPTY_CHILDREN = new PropertyNode[0];

  protected final Icon addIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/add.png"));
  protected final Icon addRolloverIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/add_rollover.png"));
  protected final Icon removeIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/delete.png"));
  protected final Icon removeRolloverIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/delete_rollover.png"));

  private NumberFormat numFormat = NumberFormat.getInstance();
    protected boolean SomethingChanged = false;

    public boolean isSomethingChanged() {
        return SomethingChanged;
    }

    public void setSomethingChanged(boolean SomethingChanged) {
        this.SomethingChanged = SomethingChanged;
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

  //-------------------------------------------------------------------------
  // Constructor
  //-------------------------------------------------------------------------

  public OpenSimObjectModel(OpenSimObject obj, boolean isEditable) {
    super(obj);
    this.isEditable = isEditable;
    root = new PropertyNode(obj);
    if (numFormat instanceof DecimalFormat) {
      ((DecimalFormat) numFormat).applyPattern("###0.#########");
    }
  }

  public String getColumnHeaderToolTip(int column) { return toolTipStr[column]; }

  //-------------------------------------------------------------------------
  // The TreeModel interface
  //-------------------------------------------------------------------------

  public int getChildCount(Object node) {
    Object[] children = getChildren(node);
    return (children == null) ? 0 : children.length;
  }

  public Object getChild(Object node, int i) {
    return getChildren(node)[i];
  }

  public boolean isLeaf(Object node) { 
     return ((PropertyNode)node).isLeaf(); 
  }

  //-------------------------------------------------------------------------
  // The TreeTableModel interface
  //-------------------------------------------------------------------------

  public int getColumnCount() { return cNames.length; }

  public String getColumnName(int column) { return cNames[column]; }

  // Unfortunately, JTable calls this method in some more places rather than always going through getCellEditor, getCellRenderer...
  // So the net result is that the editor will think it's editing string even when it edits numbers
  public Class getColumnClass(int column) { return cTypes[column]; }

  // This is for the cell renderer
  public Class getCellClass(Object node, int column) { 
     if(column==2) return ((PropertyNode)node).getValueClass();
     else return cTypes[column];
  }

  public boolean isCellEditable(Object node, int column) {
   return column == 0 || (isEditable && editableColumns[column] && ((PropertyNode)node).editable());
  }

  /**
   * Returns the value of the particular column.
   */
  public Object getValueAt(Object node, int column) {
    PropertyNode fn = (PropertyNode) node;
    try {
      switch (column) {
        case 0: // Name -- handled by tree navigator renderer (uses toString() method on PropertyNode)
          return null;
        case 1: // Controls
          return fn.getControlButton();
        case 2: // Value
          return fn.getValue();
        case 3: // Padding
          return null;
        case 4: // Description
          return fn.getDescription();
      }
    }
    catch (SecurityException se) {}

    return null;
  }

  /**
   * This's the function to be invoked when editing is finished.
   * It should update the underlying node, refresh display and fire-events as needed.
   *
   * @param aValue Object
   * @param node Object
   * @param column int
   *
   * This has the side effect of turning off the switch to useDefault value.
   * Might be better to do it inside appropriate setValue() method except that array properties 
   * and scalar properties are not handled the same. -Ayman 02/07
   */
  public void setValueAt(Object aValue, Object node, int column) {
    if (column==2){
        Object objRoot = getRoot();
        OpenSimObject saved = (getRootOpenSimObject(objRoot)).clone();
        ((PropertyNode)node).setValue(aValue);
        setSomethingChanged(true);
        getRootOpenSimObject(objRoot).updPropertyByIndex(0);
        propertyChangeSupport.firePropertyChange("Change", saved, getRootOpenSimObject(objRoot));
    }
  }

    private OpenSimObject getRootOpenSimObject(Object objRoot) {
        return (OpenSimObject)((PropertyNode)objRoot).getPropertyOrObject();
    }

  public String getToolTipText(Object node, int column) {
      if(node==null) return null;
      PropertyNode pn=(PropertyNode)node;
      String str = null;
      if(column==0) str = pn.getTreeNodeToolTipText();
      else if(column==1) str = pn.getControlButtonToolTipText();
      else if(column==2) str = pn.getValueToolTipText();
      else if(column==4) str = pn.getDescriptionToolTipText();
      if(str!=null) {
         String formattedStr = IO.formatText(str, "", 120, "<br>");
         return "<html>"+formattedStr+"</html>";
      } else return null;
  }

  //-------------------------------------------------------------------------
  // Some convenience methods
  //-------------------------------------------------------------------------

  /**
   * Reloads the children of the specified node.
   */
  public void reloadChildren(Object node) {
    PropertyNode fn = (PropertyNode) node;
    fn.loadChildren();
    fn.setChildren(fn.getChildren(), true);
    fn.nodeChanged();
  }

  protected Object[] getChildren(Object node) {
    return ((PropertyNode)node).getChildren();
  }

  //=========================================================================
  // PropertyNode
  //=========================================================================

  class PropertyNode {
    private PropertyNode parent;
    private Object propertyOrObject; 
    protected PropertyNode[] children;
    protected String propValueType = ""; // Indicates propertyOrObject type of this node (NOTE: an item of a PropertyDblArray gets the type PropertyDbl!)

    /** will this object be treated as a leaf or as expandible */
    protected boolean aggregate;
    protected boolean editable = true;
    /** in cases where the node corresponds to an array entry, keep index here, and propertyOrObject will be the propertyOrObject array containing the item */
    protected int idx;

    protected JButton controlButton = null;

    //-----------------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------------
    /**
     * PropertyNode constructor that takes object, no parent or index
     *
     * @param propertyOrObject Object
     */
    protected PropertyNode(Object propertyOrObject) {
      this(null, propertyOrObject, -1);
    }

    /**
     * PropertyNode constructor that takes parent as well as object
     *
     * @param parent PropertyNode
     * @param propertyOrObject Object
     */
    protected PropertyNode(PropertyNode parent, Object property) {
      this(parent, property, -1);
    }

    /**
     * PropertyNode constructor that takes parent, object and an index. used by
     * arrays
     *
     * @param parent PropertyNode
     * @param propertyOrObject Object
     * @param index int, -1 if this is not a member of a list
     */
    protected PropertyNode(PropertyNode parent, Object property, int index) {
      this.parent = parent;
      this.propertyOrObject = property;
      this.idx = index;
      aggregate = false;

       if (property instanceof OpenSimObject) {
         aggregate = true; // This will cause recursion downstream
      } else { // actual property rather than an Object
         AbstractProperty ap = (AbstractProperty) property;
         propValueType = ap.getTypeName();
         if(ap.isListProperty() )
         {
            if(idx==-1) {
               aggregate = true;
                 if (ap.getMinListSize()!= ap.getMaxListSize()){
                   // Button to add array item
                   controlButton = new JButton(addIcon);
                   //controlButton.setRolloverIcon(addRolloverIcon); // doesn't work right now
                   controlButton.addMouseListener(new MouseInputAdapter() {
                      public void mousePressed(MouseEvent evt) { 
                          addPropertyItem(); 
                      }
                   });
                   controlButton.setToolTipText("Add an item to this property array");
               }

            } else {
               if (ap.getMinListSize()!= ap.getMaxListSize()&& ap.size()>0){
                   // Button to delete array item
                   controlButton = new JButton(removeIcon);
                   //controlButton.setRolloverIcon(removeRolloverIcon); // doesn't work right now
                   controlButton.addMouseListener(new MouseInputAdapter() {
                                @Override
                      public void mousePressed(MouseEvent evt) { 
                                    removePropertyItem(); 
                                }
                   });
                   controlButton.setToolTipText("Remove this item from the property array");
             }
            }
         }
         else if(ap.isObjectProperty() && (!ap.isOptionalProperty() || ap.size()>=1))
         {
            aggregate = true;
         }
         else if (propValueType.equalsIgnoreCase("Transform"))
             aggregate = true;
         else if (ap.isOptionalProperty() && ap.size()==0){
             editable = false;
         }
      }

      if(controlButton!=null) {
         controlButton.setContentAreaFilled(false);
         controlButton.setMargin(new Insets(0,0,0,0));
         controlButton.setOpaque(true);
      }
    }

    //-----------------------------------------------------------------------
    // Basic get/set/queries
    //-----------------------------------------------------------------------
    
    public PropertyNode getParent() { return parent; }

    public boolean isLeaf() { return !aggregate; }

    protected PropertyNode[] getChildren() { return children; }

    public String getDescription() {
       if(propertyOrObject instanceof AbstractProperty && idx==-1) return ((AbstractProperty)propertyOrObject).getComment();
       else return " ";
    }

    public boolean editable() { return !aggregate && editable; }

    //-----------------------------------------------------------------------
    // Add/Remove for propertyOrObject arrays
    //-----------------------------------------------------------------------

    // This is the parent node for a propertyOrObject array, and we want to add an item to the array
    private void addPropertyItem() {
      AbstractProperty p = (AbstractProperty)propertyOrObject;
      // TODO: what values should we use to populate newly created item?
      if (p.getTypeName().equalsIgnoreCase("double")) PropertyHelper.appendValueDouble(0.0, p);
      else if (p.getTypeName().equalsIgnoreCase("int")) PropertyHelper.appendValueInt(0, p);
      else if (p.getTypeName().equalsIgnoreCase("string")) PropertyHelper.appendValueString("", p);
      else if (p.getTypeName().equalsIgnoreCase("bool")) PropertyHelper.appendValueBool(true, p);
      reloadChildren(this);
    }

    // This is an item in a propertyOrObject array, and we remove this item
    private void removePropertyItem() {
      if(idx != -1) {
         AbstractProperty p = (AbstractProperty)getParent().propertyOrObject;
         PropertyHelper.removeItem(p, idx);
         reloadChildren(getParent());
      }
    }

    //-----------------------------------------------------------------------
    // Basic get/set/queries
    //-----------------------------------------------------------------------

    public String getControlButtonToolTipText() {
       if(getControlButton()!=null) return getControlButton().getToolTipText(); 
       else return null;
    }

    public String getTreeNodeToolTipText() { return toString(); }

    public String getValueToolTipText() {
       if (propertyOrObject instanceof OpenSimObject) {
          return "Object of type '"+((OpenSimObject)propertyOrObject).getConcreteClassName()+"', name '"+((OpenSimObject)propertyOrObject).getName()+"'";
       } else {
            AbstractProperty p = (AbstractProperty)propertyOrObject;
            return p.toString();
       }
    }

    public String getDescriptionToolTipText() { return getDescription(); }

    public Object getValue() {
      if (propertyOrObject instanceof OpenSimObject)
        return ( (OpenSimObject) propertyOrObject).getName();
      else {
         AbstractProperty ap = (AbstractProperty)propertyOrObject;
         //System.out.println("Processing property ["+ap.getName()+ "] type = ["+propValueType+"] index ="+idx);
         if (ap.isListProperty() && idx==-1 || ap.isOptionalProperty() && idx==-1)
             return ap.toString();
         if(propValueType.equalsIgnoreCase("double")) { 
            return PropertyHelper.getValueDouble(ap, idx);
         } else if(propValueType.equalsIgnoreCase("int")) {
            return PropertyHelper.getValueInt(ap, idx); 
         } else if(propValueType.equalsIgnoreCase("string")) {
            return PropertyHelper.getValueString(ap, idx);  
         } else if(propValueType.equalsIgnoreCase("bool")) {
            return PropertyHelper.getValueBool(ap, idx);
         } else if(propValueType.equalsIgnoreCase("Transform")) {
             return PropertyHelper.getValueTransform(ap, idx);
         } else if(propValueType.equalsIgnoreCase("Object")) {
            return ap.getValueAsObject();
         } else 
             return ap.toString();
      }
    }

    public Class getValueClass() { 
      if (propertyOrObject instanceof AbstractProperty) {
          if (((AbstractProperty)propertyOrObject).isListProperty()  && idx==-1){
              return String.class;
          }
         if(propValueType.equalsIgnoreCase("double")) return Double.class;
         else if(propValueType.equalsIgnoreCase("int")) return Integer.class;
         else if(propValueType.equalsIgnoreCase("string")) return String.class;
         else if(propValueType.equalsIgnoreCase("bool")) return Boolean.class;
      }
      return String.class; // If none of the above, it's a string
    }

    // This is the name appearing in the tree navigator part of the propertyOrObject editor
    public String toString() { 
       String[] vec3Names = new String[]{"X", "Y", "Z"};
      if (propertyOrObject instanceof OpenSimObject)
         return ((OpenSimObject)propertyOrObject).getConcreteClassName();
      else if (propertyOrObject instanceof AbstractProperty && idx==-1)
        return ( (AbstractProperty) propertyOrObject).getName();
      else if (idx!=-1){
              return ( "["+String.valueOf(idx)+"]");
      }
      return ("unknown type!");
    }

   public JButton getControlButton() {
      if(isEditable && controlButton!=null) { // A controlButton is possible, but we check the sizes of the propertyOrObject array to see if we can really add/remove elements
         if(idx==-1 && ((AbstractProperty)propertyOrObject).size() < ((AbstractProperty)propertyOrObject).getMaxListSize()) return controlButton; // an add button
         else if(idx!=-1 && ((AbstractProperty)propertyOrObject).size() > ((AbstractProperty)propertyOrObject).getMinListSize()) return controlButton; // a delete button
      }
      return null;
   }

   public void setValue(Object value) {
      /**
      * The following steps need to be done
      * 1. Map propertyOrObject and index if any to a primitive type
      * 2. Cast value into a String sValue;
      * 3. Parse sValue using type info
      * 4. call API to set the value
      */
      // Only primitive properties and array entries can be edited
      if(propertyOrObject instanceof AbstractProperty) {
         AbstractProperty p = (AbstractProperty)propertyOrObject;
         if (p.isOptionalProperty() && p.size()==0) return;
         p.setValueIsDefault(false);

         // NOTE: I wanted the values to come in as Double, Integer, etc. but due to some weirdness the table editor thinks
         // it's editing a string in the cases of Double, Integer, and String... so I check this below (value instanceof String) to be safe
         try {
            NumberFormat numFormat = NumberFormat.getInstance();
            if(propValueType.equalsIgnoreCase("double")) {
               double val = (value instanceof String) ? numFormat.parse((String)value).doubleValue() : ((Double)value).doubleValue();
               PropertyHelper.setValueDouble(val, p, idx); 
            } else if(propValueType.equalsIgnoreCase("int")) {
               int val = (value instanceof String) ? numFormat.parse((String)value).intValue() : ((Integer)value).intValue();
               PropertyHelper.setValueInt(val, p, idx);
            } else if(propValueType.equalsIgnoreCase("string")) {
               String val = (String)value;
               PropertyHelper.setValueString(val, p, idx);
            } else if(propValueType.equalsIgnoreCase("bool")) {
               boolean val = ((Boolean)value).booleanValue();
              PropertyHelper.setValueBool(val, p, idx);
            } else if(propValueType.equalsIgnoreCase("Transform")) {
               double val = (value instanceof String) ? numFormat.parse((String)value).doubleValue() : ((Double)value).doubleValue();
               PropertyHelper.setValueTransform(val, p, idx);
             } else if(propValueType.equalsIgnoreCase("Vec3")) {
                 ArrayDouble d = new ArrayDouble();
                 d.fromString((String)value);
                 for(int i=0; i<3; i++) PropertyHelper.setValueVec3(d.getitem(i), p, i);
             }
         } catch (ParseException ex) { // might get a parsing exception
         }

         if(idx!=-1) reloadChildren(getParent());
      }
   }

    protected PropertyNode[] createChildren(OpenSimObject obj) {
       if(obj==null) return null;
       //System.out.println("createChildren for object: "+obj.toString()+" type="+obj.getConcreteClassName());
       int numProps = obj.getNumProperties();
       PropertyNode[] retArray = new PropertyNode[numProps];
       for (int i = 0; i < numProps; i++) {
            AbstractProperty prop=null;
            prop = obj.updPropertyByIndex(i);
            if(prop.isObjectProperty()){
                if (!prop.isListProperty() && !prop.isOptionalProperty())
                    retArray[i] = new PropertyNode(this, prop.getValueAsObject());
                else 
                    retArray[i] = new PropertyNode(this, prop); //List
            }
            else
               retArray[i] = new PropertyNode(this, prop);
       }
       //System.out.println("finished createChildren for object: "+obj.toString());
       return retArray;
    }

    /**
     * Creates PropertyNodes for the children of this node.
     *
     * @return PropertyNode[]
     * @todo Complete the list of types of AbstractProperty that are aggregates:
     *   primitive types already work as well as Arrays of doubles and Strings.
     *   Left: Arrays of objects and actual editing/change-probagation
     */
    protected PropertyNode[] createChildren() {
      PropertyNode[] retArray = null;
      ///System.out.println("createChildren for object:"+propertyOrObject.toString());

      try {
        if (propertyOrObject instanceof OpenSimObject) {
          retArray = createChildren((OpenSimObject)propertyOrObject);
        }
        else if (propertyOrObject instanceof AbstractProperty) { // Actual prop
          AbstractProperty rdprop = (AbstractProperty) propertyOrObject;
           if (rdprop.isObjectProperty()) { // Object, ObjectPtr or Array of Objects
               //System.out.println("Found Object property:"+rdprop.getTypeName());
               if (rdprop.size()==1){
                   if (rdprop.isOneObjectProperty())
                        retArray = createChildren((OpenSimObject)rdprop.getValueAsObject());
                    else
                        retArray = createChildren((OpenSimObject)rdprop.getValueAsObject(0));
               }
               else {
                   retArray = new PropertyNode[rdprop.size()];
                    for (int i = 0; i < rdprop.size(); i++) {
                        retArray[i] = new PropertyNode(this, rdprop.getValueAsObject(i), -1); // PropertyNode will figure out our type
                    }
               }
          }
          else if (rdprop.isListProperty()) 
          { // List of non Objects: transform, 
            retArray = new PropertyNode[rdprop.size()];
            for (int i = 0; i < rdprop.size(); i++) 
               retArray[i] = new PropertyNode(this, propertyOrObject, i); // PropertyNode will figure out our type
          }
          else { // simple objects
            retArray = new PropertyNode[rdprop.size()];
            //System.out.println("Name="+rdprop.getName()+" propType="+propType+" size="+rdprop.getArraySize());
            for (int i = 0; i < rdprop.size(); i++) {
              OpenSimObject subobj = rdprop.getValueAsObject(i);
              //String dName = subobj.getName(); 
              retArray[i] = new PropertyNode(this, subobj, i);
            }
          }
        }
      }
      catch (SecurityException se) {}
      if (retArray == null) {
        retArray = EMPTY_CHILDREN;
      }
      return retArray;
    }

    //-----------------------------------------------------------------------
    // Path utilities
    //-----------------------------------------------------------------------

    public PropertyNode[] getPath() {
      return getPathToRoot(this, 0);
    }

    protected PropertyNode[] getPathToRoot(PropertyNode aNode, int depth) {
      PropertyNode[] retNodes;

      if (aNode == null) {
        if (depth == 0)
          return null;
        else
          retNodes = new PropertyNode[depth];
      }
      else {
        depth++;
        retNodes = getPathToRoot(aNode.getParent(), depth);
        retNodes[retNodes.length - depth] = aNode;
      }
      return retNodes;
    }

    //-----------------------------------------------------------------------
    // Node/Children utilities
    //-----------------------------------------------------------------------

    /**
     * Recursively loads all the children of the receiver.
     */
    protected void loadChildren() {
      children = createChildren();
      for (int counter = children.length - 1; counter >= 0; counter--) {
        if (children[counter]!=null && !children[counter].isLeaf()) {
          children[counter].loadChildren();
        }
      }
    }

    /**
     * Sets the children of the receiver, updates the total size,
     * and if generateEvent is true a tree structure changed event
     * is created.
     */
    protected void setChildren(PropertyNode[] newChildren, boolean generateEvent) {
      children = newChildren;
      if (generateEvent) {
        PropertyNode[] path = getPath();
        fireTreeStructureChanged(OpenSimObjectModel.this, path, null, null);
      }
    }

    /**
     * Can be invoked when a node has changed, will create the
     * appropriate event.
     */
    protected void nodeChanged() {
      PropertyNode parent = getParent();
      if (parent != null) {
        PropertyNode[] path = parent.getPath();
        int[] index = { getIndexOfChild(parent, this)};
        Object[] children = { this};
        fireTreeNodesChanged(OpenSimObjectModel.this, path, index, children);
      }
    }

        /**
         * @return the propertyOrObject
         */
        public Object getPropertyOrObject() {
            return propertyOrObject;
        }
  }
}
