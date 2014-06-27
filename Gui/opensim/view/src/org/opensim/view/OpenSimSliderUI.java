/*
 * Copyright (c)  2005-2008, Stanford University
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * OpenSimSliderUI.java
 *
 * Created on August 1, 2007, 12:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import com.sun.java.swing.plaf.windows.WindowsSliderUI;
import java.awt.*;

import javax.swing.plaf.*;
import javax.swing.*;
import org.openide.util.Utilities;

/**
 *
 * @author Jeff Reinbolt
 */
public class OpenSimSliderUI extends WindowsSliderUI 
{
    // use static class members to avoid reloading/creation of image for every slider in the system. -Ayman
    //
    static Image sliderKnobArrowImg = Utilities.loadImage("org/opensim/view/sliderKnobArrow.png");
    static Image sliderKnobImg=Utilities.loadImage("org/opensim/view/sliderKnob.png");
    static Image sliderKnobArrow_disabledImg=Utilities.loadImage("org/opensim/view/sliderKnobArrow_disabled.png");
    static Image sliderKnob_disabledImg=Utilities.loadImage("org/opensim/view/sliderKnob_disabled.png");

    public OpenSimSliderUI(JSlider b){
	super(b);
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
