/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import org.json.simple.JSONArray;

/**
 *
 * @author Ayman-NMBL
 * 
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
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
/**
 * This interface will be used to create plugins for custom visualizations and
 * should be used for ad-hoc visualizations that do not rely on the 
 * Component::generateDecorations scheme which fails for non-components or is 
 * not flexible to handle variety of visualization options.
 * 
 * @author Ayman-NMBL
 */
    public interface VisualizerAddOn {
    // Method to be invoked once on construction of Visualization
    // Clients would likely cache modelJson for future use
    public void init(ModelVisualizationJson modelJson);
    // Method invoked whenever we're generating a Frame to send oto the Visualizer
    // Used as a hook for the VisualizerAddOn to update its owned visuals
    // The state maintained by modelJson
    public void updateVisuals(JSONArray frame_jsonArray);
    // Give AddOn a chance to cleanup and remove added visuals
    public void cleanup();
}
