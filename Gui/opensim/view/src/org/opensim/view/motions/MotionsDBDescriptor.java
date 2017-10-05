/* -------------------------------------------------------------------------- *
 * OpenSim: MotionsDBDescriptor.java                                          *
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
 * MotionsDBDescriptor.java
 *
 * Created on May 2, 2008, 9:55 AM
 *
 */

package org.opensim.view.motions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.view.motions.MotionsDB.ModelMotionPair;

/**
 *
 * @author Ayman
 *
 * This class contains a short, more "textual" representation of MotionsDB for serialization on entry/exit.
 */
public class MotionsDBDescriptor implements Externalizable {
    
    private ArrayList<String> modelFileNames=new ArrayList<String>(5);
    private ArrayList<String> motionFileNames=new ArrayList<String>(5);
    private static final long serialVersionUID = 1L;
    /** Creates a new instance of MotionsDBDescriptor
     * This constructor will be invoked from the deserialization code 
     */
    public MotionsDBDescriptor() {
        
    }

    /** Creates a new instance of MotionsDBDescriptor from MotionsDB, only current motion and only if 
     */
    public MotionsDBDescriptor(MotionsDB motionsDB) {
        int nMotions= motionsDB.getInstance().getNumCurrentMotions();
        if (nMotions==0) return;
        for (int i=0; i< nMotions; i++){
            ModelMotionPair modelMotion = motionsDB.getInstance().getCurrentMotion(i);
            Model mdl = modelMotion.model;
            Storage dMotion = modelMotion.motion;
            String file = mdl.getInputFileName();
            String motionFile = motionsDB.getInstance().getStorageFileName(dMotion);
            if (file != null && motionFile != null){
                getModelFileNames().add(file);
                getMotionFileNames().add(motionFile);
            }
        }
    }



    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);
        out.writeInt(getModelFileNames().size());
        for (int i=0; i<getModelFileNames().size(); i++) {
            out.writeUTF(getModelFileNames().get(i));
            out.writeUTF(getMotionFileNames().get(i));
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();
        int sz = in.readInt();
        for (int i=0; i<sz; i++) {
            getModelFileNames().add(in.readUTF());
            getMotionFileNames().add(in.readUTF());
        }
        MotionsDB.getInstance().rebuild(this);
    }

    public ArrayList<String> getModelFileNames() {
        return modelFileNames;
    }

    public void setModelFileNames(ArrayList<String> modelFileNames) {
        this.modelFileNames = modelFileNames;
    }

    public ArrayList<String> getMotionFileNames() {
        return motionFileNames;
    }

    public void setMotionFileNames(ArrayList<String> motionFileNames) {
        this.motionFileNames = motionFileNames;
    }


}
