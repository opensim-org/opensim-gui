/* -------------------------------------------------------------------------- *
 * OpenSim: OsimStoOpenSupport.java                                           *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.dataObjects;

import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.windows.CloneableTopComponent;
import org.opensim.utils.FileUtils;
import org.opensim.view.motions.FileLoadMotionMenuAction;

/**
 *
 * @author ayman
 */
class OsimStoOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    public OsimStoOpenSupport(OsimModelDataObject.Entry entry) {
        super(entry);
    }

    @Override
    public void open() {
        try {
            OsimMotDataObject dobj = (OsimMotDataObject) entry.getDataObject();
            FileObject fobj = dobj.getPrimaryFile();
            // make sure default directory for future GUI browsing is set to the folder containing model
            String parentPath = fobj.getParent().getPath();
            FileUtils.getInstance().setWorkingDirectoryPreference(parentPath);

            ((FileLoadMotionMenuAction) FileLoadMotionMenuAction.findObject(
                            (Class<FileLoadMotionMenuAction>)Class.forName("org.opensim.view.motions.FileLoadMotionMenuAction"))).loadMotion(fobj.getPath());
            return;
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
 
}