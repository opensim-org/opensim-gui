/*
 * FileExportSIMMModelActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:49 PM
 */



import java.io.File;
import java.io.IOException;
import org.openide.util.Exceptions;
import org.opensim.simmsupport.FileExportSIMMModelAction;
import junit.framework.*;
import org.openide.util.HelpCtx;
import org.opensim.modeling.Model;
import org.opensim.modeling.SimmFileWriter;
import org.opensim.view.FileOpenOsimModelAction;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class FileExportSIMMModelActionTest extends TestCase {
    
    public FileExportSIMMModelActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        Model aModel=null;
        try {
            aModel = new Model(TestEnvironment.getModelPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.out.println("model path="+TestEnvironment.getModelPath());
        //aModel= new Model(modelPath);
        FileOpenOsimModelAction instanceOpen = new FileOpenOsimModelAction();
        try {
            boolean result = instanceOpen.loadModel(aModel, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        SimmFileWriter modelWriter=new SimmFileWriter(OpenSimDB.getInstance().getCurrentModel());
        File f = new File(TestEnvironment.getModelPath());
        modelWriter.writeJointFile(f.getParent()+"//jntfileName.jnt");        
        modelWriter.writeMuscleFile(f.getParent()+"//musclefileName.msl");        
        OpenSimDB.getInstance().removeModel(aModel);
        // Compare output to std
    }

    /**
     * Test of getName method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        String expResult = "Export SIMM Model...";
        String result = instance.getName();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        HelpCtx expResult = HelpCtx.DEFAULT_HELP;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of isEnabled method, of class org.opensim.view.FileExportSIMMModelAction.
     */
    public void testIsEnabled() {
        System.out.println("isEnabled");
        
        FileExportSIMMModelAction instance = new FileExportSIMMModelAction();
        
        boolean expResult = false;
        boolean result = instance.isEnabled();
        assertEquals(expResult, result);
        
        
    }
    
}
