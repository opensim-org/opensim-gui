/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.opensim.rcnl;

/**
 *
 * @author ayman
 */
public class RCNLConstraintTermsInfo {
    static String[][] trackingConstraintTerms= {
        {"root_segment_residual_load", "torque_model_moment_consistency", "kinetic_consistency",
            "state_position_periodicity", "state_velocity_periodicity", "root_segment_residual_load_periodicity",
            "external_force_periodicity", "external_moment_periodicity", "synergy_weight_sum",
            "synergy_weight_magnitude"},
        {"load", "load", "load",
            "coordinate", "coordinate" , "load",
            "force", "moment", "none", "none"}
    };
    static String[][] verificationConstraintTerms= {
        {"root_segment_residual_load", "torque_model_moment_consistency", "kinetic_consistency",
            "state_position_periodicity", "state_velocity_periodicity", "root_segment_residual_load_periodicity",
            "external_force_periodicity", "external_moment_periodicity"},
        {"load", "load", "load",
            "coordinate", "coordinate" , "load",
            "force", "moment"}
    };
    static String[][] designOptimizationConstraintTerms= {
        {"root_segment_residual_load", "torque_model_moment_consistency", "kinetic_consistency",
            "state_position_periodicity", "state_velocity_periodicity", "root_segment_residual_load_periodicity",
            "external_force_periodicity", "external_moment_periodicity", "limit_normalized_fiber_length", 
            "initial_state_position_tracking", "final_state_position", "final_state_velocity",
            "final_point_position", "final_point_velocity", "muscle_model_moment_consistency",
            "limit_muscle_activation", "external_control_muscle_moment_consistency", "synergy_weight_sum",
            "synergy_weight_magnitude"
        },
        {"load", "load", "load",
            "coordinate", "coordinate" , "load",
            "force", "moment", "muscle",
           "coordinate", "coordinate" , "coordinate",
           "coordinate", "coordinate" , "load",
           "muscle", "coordinate", "none", "none"
        } 
    };
    
    public static String[] getConstraintTermTypes(TreatmentOptimizationToolModel.Mode mode) {
        switch(mode){
            case TrackingOptimization:
                return trackingConstraintTerms[0];
            case VerificationOptimization:
                return verificationConstraintTerms[0];
            case DesignOptimization:
                return designOptimizationConstraintTerms[0];
        }
        return new String[]{};
    }
   public static String[] getConstraintTermQuantityTypes(TreatmentOptimizationToolModel.Mode mode) { 
        switch(mode){
            case TrackingOptimization:
                return trackingConstraintTerms[1];
            case VerificationOptimization:
                return verificationConstraintTerms[1];
            case DesignOptimization:
                return designOptimizationConstraintTerms[1];
        }
        return new String[]{};
   }       
}
