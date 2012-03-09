import sys
import javax.swing as swing
import java.lang as lang
import org.opensim.modeling as modeling
import org.opensim.view.pub as view
import org.opensim.utils as utils
import java.io as io  

# More graceful handling of exceptions thrown by tools, may be taken out in production
modeling.OpenSimObject.setDebugLevel(3)

# Browse for Directory containing trc files to process
print "Acquire folder that contains trc files"
#trcDataFolder = utils.FileUtils.getInstance().browseForFolder()
trcDataFolder = "E:\\Projects\\OpenSimTrunk\\Gui\\opensim\\Scripts\\testData\\"
print trcDataFolder

print "Acquire setup file"
#setupFile = utils.FileUtils.getInstance().browseForFilename(".xml", "setup file for tool", 1)
setupFile = "E:\\\\Projects\\OpenSimTrunk\\Gui\\opensim\\Scripts\\testData\\leg39_Setup_InverseKinematics.xml"
print setupFile

print "Acquire folder that contains output files"
#resultsFolder = utils.FileUtils.getInstance().browseForFolder()
resultsFolder = "E:\\Projects\\OpenSimTrunk\\Gui\\opensim\\Scripts\\testData\\"
print resultsFolder

#initialize InverseKinematicsTool from setup file
ikTool = modeling.InverseKinematicsTool(setupFile)
print "Created InverseKinematicsTool"

# Cycle thru the trc files in trcDataFolder, set each as markerDataFileName
# change corresponding output file and repeat
trcFolder = io.File(trcDataFolder)
      
trcFiles = trcFolder.list() 
  
for x in range(len(trcFiles)): 
    if trcFiles[x].endswith(".trc"):
          print trcFiles[x]
          outFile = trcFiles[x].replace('.trc', '_ik.sto')
          trialSetupFile = trcFiles[x].replace('.trc', 'ik_setup.xml')
          ikTool.setMarkerDataFileName(trcDataFolder+trcFiles[x])
          ikTool.setOutputMotionFileName(resultsFolder+outFile)
          print "Runnning trial file "+ikTool.getMarkerDataFileName()+" Output:"+ikTool.getOutputMotionFileName()
          ikTool.print(trialSetupFile)
          ikTool.setVerboseLevel(modeling.Tool.Debug)
          ikTool.run()



