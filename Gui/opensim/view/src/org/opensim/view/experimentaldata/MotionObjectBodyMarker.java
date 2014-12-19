/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.experimentaldata;

import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.DecorativeSphere;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;

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

/*
 * An Object representing an Experimental Marker
 */
public class MotionObjectBodyMarker extends MotionObjectBodyPoint {
 
    private String markerName;
    public MotionObjectBodyMarker(ExperimentalDataItemType objectType, String baseName, int index) {
        super(objectType, baseName, index);
    }
    
    public String getConcreteClassName() {
        return "Experimental Marker";
    }
    /**
     * @return the markerName
     */
    public String getMarkerName() {
        return markerName;
    }

    /**
     * @param markerName the markerName to set
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }


    public void generateDecorations(boolean fixed, ModelDisplayHints hints, State state, ArrayDecorativeGeometry appendToThis) {
        if (!fixed){
            Transform xform = new Transform();
            xform.setP(new Vec3(point[0], point[1], point[2]));
            appendToThis.push_back(new DecorativeSphere(0.007).setBodyId(0).setColor(new Vec3(0., 1., 1.0)).setOpacity(0.5).setIndexOnBody(getStartIndexInFileNotIncludingTime()).setTransform(xform));            
        }
    }

    @Override
    void updateGeometry(ArrayDouble interpolatedStates) {
        int idx = getStartIndexInFileNotIncludingTime();
        setPoint(new double[]{interpolatedStates.get(idx)/1000., interpolatedStates.get(idx+1)/1000., interpolatedStates.get(idx+2)/1000.});
    }

    
 }
