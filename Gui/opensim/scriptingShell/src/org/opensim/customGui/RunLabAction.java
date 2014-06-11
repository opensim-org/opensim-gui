/*
 * RunLabAction.java
 *
 * Created on July 26, 2010, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

import java.awt.event.ActionEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.AbstractAction;
import org.opensim.tools.serializers.ToolSerializer;

/**
 *
 * @author ayman
 */
public class RunLabAction extends AbstractAction{
    
    File labFile;
    /**
     * Creates a new instance of RunLabAction
     */
    public RunLabAction(File labFile ) {
        this.labFile=labFile;
        this.putValue("Name", labFile.getName());
    }

    public void actionPerformed(ActionEvent e) {
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
                           new FileInputStream(labFile)));
             Lab lab= (Lab)decoder.readObject();
             lab.execute();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

    }
    
}
