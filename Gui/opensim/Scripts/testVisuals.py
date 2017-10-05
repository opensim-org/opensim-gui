# --------------------------------------------------------------------------- #
# OpenSim: testVisuals.py                                                     #
# --------------------------------------------------------------------------- #
# OpenSim is a toolkit for musculoskeletal modeling and simulation,           #
# developed as an open source project by a worldwide community. Development   #
# and support is coordinated from Stanford University, with funding from the  #
# U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file     #
# for more information including specific grant numbers.                      #
#                                                                             #
# Copyright (c) 2005-2017 Stanford University and the Authors                 #
# Author(s): Ayman Habib, Kevin Xu                                            #
#                                                                             #
# Licensed under the Apache License, Version 2.0 (the "License"); you may     #
# not use this file except in compliance with the License. You may obtain a   #
# copy of the License at http://www.apache.org/licenses/LICENSE-2.0           #
#                                                                             #
# Unless required by applicable law or agreed to in writing, software         #
# distributed under the License is distributed on an "AS IS" BASIS,           #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    #
# See the License for the specific language governing permissions and         #
# limitations under the License.                                              #
# --------------------------------------------------------------------------- #


# This example script demonstrates control of visuals. Loads a model 
# (Both Legs with Muscles), changes the color of the, makes the right 
# femur transparent, and aligns the model view with the x axis.

# Load model
loadModel(getInstallDir()+"/Models/Gait2392_Simbody/gait2392_simbody.osim")

# Get a handle to the current model (the one just loaded)
model = getCurrentModel()

# Color pelvis blue
pelvis = findObject(model, "Body", "pelvis") 
setObjectColor(pelvis, [0.0, 0.0, 1.0])

# Make femur half transparent
femur = findObject(model, "Body", "femur_r") 
setObjectOpacity(femur, 0.5)

# Orient view along x axis
gfxWindowSendKey('x')




