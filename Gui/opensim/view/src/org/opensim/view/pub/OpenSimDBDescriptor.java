/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimDBDescriptor.java                                          *
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
 * OpenSimDBDescriptor.java
 *
 * Created on May 2, 2008, 9:55 AM
 *
 */

package org.opensim.view.pub;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.opensim.modeling.Model;
import org.opensim.view.experimentaldata.ModelForExperimentalData;

/**
 *
 * @author Ayman
 *
 * This class contains a short, more "textual" representation of OpenSimDB for serialization on entry/exit.
 * If we want to serialize to an XML file this class should implement Externalizable instead and follow Beans convention.
 */
public class OpenSimDBDescriptor implements Externalizable {
    
    private ArrayList<String> fileNames=new ArrayList<String>(5);
    private ArrayList<Integer> modelIds=new ArrayList<Integer>(5);
    private int currentModelIndex=-1;    //instead of using index so that if there's a problem we don't pick thw wrong model'
    private static final long serialVersionUID = 1L;
    /** Creates a new instance of OpenSimDBDescriptor
     * This constructor will be invoked from the deserialization code 
     */
    public OpenSimDBDescriptor() {
        
    }

    /** Creates a new instance of OpenSimDBDescriptor from OpenSimDB
     */
    public OpenSimDBDescriptor(OpenSimDB opensimDB) {
        Object[] models = opensimDB.getAllModels();
        for (int i=0; i< models.length; i++){
            if (!(models[i] instanceof Model)|| 
                    (models[i] instanceof ModelForExperimentalData))
                continue;
            Model mdl = (Model) models[i];
            String file = mdl.getInputFileName();   // Should be Absolute Path I think
            getFileNames().add(file);
            getModelIds().add(i);
            if (opensimDB.getCurrentModel()==mdl)
                currentModelIndex=i;
        }
    }



    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeInt(currentModelIndex);
        out.writeInt(getFileNames().size());
        for (int i=0; i<getFileNames().size(); i++) {
            out.writeUTF(getFileNames().get(i));
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();
        int currentModelIndex = in.readInt();
        int sz = in.readInt();
        for (int i=0; i<sz; i++) {
            getFileNames().add(in.readUTF());
        }
        OpenSimDB.getInstance().rebuild(this);
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public ArrayList<Integer> getModelIds() {
        return modelIds;
    }

    public void setModelIds(ArrayList<Integer> modelIds) {
        this.modelIds = modelIds;
    }

    public int getCurrentModelIndex() {
        return currentModelIndex;
    }

    public void setCurrentModelIndex(int currentModelIndex) {
        this.currentModelIndex = currentModelIndex;
    }
    
}
