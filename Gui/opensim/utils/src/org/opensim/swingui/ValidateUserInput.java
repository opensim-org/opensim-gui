/* -------------------------------------------------------------------------- *
 * OpenSim: ValidateUserInput.java                                            *
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
 * ValidateUserInput
 * Author(s): Ayman Habib
 */
package org.opensim.swingui;

import java.awt.Toolkit;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 *
 * @author Ayman, a final class with static methods to verify the validity of user input (e.g. file exists, 
 * type mismatch (string in int field) etc.
 */
public final class ValidateUserInput {

   private static NumberFormat numFormat = NumberFormat.getInstance();

    /**
     * Check if a file exists, has correct permissions 
     */
    public static boolean validateFileExists(final String filename, 
                                            boolean shouldExist, 
                                            boolean directoriesOk,
                                            boolean isRequired)
    {
        boolean valid = true;
        
        if (filename.equals("") && isRequired)
            return false;
        
        try {
            File test = new File(filename);
            if (shouldExist)    // May ned to issue a warning for overwrite
                valid = test.exists();
            if (valid){
                valid = (test.isDirectory()==directoriesOk);
            }
        }
        catch (NullPointerException e){
            valid = false;
        }
        catch (SecurityException e){
            valid = false;
        }
        return valid;
    }
    /**
     * Validate a single double
     */
    public static boolean validateDouble(final String dString, boolean isRequired)
    {
        boolean valid=true;
        
        if (dString=="" && isRequired)
            return false;
        
        try {
            double dval = numFormat.parse(dString).doubleValue();
        }
        catch(ParseException e){
           Toolkit.getDefaultToolkit().beep();
           valid = false;
        }
        return valid;
    }
    /**
     * Validate a range is made up of two numbers in right order
     **/
    public static boolean validateDoubleRange(final String min, 
                                            final String max, 
                                            boolean isRequired)
    {
        boolean valid = true;
        
        valid = validateDouble(min, isRequired);
        valid = valid && validateDouble(max, isRequired);
        if (valid && isRequired) { // Validate that min < max
           try {
              valid = (numFormat.parse(max).doubleValue() > numFormat.parse(min).doubleValue());
           } catch (ParseException ex) {
              Toolkit.getDefaultToolkit().beep();
              valid = false;
           }
        }
        return valid;
    }
  
    
}
