/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimLogger.java                                                *
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
 * OpenSimLogger
 * Author(s): Ayman Habib
 */
package org.opensim.logger;

import javax.swing.SwingUtilities;

/**
 *
 * @author Ayman. A wrapper to expose the logger window/topComponent
 *
 */
public final class OpenSimLogger {
   
   static LoggerTopComponent loggerWindowInstance;
   
   
   public static int OFF=-2;   // Could use enum too
   public static int DEBUG=-1;      
   public static int INFO=0;      
   public static int WARNING=1;      
   public static int ERROR=2;
   
   private static int errorLevel=INFO;
   
   /**
    * Creates a new loggerWindowInstance of OpenSimLogger
    */
   public OpenSimLogger() {      
   }
   
   public static void logMessage(final String message, final int messageErrorLevel)
   {
       SwingUtilities.invokeLater(new Runnable(){
         public void run() {
          if (loggerWindowInstance==null)
             loggerWindowInstance=LoggerTopComponent.getDefault();

          if (messageErrorLevel>=errorLevel)
             loggerWindowInstance.log(message);
           }
       });
   }

   public static void setErrorLevel(int aErrorLevel) {
      errorLevel = aErrorLevel;
   }
   
}
