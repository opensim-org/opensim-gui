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
myModel.setName(oldName+"_heavier")

# A scale factor for mass of for-arm
massScale = 3

# Change mass of forarm in the model
forarm = myModel.getBodySet().get("r_ulna_radius_hand")
forarm.setMass(forarm.getMass() * massScale)
fullName = cModel.getInputFileName()
newName = fullName.replace('.osim', '_heavier.osim')
myModel.print(newName)

#Add model to GUI
view.gui.addModel(newName)




