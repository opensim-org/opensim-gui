import os
import re

opening_dashes = """\
/* -------------------------------------------------------------------------- *
"""
dashes = """\
 * -------------------------------------------------------------------------- *
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
 * copy of the License at  http://www.apache.org/licenses/LICENSE-2.0         *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
"""

# TODO adding licenses to other files as well, like K12 files that don't have
# the old Stanford license.

# TODO Installer.java has a comment that should be preserved.

#UserTreeCellEditor.java

# Put license at the very top.

# Retain @author lines.

# Retain descriptions of the file (CommandHistory).

potential_authors = [
        'Peter Loan',
        'Jeff Reinbolt',
        'Eran Guendelman',
        'Ayman Habib',
        'Kevin Xu',
        'Jen Hicks',
        ]
special_consideration = [
        'StringGraphScene.java',
        'ComponentTitledBorder.java',
        ]
def pad(content, center=False):
    internal_width = 74
    line = " * "
    if center:
        left_pad = internal_width / 2 - len(content) / 2
        right_pad = internal_width - left_pad - len(content)
        line += (left_pad * " ") + content + (right_pad * " ")
    else:
        line += content
    line += (77 - len(line)) * " " + " *\n"
    return line
    
def create_new_license_blurb(fname, authors):
    blurb = opening_dashes
    blurb += pad("OpenSim: %s" % fname, center=True)
    blurb += dashes
    blurb += opensim_description
    blurb += pad("")
    blurb += pad("Copyright (c) 2005-2017 Stanford University and the Authors")
    # TODO detect other authors, like Kevin Xu.
    blurb += pad("Author(s): " + ", ".join(authors))
    blurb += pad("")
    blurb += apache_boilerplate
    return blurb

def get_author_list(fpath):
    import subprocess
    if os.name == 'nt':
        return list()
    else:
        out = os.popen('git log -- %s | grep "^Author" | sort | uniq' %
                fpath).read()
        list_str = out.split('\n')
        author_list = list()
        for s in list_str[:-1]:
            author = s[len('Author: '):s.find('<')-1]
            if author == 'aymanhab':
                author = 'Ayman Habib'
            is_duplicate = False
            for existing_author in author_list:
                if author == existing_author:
                    is_duplicate = True
            if not is_duplicate:
                author_list.append(author)
        return author_list

#regex_old_license_block = re.compile(r"/\*.*?Copyright (c).*?Stanford.*\*/",
#        flags=re.DOTALL)
# DOTALL means that ".*" includes newlines.
# .*? means match any number of characters, but the non-greedily (the fewest
# necessary).
# "\*" is just "*"; we escape the "*" since it has a special meaning normally.
# [^/]* do not match a comment block that precedes the license block.
#regex_old_license_block = re.compile(r"/\*[^/]*?Copyright.*?Stanford.*?\*/",
#        flags=re.DOTALL)
regex_old_license_block = re.compile(
        r"\s*\*\s*Copyright.*?Stanford[\s\S]*SUCH DAMAGE.*")

# This will help us remove things like:
# /*
#  */
# that are caused by removing the old license block.
regex_empty_comment_block = re.compile(r"/\*[\s]*?\*/")
for dirpath, dirnames, filenames in os.walk('./opensim'):
    for fname in filenames:
        if fname.endswith('.java'):
            fpath = os.path.join(dirpath, fname)
            filecontents = ""
            with open(fpath, 'r') as f:
                filecontents = f.read()
            match = regex_old_license_block.search(filecontents)
            if match:
                authors = get_author_list(fpath)
                print('{} authors: {}'.format(fname, authors))
                newfilecontents = create_new_license_blurb(fname, authors)
                newfilecontents += regex_old_license_block.sub(
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

