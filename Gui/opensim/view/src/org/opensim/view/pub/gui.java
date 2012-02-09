/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.pub;

import java.io.IOException;
import org.openide.util.Exceptions;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;

/**
 *
 * @author ayman
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib
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

/**
 *  giu is an implementation of the Facade design pattern to 
 * 1. Hide the various classes used by the GUI
 * 2. Provide convenience methods that update the GUI if needed
 * 
 * @author ayman
 */
public final class gui {
    static public Model getCurrentModel(){
        return OpenSimDB.getInstance().getCurrentModel();
    }
    static public void setCurrentModel(Model aModel){
        OpenSimDB.getInstance().setCurrentModel(aModel);
    }
    static public OpenSimContext getModelState(Model aModel){
        return OpenSimDB.getInstance().getContext(aModel);
    }
    static public Coordinate getCoordinate(Model aModel, String name){
        return aModel.getCoordinateSet().get(name);
    }
    static public void setCoordinate(Coordinate coordinate, double newValue){
        getModelState(coordinate.getModel()).setValue(coordinate, newValue);
        ViewDB.getInstance().updateModelDisplay(coordinate.getModel());
    }
    static public void addModel(String fileName){
        try {
            Model aModel = new Model(fileName);
            String filename = aModel.getInputFileName();
            OpenSimDB.getInstance().addModel(aModel);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
