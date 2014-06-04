/*
 * Copyright (c)  2005-2008, Stanford University
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
package org.opensim.tracking;

import java.util.Vector;
import org.opensim.modeling.OpenSimObject;
import org.opensim.utils.XMLExternalFileChooser;

public class XMLExternalFileChooserHelper {
   public static class Item {
      public OpenSimObject obj;
      public String description;
      public Item(OpenSimObject obj, String description) { this.obj = obj; this.description = description; }
   }

   private String mainSettingsFileName;
   private Vector<Item> items = new Vector<Item>();

   public XMLExternalFileChooserHelper(String mainSettingsFileName) {
      this.mainSettingsFileName = mainSettingsFileName;
   }

   public void addObject(OpenSimObject obj, String description) {
      items.add(new Item(obj,description));
   }

   public boolean promptUser() {
      String[] names = new String[items.size()];
      String[] externalFileNames = new String[items.size()];
      for(int i=0; i<items.size(); i++) {
         names[i] = items.get(i).description;
         externalFileNames[i] = getXMLExternalFileName(items.get(i).obj);
      }
      boolean result = XMLExternalFileChooser.promptUser(mainSettingsFileName, names, externalFileNames);
      if(result) {
         for(int i=0; i<items.size(); i++)
            applyXMLExternalFileName(items.get(i).obj, externalFileNames[i]);
      }
      return result;
   }

   private static String getXMLExternalFileName(OpenSimObject obj) {
      return (!obj.getInlined() && !obj.getDocumentFileName().equals("")) ? obj.getDocumentFileName() : null;
   }

   private static void applyXMLExternalFileName(OpenSimObject obj, String externalFileName) {
      if(externalFileName==null) obj.setInlined(true);
      else obj.setInlined(false, externalFileName);
   }
}
