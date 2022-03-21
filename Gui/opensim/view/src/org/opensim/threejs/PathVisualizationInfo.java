/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import org.json.simple.JSONObject;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.PathWrap;
import org.opensim.modeling.WrapObject;

/**
 *
 * @author Ayman-NMBL
 */
public class PathVisualizationInfo {

    ArrayList<Vector<WrapObject>> pathSegmentWrapping = new ArrayList<Vector<WrapObject>>();
    private boolean wrapping=false;
    private UUID material_uuid;
    public PathVisualizationInfo(GeometryPath path) {
        int numWrapObjects = path.getWrapSet().getSize();
        wrapping = numWrapObjects>0;
        PathPointSet pathPointSet = path.getPathPointSet();
        for (int seg=0; seg < pathPointSet.getSize()-1; seg++)
            pathSegmentWrapping.add(new Vector<WrapObject>());
        
        // Cycle through and decide for every path-segment how many WrapObjects can be encountered
        for (int i=0; i<numWrapObjects; i++){
            PathWrap pathWrap = path.getWrapSet().get(i);
            int start = pathWrap.get_range(0);
            int end = pathWrap.get_range(1);
            // -1 means unspecified, assume all segments can wrap
            if (start==-1 && end ==-1){
                start = 1;
                end = pathPointSet.getSize()-1;
            }
                
            // Segments start to end-1 can wrap around pathWrap.WrapObject
            for (int j=start-1; j< end-1; j++){
                pathSegmentWrapping.get(j).add(pathWrap.getWrapObject());
            }
        }
    }

    /**
     * @return the wrapping
     */
    public boolean isWrapping() {
        return wrapping;
    }

    void setMaterialID(UUID mat_uuid) {
        material_uuid = mat_uuid;
    }

    UUID getMaterialID() {
        return material_uuid;
    }
    int getNumWrapObjectsForSegment(int segIndex){
        return pathSegmentWrapping.get(segIndex).size();
    }
}
