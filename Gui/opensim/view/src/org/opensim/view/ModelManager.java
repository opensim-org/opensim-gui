/*
 * Copyright (c)  2005-2008, Stanford University
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
/*
 * ModelManager.java
 *
 * Created on May 5, 2006, 1:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;


import java.util.ArrayList;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.opensim.modeling.Model;

/**
 *
 * @author ken
 */
public final class ModelManager implements ExplorerManager.Provider {
    
    private static ModelManager _instance = null;
    
    private final ExplorerManager _manager = new ExplorerManager();
    
    private ArrayList<Model> _models = new ArrayList<Model>();
    
    /** Get the single instance of the ModelManger */
    public static synchronized ModelManager getDefault() {
        if (_instance == null)
            _instance = new ModelManager();
        return _instance;
    }
    
    /** Creates a new instance of ModelManager */
    public ModelManager() {
    }
    
    public ArrayList<Model> getModels() { // TODO: make this const
        return _models;
    }
    
    public void addModel(Model m) {
        _models.add(m);
    }

    public ExplorerManager getExplorerManager() {
        return _manager;
    }
    
} // class ModelManager
