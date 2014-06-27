/*
 * MotionsDBDescriptor.java
 *
 * Created on May 2, 2008, 9:55 AM
 *
 * Copyright (c)  2005-2008, Stanford University, Ayman Habib
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
