/* -------------------------------------------------------------------------- *
 * OpenSim: Prefs.java                                                        *
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
 * Prefs
 * Author(s): Ayman Habib
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
