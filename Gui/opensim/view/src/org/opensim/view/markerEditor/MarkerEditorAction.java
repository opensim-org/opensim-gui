package org.opensim.view.markerEditor;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows MarkerEditor component.
 */
public class MarkerEditorAction extends AbstractAction {
   
   public MarkerEditorAction() {
      super(NbBundle.getMessage(MarkerEditorAction.class, "CTL_MarkerEditorAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(MarkerEditorTopComponent.ICON_PATH, true)));
   }
   
   public void actionPerformed(ActionEvent evt) {
      TopComponent win = MarkerEditorTopComponent.findInstance();
      win.open();
      win.requestActive();
   }
   
}
