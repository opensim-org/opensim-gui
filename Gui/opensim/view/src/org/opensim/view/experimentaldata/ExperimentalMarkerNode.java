/*
 * ExperimentalMarkerNode.java
 *
 * Created on February 23, 2009, 12:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.experimentaldata;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.view.ModelDisplayMenuAction;
import org.opensim.view.ModelRenameAction;
import org.opensim.view.nodes.*;

/**
 *
 * @author ayman
 */
public class ExperimentalMarkerNode extends ExperimentalDataNode {
    String markerName=null;
    /** Creates a new instance of ExperimentalMarkerNode */
    public ExperimentalMarkerNode(ExperimentalDataObject dataObject, AnnotatedMotion dMotion) {
        markerName=dataObject.getName();
        this.dMotion=dMotion;
        setDataObject(dataObject);
        setName(markerName);
        setDisplayName(markerName);
        setChildren(Children.LEAF);
        setShortDescription(bundle.getString("HINT_ExperimentalMarkerNode"));
        
    }
    
    public String getHtmlDisplayName() {
        return markerName;
    }

}
