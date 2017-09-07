/*
 * MarkersSaveToFileAction.java
 *
 * Created on June 9, 2008, 3:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.opensim.view.markerEditor;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.MarkersNode;

/**
 *
 * @author Ayman Habib
 */
public class MarkersSaveToFileAction extends AbstractAction {

   /** Creates a new instance of NewMarkerAction */
   public MarkersSaveToFileAction() {
      super(NbBundle.getMessage(MarkersSaveToFileAction.class, "CTL_ExportMarkersAction"));
   }

   public void actionPerformed(ActionEvent evt) {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // One MarkersNode must have been selected otherwise bail
        if (selected.length != 1 || !(selected[0] instanceof MarkersNode)) {
            return;
        }
        MarkersNode markersNode = (MarkersNode) selected[0];
        Model model = markersNode.getModelForNode();
        MarkerSet markerset = model.getMarkerSet();
        // Browse for file for MarkerSet
        String fileName = FileUtils.getInstance().browseForFilenameToSave(".xml", "XML file containing markers", true, "markers.xml");
        if (fileName==null) return;
         // This block of code should stay in sync with 
        // TestEditMarkers.java in opensim-core
        markerset.print(fileName);
    }
}
