/* -------------------------------------------------------------------------- *
 * OpenSim: NavigatorByTypeModel.java                                         *
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeSupport;
import org.opensim.modeling.ArrayConstObjPtr;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodySet;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.Constraint;
import org.opensim.modeling.Frame;
import org.opensim.modeling.ConstraintSet;
import org.opensim.modeling.ContactGeometry;
import org.opensim.modeling.ContactGeometrySet;
import org.opensim.modeling.Controller;
import org.opensim.modeling.ControllerSet;
import org.opensim.modeling.Force;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.Ground;
import org.opensim.modeling.Joint;
import org.opensim.modeling.JointSet;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.ObjectGroup;
import org.opensim.modeling.Probe;
import org.opensim.modeling.ProbeSet;

/**
 *
 * @author Ayman
 * 
 * This class builds internally "Sets" that will back off the various sets displayed in
 * the GUI  Navigator filtering by Type. Other possible views can be constructed similarly
 * 
 */
public class NavigatorByTypeModel {
    public static final String PROP_SETOFBODIES = "PROP_SETOFBODIES";
    public static final String PROP_SETOFJOINTS = "PROP_SETOFJOINTS";
    public static final String PROP_SETOFMARKERS = "PROP_SETOFMARKERS";
    public static final String PROP_SETOFCONSTRAINTS = "PROP_SETOFCONSTRAINTS";
    public static final String PROP_SETOFCONTACTGEOMETRY = "PROP_SETOFCONTACTGEOMETRY";
    public static final String PROP_SETOFFORCES = "PROP_SETOFFORCES";
    public static final String PROP_SETOFCONTROLLERS = "PROP_SETOFCONTROLLERS";
    
    private final BodySet setOfBodies = new BodySet();
    private final JointSet setOfJoints = new JointSet();
    private final MarkerSet setOfMarkers = new MarkerSet();
    private final ConstraintSet setOfConstraints = new ConstraintSet();
    private final ContactGeometrySet setOfContactGeometry = new ContactGeometrySet();
    private final ForceSet setOfForces = new ForceSet();
    private final ControllerSet setOfControllers = new ControllerSet();
    private final ProbeSet setOfProbes = new ProbeSet();
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    private final transient VetoableChangeSupport vetoableChangeSupport = new java.beans.VetoableChangeSupport(this);
    private Ground ground;
    
    public NavigatorByTypeModel(Model model) {
        setOfBodies.setMemoryOwner(false);
        setOfJoints.setMemoryOwner(false);
        setOfMarkers.setMemoryOwner(false);
        setOfConstraints.setMemoryOwner(false);
        setOfContactGeometry.setMemoryOwner(false);
        setOfForces.setMemoryOwner(false);
        setOfControllers.setMemoryOwner(false);
        setOfProbes.setMemoryOwner(false);
        buildCollections(model);
        createMuscleGroups(model);
    }

    // This could be made more efficient if we process all "Components" in one go, and place in correct bins
    // For now we'll use multiple iterators for different types 
    private void buildCollections(Model model) {
        ComponentsList compList = model.getComponentsList();
        ComponentIterator compIter = compList.begin();
        while (!compIter.equals(compList.end())) {
            Component comp = compIter.__deref__();
            Frame frm = Frame.safeDownCast(comp);
            if (frm != null){ // Either a generic Frame or a Body
                Body body = Body.safeDownCast(frm);
                if (body != null)
                    setOfBodies.adoptAndAppend(body);
            }
            else {
               classifyJoint(comp);
               classifyMarker(comp);
               classifyConstraint(comp);
               classifyContactGeometry(comp);
               classifyForce(comp);
               classifyController(comp);
               classifyProbe(comp);
            }
            compIter.next();
        }
        ground = model.getGround();
    }

    private void classifyJoint(Component comp) {
        Joint jnt = Joint.safeDownCast(comp);
        if (jnt != null){
            setOfJoints.adoptAndAppend(jnt);
        }
    }

    private void classifyMarker(Component comp) {
        Marker tComp = Marker.safeDownCast(comp);
        if (tComp != null){
            setOfMarkers.adoptAndAppend(tComp);
        }
    }

    private void classifyConstraint(Component comp) {
        Constraint tComp = Constraint.safeDownCast(comp);
        if (tComp != null){
            setOfConstraints.adoptAndAppend(tComp);
        }
    }

    private void classifyForce(Component comp) {
        Force tComp = Force.safeDownCast(comp);
        if (tComp != null){
            setOfForces.adoptAndAppend(tComp);
        }
    }

    private void classifyController(Component comp) {
        Controller tComp = Controller.safeDownCast(comp);
        if (tComp != null){
            setOfControllers.adoptAndAppend(tComp);
        }
    }

    private void classifyContactGeometry(Component comp) {
        ContactGeometry tComp = ContactGeometry.safeDownCast(comp);
        if (tComp != null){
            setOfContactGeometry.adoptAndAppend(tComp);
        }
    }
    
    private void classifyProbe(Component comp) {
        Probe probe = Probe.safeDownCast(comp);
        if (probe != null){
            setOfProbes.adoptAndAppend(probe);
        }
    }

    /**
     * @return the setOfBodies
     */
    public BodySet getSetOfBodies() {
        return setOfBodies;
    }

    /**
     * @return the setOfJoints
     */
    public JointSet getSetOfJoints() {
        return setOfJoints;
    }

    /**
     * @return the setOfMarkers
     */
    public MarkerSet getSetOfMarkers() {
        return setOfMarkers;
    }

    /**
     * @return the setOfConstraints
     */
    public ConstraintSet getSetOfConstraints() {
        return setOfConstraints;
    }

    /**
     * @return the setOfContactGeometry
     */
    public ContactGeometrySet getSetOfContactGeometry() {
        return setOfContactGeometry;
    }

    /**
     * @return the setOfForces
     */
    public ForceSet getSetOfForces() {
        return setOfForces;
    }

    /**
     * @return the setOfControllers
     */
    public ControllerSet getSetOfControllers() {
        return setOfControllers;
    }

    /**
     * @return the setOfProbes
     */
    public ProbeSet getSetOfProbes() {
        return setOfProbes;
    }

    public Ground getGround() {
        return ground;
    }

    private void createMuscleGroups(Model model) {
        ForceSet mForceSet= model.get_ForceSet();
        ArrayStr muscleGroups = new ArrayStr();
        mForceSet.getGroupNames(muscleGroups);
        for (int i=0; i< muscleGroups.getSize(); i++){
            setOfForces.addGroup(muscleGroups.get(i));
            ObjectGroup group = mForceSet.getGroup(i);
            ArrayConstObjPtr groupMembers = group.getMembers();
            for (int idx =0; idx < groupMembers.getSize(); idx++){
                setOfForces.addObjectToGroup(group.getName(), groupMembers.get(idx).getName());
            }
        }
    }

}
