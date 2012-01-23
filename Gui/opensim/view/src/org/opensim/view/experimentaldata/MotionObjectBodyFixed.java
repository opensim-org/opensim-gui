package org.opensim.view.experimentaldata;

import org.opensim.utils.Vec3;

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
public class MotionObjectBodyFixed extends ExperimentalDataObject {

    public MotionObjectBodyFixed(ExperimentalDataItemType objectType, String baseName, int index) {
        super(objectType, baseName, index);
    }
    public static final String PROP_BODYNAME = "bodyName";
    protected String bodyName = "ground";
    protected Vec3 position = new Vec3(0, 0, 0);

    /**
     * Get the value of bodyName
     *
     * @return the value of bodyName
     */
    public String getBodyName() {
        return bodyName;
    }

    /**
     * Get the value of position
     *
     * @return the value of position
     */
    public Vec3 getPosition() {
        return position;
    }

    /**
     * Set the value of bodyName
     *
     * @param bodyName new value of bodyName
     */
    public void setBodyName(String bodyName) {
        String oldBodyName = this.bodyName;
        this.bodyName = bodyName;
    }

    /**
     * Set the value of position
     *
     * @param position new value of position
     */
    public void setPosition(Vec3 position) {
        this.position = position;
    }
    
}
