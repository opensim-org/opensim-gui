/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.pub;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import javax.swing.TransferHandler;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.view.FileOpenOsimModelAction;
import org.opensim.view.motions.FileLoadMotionAction;

/**
 *
 * @author Ayman-NMBL
 */
public class OpenSimDragNDropHandler extends TransferHandler {

    public boolean canImport(TransferHandler.TransferSupport support) {
        return true;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        Transferable t = support.getTransferable();
        try {
            java.util.List<File> l
                    = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

            for (File f : l) {
               String absolutePath = f.getAbsolutePath();
               if (f.isFile() && f.getPath().toLowerCase().endsWith(".osim")) {
                    handleOsimFile(absolutePath);
                }
                else if (f.isFile() && f.getPath().toLowerCase().endsWith(".mot") ||
                        f.getPath().toLowerCase().endsWith(".sto")) {
                    handleMotionFile(absolutePath);
                }
               else
                    DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message("Unsupported file " + absolutePath+ " will be ignored"));
            }
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void handleOsimFile(String absolutePath) {
        try {
            // Display original model
            ((FileOpenOsimModelAction) FileOpenOsimModelAction.findObject(
                    (Class) Class.forName("org.opensim.view.FileOpenOsimModelAction"), true)).loadModel(absolutePath, true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message("Error opening model file " + absolutePath));
        };
    }
    
    private void handleMotionFile(String absolutePath) {
        try {
            // Display original model
            ((FileLoadMotionAction) FileLoadMotionAction.findObject(
                    (Class) Class.forName("org.opensim.view.motions.FileLoadMotionAction"), true)).loadMotion(absolutePath);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } 
    }
}
