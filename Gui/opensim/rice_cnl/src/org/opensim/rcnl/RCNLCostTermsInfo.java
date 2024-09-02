/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.opensim.rcnl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import org.openide.util.Exceptions;
import org.opensim.modeling.ArrayBool;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;

/**
 *
 * @author ayman
 */
public class RCNLCostTermsInfo {
    static String[][] trackingCostTerms= {
        {"generalized_coordinate_tracking", "generalized_speed_tracking", "marker_position_tracking",
            "inverse_dynamics_load_tracking", "inverse_dynamics_load_minimization", "inverse_dynamics_slope_tracking",
            "kinetic_inconsistency_minimization", "joint_acceleration_minimization", "external_force_tracking",
            "external_moment_tracking", "muscle_activation_tracking", "controller_slope_minimization"},
        {"coordinate", "coordinate", "marker",
            "load", "load" , "load",
            "load" , "coordinate" , "force",
            "moment", "muscle", "controller"}
    };
    static String[][] verificationCostTerms= {
        {"generalized_coordinate_tracking", "generalized_speed_tracking",
            "marker_position_tracking", "joint_acceleration_minimization", 
            "controller_tracking", "controller_slope_minimization", "controller_frequency_minimization"},
        {"coordinate", "coordinate", "marker", "coordinate", "controller", "controller", "controller"}
    };
    static String[][] designOptimizationCostTerms= {
        {"generalized_coordinate_tracking", "generalized_speed_tracking", "marker_position_tracking",
            "inverse_dynamics_load_tracking", "inverse_dynamics_slope_tracking", 
            "external_force_tracking", "external_moment_tracking",
            "muscle_activation_tracking", "controller_tracking", "controller_slope_minimization",
            "controller_frequency_minimization", "controller_shape_tracking", "joint_acceleration_minimization",
            "joint_power_minimization", "joint_energy_generation_goal", "joint_energy_absorption_goal", 
            "muscle_activation_minimization", "external_torque_control_minimization", "angular_momentum_minimization", 
            "synergy_vector_tracking", "belt_speed_goal", "relative_walking_speed_goal", 
            "relative_metabolic_cost_per_time", "relative_metabolic_cost_per_distance", "propulsive_impulse_goal", 
            "braking_impulse_goal"},
        {"coordinate", "coordinate", "marker", 
            "load" , "load" , "force", "moment", 
            "muscle", "controller", "controller", 
            "controller", "controller", "coordinate", 
            "coordinate", "coordinate", "coordinate", 
            "muscle", "coordinate",
            "none", "none", "none", "none", "none", "none", "none", "none"},
        {"N","N","N","N","N","N",
            "N","N","N","N","N","N",
            "N","N","N","N","N","N",
            "Y","Y","Y","Y","Y","Y","Y","Y" 
        } 
    };
    private static String[] coordinateList = null;
    private static String[] muscleList = null;
    private static String[] markerList = null;
    private static String[] loadList = null;
    private static String[] forces_List = null;
    private static String[] moments_List = null;
    private static String[] controller_List = null;
    private static String[] synergy_Groups = null;
    private static Model currentModel=null;
    private static String trackedDir = "";
    private static String initalGuessDir = "";
    private static String osimxFile = "";
    

    // Create lists of the proper types to be used in Cost/Constraint create/edit
    public static String[] getAvailableNamesForComponentType(String componentType, Model model, String testTrackedDir, String testInitalGuessDir, String testOsimxFile) {
        //Will evaluate these lazily for componentTypes not in model
        if (currentModel != model){
            // fresh or changed model
            currentModel = model;
            coordinateList = null;
            muscleList = null;
            markerList = null;
            loadList = null;
        }

        ArrayStr componentNames = new ArrayStr();
        String[] availableQuantities;

        switch(componentType) {
            case "coordinate":
                if (coordinateList == null){
                    model.getCoordinateSet().getNames(componentNames);
                    availableQuantities = new String[componentNames.getSize()];
                    componentNames.toVector().copyInto(availableQuantities);
                    coordinateList= availableQuantities;
                }
                return coordinateList;
            case "marker":
                if (markerList == null){
                    model.getMarkerSet().getNames(componentNames);
                    availableQuantities = new String[componentNames.getSize()];
                    componentNames.toVector().copyInto(availableQuantities);
                    markerList= availableQuantities;
                }
                return markerList;
            case "muscle":
                if (muscleList == null){
                    model.getMuscles().getNames(componentNames);
                    availableQuantities = new String[componentNames.getSize()];
                    componentNames.toVector().copyInto(availableQuantities);
                    muscleList= availableQuantities;
                }
                return muscleList;
            case "load":
                if (loadList == null){
                    CoordinateSet coordinates = model.getCoordinateSet();
                    Vector<String> loadNames = new Vector<String>(4);
                    for (int i=0; i< coordinates.getSize(); i++){
                        Coordinate co = coordinates.get(i);
                        String loadName = co.getName();
                        String suffix= "";
                        if (co.getMotionType() == Coordinate.MotionType.Rotational) 
                            suffix = "_moment";
                        else
                            suffix = "_force";
                        loadNames.add(loadName.concat(suffix));
                    }
                    loadList = new String[loadNames.size()];
                    loadNames.copyInto(loadList);
                }
                return loadList;
            case "controller":
                if (trackedDir.equalsIgnoreCase(testTrackedDir) && initalGuessDir.equalsIgnoreCase(testInitalGuessDir) ){
                    // No need to search for synergyCommands.sto or torqueControls.sto to populate controller_List or synergy_Groups
                    if (controller_List == null){
                        populateControllersList();
                    }
                    return controller_List;
                }
                else { 
                    trackedDir = testTrackedDir;
                    initalGuessDir = testInitalGuessDir;
                    populateControllersList();
                }
                return controller_List;

        }
        return new String[]{};
    }

    private static void populateControllersList() {
        String[] availableQuantities;
        boolean found = false;
        String[] candidateFolders = new String[]{trackedDir, initalGuessDir};
        for (String folder : candidateFolders){
            File dir = new File(folder);
            File [] files = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("_synergyCommands.sto");
                }
            });
            
            for (File commandFile : files) {
                try {
                    Storage commandSto = new Storage(commandFile.getAbsolutePath(), true);
                    // Successful find, get labels to populate controller_List, then break
                    found = true;
                    ArrayStr controllerNames = commandSto.getColumnLabels();
                    controllerNames.remove(0); // time needs to be removed
                    availableQuantities = new String[controllerNames.getSize()];
                    controllerNames.toVector().copyInto(availableQuantities);
                    controller_List= availableQuantities;
                    break;
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (found)
                break;
        }
    }
    public static String[] getCostTermTypes(TreatmentOptimizationToolModel.Mode mode) {
        switch(mode){
            case TrackingOptimization:
                return trackingCostTerms[0];
            case VerificationOptimization:
                return verificationCostTerms[0];
            case DesignOptimization:
                return designOptimizationCostTerms[0];
        }
        return new String[]{};
    }
   public static String[] getCostTermQuantityTypes(TreatmentOptimizationToolModel.Mode mode) { 
        switch(mode){
            case TrackingOptimization:
                return trackingCostTerms[1];
            case VerificationOptimization:
                return verificationCostTerms[1];
            case DesignOptimization:
                return designOptimizationCostTerms[1];
        }
        return new String[]{};
   }   

    static String[] getCostTermErrorCenter(TreatmentOptimizationToolModel.Mode mode) {
        return designOptimizationCostTerms[2];
    }
}
