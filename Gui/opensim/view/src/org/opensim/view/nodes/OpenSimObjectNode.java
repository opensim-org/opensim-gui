/*
 *
 * OpenSimObjectNode
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
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
package org.opensim.view.nodes;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Property.PropertyType;
import org.opensim.modeling.PropertyDblArray;
import org.opensim.view.ObjectDisplayHideAction;
import org.opensim.view.ObjectDisplayMenuAction;
import org.opensim.view.ObjectDisplayShowAction;
import org.opensim.view.ObjectGenericReviewAction;
import org.opensim.view.pub.ViewDB;
import org.opensim.view.ModelWindowVTKTopComponent;

/**
 *
 * @author Ayman. A node backed by an OpenSim Object 
 * (or a Set as a set is also an Object)
 */
public class OpenSimObjectNode extends OpenSimNode {
    
    private OpenSimObject openSimObject;
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
    public String getHtmlDisplayName()  { return getOpenSimObject().getName(); }

    /**
     * Action to be invoked on double clicking.
     */
    public Action getPreferredAction() {
         if( getValidDisplayOptions().size() ==0 ) return null;  // Nothing to show or hide.
          
         // If this is a rigid body, open the easy-to-use rigid body property editor (also provides the older table version). 
         // Appearance panel allows user to show/hide the body.
         if( this instanceof OneBodyNode )
         {
            ModelWindowVTKTopComponent ownerWindow = ViewDB.getInstance().getCurrentModelWindow();
            new LSJava.LSPropertyEditors.LSPropertyEditorRigidBody( (OneBodyNode)this, ownerWindow );
            return null;
         }
         
         OpenSimObject obj = getOpenSimObject();
         int currentStatus = ViewDB.getInstance().getDisplayStatus( obj );
         try {
            if( currentStatus == 0 ) {   // 0 for hidden
               return ((ObjectDisplayShowAction)ObjectDisplayShowAction.findObject( (Class)Class.forName("org.opensim.view.ObjectDisplayShowAction"), true));
            }
            else if( currentStatus==1 || currentStatus==2 ) { // 2 for mixed, some shown some hidden, pick show
               return ((ObjectDisplayHideAction) ObjectDisplayHideAction.findObject( (Class)Class.forName("org.opensim.view.ObjectDisplayHideAction"), true));
            }
         } catch( ClassNotFoundException ex ) {
            ex.printStackTrace();
         }
            
         return getReviewAction();
    }
       
    /**
     * Return the list of available actions.
     * Subclasses should user super.getActions() to use this
     */
    public Action[] getActions(boolean b) {
      Action[] objectNodeActions;
      try {
         boolean showDisplayMenu = validDisplayOptions.size()>0;
         if (showDisplayMenu)
            objectNodeActions = new Action[]  {getReviewAction(), 
                                          null, 
                                          (ObjectDisplayMenuAction) ObjectDisplayMenuAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectDisplayMenuAction"), true)};
         else
            objectNodeActions = new Action[]  {getReviewAction()};
         
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

   protected Action getReviewAction() {
      Action act =null;
      try {
         act = (ObjectGenericReviewAction) ObjectGenericReviewAction.findObject( (Class)Class.forName("org.opensim.view.ObjectGenericReviewAction"), true);
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      return act;
   }
   
   protected void addDisplayOption(displayOption newOption)
   {
      if( !getValidDisplayOptions().contains(newOption) )
         getValidDisplayOptions().add(newOption);
   }

   public ArrayList<displayOption> getValidDisplayOptions()  { return validDisplayOptions; }

    @Override
    public Sheet createSheet() {
        Sheet retValue;
        
        retValue = super.createSheet();
        Sheet.Set set = retValue.get("properties");
        OpenSimObject obj = ((OpenSimObjectNode) (this)).getOpenSimObject();
        
        org.opensim.modeling.PropertySet ps= obj.getPropertySet();
        
        for(int i=0; i<ps.getSize(); i++){
            try {
                org.opensim.modeling.Property prop = ps.get(i);
                final PropertyType currentPropType = prop.getType();
                //Class dClass = prop.getClass();
                if (currentPropType==PropertyType.DblVec ||
                        currentPropType==PropertyType.DblArray){
                     PropertySupport.Reflection nextNodeProp = new PropertySupport.Reflection(prop, mapPropertyEnumToClass.get(currentPropType),
                            mapPropertyEnumToGetters.get(currentPropType),
                            mapPropertyEnumToSetters.get(currentPropType));
                     nextNodeProp.setValue("canEditAsText", Boolean.TRUE); 
                     nextNodeProp.setValue("suppressCustomEditor", Boolean.FALSE);
                     nextNodeProp.setName(prop.getName());
                     set.put(nextNodeProp);
                     //org.opensim.modeling.PropertyDblArray pda = PropertyDblArray.safeDownCast(prop);
                     int sz = prop.getValueDblArray().getSize();
                     double val = prop.getValueDblArray().getitem(0);
                     int x=0;
                     //System.out.println(prop.getName()+" "+val);
                }
                //getArraySize()
                else if (mapPropertyEnumToClass.containsKey(currentPropType)) {
                    // Need Class, functionToGet, functioToSet // Editor
                    PropertySupport.Reflection nextNodeProp = new PropertySupport.Reflection(prop, mapPropertyEnumToClass.get(currentPropType),
                            mapPropertyEnumToGetters.get(currentPropType),
                            mapPropertyEnumToSetters.get(currentPropType));
                    nextNodeProp.setName(prop.getName());
                    if (currentPropType==PropertyType.Str){
                        ((Node.Property)nextNodeProp).setValue("oneline", Boolean.TRUE);
                        ((Node.Property)nextNodeProp).setValue("suppressCustomEditor", Boolean.TRUE);
                    }
                    set.put(nextNodeProp);
                }
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return retValue;
    }
}
