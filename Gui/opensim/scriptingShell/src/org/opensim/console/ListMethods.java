/* -------------------------------------------------------------------------- *
 * OpenSim: ListMethods.java                                                  *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.lang.reflect.Method;
import org.openide.util.Exceptions;

/**
 *
 * @author Ayman
 */
public class ListMethods {
    public static String listMethodsForClass(String name) {
        String ouputString = "";
        try {
            Class c = Class.forName(name);
            Method methods[] = c.getMethods();
            for (int i = 0, n = methods.length; i < n; i++) {
                //ScriptingShellTopComponent.getDefault().logMessage("\t" + methods[i]);
                ouputString = ouputString.concat(methods[i].toString()+"\n");
            }
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ouputString;
    }
    public static String listMethodsForObject(Object obj) {
        return listMethodsForClass(obj.getClass().getName());
    }
}
