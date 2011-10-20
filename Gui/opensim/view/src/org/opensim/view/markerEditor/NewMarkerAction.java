/*
 * NewMarkerAction.java
 *
 * Created on June 9, 2008, 3:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.markerEditor;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author Peter Loan
 */
public class NewMarkerAction extends AbstractAction {

   /** Creates a new instance of NewMarkerAction */
   public NewMarkerAction() {
      super(NbBundle.getMessage(NewMarkerAction.class, "CTL_NewMarkerAction"));
   }

   public void actionPerformed(ActionEvent evt) {
       MarkerEditorTopComponent win = MarkerEditorTopComponent.findInstance();
       win.addMarker();
   }

}
