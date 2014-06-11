/*
 * PluginsDB.java
 *
 * Created on Jan 19, 2009, 9:55 AM
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
