/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Portions Copyrighted 2013 Ayman Habib
 */
package org.opensim.topologyEditor;

import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.*;
import org.openide.util.Utilities;

import java.awt.*;

/**
 * @author Ayman Habib following example in VisualAPI
 */
public class OpenSimObjectWidget extends Widget {

    private static final Image IMAGE_CLASS = Utilities.loadImage ("test/resources/class.gif"); // NOI18N
    private static final Image IMAGE_MEMBER = Utilities.loadImage ("test/resources/variablePublic.gif"); // NOI18N
    private static final Image IMAGE_OPERATION = Utilities.loadImage ("test/resources/methodPublic.gif"); // NOI18N

    private LabelWidget className;
    
    private boolean highLighted;
    /*
    private Widget members;
    private Widget operations;
     * 
     */
    private static final Border BORDER_4 = BorderFactory.createEmptyBorder (4);
    
    public OpenSimObjectWidget (Scene scene) {
        super (scene);
        setLayout (LayoutFactory.createVerticalFlowLayout ());
        setBorder (BorderFactory.createLineBorder ());
        setOpaque (true);
        setCheckClipping (true);
        
        Widget classWidget = new Widget (scene);
        classWidget.setLayout (LayoutFactory.createHorizontalFlowLayout ());
        classWidget.setBorder (BORDER_4);

        ImageWidget classImage = new ImageWidget (scene);
        classImage.setImage (IMAGE_CLASS);
        classWidget.addChild (classImage);

        className = new LabelWidget (scene);
        className.setFont (scene.getDefaultFont ().deriveFont (Font.BOLD));
        classWidget.addChild (className);
        addChild (classWidget);

    }

    public String getClassName () {
        return className.getLabel ();
    }

    public void setClassName (String className) {
        this.className.setLabel (className);
    }

    public Widget createMember (String member) {
        Scene scene = getScene ();
        Widget widget = new Widget (scene);
        widget.setLayout (LayoutFactory.createHorizontalFlowLayout ());

        ImageWidget imageWidget = new ImageWidget (scene);
        imageWidget.setImage (IMAGE_MEMBER);
        widget.addChild (imageWidget);

        LabelWidget labelWidget = new LabelWidget (scene);
        labelWidget.setLabel (member);
        widget.addChild (labelWidget);

         return widget;
    }

    public Widget createOperation (String operation) {
        Scene scene = getScene ();
        Widget widget = new Widget (scene);
        widget.setLayout (LayoutFactory.createHorizontalFlowLayout ());

        ImageWidget imageWidget = new ImageWidget (scene);
        imageWidget.setImage (IMAGE_OPERATION);
        widget.addChild (imageWidget);

        LabelWidget labelWidget = new LabelWidget (scene);
        labelWidget.setLabel (operation);
        widget.addChild (labelWidget);

        return widget;
    }

    /**
     * @return the highLighted
     */
    public boolean isHighLighted() {
        return highLighted;
    }

    /**
     * @param highLighted the highLighted to set
     */
    public void setHighLighted(boolean highLighted) {
        if (highLighted)
            setForeground (Color.RED);
        else
            setForeground (Color.BLACK);
        this.highLighted = highLighted;
    }

}
