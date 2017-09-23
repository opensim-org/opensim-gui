/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimDialog.java                                                *
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
 * OpenSimDialog
 * Author(s): Ayman Habib
 */
package org.opensim.utils;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author Ayman
 */
public class OpenSimDialog extends JDialog{
    
    public static final int OK_OPTION=1;
    public static final int CANCEL_OPTION=0;
    
    private int dialogReturnValue;
    
    /** Creates a new instance of OpenSimDialog */
    public OpenSimDialog(Frame parent) {
        super(parent);
    }

    public int getDialogReturnValue() {
        return dialogReturnValue;
    }

    public void setDialogReturnValue(int dialogReturnValue) {
        this.dialogReturnValue = dialogReturnValue;
    }
    
}
