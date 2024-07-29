/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.CallableSystemAction;

@ActionID(
        category = "Edit",
        id = "org.opensim.rcnl.TreatmentOptimizationAction"
)
@ActionRegistration(
        displayName = "#CTL_TreatmentOptimizationAction"
)
@ActionReference(path = "Menu/Tools", position = 1600)
//@Messages("CTL_ModelPersonalizationAction=Model Personalization Tool")
public final class TreatmentOptimizationAction extends CallableSystemAction {

    public void performAction() {
        // TODO implement action body
    }
    
    public JMenuItem getMenuPresenter() {
        JMenu menu = new MPMenu(getName());
        return menu;
    }
    /** {@inheritDoc} */
    public String getName() {
        return NbBundle.getMessage(TreatmentOptimizationAction.class, "CTL_TreatmentOptimizationAction");
    }
    /** {@inheritDoc} */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
  
 
 
   class MPMenu extends JMenu implements DynamicMenuContent {
 
        public MPMenu(String s) {
            super(s);

            updateMenu();
        }
 
        public JComponent[] getMenuPresenters() {
            return new JComponent[] {this};
        }
 
        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }
 
        private void updateMenu() {
            javax.swing.JMenuItem tOptMenuItem = new JMenuItem("Tracking Optimization...");
            tOptMenuItem.addActionListener(new JointPersonalizationAction());
            this.add(tOptMenuItem);
            javax.swing.JMenuItem vOptMenuItem = new JMenuItem("Verification Optimization...");
            vOptMenuItem.addActionListener(new JointPersonalizationAction());
            this.add(vOptMenuItem);
            javax.swing.JMenuItem desOptMenuItem = new JMenuItem("Design Optimization...");
            desOptMenuItem.addActionListener(new JointPersonalizationAction());
            this.add(desOptMenuItem);
        }
    }
}
