package org.opensim.view;

import java.io.File;
import java.io.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class FileLoadStandardModelsAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
    }
    
    public String getName() {
        return NbBundle.getMessage(FileLoadStandardModelsAction.class, "CTL_FileLoadBuiltinModelsAction");
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
      JMenu displayMenu = new JMenu("Models");
      FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    return (!file.isDirectory()&& file.getName().endsWith(".osim"));
                }
      };
      String modelsRootDirectory=NbBundle.getMessage(FileLoadStandardModelsAction.class, "CTL_BuiltinModels_Root");
      File rootModelsDirectory= new File(modelsRootDirectory);
      String fullPath = rootModelsDirectory.getAbsolutePath();
      
      System.out.println("Showing models from directory "+fullPath);
      File[] files = rootModelsDirectory.listFiles(fileFilter);
      if (files == null)  return displayMenu;
      
      for (int i=0; i<files.length; i++){
        // List all files in models directory 
         displayMenu.add(new OpenModelFileAction(files[i]));
      }
      return displayMenu;
    }
    
}
