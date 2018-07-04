/* -------------------------------------------------------------------------- *
 * OpenSim: BrowserPageDisplayerAction.java                                   *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
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

package org.opensim.helputils.helpmenu;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.TheApp;


/**
 * A Class that displays a passed in url
 */
class BrowserPageDisplayerAction extends AbstractAction
{
    String url;
    String displayName;
    
    public BrowserPageDisplayerAction(File localFile){
        super(localFile.getName());
        this.url = localFile.getAbsolutePath();
    }
    
    public BrowserPageDisplayerAction(String dispName, String url){
        super(dispName);
        this.url = url;          
        this.displayName = dispName;
    }
        
    @Override
    public void actionPerformed(ActionEvent e) {
        String file="";
        try {
            URL onlineURL = new URL(url);
            file = onlineURL.getFile();
            int lastSeparatorIndex = file.lastIndexOf('/');
            file = file.substring(lastSeparatorIndex);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        // If issues with online tutorials then open local file
        String path = BrowserLauncher.isConnected() ? url : TheApp.getCrossPlatformInstallDir() +  "/doc/" + file+".pdf";
        
        System.out.println("Path: " + path + "or "+
                TheApp.getInstallDir() + File.separator + "../doc" + File.separator + file+".pdf");
        BrowserLauncher.openURL(path);            
    }
    
}