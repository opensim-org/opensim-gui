/*
 *
 * ModelSettingsSerializer
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
