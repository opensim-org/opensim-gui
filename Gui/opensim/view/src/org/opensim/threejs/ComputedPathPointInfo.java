/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import org.opensim.modeling.AbstractPathPoint;

/**
 *
 * @author Ayman-NMBL
 */
public class ComputedPathPointInfo {

    AbstractPathPoint pt1;
    AbstractPathPoint pt2;
    double ratio;

    public ComputedPathPointInfo(AbstractPathPoint p1, AbstractPathPoint p2, double ratio) {
        this.pt1 = p1;
        this.pt2 = p2;
        this.ratio = ratio;
    }
}
