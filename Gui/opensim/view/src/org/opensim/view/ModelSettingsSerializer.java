/* -------------------------------------------------------------------------- *
 * OpenSim: ModelSettingsSerializer.java                                      *
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
 *
 * ModelSettingsSerializer
 * Author(s): Ayman Habib
 */
package org.opensim.view;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.Model;

/**
 *
 * @author Ayman
 */
public class ModelSettingsSerializer {
   
   private String filename;
   private ModelSettings settings;
   /**
    * Creates a new instance of ModelSettingsSerializer 
    * filename to serialize from/to for the model
    */
   public ModelSettingsSerializer(String filename, boolean read) {
      this.setFilename(filename);
      if (read)
         read();
   }
   
   public void read()
   {
      if (getFilename()==null){ // Model has not been serialized ever before
         settings = new ModelSettings(); 
         return;
      }
      try{
         XMLDecoder decoder = new XMLDecoder(
                 new FileInputStream(getFilename()));
         settings=((ModelSettings)decoder.readObject());
         //System.out.println("Found ModelGUIPrefs with");
      }catch (FileNotFoundException e){
         //System.out.println("not found");
         settings = new ModelSettings();
      }
   }
   
   public Object confirmAndWrite(Model model)
   {
      if(getFilename()==null) { // Settings don't have a filename associated with them, can't save
         return NotifyDescriptor.NO_OPTION;
      }
      // Show message to prompt user
     String modelName = (model!=null && model.getName()!=null) ? model.getName() : "";
     String modelFileName = (model!=null && model.getInputFileName()!=null) ? (new File(model.getInputFileName())).getName() : "";
     String modelStr = modelName+" ("+modelFileName+")";
     NotifyDescriptor dlg = new NotifyDescriptor.Confirmation("Do you want to save settings (e.g. poses) for model "+modelStr+" to file?", "Save model settings?");
     Object userSelection=DialogDisplayer.getDefault().notify(dlg);
     if(((Integer)userSelection).intValue()==((Integer)NotifyDescriptor.OK_OPTION).intValue()){
         XMLEncoder encoder;
         try {
            encoder = new XMLEncoder(new FileOutputStream(getFilename()));
            encoder.writeObject(settings);
            encoder.close();
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
         }
     }
     return userSelection;
   }

   public ModelSettings getPrefs() {
      return settings;
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }
   
}
