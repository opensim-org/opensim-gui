/*
 *
 * ExecOpenSimProcess
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

