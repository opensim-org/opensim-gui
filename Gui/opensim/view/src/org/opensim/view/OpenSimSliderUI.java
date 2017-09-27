/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimSliderUI.java                                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Christopher Dembia, Jeff Reinbolt                  *
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
 * OpenSimSliderUI.java
 *
 * Created on August 1, 2007, 12:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

import javax.swing.plaf.*;
import javax.swing.*;
import org.openide.util.Utilities;

/**
 *
 * @author Jeff Reinbolt
 */
public class OpenSimSliderUI extends BasicSliderUI 
{
    // use static class members to avoid reloading/creation of image for every slider in the system. -Ayman
    //
    static Image sliderKnobArrowImg = Utilities.loadImage("org/opensim/view/sliderKnobArrow.png");
    static Image sliderKnobImg=Utilities.loadImage("org/opensim/view/sliderKnob.png");
    static Image sliderKnobArrow_disabledImg=Utilities.loadImage("org/opensim/view/sliderKnobArrow_disabled.png");
    static Image sliderKnob_disabledImg=Utilities.loadImage("org/opensim/view/sliderKnob_disabled.png");

    public OpenSimSliderUI(JSlider b){
	super(b);
        // For macOS: the background changes color based on focus, and if
        // the slider is opaque, then the slider's color doesn't match the
        // background.
        b.setOpaque(false);
    }

    public static ComponentUI createUI(JComponent b) {
        return new OpenSimSliderUI((JSlider)b);
    }
    
    protected Dimension getThumbSize() {
        Dimension size = new Dimension();

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
	    size.width = 22;
	    size.height = 12;
	}
	else {
	    size.width = 12;
	    size.height = 22;
	}

	return size;
    }
        
//    public void paintTrack(Graphics g)  {        
//
//        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
//            int cy, cw, ch;
//
//            Rectangle trackBounds = trackRect;
//            cy = (trackBounds.height / 2) - 3;
//            cw = trackBounds.width;
//            ch = 5;
//
//            g.translate(trackBounds.x, trackBounds.y + cy);
//
//            g.setColor(getShadowColor());
//            g.drawLine(0, 0, cw-1, 0);
//            g.drawLine(0, 1, 0, ch);
//            g.setColor(getHighlightColor());
//            g.drawLine(1, ch, cw, ch);
//            g.drawLine(cw, 0, cw, ch-1);
//            if (slider.isEnabled()) {
//                g.setColor(new Color(184, 193, 211));
//            }
//            else {
//                g.setColor(new Color(199, 199, 199));
//            }
//            g.fillRect(1, 1, cw-1, ch-1);
//
//            g.translate(-trackBounds.x, -(trackBounds.y + cy));
//        }
//        else {
//            super.paintTrack(g);
//        }
//            
//    }

    public void paintThumb(Graphics g)  {    
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            Rectangle knobBounds = thumbRect;
            Image sliderKnob;

            if (slider.isEnabled()) {
                if (slider.getPaintTicks()) {
                    sliderKnob = sliderKnobArrowImg;
                }
                else {
                    sliderKnob = sliderKnobImg;
                }
            }
            else {
                if (slider.getPaintTicks()) {
                    sliderKnob = sliderKnobArrow_disabledImg;
                }
                else {
                    sliderKnob = sliderKnob_disabledImg;
                }
            }

            g.drawImage(sliderKnob,knobBounds.x,knobBounds.y,slider);
        }
        else {
            super.paintThumb(g);
        }
    }

}
