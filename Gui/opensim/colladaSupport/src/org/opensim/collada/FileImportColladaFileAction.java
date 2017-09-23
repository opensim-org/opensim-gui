/* -------------------------------------------------------------------------- *
 * OpenSim: FileImportColladaFileAction.java                                  *
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.modeling.Model;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ActionID(category = "File",
id = "org.opensim.collada.FileImportColladaFileAction")
@ActionRegistration(displayName = "#CTL_FileImportColladaFileAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 2425, separatorBefore = 2412)
})
@Messages("CTL_FileImportColladaFileAction=Import Collada File...")
public final class FileImportColladaFileAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
        String file = FileUtils.getInstance().browseForFilename(".dae", "Collada file");
        if (file != null){
            ModelBuilderFromCollada modelBuilder = new ModelBuilderFromCollada();
            try {
                File colladaFile = new File(file);
                String baseFileName = colladaFile.getName().substring(0, colladaFile.getName().lastIndexOf("."));
                modelBuilder.setName(baseFileName);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                // Parse file as plain XML
                Document doc = dBuilder.parse(colladaFile);
                doc.getDocumentElement().normalize();
                NodeList topLevelNodes = doc.getDocumentElement().getChildNodes();

                Node libraryPhysicsModelNode = ModelBuilderFromCollada.findChildNodeByName(doc.getDocumentElement(), "library_physics_models");
                // Look for Node with id="Scene0-PhysicsModel"
                if (libraryPhysicsModelNode==null) return;
                 
                Node physicsModelNode = ModelBuilderFromCollada.findChildNodeByName(libraryPhysicsModelNode, "physics_model");

                if (physicsModelNode==null) return;
                
                NodeList bodyNodes = physicsModelNode.getChildNodes();
                for (int i=0; i< bodyNodes.getLength();i++){
                    Node nextBodyNode = bodyNodes.item(i);
                    if (nextBodyNode.getNodeName().equalsIgnoreCase("rigid_body")){
                        createBodyInModel(nextBodyNode, modelBuilder);
                    }
                    // If "rigid_constraint" then it's a joint
                }
            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ParserConfigurationException ex) {
                Exceptions.printStackTrace(ex);
            }
            // Get the model from modelBuilder and add it to the GUI
            Model m = modelBuilder.getOpenSimModel();
            try {
                m.initSystem();
                OpenSimDB.getInstance().addModel(m);
           } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }
    // Create body based on passed in Node in the passed in Model
    private void createBodyInModel(Node nextBodyNode, ModelBuilderFromCollada modelBuilder ) {
        // Extract attributes, only name for now, then Joint, then display geometry
        modelBuilder.addBodyFromNode(nextBodyNode);
   }

}
