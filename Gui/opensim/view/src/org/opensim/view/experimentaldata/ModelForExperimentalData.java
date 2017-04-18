/*
 * 
 * ModelForExperimentalData.java
 *
 * Created on Feb 23, 09
 *
 * Copyright (c)  2006, Stanford University and Ayman Habib. All rights reserved.
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

package org.opensim.view.experimentaldata;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import org.opensim.modeling.*;
import org.opensim.utils.TheApp;

/**
 *
 * @author ayman
 *
 * A fake model to be used for data import. 
 * has 0 states.
 * potentially one body (ground)
 * empty or a frame visuals
 */
public class ModelForExperimentalData extends Model{
    
    MarkerSet markers;
    private Ground    ground;
    SimbodyEngine dEngine;
    private AnnotatedMotion motionData;
    private ForceSet forces;
    /**
     * Creates a new instance of ModelForExperimentalData
     */
    public ModelForExperimentalData(int i, AnnotatedMotion motionData) throws IOException {
        super();
        setName("ExperimentalData_"+i);
        this.motionData=motionData;
        //setup();
        dEngine = this.getSimbodyEngine();
        markers = this.getMarkerSet();
        ground = this.get_ground();
        forces = this.getForceSet();
        // blank filename to make sure it doesn't get overwritten
        this.setInputFileName("");
       
   }
    
    public void addMarkers(Vector<String> experimentalMarkers)
    {
        for (int i=0; i<experimentalMarkers.size(); i++){
            markers.addMarker(experimentalMarkers.get(i), new Vec3(0.), getGround());
        }
    }
    
    public void addForces(Vector<String> recordedForces)
    { /*OpenSim20
        for (int i=0; i<recordedForces.size(); i++){
            Force newForce = new Force(getGround().getName());
            newForce.setName(recordedForces.get(i));
            getForces().append(newForce);
        }*/
    }

    public AnnotatedMotion getMotionData() {
        return motionData;
    }

    private void setMotionData(AnnotatedMotion motionData) {
        this.motionData = motionData;
    }

    public Ground getGround() {
        return ground;
    }

    public ForceSet getForces() {
        return forces;
    }

    public void setForces(ForceSet forces) {
        this.forces = forces;
    }

    public SimbodyEngine getSimbodyEngine() {
        return dEngine;
    }
}
