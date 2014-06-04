package org.opensim.customGui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Parameters component.
 */
public class ParametersAction extends AbstractAction {
    
    public ParametersAction() {
        super(NbBundle.getMessage(ParametersAction.class, "CTL_ParametersAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(ParametersTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = ParametersTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
