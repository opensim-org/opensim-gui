/*
 *
 * Prefs
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
package org.opensim.utils;
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

/**
 *
 * @author Ayman. Utilities to support/back preferences.
 */
public final class Prefs {
   
   private static Prefs instance;
   private static NumberFormat numFormat = NumberFormat.getInstance();

    /**
     * Creates a new instance of Prefs
     */
    public static Prefs getInstance()
    {
       if (instance==null)
          instance = new Prefs();
       return instance;
       
    }
    public void putPref(String key, String value)
    {
        Preferences.userNodeForPackage(this.getClass()).put(key, value);
    }

    public String getPref(String key, String defaultValue)
    {
        return Preferences.userNodeForPackage(this.getClass()).get(key, defaultValue);
    }
    public String getPreferredDirectory()
    {
        String defaultDir="";
        defaultDir = Preferences.userNodeForPackage(this.getClass()).get("WorkDirectory", defaultDir);
 
        return defaultDir;
    }
    /**
     * Parse a string of color attributes and return it in an array of doubles
     */
    public static double[] parseColor(String aColor) {
        double[] color = new double[3];
        int i=0;
        StringTokenizer tokenizer = new StringTokenizer(aColor, " \t\n\r\f,;");
        while (tokenizer.hasMoreTokens() && i<3){
            String nextToken = tokenizer.nextToken();
            try {
               color[i] = numFormat.parse(nextToken).doubleValue();
            } catch (ParseException ex) {
               Toolkit.getDefaultToolkit().beep();
               color[i] = 0.0;
            }
            i++;
        }
        return color;
    }

}
