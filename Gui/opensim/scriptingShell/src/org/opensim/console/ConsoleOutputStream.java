/* -------------------------------------------------------------------------- *
 * OpenSim: ConsoleOutputStream.java                                          *
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

import java.io.IOException;
import java.io.Writer;


/**
 *
 * Data written to this will be displayed into the console
 * 
 * @author Ayman based on JavaForum code
 */
public class ConsoleOutputStream extends Writer
{
	private JConsole	console;

	/**
	 * @param console
	 */
	public ConsoleOutputStream(JConsole console)
	{
		this.console = console;
	}

	@Override
	public synchronized void close() throws IOException
	{
		console = null;
	}

	@Override
	public void flush() throws IOException
	{
		// no extra flushing needed
	}

	@Override
	public synchronized void write(char[] cbuf, int off, int len) throws IOException
	{
		StringBuilder temp = new StringBuilder(console.getText());
		for (int i = off; i < off + len; i++)
		{
			temp.append(cbuf[i]);
		}
		console.setText(temp.toString());
	}
}
