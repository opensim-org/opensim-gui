import sys
import javax.swing as swing
import java.lang as lang
import org.opensim.modeling as modeling
import org.opensim.view.pub as view

# Get handle to current model in GUI
cModel = view.gui.getCurrentModel()
# Create a fresh copy
myModel = modeling.Model(cModel)
# initialize the copy
myModel.initSystem()
# name the copy for later showing in GUI
oldName = cModel.getName()
myModel.setName(oldName+"_Stronger")

# A scale factor for MaxIsometricForce of muscles
scaleFactor = 1.2

# Apply scale factor to MaxIsometricForce
for i in range(myModel.getMuscles().getSize()):
	currentMuscle = myModel.getMuscles().get(i)
	currentMuscle.setMaxIsometricForce(currentMuscle.getMaxIsometricForce()*scaleFactor)

# Save resulting model
fullName = cModel.getInputFileName()
newName = fullName.replace('.osim', '_stronger.osim')
myModel.print(newName)

#Popup a dialog to show the file name used to save the model
win = swing.JFrame("Confirm")
dLabel = swing.JLabel("Wrote stronger model to file "+newName)
win.getContentPane().add(dLabel)
win.pack()
win.show()







