/* -------------------------------------------------------------------------- *
 * OpenSim: FileUtils.java                                                    *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
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
 * FileUtils
 * Author(s): Ayman Habib
 */
package org.opensim.utils;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Ayman
 *
 * Class intended to have only static helper methods for operating on files and directories
 */
public final class FileUtils {
    
    /**
     * getNextAvailableName for a file with prefix baseName in passed in folder
     * use "." for current directory
     *
     */
    static FileUtils instance=null;
    static JFileChooser dlog = new JFileChooser();
    
    // Some predefined filters
    public static FileFilter OpenSimModelFileFilter = getFileFilter(".osim", "OpenSim model");
    public static FileFilter MotionFileFilter = getFileFilter(".mot,.sto", "Motion or storage file");
    public static FileFilter TrcFileFilter = getFileFilter(".trc", "Marker trajectory file");
    public static FileFilter ScriptFileFilter = getFileFilter(".py", "OpenSim script");
    
    /**
     * 
     * @param folder name of folder to search
     * @param baseName starting guess for unused filename located under folder
     * @return name of unused filename that can be created under folder in the form base_X 
     */
    public static String getNextAvailableName(String folder, String baseName) {
        File baseFile = new File(baseName);
        if (baseFile.isAbsolute()){ // user specified a full path. Ignore passed in folder
            folder = baseFile.getParent();
        }
        else
            baseName = folder+baseName;
        // Here folder and baseName are consistent for a file and a parent directory
        File parentDir = new File(folder);
        // Handle extension
        String stripExtension = baseName.substring(0, baseName.lastIndexOf('.'));
        if (stripExtension.contains("_"))
            stripExtension = stripExtension.substring(0, baseName.lastIndexOf('_'));
        String extensionString = baseName.substring(baseName.lastIndexOf('.')); // includes .
        // Cycle thru and check if the file exists, return first available
        boolean found = false;
        int index=1;
        while(!found){
            String suffix = "_"+String.valueOf(index);
            File nextCandidate = new File(stripExtension+suffix+extensionString);
            if (!nextCandidate.exists()){
                return stripExtension+suffix+extensionString;
            }
            index++;
        }
        // unreached
        return null;
    }
   
    /**
     * 
     * @param fileName including extension
     * @return extension part of passed in fileName
     */
   public static String getExtension(String fileName) {
      int lastPathSeparatorIndex = fileName.lastIndexOf(File.separatorChar);
      String nameAndExtension;
      if (lastPathSeparatorIndex!=-1) 
         nameAndExtension = fileName.substring(lastPathSeparatorIndex+1);
      else
          nameAndExtension = fileName;
      int index = nameAndExtension.lastIndexOf('.');
      return (index==-1) ? null : fileName.substring(index+1);
   }

    /**
     * utility method to add suffix to a file name, keeping extension
     */
    public static String addSuffix(String filenameWithExtension, String suffix) {
        if( filenameWithExtension == null ) return null;
        int lastDotLocation  = filenameWithExtension.lastIndexOf(".");
        if (lastDotLocation==-1)
            return null;
        else
            return filenameWithExtension.substring(0, lastDotLocation)
            +suffix
                    +filenameWithExtension.substring(lastDotLocation);
        
    }
    /**
     * Extension should contain the leading . e.g. ".xml"
     * @param path
     * @param extension
     * @return path appended with extension if it doesn't have it already
     */
    public static String addExtensionIfNeeded(String path, String extension) {
        if (path.endsWith(extension) || getExtension(path)!=null)
            return path;
        // just append extension
        return path+extension;
    }
        
   /**
     * Utility to create file filters to browse for files of specified "extension" with "description" desc
     */
    public static FileFilter getFileFilter(final String extensions, final String desc) {
        if(extensions==null || desc==null) return null;
        // Parse the list of extensions passed in as (*.xyz, *.abc .de) into an array[]
        Vector<String> extensionList = new Vector<String>(2);
        if (extensions.contains(",")){
            StringTokenizer tokenizer = new StringTokenizer(extensions," ,*()");
            while(tokenizer.hasMoreElements()){
                extensionList.add(tokenizer.nextToken());
            }
        }
        else
            extensionList.add(0, extensions);
        
        // Copy list into an array[] (not necessary but to make a final object to be used by inner class
        // Could be done more efficiently
        final String[] extensionsArray = new String[extensionList.size()];
        for(int i=0; i<extensionsArray.length;i++)
            extensionsArray[i] = extensionList.get(i);
        
        return  new FileFilter() {
            public boolean accept(File f) {
                boolean test = false;
                for(int i=0; i<extensionsArray.length && !test; i++){
                    test = f.isDirectory() || f.getName().toLowerCase().endsWith(extensionsArray[i]);
                 }
                return test;
            }
            
            public String getDescription() {
                return desc +" ("+extensions+")";
            }
        };
    }

    /**
     * Check if a file or directory path exists
     * @param filePath
     * @return if the file/directory exists
     */
    public boolean exists(String filePath) {
        return new File(filePath).exists();
    }
    
    // If promptIfReplacing==true then it prompts user if they are trying to replacing an existing file.  
    // If currentFilename!=null, and the user chooses that file, then the prompt is skipped, since it is assumed they're simply saving over their currently loaded copy.
    public String browseForFilenameToSave(FileFilter filter, boolean promptIfReplacing, String currentFilename)
    {
        return browseForFilenameToSave(filter, promptIfReplacing, currentFilename, null);
    }
    
    public String browseForFilenameToSave(FileFilter filter, boolean promptIfReplacing, String currentFilename, Component parent)
    {
        // Init dialog to use "Internal.WorkDirectory" as thought of by user
        String defaultDir = TheApp.getCurrentVersionPreferences().get("Internal.WorkDirectory", "");
        //final JFileChooser dlog = new JFileChooser(defaultDir);
        dlog.setCurrentDirectory(new File(defaultDir));
        dlog.setName("");
        if(filter!=null) { dlog.resetChoosableFileFilters(); dlog.setFileFilter(filter); }
        
        dlog.setDialogTitle(filter.getDescription());
        String outFilename=null;
        Component topWindow;
        if (parent==null)
            topWindow = TheApp.getAppFrame();
        else
            topWindow = parent;
        
        for (;;) {
           dlog.setSelectedFile(new File(currentFilename));
           int result = dlog.showSaveDialog(topWindow);
           outFilename = null;
           if (result == JFileChooser.APPROVE_OPTION && dlog.getSelectedFile() != null)
                outFilename = dlog.getSelectedFile().getAbsolutePath();
           if(outFilename!=null && promptIfReplacing && (new File(outFilename)).exists() && (currentFilename==null || !currentFilename.equals(outFilename))) {
              // Attempting to overwrite a file other than currentFilename
              Object answer = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation("Replace file "+outFilename+"?","Replace file?",NotifyDescriptor.YES_NO_OPTION));
              if(answer==NotifyDescriptor.YES_OPTION) break;
           } else break;
       }
       if(outFilename != null){
           String workDirectoryString = dlog.getSelectedFile().getParent();
            setWorkingDirectoryPreference(workDirectoryString);
       }
       return outFilename;
    }
    public String browseForFilenameToSave(String extensions, String description, boolean promptIfReplacing, String currentFilename)
    {
       return browseForFilenameToSave(FileUtils.getFileFilter(extensions, description), promptIfReplacing, currentFilename);
    }

    public String browseForFolder()
    {
       return browseForFolder(null, "", false);
    }
    
    public String browseForFolder(String description, boolean allowCreate)
    {
       return browseForFolder(null, description, allowCreate);
    }
    
    public String browseForFolder(Frame parent, String description, boolean allowCreate)
    {
        // Init dialog to use "Internal.WorkDirectory" as thought of by user
        String defaultDir = TheApp.getCurrentVersionPreferences().get("Internal.WorkDirectory", "");
        //final JFileChooser dlog = new JFileChooser(defaultDir);
        dlog.setCurrentDirectory(new File(defaultDir));
        dlog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if(!description.equals("")) {
            dlog.setDialogTitle(description);
        }
        
        String outFilename=null;
        Frame topFrame = (parent==null)?TheApp.getAppFrame():parent;
        for (;;) {
           dlog.setSelectedFile(new File(""));
           int result;
           if (allowCreate)
               result= dlog.showSaveDialog(topFrame);
           else
               result= dlog.showOpenDialog(topFrame);
           outFilename = null;
           if (result == JFileChooser.APPROVE_OPTION && dlog.getSelectedFile() != null)
                outFilename = dlog.getSelectedFile().getAbsolutePath();

           // TODO: prompt to create directory if it doesn't exist?
           break;
       }
       
       if(outFilename != null){
           String workDirectoryString = dlog.getSelectedFile().getParent();
           setWorkingDirectoryPreference( workDirectoryString);
       }
       // Change settings to default (FILES_ONLY) so dialog isn't stuck in browse for folders mode
       // This has to be done as last step since it wipes selectedFile value (on OSX, issue 869)
       dlog.setFileSelectionMode(JFileChooser.FILES_ONLY);
       return outFilename;
    }
    /**
     * set Preference for directory to be used for future file browsing
     * @param workDirectoryString 
     */
    public void setWorkingDirectoryPreference(String workDirectoryString) {
        TheApp.getCurrentVersionPreferences().put("Internal.WorkDirectory", workDirectoryString);
    }

    /**
     * One common place to do the following common functions:
     * 1. Browse for a file using user's "workingDirectory" as initial dir.
     * 2. get file name
     * 3. set new "workingDirectory" based on selection
     * 4. return full path name of selected file
     *
     * If the file is required and a non-existant name is entered and isRequired2Exist==true
     *    then this function returns null 
     *
     */
    public String browseForFilename(FileFilter filter, boolean isRequired2Exist, Component parent)
    {
        // Init dialog to use "Internal.WorkDirectory" as thought of by user
        String defaultDir = TheApp.getCurrentVersionPreferences().get("Internal.WorkDirectory", "");
        //JFileChooser dlog = new JFileChooser(defaultDir);
        dlog.setCurrentDirectory(new File(defaultDir));
        dlog.setDialogTitle(filter.getDescription());
        if(filter!=null) { dlog.resetChoosableFileFilters(); dlog.setFileFilter(filter); }
        
        String outFilename=null;
        Component topWindow;
        if (parent==null)
            topWindow = TheApp.getAppFrame();
        else
            topWindow = parent;
        for (;;) {
           dlog.setSelectedFile(new File(" "));
           int result = dlog.showOpenDialog(topWindow);
           outFilename = null;
           if (result == JFileChooser.APPROVE_OPTION && dlog.getSelectedFile() != null)
                outFilename = dlog.getSelectedFile().getAbsolutePath();
           /** 
            * If isRequired2Exist flag is passed in as true we need to make sure the file really exists
            */
           if (isRequired2Exist && outFilename!= null && !(new File(outFilename)).exists())
              DialogDisplayer.getDefault().notify(
                      new NotifyDescriptor.Message("Selected file "+outFilename+" does not exist."));
           else break;
       }
       if(outFilename != null) setWorkingDirectoryPreference(dlog.getSelectedFile().getParent());
       return outFilename;
    }
    
        public String browseForFilename(FileFilter filter, String dialog, boolean isRequired2Exist, Component parent)
    {
        // Init dialog to use "Internal.WorkDirectory" as thought of by user
        String defaultDir = TheApp.getCurrentVersionPreferences().get("Internal.WorkDirectory", "");
        //JFileChooser dlog = new JFileChooser(defaultDir);
        dlog.setCurrentDirectory(new File(defaultDir));
        dlog.setDialogTitle(dialog);
        if(filter!=null) { dlog.resetChoosableFileFilters(); dlog.setFileFilter(filter); }
        
        String outFilename=null;
        Component topWindow;
        if (parent==null)
            topWindow = TheApp.getAppFrame();
        else
            topWindow = parent;
        for (;;) {
           dlog.setSelectedFile(new File(" "));
           int result = dlog.showOpenDialog(topWindow);
           outFilename = null;
           if (result == JFileChooser.APPROVE_OPTION && dlog.getSelectedFile() != null)
                outFilename = dlog.getSelectedFile().getAbsolutePath();
           /** 
            * If isRequired2Exist flag is passed in as true we need to make sure the file really exists
            */
           if (isRequired2Exist && outFilename!= null && !(new File(outFilename)).exists())
              DialogDisplayer.getDefault().notify(
                      new NotifyDescriptor.Message("Selected file "+outFilename+" does not exist."));
           else break;
       }
       if(outFilename != null) setWorkingDirectoryPreference(dlog.getSelectedFile().getParent());
       return outFilename;
    }
     
    public String browseForFilename(String extensions, String description, boolean isRequired2Exist, Component parent)
    {
       return browseForFilename(FileUtils.getFileFilter(extensions, description), isRequired2Exist, parent);
    }
    
    public String browseForFilename(FileFilter filter, String dialog)
    {
        return browseForFilename(filter, dialog, true, null);
    }
    
    public String browseForFilename(String extensions, String description, Component parent)
    {
        return browseForFilename(extensions, description, true, parent);
    }
    public String browseForFilename(FileFilter filter, Component parent)
    {
        return browseForFilename(filter, true, parent);
    }
    /**
     * A variation that assumes no parent frame (== parent is the Application window)
     */
    public String browseForFilename(String extensions, String description, boolean isRequired2Exist)
    {
       return browseForFilename(FileUtils.getFileFilter(extensions, description), isRequired2Exist, null);
    }
    public String browseForFilename(String extensions, String description)
    {
        return browseForFilename(extensions, description, true, null);
    }
    public String browseForFilename(FileFilter filter)
    {
        return browseForFilename(filter, true, null);
    }
    
    /**
     * getInstance: gets the singlton instance of the FileUtils class
     */
     public static FileUtils getInstance()
    {
        if (instance==null)
            instance = new FileUtils();
        
        return instance;
    }
    /**
     * makePathRelative converts the second parameter (file) to be relative
     * to baseDir. If this is not possible it returns null. The caller
     * would need to use the absolute path then.
     *
     * Both file and baseDir, baseDir is assumed to be a Dir
     *
     */
    public static String makePathRelative(String filename, String baseDirname)
    {
        if (effectivelyNull(filename)) return filename;
        return makePathRelative(new File(baseDirname), new File(filename));
    }
    
    public static String makePathRelative(File baseDir, File file)
    {
        if (!file.exists()){
            //int test=0;
            return file.getAbsolutePath();
        }
        if (isWindows()){
            String absPathBase = baseDir.getAbsolutePath();
            String absFilename = file.getAbsolutePath();
            String driveLetter = absPathBase.substring(0, absPathBase.indexOf(":")+1);
            if (!absFilename.startsWith(driveLetter))
                return absFilename;
        }
        String relative = null;
        if (baseDir.isDirectory()){
            if (baseDir.equals(file))
                relative = ".";
            else {
                StringBuffer b = new StringBuffer();
                File base = baseDir;
                String filePath = file.getAbsolutePath();
                while(!filePath.startsWith(slashify(base.getAbsolutePath()))){
                    base = base.getParentFile();
                    if (base == null)
                        return null;
                    b.append("../");
                }
                URI u = base.toURI().relativize(file.toURI());
                b.append(u.getPath());
                if (b.length()>=1 && b.charAt(b.length() -1)=='/'){
                    b.setLength(b.length() -1);
                }
                // if windows and contain drive letter make sure there's no leading /'
                if (isWindows()){
                    if (b.indexOf(":")!= -1){
                    String driveLetter = b.substring(0, b.indexOf(":")+1);
                    if (b.charAt(0)=='/') b.deleteCharAt(0);
                    }
                }
                relative = b.toString();
            }            
        }
        return relative;
    }
    
    public static String slashify(String path){
        if (path.endsWith(File.separator))
            return path;
        else
            return path + File.separatorChar;
    }

   // Treat null, empty string, and "Unassigned" all essentially as empty strings
   public static boolean effectivelyNull(String fileName) {
      return fileName==null || fileName.equals("") || fileName.equals("Unassigned");
   }

   public static String makePathAbsolute(String fileName, String parentDir) {
      if (effectivelyNull(fileName) || (new File(fileName)).isAbsolute()) return fileName;
      else return (new File(parentDir, fileName)).getAbsolutePath();
   }

    public String[] browseForSIMMModelFiles() {
        String extensions="*.jnt,*.msl";
        String desc="SIMM model files, one .jnt file and optional one .msl file";
        // Init dialog to use "Internal.WorkDirectory" as thought of by user
        String defaultDir = TheApp.getCurrentVersionPreferences().get("Internal.WorkDirectory", "");
        // final JFileChooser dlog = new JFileChooser(defaultDir);
        dlog.setCurrentDirectory(new File(defaultDir));
        dlog.setMultiSelectionEnabled(true);
        dlog.resetChoosableFileFilters(); 
        dlog.setFileFilter(getFileFilter(extensions, desc));
        
        File[] outFiles=null;
        JFrame topFrame = TheApp.getAppFrame();
        for (;;) {
           dlog.setSelectedFile(new File(""));
           int result = dlog.showOpenDialog(topFrame);
           outFiles = null;
           if (result == JFileChooser.APPROVE_OPTION && dlog.getSelectedFiles() != null)
                outFiles = dlog.getSelectedFiles();
           else
              break;
           boolean allExist=true;
           String badFiles="";
           int numJntFiles=0;
           int numMslFiles=0;
           for( int i=0;i<outFiles.length;i++){
               File nextSelectedFile = outFiles[i];
               if (!nextSelectedFile.exists()){
                    try {
                        badFiles += outFiles[i].getCanonicalFile().getName()+" ";
                    } catch (IOException ex) {
                        ErrorDialog.displayExceptionDialog(ex);
                    }
                   allExist = false;
               }
               else if (nextSelectedFile.getName().endsWith(".jnt"))
                       numJntFiles++;
               else if (nextSelectedFile.getName().endsWith(".msl"))
                       numMslFiles++;                      
           }
           /** 
            * If isRequired2Exist flag is passed in as true we need to make sure the file really exists
            */
           if (allExist==false)
              ErrorDialog.showMessageDialog("Selected file(s) "+badFiles+" do not exist.");
           else if (numJntFiles!=1)
              ErrorDialog.showMessageDialog("One jnt file must be selected.");
           else if (numMslFiles>1)
              ErrorDialog.showMessageDialog("At most one msl file can be selected.");
           else
               break;
       }
       String[] outFilenames=null;
       if(outFiles!=null) {
          outFilenames = new String[outFiles.length];
          for (int i=0; i<outFiles.length;i++){ // either one or two
              if (outFiles[i].getName().endsWith(".jnt"))
                  outFilenames[0]=outFiles[i].getAbsolutePath();
              else
                   outFilenames[1]=outFiles[i].getAbsolutePath();             
          }
          if(outFilenames.length>0) setWorkingDirectoryPreference(dlog.getSelectedFiles()[0].getParent());
       }
       // get jnt file followed by muscle file of any into output array
        
       return outFilenames;
    }
    /**
     * Check if we're running on Windows
     * @return true if running on Windows, false otherwise
     * 
     * This's used to makePathRelative which is no a cross-platform operation. 
     * The use of this function is thus discouraged
     */
    public static boolean isWindows(){
 
	String os = System.getProperty("os.name").toLowerCase();
	//windows
	return (os.indexOf( "win" ) >= 0); 
 
    }
    public static void copyFiles(final Path sourcePath, final Path targetPath) throws IOException{
        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
        public FileVisitResult preVisitDirectory(final Path dir,
                final BasicFileAttributes attrs) throws IOException {
            Files.createDirectories(targetPath.resolve(sourcePath
                    .relativize(dir)));
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult visitFile(final Path file,
                final BasicFileAttributes attrs) throws IOException {
            Files.copy(file,
                    targetPath.resolve(sourcePath.relativize(file)));
            return FileVisitResult.CONTINUE;
        }
    });
    }
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists).
     * http://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public static void unzip(final Path zipFilePath, final Path destDirectory) throws IOException {
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath.toFile()));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory.toString() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    /**
     * Extracts a zip entry (file entry).
     * http://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        final int BUFFER_SIZE = 4096;
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
