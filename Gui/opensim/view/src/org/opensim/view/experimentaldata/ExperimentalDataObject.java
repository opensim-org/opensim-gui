/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalDataObject.java                                       *
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
 * ExperimentalDataObject.java
 *
 * Created on March 10, 2009, 10:56 AM
 *
 *
 *
 */

package org.opensim.view.experimentaldata;

import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.opensim.modeling.AdhocModelComponent;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.State;
import org.opensim.view.OpenSimvtkGlyphCloud;
import org.opensim.view.motions.MotionDisplayer;

/**
 * An Object representing data to be visualized from a data/motion file e.g. Marker, GRF, etc.
 * @author ayman
 */
public class ExperimentalDataObject extends AdhocModelComponent {
    
    private ExperimentalDataItemType objectType=ExperimentalDataItemType.Unknown;
    //private String baseName="";
    private int startIndexInFileNotIncludingTime=-1;
    private boolean displayed=true;
    private boolean trailDisplayed=false;
    private int glyphIndex=-1;
    private UUID dataObjectUUID;
    private OpenSimvtkGlyphCloud myGlyph;
    /** Creates a new instance of ExperimentalDataObject */
    public ExperimentalDataObject(ExperimentalDataItemType objectType, String baseName, int index) {
        super();
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

    void updateDecorations(ArrayDouble interpolatedStates) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @return the dataObjectUUID
     */
    public UUID getDataObjectUUID() {
        return dataObjectUUID;
    }

    /**
     * @param dataObjectUUID the dataObjectUUID to set
     */
    public void setDataObjectUUID(UUID dataObjectUUID) {
        this.dataObjectUUID = dataObjectUUID;
    }
    
    public JSONObject createDecorationJson(ArrayList<UUID> comp_uuids, MotionDisplayer motionDisplayer){
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
