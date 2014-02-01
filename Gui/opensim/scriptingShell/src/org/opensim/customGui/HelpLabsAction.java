package org.opensim.customGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.customGui.RunLabAction;

public final class HelpLabsAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
    }
    
    public String getName() {
        return NbBundle.getMessage(HelpLabsAction.class, "CTL_HelpLabsAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
   public JMenuItem getMenuPresenter() {
      JMenu labsMenu = new JMenu( getName());
      FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    return (!file.isDirectory()&& file.getName().endsWith(".oscript"));
                }
      };
      File labsDirectory = new File("labs");
      File[] files = labsDirectory.listFiles(fileFilter);
      if (files == null)  return labsMenu;
      
      for (int i=0; i<files.length; i++){
          final File labFile=files[i];
          // List files in labs directory
          JMenuItem labMenuItem = new JMenuItem(new RunLabAction(labFile));
          labsMenu.add(labMenuItem);
      }
      return labsMenu;
    }
    
}
