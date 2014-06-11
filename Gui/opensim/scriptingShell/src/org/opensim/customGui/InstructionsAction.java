package org.opensim.customGui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Instructions component.
 */
public class InstructionsAction extends AbstractAction {
    
    public InstructionsAction() {
        super(NbBundle.getMessage(InstructionsAction.class, "CTL_InstructionsAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(InstructionsTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = InstructionsTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
