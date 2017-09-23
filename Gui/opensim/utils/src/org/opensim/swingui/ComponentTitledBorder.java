/* -------------------------------------------------------------------------- *
 * OpenSim: ComponentTitledBorder.java                                        *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Eran Guendelman                                    *
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
