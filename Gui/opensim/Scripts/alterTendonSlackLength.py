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
myModel.setName(oldName+"_modified")

# A scale factor for MaxIsometricForce of muscles
tendonSlackLengthScale = 1.5

# Change TendonSlackLength for all muscles in the model
for i in range(myModel.getMuscles().getSize()):
	currentMuscle = myModel.getMuscles().get(i)
	oldSL = currentMuscle.getTendonSlackLength()
	currentMuscle.setTendonSlackLength(oldSL * tendonSlackLengthScale)

fullName = cModel.getInputFileName()
newName = fullName.replace('.osim', '_modified.osim')
myModel.print(newName)

#Add model to GUI
view.gui.addModel(newName)




