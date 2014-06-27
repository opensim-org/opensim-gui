/*
 * ExperimentalDataObject.java
 *
 * Created on March 10, 2009, 10:56 AM
 *
 *
 *
 * Copyright (c)  2009, Stanford University and Ayman Habib. All rights reserved.
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

import org.opensim.modeling.OpenSimJavaObject;
import org.opensim.view.OpenSimvtkGlyphCloud;

/**
 * An Object representing data to be visualized from a data/motion file e.g. Marker, GRF, etc.
 * @author ayman
 */
public class ExperimentalDataObject extends OpenSimJavaObject {
    
    private ExperimentalDataItemType objectType=ExperimentalDataItemType.Unknown;
    //private String baseName="";
    private int startIndexInFileNotIncludingTime=-1;
    private boolean displayed=true;
    private boolean trailDisplayed=false;
    private int glyphIndex=-1;
    private OpenSimvtkGlyphCloud myGlyph;
    /** Creates a new instance of ExperimentalDataObject */
    public ExperimentalDataObject(ExperimentalDataItemType objectType, String baseName, int index) {
        this.setObjectType(objectType);
        this.setName(baseName);
        this.startIndexInFileNotIncludingTime = index;
    }

    public ExperimentalDataItemType getObjectType() {
        return objectType;
    }

    public void setObjectType(ExperimentalDataItemType objectType) {
        this.objectType = objectType;
    }
    public int getStartIndexInFileNotIncludingTime() {
        return startIndexInFileNotIncludingTime;
    }
    
    public String toString() {
        return (getName());
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isTrailDisplayed() {
        return trailDisplayed;
    }

    public void setTrailDisplayed(boolean trailDisplayed) {
        this.trailDisplayed = trailDisplayed;
    }

    public int getGlyphIndex() {
        return glyphIndex;
    }

    public void setGlyphInfo(int glyphIndex, OpenSimvtkGlyphCloud glyphUsed) {
        this.glyphIndex = glyphIndex;
        myGlyph = glyphUsed;
    }

    public OpenSimvtkGlyphCloud getMyGlyph() {
        return myGlyph;
    }

    /**
     * @param startIndexInFileNotIncludingTime the startIndexInFileNotIncludingTime to set
     */
    public void setStartIndexInFileNotIncludingTime(int startIndexInFileNotIncludingTime) {
        this.startIndexInFileNotIncludingTime = startIndexInFileNotIncludingTime;
    }


}
