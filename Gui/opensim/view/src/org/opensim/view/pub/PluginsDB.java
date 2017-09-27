/* -------------------------------------------------------------------------- *
 * OpenSim: PluginsDB.java                                                    *
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
 * PluginsDB.java
 *
 * Created on Jan 19, 2009, 9:55 AM
 *
 */

package org.opensim.view.pub;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.Model;

/**
 *
 * @author Ayman
 *
 * This class contains a representation of PluginsDB for serialization on entry/exit.
 * If we want to serialize to an XML file this class should implement Externalizable instead and follow Beans convention.
 */
public class PluginsDB implements Externalizable {
    
    private ArrayList<String> fileNames=new ArrayList<String>(5);
    private static final long serialVersionUID = 1L;
    private static PluginsDB instance;
    /** Creates a new instance of PluginsDB
     * This constructor will be invoked from the deserialization code 
     */
    public PluginsDB() {
        System.out.println("PluginsDB constructor called");
        instance = this;
    }

    public static synchronized PluginsDB getInstance() {
        if (instance == null) {
             new PluginsDB();
        }
        return instance;
    }
 
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeInt(getFileNames().size());
        for (int i=0; i<getFileNames().size(); i++) {
            out.writeUTF(getFileNames().get(i));
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();
        int sz = in.readInt();
        for (int i=0; i<sz; i++) {
            String sharedLibraryName = in.readUTF();
            try{
                System.load(sharedLibraryName);
                getFileNames().add(sharedLibraryName);
            }
            catch(UnsatisfiedLinkError e){
                System.out.println("Error trying to load library "+sharedLibraryName+" ignored.");
            }
        };
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }    
    
    public void addLibrary(String fileName) {
        fileNames.add(fileName);
    }

    public void loadPlugins() {
        for (int i=0; i<fileNames.size(); i++) {
            String sharedLibraryName = fileNames.get(i);
            try{
                System.load(sharedLibraryName);
                OpenSimLogger.logMessage("Successfully loaded library "+sharedLibraryName+".\n", OpenSimLogger.INFO);
            }
            catch(UnsatisfiedLinkError e){
                OpenSimLogger.logMessage("Error trying to load library "+sharedLibraryName+". Ignored, removed from persistent list.", OpenSimLogger.ERROR);
                fileNames.remove(i);
                i--;
            }
        };
    }
}
