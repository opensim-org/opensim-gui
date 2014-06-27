/*
 *
 * PlotterSourceStringArray
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
package org.opensim.plotter;

import java.io.File;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;

/**
 *
 * @author Ayman. A class representing the Source=File radio button. The file is parsed
 * and its contents cached in this object for quick reference later.
 */
public class PlotterSourceStringArray implements PlotterSourceInterface {
    // Data members
    public String[] allAvailable;

    public boolean[] selectionStatus;

   /** Creates a new instance of PlotterSourceFile */
   public PlotterSourceStringArray(String[] names) {
      allAvailable = new String[names.length];
      System.arraycopy(names, 0, allAvailable, 0, names.length);
      selectionStatus = new boolean[names.length];
      for(int i=0; i<names.length; i++){
         selectionStatus[i]=false;
      }
   }

   public String[] getAllQuantities() {
      return allAvailable;
   }

   public Vector<String> getSelectedQuantities() {
      int countSelected = 0;
      Vector<String> selectedVec = new Vector<String>(4);
      for(int i=0; i<selectionStatus.length ;i++){
         if (selectionStatus[i]){
               countSelected += 1;
               selectedVec.add(allAvailable[i]);
         }
      }
      return selectedVec;
   }

   public boolean[] filterByRegularExprssion(String regex) throws PatternSyntaxException {
       Pattern p = Pattern.compile(regex);
       
      for(int i=0; i<allAvailable.length ;i++)
       selectionStatus[i] = p.matcher(allAvailable[i]).matches();
       
       return selectionStatus;
   }

   public boolean[] toggleItemSelection(String item) {      
      for(int i=0; i<allAvailable.length ;i++){
         if (allAvailable[i].compareTo(item)==0)
            selectionStatus[i] = !selectionStatus[i];
      }
      return getSelectionStatus();
   }

   public boolean[] getSelectionStatus() {
      return selectionStatus;
   }

   public void setSelectionStatus(boolean[] selectionStatus) {
      this.selectionStatus = selectionStatus;
   }

   public String getDisplayName() {
      return "";
   }

   public Storage getStorage() {
      return null;
   }

    public void clearSelectionStatus() {
       for(int i=0; i<selectionStatus.length ;i++)
        selectionStatus[i]=false;
    }

    public boolean isStorageLive() {
        return true;
    }

    public void setStorageLive(boolean b) {
    }
    
    public double getDefaultMin(String domainName)
    {
       throw new UnsupportedOperationException("Cant get min for names only source");
       // Unreachable
     }
    
   
    public double getDefaultMax(String domainName)
    {
       throw new UnsupportedOperationException("Cant get min for names only source");
       // Unreachable
     }

   public boolean isValidName(String columnName) {
      for(int i=0;i<allAvailable.length;i++){
         if (allAvailable[i].compareTo(columnName)==0)
            return true;
      }
      return false;
   }

    public boolean convertAngularUnits() {
        return false;
    }

    public boolean hasFullState(Model model) {
        return false;
    }

}
