/*
 * FileImportSIMMActionTest.java
 * JUnit based test
 *
 * Created on September 24, 2010, 2:49 PM
 */



import org.opensim.simmsupport.FileImportSIMMAction;
import junit.framework.*;
import java.io.File;
import java.io.IOException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.logger.OpenSimLogger;
import org.opensim.view.base.ExecOpenSimProcess;
import org.opensim.view.FileOpenOsimModelAction;

/**
 *
 * @author Ayman
 */
public class FileImportSIMMActionTest extends TestCase {
    
    public FileImportSIMMActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of performAction method, of class org.opensim.view.actions.FileImportSIMMAction.
     */
    public void testPerformAction() {
        System.out.println("performAction");
        
        FileImportSIMMAction instance = new FileImportSIMMAction();
        
        //instance.importSIMMModel();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class org.opensim.view.actions.FileImportSIMMAction.
     */
    public void testGetName() {
        System.out.println("getName");
        
        FileImportSIMMAction instance = new FileImportSIMMAction();
        
        String expResult = "Import SIMM Model...";
        String result = instance.getName();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getHelpCtx method, of class org.opensim.view.actions.FileImportSIMMAction.
     *
    public void testGetHelpCtx() {
        System.out.println("getHelpCtx");
        
        FileImportSIMMAction instance = new FileImportSIMMAction();
        
        HelpCtx expResult = HelpCtx.DEFAULT_HELP;
        HelpCtx result = instance.getHelpCtx();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
    
}
