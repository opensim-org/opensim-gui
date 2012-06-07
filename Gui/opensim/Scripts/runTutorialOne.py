# This example performs some of the initial steps of Tutorial One

# This line allows for more graceful handling of low level exceptions, just in case
modeling.OpenSimObject.setDebugLevel(3)

# Load model
addModel(getScriptsDir()+"/testData/BothLegs.osim")

# Load a motion
loadMotion(getScriptsDir()+"/testData/BothLegsWalk.mot")

# Get a handle to the current model
myModel = getCurrentModel()

# Put the model in the default pose
for i in range(myModel.getCoordinateSet().getSize()):
	coord = myModel.getCoordinateSet().get(i)
	setCoordinateValue(coord, coord.getDefaultValue())

# Plot the RF and VASINT fiber lengths with the model in the default pose
plotterPanel = createPlotterPanel("Plot ")
crv1 = addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
setCurveLegend(crv1, "RF")
crv2 = addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
setCurveLegend(crv2, "VASINT")

# Plot the RF and VASINT fiber lengths with the model with the hip flexed to 45 deg
hip_coord = myModel.getCoordinateSet().get("r_hip_flexion")
setCoordinateValue(coord, 45)
crv3 = addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
setCurveLegend(crv3, "RF_hip45")
crv4 = addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
setCurveLegend(crv4, "VASINT_hip45")

# Save the results to file
exportData(plotterPanel, getScriptsDir()+"/testData/RF_VASINT_FiberLengths.sto")
