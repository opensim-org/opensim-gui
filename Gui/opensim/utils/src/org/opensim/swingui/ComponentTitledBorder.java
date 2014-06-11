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
package org.opensim.swingui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/*
 * ComponentTitledBorder taken from
 * http://www.javalobby.org/java/forums/t33048.html
 * Copyright (C) 2005  Santhosh Kumar T 
 *
 * Modified by Eran Guendelman
 *
 * <p/> 
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version. 
 * <p/> 
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * Lesser General Public License for more details. 
 */ 
 
public class ComponentTitledBorder implements Border, MouseListener, SwingConstants, ChangeListener { 
    int offset = 5; 
 
    Component comp; 
    JComponent container; 
    Rectangle rect; 
    Border border; 
 
    public ComponentTitledBorder(Component comp, JComponent container, Border border){ 
        this.comp = comp; 
        this.container = container; 
        this.border = border; 
        container.addMouseListener(this); 
        if(comp instanceof AbstractButton) ((AbstractButton)comp).addChangeListener(this);
    } 

    // Added this so that if you programatically change the state of the button component in this border, the border repaints (forcing the button to repaint)
    // -- Eran
    public void stateChanged(ChangeEvent evt) {
       if(evt.getSource()==comp) container.repaint();
    }
 
    public boolean isBorderOpaque(){ 
        return true; 
    } 
 
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){ 
        Insets borderInsets = border.getBorderInsets(c); 
        Insets insets = getBorderInsets(c); 
        int temp = (insets.top-borderInsets.top)/2; 
        border.paintBorder(c, g, x, y+temp, width, height-temp); 
        Dimension size = comp.getPreferredSize(); 
        rect = new Rectangle(offset, 0, size.width, size.height); 
        SwingUtilities.paintComponent(g, comp, (Container)c, rect); 
    } 
 
    public Insets getBorderInsets(Component c){ 
        Dimension size = comp.getPreferredSize(); 
        Insets insets = border.getBorderInsets(c); 
        insets.top = Math.max(insets.top, size.height); 
        return insets; 
    } 
 
    private void dispatchEvent(MouseEvent me){ 
        if(rect!=null && rect.contains(me.getX(), me.getY())){ 
            Point pt = me.getPoint(); 
            pt.translate(-offset, 0); 
            comp.setBounds(rect); 
            comp.dispatchEvent(new MouseEvent(comp, me.getID() 
                    , me.getWhen(), me.getModifiers() 
                    , pt.x, pt.y, me.getClickCount() 
                    , me.isPopupTrigger(), me.getButton())); 
            if(!comp.isValid()) 
                container.repaint(); 
        } 
    } 
 
    public void mouseClicked(MouseEvent me){ 
        dispatchEvent(me); 
    } 
 
    public void mouseEntered(MouseEvent me){ 
        dispatchEvent(me); 
    } 
 
    public void mouseExited(MouseEvent me){ 
        dispatchEvent(me); 
    } 
 
    public void mousePressed(MouseEvent me){ 
        dispatchEvent(me); 
    } 
 
    public void mouseReleased(MouseEvent me){ 
        dispatchEvent(me); 
    } 
}
