import os
import re

opening_dashes = """\
/* -------------------------------------------------------------------------- *
"""
dashes = """\
 * -------------------------------------------------------------------------- *
"""

opening_dashes_py = """\
# --------------------------------------------------------------------------- #
"""
dashes_py = """\
# --------------------------------------------------------------------------- #
"""

opensim_description = """\
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
"""
#opensim_description = """\
# * OpenSim is a toolkit for musculoskeletal modeling and simulation.          *
# * See http://opensim.stanford.edu and the NOTICE file for more information,  *
# * including about our funding sources (NIH, DARPA).                          *
#"""

apache_boilerplate = """\
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
"""

opensim_description_py = """\
# OpenSim is a toolkit for musculoskeletal modeling and simulation,           #
# developed as an open source project by a worldwide community. Development   #
# and support is coordinated from Stanford University, with funding from the  #
# U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file     #
# for more information including specific grant numbers.                      #
"""
#opensim_description = """\
# * OpenSim is a toolkit for musculoskeletal modeling and simulation.          *
# * See http://opensim.stanford.edu and the NOTICE file for more information,  *
# * including about our funding sources (NIH, DARPA).                          *
#"""

apache_boilerplate_py = """\
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
"""



# opensim-visualizer repo license files??

# TODO make sure that the old license no longer appears in the repository.
# Search for LGPL anywhere in the repo

# Some threejs examples and utils use the GPL license.

# TODO files that need more work:
# TreeTableModel
# OpenSimObjectWidget
# StringGraphScene
# CoordinateViewerAction.java extra slash (/) - DONE
#UserTreeCellEditor.java - DONE
"""
BrowserLauncher.java: This file says it's public domain, so I removed our old
license from it.
"""

potential_authors = [
        'Peter Loan',
        'Jeff Reinbolt',
        'Eran Guendelman',
        'Ayman Habib',
        'Kevin Xu',
        'Jen Hicks',
        'Thomas Uchida',
        'Christopher Dembia',
        'Ajay Seth',
        'Edith Arnold',
        ]
#special_consideration = [
#        'StringGraphScene.java',
#        'ComponentTitledBorder.java',
#        ]
def pad(content, center=False):
    internal_width = 74
    line = " * "
    if center:
        left_pad = internal_width / 2 - len(content) / 2
        right_pad = internal_width - left_pad - len(content)
        line += (left_pad * " ") + content + (right_pad * " ")
    else:
        line += content
    if len(line) >= 77:
        raise Exception("line too long: " + line)
    line += (77 - len(line)) * " " + " *\n"
    return line
    
def create_new_license_blurb(fname, authors):
    blurb = opening_dashes
    #blurb += pad("OpenSim: %s" % fname, center=True)
    blurb += pad("OpenSim: %s" % fname)
    blurb += dashes
    blurb += opensim_description
    blurb += pad("")
    blurb += pad("Copyright (c) 2005-2017 Stanford University and the Authors")
    if len(authors) == 0:
        authors = ['Ayman Habib']
    blurb += pad("Author(s): " + ", ".join(authors))
    blurb += pad("")
    blurb += apache_boilerplate
    return blurb

def pad_py(content, center=False):
    internal_width = 74
    line = "# "
    if center:
        left_pad = internal_width / 2 - len(content) / 2
        right_pad = internal_width - left_pad - len(content)
        line += (left_pad * " ") + content + (right_pad * " ")
    else:
        line += content
    if len(line) >= 77:
        raise Exception("line too long: " + line)
    line += (77 - len(line)) * " " + " #\n"
    return line
    
def create_new_license_blurb_py(fname, authors):
    blurb = opening_dashes_py
    #blurb += pad("OpenSim: %s" % fname, center=True)
    blurb += pad_py("OpenSim: %s" % fname)
    blurb += dashes_py
    blurb += opensim_description_py
    blurb += pad_py("")
    blurb += pad_py("Copyright (c) 2005-2017 Stanford University and the Authors")
    if len(authors) == 0:
        authors = ['Ayman Habib']
    blurb += pad_py("Author(s): " + ", ".join(authors))
    blurb += pad_py("")
    blurb += apache_boilerplate_py
    return blurb

def get_author_list(fpath):
    import subprocess
    if os.name == 'nt':
        # Cannot expect to run the following git log command on Windows.
        return list()
    else:
        out = os.popen('git log -- %s | grep "^Author" | sort | uniq' %
                fpath).read()
        list_str = out.split('\n')
        author_list = list()
        for s in list_str:
            if s == "": continue
            author = s[len('Author: '):s.find('<')-1]
            if author == 'aymanhab':
                author = 'Ayman Habib'
            #is_duplicate = False
            #for existing_author in author_list:
            #    if author == existing_author:
            #        is_duplicate = True
            #if not is_duplicate:
            #    author_list.append(author)
            author_list.append(author)
        # For some reason, some authors are not detected by the above.
        with open(fpath, 'r') as f:
            filecontents = f.read()
            for potential in potential_authors:
                if potential in filecontents:
                    author_list.append(potential)
        deduplicated = list()
        [deduplicated.append(x) for x in author_list if x not in deduplicated]
        return deduplicated

#regex_old_license_block = re.compile(r"/\*.*?Copyright (c).*?Stanford.*\*/",
#        flags=re.DOTALL)
# DOTALL means that ".*" includes newlines.
# .*? means match any number of characters, but the non-greedily (the fewest
# necessary).
# "\*" is just "*"; we escape the "*" since it has a special meaning normally.
# [^/]* do not match a comment block that precedes the license block.
#regex_old_license_block = re.compile(r"/\*[^/]*?Copyright.*?Stanford.*?\*/",
#        flags=re.DOTALL)

# [\s\S] means all characters (all whitespace and non-whitespace characters).
regex_old_license_block = re.compile(
        r"\s*\*\s*Copyright.*?Stanford[\s\S]*SUCH DAMAGE.*")

regex_old_license_block_py = re.compile(
        r"# ---.*?Copyright.*?Stanford.*?--- #", re.DOTALL)

# This will help us remove things like:
# /*
#  */
# that are caused by removing the old license block.
unedited_files = list()
files_with_replaced_license = list()
files_with_added_license = list()
regex_empty_comment_block = re.compile(r"/\*[\s]*?\*/")
for dirpath, dirnames, filenames in os.walk('./opensim'):
    for fname in filenames:
        if fname.endswith('.java') or fname.endswith('.py'):
            if fname.endswith('.py'):
                regex_old_block = regex_old_license_block_py
                create_blurb_func = create_new_license_blurb_py
                ext = 'py'
            else:
                regex_old_block = regex_old_license_block
                create_blurb_func = create_new_license_blurb
                ext = 'java'
            fpath = os.path.join(dirpath, fname)
            filecontents = ""
            with open(fpath, 'r') as f:
                filecontents = f.read()
            if regex_old_block.search(filecontents):
                # Hack for the situation in CoordinateViewerAction.java
                # where the first line is /* Copyright (c) ...
                filecontents = filecontents.replace('/* Copyright',
                        '/*\n * Copyright')
                authors = get_author_list(fpath)
                print('{} REPLACE authors: {}'.format(fname, authors))
                if not 'Public Domain' in filecontents:
                    newfilecontents = create_blurb_func(fname, authors)
                else:
                    # For BrowserLauncher.java.
                    newfilecontents = ""
                # Remove old license block and append resulting file.
                newfilecontents += regex_old_block.sub(
                        "",
                        filecontents,
                        count=1 # only match once.
                        )
                newfilecontents = regex_empty_comment_block.sub(
                        "",
                        newfilecontents,
                        )
            #if "Stanford University" in filecontents:
            #    start = filecontents.find(" * Copyright")
            #    ending = "SUCH DAMAGE.\n"
            #    end = filecontents.find(ending) + len(ending)
            #    print("%s, %i, %i" % (fname, start, end))
            #    newfilecontents = create_new_license_blurb(fname)
            #    newfilecontents += filecontents[0:start]
            #    newfilecontents += filecontents[end::]
                with open(fpath, 'w') as f:
                    f.write(newfilecontents)
                files_with_replaced_license.append(fpath)
                continue
            if (not 'Copyright' in filecontents) and (not 'Stanford' in
                    filecontents) and (not 'SWIG' in filecontents[0:800]):
                # Look for SWIG in the first 10 lines.
                authors = get_author_list(fpath)
                print('{} ADD authors: {}'.format(fname, authors))
                license_blurb = create_blurb_func(fname, authors)
                newfilecontents = license_blurb + filecontents
                with open(fpath, 'w') as f:
                    f.write(newfilecontents)
                files_with_added_license.append(fpath)
                continue
            unedited_files.append(fpath)

import pprint


print('Files with replaced license (count: %i):' %
        len(files_with_replaced_license))
pprint.pprint(files_with_replaced_license)
print('Files with added license (count: %i):' %
        len(files_with_added_license))
pprint.pprint(files_with_added_license)
unedited_files_curated = list()
for fpath in unedited_files:
    if (not fpath.startswith('./opensim/modeling/src/') and not
            fpath.startswith('./opensim/jfreechart')):
        unedited_files_curated.append(fpath)
pprint.pprint(unedited_files_curated)


print('Files with the old OpenSim license:')
for dirpath, dirnames, filenames in os.walk('./opensim'):
    for fname in filenames:
        if fname.endswith('.java'):
            fpath = os.path.join(dirpath, fname)
            filecontents = ""
            with open(fpath, 'r') as f:
                filecontents = f.read()
            if 'non-commercial' in filecontents:
                print("    " + fpath)



