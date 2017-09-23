/* -------------------------------------------------------------------------- *
 * OpenSim: XMLExternalFileChooserHelper.java                                 *
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
