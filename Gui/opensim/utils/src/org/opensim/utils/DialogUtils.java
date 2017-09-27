/* -------------------------------------------------------------------------- *
 * OpenSim: DialogUtils.java                                                  *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
/*
 *
 * DialogUtils.java
 * Author(s): Ayman Habib
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
