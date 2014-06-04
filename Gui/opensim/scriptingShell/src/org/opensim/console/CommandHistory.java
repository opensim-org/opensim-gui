package org.opensim.console;

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
 * CommandHistory to support retrieving old commands
 * 
 * @author Ayman based on JavaForum code
 */
public class CommandHistory {

    private class Node {

        public String command;
        public Node next;
        public Node prev;

        public Node(String command) {
            this.command = command;
            next = null;
            prev = null;
        }
    }
    private int length;
    /**
     * The top command with an empty string
     */
    private Node top;
    private Node current;
    private int capacity;

    /**
     * Creates a CommandHistory with the default capacity of 64
     */
    public CommandHistory() {
        this(64);
    }

    /**
     * Creates a CommandHistory with a specified capacity
     * 
     * @param capacity
     */
    public CommandHistory(int capacity) {
        top = new Node("");
        current = top;
        top.next = top;
        top.prev = top;
        length = 1;
        this.capacity = capacity;
    }

    /**
     * @return
     */
    public String getPrevCommand() {
        current = current.prev;
        return current.command;
    }

    /**
     * @return
     */
    public String getNextCommand() {
        current = current.next;
        return current.command;
    }

    /**
     * Adds a command to this command history manager. Resets the command
     * counter for which command to select next/prev.<br>
     * If the number of remembered commands exceeds the capacity, the oldest
     * item is removed.<br>
     * Duplicate checking only for most recent item.
     * 
     * @param command
     */
    public void add(String command) {
        // move back to the top
        current = top;
        // see if we even need to insert
        if (top.prev.command.equals(command)) {
            // don't insert
            return;
        }
        // insert before top.next
        Node temp = new Node(command);
        Node oldPrev = top.prev;
        temp.prev = oldPrev;
        oldPrev.next = temp;
        temp.next = top;
        top.prev = temp;
        length++;
        if (length > capacity) {
            // delete oldest command
            Node newNext = top.next.next;
            top.next = newNext;
            newNext.prev = top;
        }
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }
}
