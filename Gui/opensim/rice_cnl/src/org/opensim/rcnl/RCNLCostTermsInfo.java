/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.opensim.rcnl;

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
        {"generalized_coordinate_tracking", "coordinate"},
        {"generalized_speed_tracking", "coordinate"},
        {"marker_position_tracking", "marker"},
        {"inverse_dynamics_load_tracking", "load" },
        {"inverse_dynamics_slope_tracking", "load" },
        {"kinetic_inconsistency_minimization", "load" },
        {"joint_acceleration_minimization", "coordinate" },
        {"external_force_tracking", "force" },
        {"external_moment_tracking", "moment" },
        {"muscle_activation_tracking", "muscle" },
        {"controller_tracking", "controller" },
        {"controller_slope_minimization", "controller"},
        {"controller_frequency_minimization", "controller"},
        {"controller_shape_tracking", "controller"},
        {"joint_acceleration_minimization", "coordinate"},
        {"joint_power_minimization", "coordinate"},
        {"joint_energy_generation_goal", "coordinate"},
        {"joint_energy_absorption_goal", "coordinate"},
        {"muscle_activation_minimization", "muscle"},
        {"external_torque_control_minimization", "coordinate"}
    };
    static String[][] designOptimizationCostTermsWithCenter= {
        {"generalized_coordinate_tracking", "coordinate"},
        {"generalized_speed_tracking", "coordinate"},
        {"marker_position_tracking", "marker"},
        {"inverse_dynamics_load_tracking", "load" },
        {"inverse_dynamics_slope_tracking", "load" },
        {"kinetic_inconsistency_minimization", "load" },
        {"joint_acceleration_minimization", "coordinate" },
        {"external_force_tracking", "force" },
        {"external_moment_tracking", "moment" },
        {"muscle_activation_tracking", "muscle" },
        {"controller_tracking", "controller" },
        {"controller_slope_minimization", "controller"},
        {"controller_frequency_minimization", "controller"},
        {"controller_shape_tracking", "controller"},
        {"joint_acceleration_minimization", "coordinate"},
        {"joint_power_minimization", "coordinate"},
        {"joint_energy_generation_goal", "coordinate"},
        {"joint_energy_absorption_goal", "coordinate"},
        {"muscle_activation_minimization", "muscle"},
        {"external_torque_control_minimization", "coordinate"}
    };
    String[] getCostTermTypes(TreatmentOptimizationToolModel.Mode mode) {
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
   String[] getCostTermQuantityTypes(TreatmentOptimizationToolModel.Mode mode) { 
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
}
