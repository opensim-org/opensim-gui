# This example script creates a standalone dialog window to 
# display information about the current model (name, number of 
# bodies, and number of joints). This script only works if a model 
# is currently loaded

# import needed packages
# swing is the generic GUI kit for Java, allows for I/O and creating windows if needed
import javax.swing as swing

# create a window
win = swing.JFrame("Welcome")

# access the current model
model = getCurrentModel()

# if there is no loaded model output message in window
if not model:
    win.getContentPane().add(swing.JLabel("No models"))
    win.setTitle("Example Window")
# otherwise display model summary statistics
else:
	# set the window layout
    win.getContentPane().setLayout(swing.BoxLayout(win.getContentPane(), 1))
	
	# display the model name
    dLabel = swing.JLabel("Model name: "+model.getName())
    win.add(dLabel)
	
	# display the number of bodies
    numBodiesString = "Number of bodies ="+lang.String.valueOf(model.getNumBodies())
    bLabel = swing.JLabel(numBodiesString)
    win.add(bLabel)
	
	#display the number of joints
    numJointsString = "Number of Joints ="+lang.String.valueOf(model.getNumJoints())
    njLabel = swing.JLabel(numJointsString)
    win.add(njLabel)
	
# Set title and size of the window
win.setTitle("Example Window")	
win.size = (200, 100)

# Display the window
win.show()