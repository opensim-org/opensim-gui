/**
 * MySwing: Advanced Swing Utilites
 * Copyright 2005 Santhosh Kumar T
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file was downloaded from https://github.com/santhosh-tekuri/MyBlog
 * on 2017 September 28. At this time, the README at this link said that the
 * code may be used under the LGPL or Apache License 2.0, and we choose the
 * Apache License 2.0.
 *
 * Christopher Dembia carried over previous modifications made by Eran
 * Guendelman to a previous version of this file.
 */

package org.opensim.swingui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
// [2017 Sep 28] These next two lines were carried over by Christopher Dembia
// from changes that Eran Guendelman made to a previous version of this file.
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ComponentTitledBorder implements Border, MouseListener, MouseMotionListener, SwingConstants, ChangeListener{
    int offset = 5;

    Component comp;
    JComponent container;
    Rectangle rect;
    Border border;

    public ComponentTitledBorder(Component comp, final JComponent container, Border border){
        this.comp = comp;
        this.container = container;
        this.border = border;
        container.addMouseListener(this);
        // [2017 Sep 28] These lines were carried over by Christopher Dembia
        // from changes that Eran Guendelman made to a previous version of this
        // file.
        if(comp instanceof AbstractButton) ((AbstractButton)comp).addChangeListener(this);
    }

    // [2017 Sep 28] These lines were carried over by Christopher Dembia
    // from changes that Eran Guendelman made to a previous version of this
    // file. Eran Guendelman's original comment:
    //   Added this so that if you programatically change the state of the
    //   button component in this border, the border repaints (forcing the
    //   button to repaint)
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

    public boolean isInside(MouseEvent me){
        return rect!=null && rect.contains(me.getX(), me.getY());
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

    public void mouseDragged(MouseEvent me){
        dispatchEvent(me);
    }

    public void mouseMoved(MouseEvent me){
        // [2017 Sep 28] These lines were commented out by Christopher Dembia.
        // if(rect!=null && rect.contains(me.getX(), me.getY()))
        //     System.out.println("");
        dispatchEvent(me);
    }
}
