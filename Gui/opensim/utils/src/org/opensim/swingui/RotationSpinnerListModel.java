/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.swingui;

import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Ayman
 */
public class RotationSpinnerListModel extends SpinnerNumberModel {

    private double lastValue;

    public RotationSpinnerListModel(double initial, double min, double max, double step) {
        super(initial, min, max, step);
        setLastValue(initial);
    }

    public double getLastValue() {
        return lastValue;
    }

    public void setLastValue(double lastValue) {
        this.lastValue = lastValue;
    }

} //RotationSpinnerListModel
