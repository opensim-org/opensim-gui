/*
 * Copyright (c)  2005-2008, Stanford University
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
 */
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
