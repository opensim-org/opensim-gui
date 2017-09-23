/* -------------------------------------------------------------------------- *
 * OpenSim: ExecOpenSimProcess.java                                           *
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
 * ExecOpenSimProcess
 * Author(s): Ayman Habib
 */
package org.opensim.view.base;

import java.io.*;
import org.opensim.logger.OpenSimLogger;
/**
 * Stream Gobbler class ref. http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 * to deal with stdout, stderr.
 */
class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    
    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    public void run() {
       try {
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader br = new BufferedReader(isr);
          String line=null;
          while ( (line = br.readLine()) != null){
             if (type.compareToIgnoreCase("ERROR")==0)
               OpenSimLogger.logMessage(line+"\n", OpenSimLogger.ERROR);
             else
               OpenSimLogger.logMessage(line+"\n", OpenSimLogger.INFO);
          }
       } catch (IOException ioe) {
          ioe.printStackTrace();
       }
    }
}

public class ExecOpenSimProcess
{
    public static boolean execute(String command, String[] env, File dirToExecuteIn)
    {
        try {            
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, env, dirToExecuteIn);
            // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(proc.getErrorStream(), "ERROR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(), "OUTPUT");
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();                
            
            int exitVal = proc.waitFor();
            System.out.println("Exit value from process="+exitVal);
            return (exitVal==0);	// More detailed error message will be needed
        } 
        catch (Throwable t) {
           t.printStackTrace();
           return false;
        }
    }
}

