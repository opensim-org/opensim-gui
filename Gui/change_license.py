import os

opening_dashes = """\
/* -------------------------------------------------------------------------- *
"""
dashes = """\
 * -------------------------------------------------------------------------- *
"""

opensim_description = """\
 * OpenSim is a toolkit for musculoskeletal modeling and simulation.          *
 * See http://opensim.stanford.edu and the NOTICE file for more information.  *
"""

apache_boilerplate = """\
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at  http://www.apache.org/licenses/LICENSE-2.0         *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
"""

def pad(content):
    line = " * "
    line += content
    line += (77 - len(line)) * " " + " * \n"
    return line
    
def create_new_license_blurb(fname):
    blurb = opening_dashes
    blurb += pad("OpenSim: %s" % fname)
    blurb += dashes
    blurb += opensim_description
    blurb += pad("")
    blurb += pad("Copyright (c) 2005-2016 Stanford University and the Authors")
    # TODO detect other authors, like Kevin Xu.
    blurb += pad("Author(s): Ayman Habib")
    blurb += pad("")
    blurb += apache_boilerplate
    return blurb

for dirpath, dirnames, filenames in os.walk('.'):
    for fname in filenames:
        if fname.endswith('.java'):
            fpath = os.path.join(dirpath, fname)
            filecontents = ""
            with open(fpath, 'r') as f:
                filecontents = f.read()
            if "Stanford University" in filecontents:
                start = filecontents.find(" * Copyright")
                ending = "SUCH DAMAGE.\n"
                end = filecontents.find(ending) + len(ending)
                print("%s, %i, %i" % (fname, start, end))
                newfilecontents = create_new_license_blurb(fname)
                newfilecontents += filecontents[0:start]
                newfilecontents += filecontents[end::]
                with open(fpath, 'w') as f:
                    f.write(newfilecontents)

