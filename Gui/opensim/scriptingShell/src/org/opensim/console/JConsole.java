package org.opensim.console;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Reader;
import java.io.Writer;

import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.util.InteractiveInterpreter;

/**
 * Copyright (c)  2005-2012, Stanford University and Ayman Habib
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    InteractiveInterpreter engine;
    /**
     * The allowed variables and stuff to use
     */
    // private Bindings bindings;
    // ScriptContext context;
    private ConsoleFilter filter;
    private Thread pythonThread;

    /**
     * 
     */
    public JConsole() {
        // create streams that will link with this
        in = new ConsoleInputStream(this);
        // System.setIn(in);
        out = new ConsoleOutputStream(this);
        // System.setOut(new PrintStream(out));
        err = new ConsoleOutputStream(this);
        // setup the command history
        history = new CommandHistory();
        // setup the script engine
        engine = new InteractiveInterpreter();

        Py.getSystemState().setClassLoader(
                this.getClass().getClassLoader());
        engine.exec("import sys");
        engine.exec("import javax.swing as swing");
        engine.exec("import java.lang as lang");
        engine.exec("import org.opensim.modeling as modeling");
        engine.exec("import org.opensim.view.pub as view");
        engine.exec("import org.opensim.tracking as tools");
        engine.setOut(out);
        engine.setErr(err);
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
        if (e.isControlDown()) {
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
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && !e.isShiftDown() && !e.isAltDown()) {
                // next in history
                StringBuilder temp = new StringBuilder(getText());
                // remove the current command
                temp.delete(editStart, temp.length());
                temp.append(history.getNextCommand());
                setText(temp.toString(), false);
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_UP && !e.isShiftDown() && !e.isAltDown()) {
                // prev in history
                StringBuilder temp = new StringBuilder(getText());
                // remove the current command
                temp.delete(editStart, temp.length());
                temp.append(history.getPrevCommand());
                setText(temp.toString(), false);
                e.consume();
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
            try {
                engine.runsource(commands);
            } catch (PyException e) {
                // prints out the python error message to the console
                e.printStackTrace();
            }
            // engine.eval(commands, context);
            StringBuilder text = new StringBuilder(getText());
            text.append(">>> ");
            setText(text.toString());
            running = false;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void finalize() {
        if (running) {
            // I know it's depracated, but since this object is being destroyed,
            // this thread should go, too
            pythonThread.stop();
            pythonThread.destroy();
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
