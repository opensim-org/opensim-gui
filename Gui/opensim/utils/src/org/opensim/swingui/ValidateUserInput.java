/*
 *
 * ValidateUserInput
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
