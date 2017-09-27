/* -------------------------------------------------------------------------- *
 * OpenSim: CommandHistory.java                                               *
 * -------------------------------------------------------------------------- *
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
package org.opensim.console;

/**
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
