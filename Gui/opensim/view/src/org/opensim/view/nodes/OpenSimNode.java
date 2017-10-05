/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimNode.java                                                  *
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
 * OpenSimNode.java
 *
 * Created on May 5, 2006, 12:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.nodes;


import java.awt.Image;
import java.net.URL;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
//import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;

/**
 * 
 * @author   Ayman based on an earlier version by ken
 *
 * Intended as a common base node to enforce common behavior for all OpenSim created nodes.
 */
public class OpenSimNode extends AbstractNode {
    
    static final Hashtable<String, Class> mapPropertyTypeNameToClass = new Hashtable<String, Class>();
    static {
        mapPropertyTypeNameToClass.put("int", Integer.class);
        mapPropertyTypeNameToClass.put("double", Double.class);
        mapPropertyTypeNameToClass.put("string", String.class);
        mapPropertyTypeNameToClass.put("bool", Boolean.class);
        mapPropertyTypeNameToClass.put("Object", OpenSimObject.class);
    }
    /**
     * Creates a new instance of OpenSimNode
     */
    public OpenSimNode() {
        super(new Children.Array());
     }
    
    public OpenSimNode(Children children) {
        super(children);
    }

    public OpenSimNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    /**
     * public method to enable refreshing node display once changes have been made
     */
    public void refreshNode() {
        firePropertySetsChange(null, getPropertySets());
    }


    /** Root node (has all open models as its children). Unused!*/
    public static class RootNode extends OpenSimNode {
        public RootNode() {
            setDisplayName("Models");
        }
    }
    /**
     * Find the Model for a node by traversing up the tree
     */
    public Model getModelForNode() {
        if (this instanceof OneModelNode)
            return ((OneModelNode)this).getModel();
        else 
            return ((OpenSimNode)getParentNode()).getModelForNode();
    }

    /**
     * Icon for the node 
     **/
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/osimNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        } else {
            return null;
        }
    }

    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    public OpenSimObjectNode findChild(Object objectToMatch){
       if (this instanceof OpenSimObjectNode){
          if(((OpenSimObjectNode)this).getOpenSimObject().equals(objectToMatch))
             return (OpenSimObjectNode)this;
       }
       
       Node[] children = getChildren().getNodes();
       for(int i=0; i<children.length; i++){
          OpenSimObjectNode matchingNode = ((OpenSimNode)children[i]).findChild(objectToMatch);
          if(matchingNode !=null)
             return matchingNode;
       }
       
       return null;
    }
 
    /* Rename a node that contain a certain object. Check the 'this' node
     * and all its children, recursively, because there may be more than
     * one node that contains the object.
     */
    public void renameObjectNode(Object objectToRename, String newName) {
       if (this instanceof OpenSimObjectNode){
          if (((OpenSimObjectNode)this).getOpenSimObject().equals(objectToRename)) {
             setName(newName);
             setDisplayName(newName);
          }
       }
       
       Node[] children = getChildren().getNodes();
       for (int i=0; i<children.length; i++) {
          ((OpenSimNode)children[i]).renameObjectNode(objectToRename, newName);
       }
    }
    
    Class getClassForTypeName(String string){
        Class classEditAs = mapPropertyTypeNameToClass.get(string);
        
        if (classEditAs != null )
            return classEditAs;
        else
            return String.class;
    }
/*
    @Override
    public PasteType getDropType(Transferable t, final int action, int index) {
       if (this instanceof OpenSimObjectNode)
        System.out.println(((OpenSimObjectNode)this).toString()+":"+"getDropType");
       DataFlavor[] flavors= t.getTransferDataFlavors();
       System.out.println("Flavors:"+flavors.toString());
        final Node dropNode = NodeTransfer.node( t,
                DnDConstants.ACTION_COPY_OR_MOVE+NodeTransfer.CLIPBOARD_CUT );
        if( null != dropNode ) {
            /*final Movie movie = (Movie)dropNode.getLookup().lookup( Movie.class );
            if( null != movie  && !this.equals( dropNode.getParentNode() )) {
                return new PasteType() {
                    public Transferable paste() throws IOException {
                        getChildren().add( new Node[] { new MovieNode(movie) } );
                        if( (action & DnDConstants.ACTION_MOVE) != 0 ) {
                            dropNode.getParentNode().getChildren().remove( new Node[] {dropNode} );
                        }
                        return null;
                    }
                };
            }
        }
        return null;
    }

    @Override
    public Cookie getCookie(Class clazz) {
       if (this instanceof OpenSimObjectNode)
        System.out.println(((OpenSimObjectNode)this).toString()+":"+"getCookie");
        Children ch = getChildren();

        if (clazz.isInstance(ch)) {
            return (Cookie) ch;
        }

        return super.getCookie(clazz);
    }

    @Override
    protected void createPasteTypes(Transferable t, List s) {
        if (this instanceof OpenSimObjectNode)
       System.out.println(((OpenSimObjectNode)this).toString()+":"+"createPasteTypes");
       super.createPasteTypes(t, s);
        PasteType paste = getDropType( t, DnDConstants.ACTION_COPY, -1 );
        if( null != paste )
            s.add( paste );
    }
*/
    @Override
    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Sheet.Set setExpert = Sheet.createExpertSet();
        sheet.put(set);
        sheet.put(setExpert);
        return sheet;
    }
    /*
    protected void createPasteTypes(Transferable t, List ls) {
    final Node[] ns = NodeTransfer.nodes (t, NodeTransfer.COPY);
    if (ns != null) {
      ls.add (new PasteType () {
        public Transferable paste () throws IOException {
          Node[] nue = new Node[ns.length];
          for (int i = 0; i < nue.length; i++)
            nue[i] = ns[i].cloneNode ();
          getChildren ().add (nue);
          return null;
        }
      });
    }
    // Also try superclass, but give it lower priority:
    super.createPasteTypes(t, ls);
  }*/

} // class OpenSimNode
