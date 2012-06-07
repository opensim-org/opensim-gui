# This example script shows how to create and display a plot window. The script loads the 
# BothLegs OpenSim model adds curves of fiber length for the model. Then, it loads and plots 
# data from a storage file, which contains fiber lengths for the model Subject01_simbody
# that is included with the OpenSim distribution

# This line allows for more graceful handling of low level exceptions, just in case
modeling.OpenSimObject.setDebugLevel(3)

# Load the model BothLegs.osim
addModel(getScriptsDir()+"/testData/BothLegs.osim")

# Create a plotter panel and set the title
plotterPanel = createPlotterPanel("Plot Example")

# Add curves showing rectus femoris and vasti fiber lengths vs. right knee angle and set the legend
crv1 = addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
crv2 = addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
crv1.setLegend("RF_BothLegs")
crv2.setLegend("VASINT_BothLegs")

# Load data from an external data source and plot
src = addDataSource(plotterPanel, getScriptsDir()+"/testData/Subject01_FiberLengths.sto")
crv3 = addCurve(plotterPanel, src, "knee_angle_r", "rect_fem_r")
crv4 = addCurve(plotterPanel, src, "knee_angle_r", "vas_int_r")
crv3.setLegend("RF_Subject01")
crv4.setLegend("VASINT_Subject01") 

# Change the color of the first curve in the plot
setCurveColor(plotterPanel, 0, 0.0, 1.0, 1.0)

# Export the data in the plotter window to a file
exportData(plotterPanel, getScriptsDir()+"/testData/cvs_export.sto")
