/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 *
 * @author Jingjing
 */
public class TestEnvironment {

    private static String modelPath = null;

    public static void initializePath() {
        ProtectionDomain pd = FileOpenOsimModelAction.class.getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        URL url = cs.getLocation();
        File f = new File(url.getFile());
        File targetFile = f.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
        String ap = targetFile.getAbsolutePath() + "\\opensim-models\\Models\\Arm26\\arm26.osim";
        modelPath = ap;

    }

    public static String getModelPath() {
        if (modelPath == null) {
            initializePath();
        }
        return modelPath;
    }
}
