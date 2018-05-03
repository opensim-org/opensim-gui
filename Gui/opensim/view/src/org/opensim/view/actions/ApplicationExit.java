/* -------------------------------------------------------------------------- *
 * OpenSim: ApplicationExit.java                                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Peter Loan                                         *
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
 * ApplicationExit.java
 *
 * Created on August 17, 2007, 1:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.actions;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.JOptionPane;
import org.opensim.utils.ApplicationState;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.motions.MotionsDBDescriptor;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.modeling.Model;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.TheApp;
import org.opensim.view.pub.OpenSimDBDescriptor;
import org.opensim.view.pub.PluginsDB;
import org.opensim.view.pub.ViewDB;
import org.opensim.view.pub.ViewDBDescriptor;

/**
 *
 * @author Peter Loan
 */
public class ApplicationExit extends WindowAdapter
{
   // This function is called when the app's close (X) box is clicked.
   public void windowClosing(WindowEvent e)
   {
      // Do nothing, because Installer::closing() will be called next,
      // and that function needs to process the exit because it's the
      // only function called when File...Exit is chosen.
      //confirmExit();
   }

   static public boolean confirmExit()
   {
      // Make sure the user really wants to quit.
      Object[] options = {"Yes", "Cancel"};
      int answer = JOptionPane.showOptionDialog(null,
         "Are you sure you want to exit?",
         "OpenSim",
         JOptionPane.YES_NO_OPTION,
         JOptionPane.WARNING_MESSAGE,
         null,
         options,
         options[1]);
      if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION)
         return false;

      System.out.println("Start saving application state to file:"+TheApp.getUserDir()+"AppState.xml");
      try {
          ApplicationState state = ApplicationState.getInstance();
          OpenSimDBDescriptor dbDesc = new OpenSimDBDescriptor(OpenSimDB.getInstance());
          state.addObject("OpenSimDB", dbDesc);
          ViewDBDescriptor vudbDesc = new ViewDBDescriptor(ViewDB.getInstance());
          state.addObject("ViewDB", vudbDesc);
          //release 1.8 remember plugins
          state.addObject("PluginsDB", PluginsDB.getInstance());
          state.addObject("MotionsDB", new MotionsDBDescriptor(MotionsDB.getInstance()));
          XMLEncoder e = new XMLEncoder(new BufferedOutputStream(
                new FileOutputStream(TheApp.getUserDir()+"AppState.xml")));
          
          e.writeObject(state); // This method serializes an object graph 
          e.close();

      } catch (FileNotFoundException ex) {
          ex.printStackTrace();
      } catch (IOException ex) {
          ErrorDialog.displayExceptionDialog(ex);
      }
      //System.out.println("Finish saving application state.");

      // Close all of the models, prompting the user to save them if necessary.
      // If any of the closes returns false, that model was not closed, so abort
      // the exit.
      Object[] models = OpenSimDB.getInstance().getAllModels();
      // We will assume that models will be closed because that's more conservative
      // if user cancels that's ok since later session will overwrite the applicationState file
      for (int i=0; i<models.length; i++) {
         if (FileCloseAction.closeModel((Model)models[i]) == false)
            return false;
      }
      return true;
   }
}

