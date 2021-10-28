/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.motions;

/**
 *
 * @author Ayman-NMBL
 */
public class SensorLayoutOptions {

    /**
     * @return the layout
     */
    public Layout getLayout() {
        return layout;
    }

    /**
     * @param layout the layout to set
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    void setSensorLayout(int index) {
        layout = Layout.valueOf(layoutNames[index]);
    }

    public enum Layout{Origin, LineX, LineY, LineZ, CircleYZ, UseCurrentModelPosition, AttachCurrentModel};
    private String[] layoutNames = {"Origin", "LineX", "LineY", "LineZ", "CircleYZ", "UseCurrentModelPosition", "AttachCurrentModel"};
    private Layout layout = Layout.Origin;
    /**
     * @return the rotations
     */
    public double[] getRotations() {
        return rotations;
    }

    /**
     * @param rotations the rotations to set
     */
    public void setRotations(double[] rotations) {
        this.rotations = rotations;
    }

    private double[] rotations = new double[]{0.0, 0.0, 0.0};
    
}
