
import os
for dirpath, dirnames, filenames in os.walk('./opensim'):
    for fname in filenames:
        if fname.endswith('.java'):
            fpath = os.path.join(dirpath, fname)
            filecontents = ""
            with open(fpath, 'r') as f:
                filecontents = f.read()
            if ('LGPL' in filecontents or 'Lesser' in filecontents):
                print(fpath + ' LGPL')
            if (('Copyright' in filecontents or 'License' in filecontents) and
                    not 'Stanford' in filecontents):
                print(fpath)

print('Files with the old OpenSim license:')
for dirpath, dirnames, filenames in os.walk('./opensim'):
    for fname in filenames:
        if fname.endswith('.java'):
            fpath = os.path.join(dirpath, fname)
            filecontents = ""
            with open(fpath, 'r') as f:
                filecontents = f.read()
            if ('non-commercial' in filecontents or
                    'simtk.org/home/opensim' in filecontents):
                print("    " + fpath)



