/* -------------------------------------------------------------------------- *
 * OpenSim: JConsole.java                                                     *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
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
package org.opensim.console;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.opensim.utils.ErrorDialog;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.util.InteractiveConsole;
import org.python.util.JLineConsole;

/**
 *
 * @author Ayman based on JavaForum code
 */
public class JConsole extends JTextArea implements KeyListener {

    /**
     * The input stream that will pass data to the script engine
     */
    public final Reader in;
    /**
     * The output stream from the script engine
     */
    public final Writer out;
    /**
     * The error stream from the script engine
     */
    public final Writer err;
    private CommandHistory history;
    /**
     * index of where we can start editing text
     */
    int editStart;
    /**
     * True when a script is running
     */
    boolean running;
    /**
     * The script engine and scope we're using
     */
    private InteractiveConsole interp;
    /**
     * The allowed variables and stuff to use
     */
    // private Bindings bindings;
    // ScriptContext context;
    private ConsoleFilter filter;
    private Thread pythonThread;

    private ArrayList<ConsoleListener> consoleListeners = new ArrayList<ConsoleListener>();
    
    private String moreCommand = "";
    
    private final String ps1 = ">>> ";    
    private final String ps2 = "... "; 
    /**
     * 
     */
    public JConsole() {
        
        // create streams that will link with this
        in = new ConsoleInputStream(this);
        // System.setIn(in);
        out = new ConsoleOutputStream(this);
        //System.setOut(new PrintStream(out));
        err = new ConsoleOutputStream(this);
        // setup the command history
        history = new CommandHistory();
        // setup the script engine
//        interp = new InteractiveInterpreter();

        // no postProps, registry values used 
        JLineConsole.initialize(System.getProperties(), null, new String[0]);

        interp = new JLineConsole();
	
        // important line, set JLineConsole to internal python variable to be able to 
        // acces console from python interface
        interp.getSystemState().__setattr__("_jy_interpreter", Py.java2py(interp));
        interp.getSystemState().__setattr__("_jy_main", Py.java2py(this));
	
        // this instance - in order to call interrupt on correct thread

        // JLINE console initialization
//        JLineConsole.initialize(System.getProperties(), null, new String[0]);       // setup the script interp
        
        // enable autocomplete by default:)
        interp.exec("import rlcompleter, readline");
        interp.exec("readline.parse_and_bind('tab: complete')");

        Py.getSystemState().setClassLoader(
                this.getClass().getClassLoader());
        interp.exec("import sys");
        interp.exec("import javax.swing as swing");
        interp.exec("import java.lang as lang");
        interp.exec("import org.opensim.modeling as modeling");
        interp.exec("from org.opensim.console.gui import *");
        interp.exec("from org.opensim.console.OpenSimPlotter import *");
        
        //engine.exec("import org.opensim.tracking as tools");
        //interp.setIn(in);
        interp.setOut(out);
        interp.setErr(err);
        setTabSize(4);
        // setup the event handlers and input processing
        // setup the document filter so output and old text can't be modified
        addKeyListener(this);
        filter = new ConsoleFilter(this);
        ((AbstractDocument) getDocument()).setDocumentFilter(filter);
        // start text and edit location
        setText("Jython Interactive Console\r\n>>> ");
        // editStart = getText().length();
        getCaret().setDot(editStart);
       //((JLineConsole)interp).interact();
    }

    private void fireCommandExecuted(String commands) {
        for(ConsoleListener listener : consoleListeners) {
            listener.onExecution(commands);
        }
    }
        
    private void fireKeyPressed(KeyEvent e) {
        for(ConsoleListener listener : consoleListeners) {
            listener.onKeyPressed(e);
        }
    }
    
    public void registerConsoleListener(ConsoleListener listener) {
        consoleListeners.add(listener);
    }
    
    public void executeFile(String filename) {
        try {
            interp.execfile(filename);
        } catch (PyException e) {
            e.printStackTrace();
        }                    
  
        fireCommandExecuted(getFileContents(filename));
        fireCommandExecuted("Finished executing script file " + filename);

    }
    
    public void executeCommand(String commands) {

        StringBuilder text;

        moreCommand += commands;

        try {           
            // If command is not finished (i.e. loop)
            if(interp.runsource(moreCommand)) {
                text = new StringBuilder(getText()); 
                text.append(ps2);
                moreCommand += "\n";
            } else {
                text = new StringBuilder(getText()); 
                text.append(ps1);
                moreCommand = "";
            }

            setText(text.toString());
        } catch (PyException e) {
            e.printStackTrace();
        }
        fireCommandExecuted(commands);
    }
            
    private String getFileContents(String filename) {
        StringBuilder contents = new StringBuilder();

        File aFile = new File(filename);
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
        		/*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ErrorDialog.displayExceptionDialog(ex);
        }

        return contents.toString();
    }
        
    @Override
    public void setText(String text) {
        setText(text, true);
    }

    /**
     * @param text
     * @param updateEditStart
     */
    public void setText(String text, boolean updateEditStart) {
        filter.useFilters = false;
        super.setText(text);
        filter.useFilters = true;
        if (updateEditStart) {
            editStart = text.length();
        }
        getCaret().setDot(text.length());
    }

    private class ConsoleFilter extends DocumentFilter {

        private JConsole console;
        public boolean useFilters;

        public ConsoleFilter(JConsole console) {
            this.console = console;
            useFilters = true;
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (useFilters) {
                // determine if we can insert
                if (console.getSelectionStart() >= console.editStart) {
                    // can insert
                    fb.insertString(offset, string, attr);
                } else {
                    // insert at the end of the document
                    fb.insertString(console.getText().length(), string, attr);
                    // move cursor to the end
                    console.getCaret().setDot(console.getText().length());
                    // console.setSelectionEnd(console.getText().length());
                    // console.setSelectionStart(console.getText().length());
                }
            } else {
                fb.insertString(offset, string, attr);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (useFilters) {
                // determine if we can replace
                if (console.getSelectionStart() >= console.editStart) {
                    // can replace
                    fb.replace(offset, length, text, attrs);
                } else {
                    // insert at end
                    fb.insertString(console.getText().length(), text, attrs);
                    // move cursor to the end
                    console.getCaret().setDot(console.getText().length());
                    // console.setSelectionEnd(console.getText().length());
                    // console.setSelectionStart(console.getText().length());
                }
            } else {
                fb.replace(offset, length, text, attrs);
            }
        }

        @Override
        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
            if (useFilters) {
                if (offset > console.editStart) {
                    // can remove
                    fb.remove(offset, length);
                } else {
                    // only remove the portion that's editable
                    fb.remove(console.editStart, length - (console.editStart - offset));
                    // move selection to the start of the editable section
                    console.getCaret().setDot(console.editStart);
                    // console.setSelectionStart(console.editStart);
                    // console.setSelectionEnd(console.editStart);
                }
            } else {
                fb.remove(offset, length);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // Ctrl+Z pressed. Abort current sequence and get
            StringBuilder text = new StringBuilder(getText()); 
            text.append(" ESC.");
            text.append(System.getProperty("line.separator"));
            text.append(ps1);
            setText(text.toString(), true);
            moreCommand = "";
            e.consume();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP && !e.isShiftDown() && !e.isAltDown()) {
            // prev in history
            StringBuilder temp = new StringBuilder(getText());
            // remove the current command
            temp.delete(editStart, temp.length());
            temp.append(history.getPrevCommand());
            setText(temp.toString(), false);
            e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && !e.isShiftDown() && !e.isAltDown()) {
            // next in history
            StringBuilder temp = new StringBuilder(getText());
            // remove the current command
            temp.delete(editStart, temp.length());
            temp.append(history.getNextCommand());
            setText(temp.toString(), false);
            e.consume();
        }        
        else if (e.isControlDown()) {
            if (e.getKeyCode() == KeyEvent.VK_A && !e.isShiftDown() && !e.isAltDown()) {
                // handle select all
                // if selection start is in the editable region, try to select
                // only editable text
                if (getSelectionStart() >= editStart) {
                    // however, if we already have the editable region selected,
                    // default select all
                    if (getSelectionStart() != editStart || getSelectionEnd() != this.getText().length()) {
                        setSelectionStart(editStart);
                        setSelectionEnd(this.getText().length());
                        // already handled, don't use default handler
                        e.consume();
                    }
                }
            } 
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // handle script execution
            if (!e.isShiftDown() && !e.isAltDown()) {
                if (running) {
                    // we need to put text into the input stream
                    StringBuilder text = new StringBuilder(this.getText());
                    text.append(System.getProperty("line.separator"));
                    String command = text.substring(editStart);
                    setText(text.toString());
                    ((ConsoleInputStream) in).addText(command);
                } else {
                    // run the engine
                    StringBuilder text = new StringBuilder(this.getText());
                    String command = text.substring(editStart);
                    text.append(System.getProperty("line.separator"));
                    setText(text.toString());
                    // add to the history
                    history.add(command);
                    // run on a separate thread
                    pythonThread = new Thread(new PythonRunner(command));
                    // so this thread can't hang JVM shutdown
                    pythonThread.setDaemon(true);
                    pythonThread.start();
                }
                e.consume();
            } else if (!e.isAltDown()) {
                // shift+enter
                StringBuilder text = new StringBuilder(this.getText());
                if (getSelectedText() != null) {
                    // replace text
                    text.delete(getSelectionStart(), getSelectionEnd());
                }
                text.insert(getSelectionStart(), System.getProperty("line.separator"));
                setText(text.toString(), false);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_HOME) {
            int selectStart = getSelectionStart();
            if (selectStart > editStart) {
                // we're after edit start, see if we're on the same line as edit
                // start
                for (int i = editStart; i < selectStart; i++) {
                    if (this.getText().charAt(i) == '\n') {
                        // not on the same line
                        // use default handle
                        return;
                    }
                }
                if (e.isShiftDown()) {
                    // move to edit start
                    getCaret().moveDot(editStart);
                } else {
                    // move select end, too
                    getCaret().setDot(editStart);
                }
                e.consume();
            }
        }
    }

    private class PythonRunner implements Runnable {

        private String commands;

        public PythonRunner(String commands) {
            this.commands = commands;
        }

        @Override
        public void run() {
            running = true;
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    executeCommand(commands);
                }
            });

            running = false;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void finalize() {
        if (running) {
            // I know it's depracated, but since this object is being destroyed,
            // this thread should go, too
            pythonThread.interrupt();
            pythonThread = null;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // don't need to use this for anything
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // don't need to use this for anything
    }
}