/* -------------------------------------------------------------------------- *
 * OpenSim: ModelBuilderFromCollada.java                                      *
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
package org.opensim.collada;

import java.util.HashMap;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Body;
import org.opensim.modeling.FreeJoint;
import org.opensim.modeling.Joint;
import org.opensim.modeling.Model;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ayman
 * 
 * Class to support building an OpenSim model from a Collada file. 
 * 
 * This is targeted toward files exported from SolidWorks (.dae version 1.4)
 * Book-keeping to create objects for later reference should all be done in this class
 * 
 */
public class ModelBuilderFromCollada {
    HashMap<String, String> mapInternalNamesToModelNames = new HashMap<String, String>();
    private Model openSimModel = new Model();

    void setName(String name) {
        getOpenSimModel().setName(name);
    }

    String getName() {
        return getOpenSimModel().getName();
    }
    
    void addBodyFromNode(Node nextBodyNode) {
        Node bodyNameNode = nextBodyNode.getAttributes().getNamedItem("name");
        Node internalNameNode = nextBodyNode.getAttributes().getNamedItem("sid");
        String internalNameString = internalNameNode.getNodeValue();
        String modelNameString = bodyNameNode.getNodeValue();
         mapInternalNamesToModelNames.put(internalNameString, internalNameString);
        Body body = new Body();
        body.setName(modelNameString);
        
        boolean isDynamic = checkDynamic(nextBodyNode);
        Node techCommonNode = findChildNodeByName(nextBodyNode, "technique_common");
        if (isDynamic) {
            // Set Mass
            body.setMass(extractMass(techCommonNode));
            // get com, if we're here then techCommonNode exists
            Node massFrameNode = findChildNodeByName(techCommonNode, "mass_frame");
            if (massFrameNode!=null){
                Node massTranslateNode = findChildNodeByName(massFrameNode, "translate");
                String comString = massTranslateNode.getTextContent();
                ArrayDouble com = new ArrayDouble();
                com.fromString(comString);
                body.setMassCenter(com.getAsVec3());
            }   
        }
        // Now the geometry
        boolean hasShape = (techCommonNode!= null)&& findChildNodeByName(techCommonNode, "shape")!=null;
        if (hasShape){
            Node shapeNode = findChildNodeByName(techCommonNode, "shape");
            Node geometryNode = findChildNodeByName(shapeNode, "instance_geometry");
            if (geometryNode!=null){
                Node geomInstanceNode = geometryNode.getAttributes().getNamedItem("url");
                String geomName = geomInstanceNode.getTextContent();
                String shortGeomName = geomName.substring(1);
                body.getDisplayer().setGeometryFileName(0, shortGeomName);
            }
        }
        body.getDisplayer().setShowAxes(true);
        getOpenSimModel().getBodySet().cloneAndAppend(body);
        Body modelBody = getOpenSimModel().getBodySet().get(modelNameString);
        
        // Inertia
        
        // Set Joint, for now a FreeJoint with name "${BodyName}_joint" all defaults
        FreeJoint fj = new FreeJoint();
        fj.setName(modelNameString+"_joint");
        fj.setParentName("ground");
        modelBody.setJoint(Joint.safeDownCast(fj.clone()));
        
        // Set Geometry
    }

    /**
     * @return the openSimModel
     */
    public Model getOpenSimModel() {
        return openSimModel;
    }
    //  <technique_common>/<mass>
    private double extractMass(Node techCommonNode) {
        if (techCommonNode==null) return 1.0;   // node not found, ignore mass 
        Node massNode = findChildNodeByName(techCommonNode, "mass");
        if (massNode==null || massNode.getTextContent()==null) return 1.0;   // node not found, ignore mass we may want to check "dynamic"
        return Double.valueOf(massNode.getTextContent()).doubleValue();
    }
    public static Node findChildNodeByName(Node aNode, String nameToFind)
    {
        NodeList childNodes = aNode.getChildNodes();
        for (int i=0; i< childNodes.getLength();i++){
            Node nextChildNode = childNodes.item(i);
            if (nextChildNode.getNodeName().equalsIgnoreCase(nameToFind)){
                return nextChildNode;
            }
        }
        return null;
    }

    private boolean checkDynamic(Node nextBodyNode) {
        Node techCommonNode = findChildNodeByName(nextBodyNode, "technique_common");
        if (techCommonNode==null) return false;   // node not found, ignore mass we may want to check "dynamic"
        Node dynamicNode = findChildNodeByName(techCommonNode, "dynamic");
        if (dynamicNode==null || dynamicNode.getTextContent()==null) return false;   // node not found, ignore mass we may want to check "dynamic"
        return Boolean.valueOf(dynamicNode.getTextContent()).booleanValue();
    }
}
