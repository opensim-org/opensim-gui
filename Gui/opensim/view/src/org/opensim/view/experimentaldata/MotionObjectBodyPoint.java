package org.opensim.view.experimentaldata;

import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.opensim.modeling.Sphere;
import org.opensim.view.motions.MotionDisplayer;

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
 * A class representing a point fixed to a body read from a data/motion file. 
 */
public class MotionObjectBodyPoint extends ExperimentalDataObject {
    protected double[] point = new double[]{0, 0, 0};
    Sphere sphere = new Sphere(.01);
    public MotionObjectBodyPoint(ExperimentalDataItemType objectType, String baseName, int index) {
        super(objectType, baseName, index);
        setPointIdentifier(baseName);
        //FIXME DEVWEEK sphere.setFrameName("ground");
        // Will get point and set it as Sphere.center, this's a hack
    }
    protected String pointExpressedInBody = "ground";
    private String pointIdentifier="";
    /**
     * Get the value of pointExpressedInBody
     *
     * @return the value of pointExpressedInBody
     */
    public String getPointExpressedInBody() {
        return pointExpressedInBody;
    }

    /**
     * Set the value of pointExpressedInBody
     *
     * @param pointExpressedInBody new value of pointExpressedInBody
     */
    public void setPointExpressedInBody(String bodyName) {
         this.pointExpressedInBody = bodyName;
    }


    public void setPointIdentifier(String makeIdentifier) {
        pointIdentifier = makeIdentifier;
    }

    public String getPointIdentifier() {
        return pointIdentifier;
    }


    /**
     * @return the point
     */
    public double[] getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(double[] point) {
        this.point = point;
    }

    boolean appliesForce() {
        return false;
    }
}
