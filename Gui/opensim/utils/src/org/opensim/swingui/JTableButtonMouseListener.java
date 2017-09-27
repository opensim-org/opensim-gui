/* -------------------------------------------------------------------------- *
 * OpenSim: JTableButtonMouseListener.java                                    *
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

package org.opensim.swingui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

// NOTE: Apparently even using this listener I can't get JButton actions to activate -- i.e. if you
// create a JButton using an AbstractAction, typically pressing that button would activate that action,
// but if you put the button in a table and use this listener, that doesn't seem to work despite the fact
// that this listener forwards mouse events to the button...   I think the reason is that the JButton's
// position on the screen is not really defined since the table's cell renderer stamps the same button
// in multiple places...  So the button's "pressed" state never gets properly updated.
public class JTableButtonMouseListener implements MouseListener {

  private JTable table;

  private void forwardEventToButton(MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel();
    int column = columnModel.getColumnIndexAtX(e.getX());
    int row    = e.getY() / table.getRowHeight();
    Object value;
    JButton button;

    if(row >= table.getRowCount() || row < 0 || column >= table.getColumnCount() || column < 0)
      return;

    value = table.getValueAt(row, column);

    if(!(value instanceof JButton)) return;

    button = (JButton)value;

    MouseEvent buttonEvent = (MouseEvent)SwingUtilities.convertMouseEvent(table, e, button);
    button.dispatchEvent(buttonEvent);
    // This is necessary so that when a button is pressed and released
    // it gets rendered properly.  Otherwise, the button may still appear
    // pressed down when it has been released.
    table.repaint();
  }

  public JTableButtonMouseListener(JTable table) { this.table = table; }

  public void mouseClicked(MouseEvent e) { forwardEventToButton(e); }
  public void mouseEntered(MouseEvent e) { forwardEventToButton(e); }
  public void mouseExited(MouseEvent e) { forwardEventToButton(e); }
  public void mousePressed(MouseEvent e) { forwardEventToButton(e); }
  public void mouseReleased(MouseEvent e) { forwardEventToButton(e); }
}
