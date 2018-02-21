/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.pub;

import java.util.EventObject;

/**
 *
 * @author Ayman
 */
public class ModeChangeEvent extends EventObject{

    /**
     * @return the newMode
     */
    public OpenSimDB.Mode getNewMode() {
        return newMode;
    }
     private OpenSimDB.Mode newMode;
     
     public ModeChangeEvent(OpenSimDB.Mode mode) {
         super(OpenSimDB.getInstance());
         newMode = mode;
    }
}
