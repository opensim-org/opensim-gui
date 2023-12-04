/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

/**
 *
 * @author Ayman-NMBL
 */
/* -------------------------------------------------------------------------- *
 * OpenSim: BaseToolPanel.java                                                *
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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.opensim.modeling.OpenSimObject;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class BaseToolPanel extends JPanel implements ActionListener, Observer {
  
   private FileFilter settingsFilter = null;

   protected JButton loadSettingsButton = new JButton("Load...");
   protected JButton saveSettingsButton = new JButton("Save...");
   protected JButton applyButton = new JButton("Run");
   protected JButton okButton = new JButton("Close");
   //protected JButton cancelButton = new JButton("Cancel");
   protected JButton helpButton = new JButton("Help");
   protected Dialog ownerDialog = null;
   protected boolean cleanupAfterExecuting = false;  // Keep track if cleaning up needs to be done on execution finish vs. dialog close

    String stripOuterTags(String nmsmFilename) {
       try {
           DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
           DocumentBuilder builder = dbf.newDocumentBuilder();
           FileInputStream fis = new FileInputStream(nmsmFilename);
           
           Document doc = builder.parse(new File(nmsmFilename));
           InputSource is = new InputSource(fis);
           // get the first element
           Element element = doc.getDocumentElement();
           
           // get all child nodes
           NodeList nodes = element.getChildNodes();
           String osimFile = nmsmFilename.replace(".xml", "osim.xml");
           TransformerFactory transformerFactory = TransformerFactory.newInstance();
           Transformer transformer = transformerFactory.newTransformer();
           DOMSource source = new DOMSource(nodes.item(1));
           FileWriter writer = new FileWriter(new File(osimFile));
           StreamResult result = new StreamResult(writer);
           transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
           transformer.transform(source, result);
           writer.flush();
           writer.close();
           
           return osimFile;
       } catch (ParserConfigurationException ex) {
           Exceptions.printStackTrace(ex);
       } catch (FileNotFoundException ex) {
           Exceptions.printStackTrace(ex);
       } catch (SAXException ex) {
           Exceptions.printStackTrace(ex);
       } catch (IOException ex) {
           Exceptions.printStackTrace(ex);
       } catch (TransformerConfigurationException ex) {
           Exceptions.printStackTrace(ex);
       } catch (TransformerException ex) {
           Exceptions.printStackTrace(ex);
       }
       return "";
    }

   //------------------------------------------------------------------------
   // Load/Save Settings Actions
   //------------------------------------------------------------------------
   class LoadSettingsAction extends AbstractAction {
      public LoadSettingsAction() { super("Load Settings..."); }
      public void actionPerformed(ActionEvent evt) {
         String fileName = FileUtils.getInstance().browseForFilename(settingsFilter);
         if(fileName!=null) loadSettings(fileName);
      }
   }
   class SaveSettingsAction extends AbstractAction {
      public SaveSettingsAction() { super("Save Settings..."); }
      public void actionPerformed(ActionEvent evt) {
         String fileName = FileUtils.getInstance().browseForFilenameToSave(settingsFilter, true, "", null);
         if(fileName!=null) saveSettings(fileName, getToolXML());
      }
   }

   public BaseToolPanel() {

      loadSettingsButton.addActionListener(new LoadSettingsAction());
      saveSettingsButton.addActionListener(new SaveSettingsAction());
 
      OpenSimDB.getInstance().addObserver(this);
   }

   public void setSettingsFileDescription(String description) {
      settingsFilter = FileUtils.getFileFilter(".xml", description);
   }

   //------------------------------------------------------------------------
   // Override in derived classes
   //------------------------------------------------------------------------
   public void loadSettings(String fileName) {
   
   }
   public void saveSettings(String fileName, String contents) {
         String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
         OpenSimObject.setSerializeAllDefaults(true);
         //String toolFileContent = jointPersonalizationToolModel.getToolAsObject().dump();
         BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fullFilename));
            writer.write("<NMSMPipelineDocument Version=\"1.0.0\">\n");
            writer.write(contents);
            writer.write("</NMSMPipelineDocument>");
            writer.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
   }
   public void pressedCancel() {}
   public void pressedClose() {}
   public void pressedApply() {}

   //------------------------------------------------------------------------
   // Dialog Operations
   //------------------------------------------------------------------------
   public JButton[] getDialogOptions() {
      return new JButton[]{loadSettingsButton, saveSettingsButton, applyButton, okButton, helpButton};
   }

   public void setOwner(Dialog window) { ownerDialog = window; }

   public void updateApplyButton(boolean applyEnabled) {
      applyButton.setEnabled(applyEnabled);
   }

   public void actionPerformed(ActionEvent evt) {
      if(evt.getSource() == okButton) {
         pressedClose();
         ownerDialog.dispose();
      } else if(evt.getSource() == applyButton) {
         pressedApply();
      }
   }

   //------------------------------------------------------------------------
   // Helper function to create dialog
   //------------------------------------------------------------------------
   public static void openToolDialog(final BaseToolPanel panel, String name) {
      DialogDescriptor dlg = new DialogDescriptor(panel, name, false, panel);
      dlg.setOptions(panel.getDialogOptions());
      Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
      panel.setOwner(dialog);
      dialog.setVisible(true);      
      dialog.requestFocus();      
      //JFrame toolFrame = DialogUtils.createFrameForPanel(panel, name);
      //DialogUtils.addButtons(toolFrame, (JButton[]) panel.getDialogOptions(), panel);
      //panel.setOwner(toolFrame);
      //toolFrame.setVisible(true);      
      dialog.requestFocus();
      dialog.addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
            panel.cleanup();
            OpenSimDB.getInstance().deleteObserver(panel);
         }

         public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            panel.cleanup();
            OpenSimDB.getInstance().deleteObserver(panel);
         }
      });
   }
   // Relinquish C++ resources by setting references to them to null
   public void cleanup()
   {
      // If tool is still running don't do any cleanup until the tool is done otherwise cleanup now
      // by freeing C++ allocated resources.
   }

   public void close()
   {
       pressedCancel();
       OpenSimDB.getInstance().deleteObserver(this);
       ownerDialog.dispose();
   }
   
   abstract String getToolXML();
}
