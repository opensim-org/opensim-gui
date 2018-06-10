/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.opensim.modeling.AbstractPathPoint;
import org.opensim.modeling.ConditionalPathPoint;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.MovingPathPoint;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.State;

/**
 *
 * @author Ayman-NMBL
 * 
 * Utility class to handle pathVisualization by breaking GeometryPath into segments,
 * handling wrapping
 */
public class PathVisualization {

    ModelVisualizationJson modelVis;
    private ArrayList<PathSegment> segments;
    boolean hasWrapping = false;
    int numWrapObjects = 0;
    String pathPointMaterial;
    GeometryPath path;
    
    public PathVisualization(ModelVisualizationJson modelVis, GeometryPath path) {
        this.modelVis = modelVis;
        this.path = path;
        PathPointSet pptSet = path.getPathPointSet();
        int numSegments = path.getPathPointSet().getSize() - 1;
        numWrapObjects = path.getWrapSet().getSize();
        hasWrapping = numWrapObjects > 0;
        segments = new ArrayList<PathSegment>(numSegments);
        for (int seg = 0; seg < numSegments; seg++) {
            PathSegment nextSegment = new PathSegment(pptSet.get(seg), pptSet.get(seg + 1));
            if (hasWrapping)
                nextSegment.numInternalPoints = numWrapObjects*modelVis.NUM_PATHPOINTS_PER_WRAP_OBJECT;
            segments.add(nextSegment);
        }
    }
    /**
     * @return the segments
     */
    public ArrayList<PathSegment> getSegments() {
        return segments;
    }

    void setPathPointMaterial(String ppmat) {
        pathPointMaterial = ppmat;
    }
    /*
     * After this call, every segment has populated UUIDs for end points, wrapping is ignored
    */
    void createPathpointsInSceneGraph(State state) {
        int numSegments = segments.size();
        for (int seg = 0; seg < numSegments; seg++) {
            PathSegment nextSegment = segments.get(seg);
            AbstractPathPoint currentPoint = nextSegment.startPoint;
            UUID uuid = findOrCreateUUIDForPathPoint(currentPoint, nextSegment);
            addToSpecialListsIfNeeded(currentPoint, uuid, seg);
            // repeat for lastPoint
            currentPoint = nextSegment.lastPoint;
            uuid = findOrCreateUUIDForPathPoint(currentPoint, nextSegment);
            addToSpecialListsIfNeeded(currentPoint, uuid, seg+1);
        }
    }

    private UUID findOrCreateUUIDForPathPoint(AbstractPathPoint currentPoint, PathSegment nextSegment) {
        ArrayList<UUID> findUUIDForObject = modelVis.findUUIDForObject(currentPoint);
        UUID uuid;
        if (findUUIDForObject==null){
            UUID pathpoint_uuid = modelVis.addPathPointObjectToParent(currentPoint,
                    pathPointMaterial, false);
            modelVis.addComponentToUUIDMap(currentPoint, pathpoint_uuid);
            nextSegment.getUuids().add(pathpoint_uuid);
            uuid = pathpoint_uuid;
        }
        else{
            uuid = findUUIDForObject.get(0);
            nextSegment.getUuids().add(uuid);
        }
        return uuid;
    }
    public void collectPathPointArrays(JSONArray pathpoint_jsonArr, JSONArray pathpointActive_jsonArr){
        int numSegments = segments.size();
        // From firt segment will get all UUID, active
        ArrayList<UUID> uuids = segments.get(0).getUuids();
        for(UUID uuid:uuids){
            pathpoint_jsonArr.add(uuid.toString());
            pathpointActive_jsonArr.add(true);
        }
        for (int seg = 1; seg < numSegments; seg++) {
            // Will skip first entry to avoid duplicates
            PathSegment nextSegment = segments.get(seg);
            uuids = nextSegment.getUuids();
            for(int i=1; i<uuids.size();i++){
                pathpoint_jsonArr.add(uuids.get(i).toString());
                pathpointActive_jsonArr.add(true);
            }
        }
    }

    private void addToSpecialListsIfNeeded(AbstractPathPoint currentPoint, UUID uuid, int segNumber) {
        // MovingPathPoint and ConditionalPathPoints need special treatment
        if (MovingPathPoint.safeDownCast(currentPoint)!=null)
            modelVis.getMovingComponents().put(currentPoint, uuid);
        else if (ConditionalPathPoint.safeDownCast(currentPoint)!=null){
            // Always create a Proxy. On the fly we'll either use the cpt location or Proxy to find xform
            ConditionalPathPoint cppt = ConditionalPathPoint.safeDownCast(currentPoint);
            PathPointSet pptSet = path.getPathPointSet();
            ComputedPathPointInfo cppInfo= new ComputedPathPointInfo(pptSet.get(segNumber-1), pptSet.get(segNumber+1), 0.5);
            modelVis.getProxyPathPoints().put(currentPoint, cppInfo);
        }
    }
}

class PathSegment {

    AbstractPathPoint startPoint;
    AbstractPathPoint lastPoint;
    private ArrayList<UUID> uuids;
    int numInternalPoints = 0;

    PathSegment(AbstractPathPoint startPt, AbstractPathPoint endPt) {
        startPoint = startPt;
        lastPoint = endPt;
        uuids = new ArrayList<UUID>();
    }
    /**
     * @return the uuids
     */
    public ArrayList<UUID> getUuids() {
        return uuids;
    }
}
