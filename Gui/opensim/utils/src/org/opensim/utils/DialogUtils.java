/*
 *
 * DialogUtils.java
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
* Use of the OpenSim software in source form is permitted provided that the following
* conditions are met:
* 	1. The software is used only for non-commercial research and education. It may not
*     be used in relation to any commercial activity.
* 	2. The software is not distributed or redistributed.  Software distribution is allowed 
*     only through https://simtk.org/home/opensim.
* 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
*      presentations, or documents describing work in which OpenSim or derivatives are used.
* 	4. Credits to developers may not be removed from executables
*     created from modifications of the source.
* 	5. Modifications of source code must retain the above copyright notice, this list of
*     conditions and the following disclaimer. 
* 
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
*  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
*  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
*  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
*  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
*  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.opensim.utils;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author Ayman
 * Utilities to support Dialog creattion. Normally this is not needed since Netbeans provides
 * a nice Dialog constructionmechanism. Problem is that we have "modeless" frames to fix bug 485
 * and so we need to explicitly specify owners/parents of dialogs.
 */
public final class DialogUtils {
    
    /** Creates a new instance of DialogUtils */
    
    public static JFrame createFrameForPanel(final JPanel panel, String title) {
        JFrame f = new JFrame(title);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(panel, BorderLayout.CENTER);
        // potentially add a "Close" button "
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setIconImage(TheApp.getAppImage());
        return f;
    }
    
    public static OpenSimDialog createDialogForPanelWithParent(Frame parent, final JPanel panel, String title) {
        OpenSimDialog d = new OpenSimDialog(parent);
        d.setTitle(title);
        d.getContentPane().setLayout(new BorderLayout());
        d.getContentPane().add(panel, BorderLayout.CENTER);
        // potentially add a "Close" button "
        d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        d.pack();
        d.setLocationRelativeTo(parent);
        return d;
    }
    
    public static void addStandardButtons(final OpenSimDialog dlg) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createRigidArea(new Dimension(50, 50)));
        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(Box.createGlue());
        dlg.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        JButton okButton = new JButton("OK");
        buttonPanel.add(okButton);
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                dlg.setDialogReturnValue(OpenSimDialog.OK_OPTION);
                dlg.dispose();
            }});
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    dlg.setDialogReturnValue(OpenSimDialog.CANCEL_OPTION);
                    dlg.dispose();
                }});
                buttonPanel.add(cancelButton);
                // Window listener is added here since closing the dialog with [x] should
                // behave the same as close/cancel
        dlg.addWindowListener(new WindowAdapter(){
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                dlg.setDialogReturnValue(OpenSimDialog.CANCEL_OPTION);
            }

            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dlg.setDialogReturnValue(OpenSimDialog.CANCEL_OPTION);
            }
        });
        //dlg.doLayout();
        dlg.pack();

    }

    public static void addButtons(JFrame frame, JButton[] buttonsList, ActionListener listener) {
        // Create a control panel at the bottom of the passed in window for control buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createRigidArea(new Dimension(50, 50)));
        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(Box.createGlue());
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        for (int i=0; i<buttonsList.length; i++){
            buttonPanel.add(buttonsList[i]);
            buttonsList[i].addActionListener(listener);
            // Should also set return value for ok, cancel
        }
       //frame.doLayout();
       frame.pack();
    }

    public static void addCloseButton(Dialog dDialog, ActionListener actionListener) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createRigidArea(new Dimension(50, 50)));
        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(Box.createGlue());
        dDialog.add(buttonPanel, BorderLayout.SOUTH);
        JButton closeButton = new JButton("Close");
        buttonPanel.add(closeButton);
        closeButton.addActionListener(actionListener);
        dDialog.doLayout();
        dDialog.pack();
    }

}
