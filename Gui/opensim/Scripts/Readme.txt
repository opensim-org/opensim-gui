Scripting Environment(s) in OpenSim 3.0

OpenSim 3.0 introduces a new scripting capability that has been in demand for a while to achieve batch processing as well 
as utilize the improvements in the API without the overhead of learning to program in C++ or dealing with low level issues
that comes with it (e.g.  binary compatibility between compiler versions etc.). There are many pathways that can exercise 
the scripting layer but the capabilities available to them vary as follows:
1.	Access to OpenSim interface through Matlab.
2.	Access through a scripting shell in the OpenSim application.
3.	Access to the OpenSim interface through a Jython interpreter.

The sections below outline how to set each of these up and describe the available functionality as well as the limitations.

1. OpenSim interface from Matlab
===========================
In this scenario, you can load all the OpenSim libraries into a Matlab session following a simple setup. 

- What’s Available: In this mode, users can use all the OpenSim API to create a model (e.g. OpenSimCreateTugOfWarModel.m), 
instantiate and run tools from setup files or programmatically (e.g. TugOfWar_CompleteRunVisualize.m), and also can repeatedly
 run various tools similar to main programs written in C++ while taking advantage of the Matlab environment and tools. 
There’s a new low level visualizer to be used by API users that can be invoked in this mode.

- Limitations: In this mode, there’s no access to the plotter  (use Matlab’s native plotter) or the graphics window (use the model 
visualizer instead), also Simtk-proper classes (those that belong to the SimTK namespace and simbody internals) are not available 
for construction though they could be used for passing data around. 

2. OpenSim Scripting shell
=====================
OpenSim 3.0 comes with a built in scripting shell that allows user access to the OpenSim API for model building and tools 
loading/reading/writing. 

- What’s Available:  Access to the API for model and tools processing, this environment also offers access to most of the commands 
available from the OpenSim application menu bar as well as GUI tasks for selection and a streamlined interface to the plotter so 
that users can create, customize and export  curves from the plotter easily.  Limited access to the graphics window is also available 
(e.g. selection and camera control)
- Limitations:  Similar to the Matlab interface, in this mode Simtk-proper classes  are not available for construction. The syntax of 
the scripting language is Python.

Files in this folder: Exercise in this order
---------------------
testVisuals.py :		demonstrates control of visuals  including color, opacity, selection and view control.
testShowModelSummary.py:	 demonstrate displaying info about the model in a standalone dialog.
plotMuscleFiberLengthAgainstFile.py : demonstrates  opening a plot window, making curves and plotting data from files.
makeUlnaHeavy.py	 : 	 demonstrates changing some attributes of Bodies( Ulna's mass in this example in Arm26 model)
alterTendonSlackLength.py:    demonstrates changing some attributes of Muscles (tendonSlackLength in this example)


tips:
===
viewmethods(aObject) will bring up a dialog with all available methods for the specified object
type(aObject) will print the full qualified type of the passed in aObject.
getCurrentModel() returns the current model loaded in the GUI.
after loading a script, variables defined in the script are available for future reference
multiline commands (for iteration or if/else are not supported in the GUI shell but are supported in files
A panel to echo commands and files executed in the scripting shell is now available and shown by default

