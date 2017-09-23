/* -------------------------------------------------------------------------- *
 * OpenSim: ConsoleInputStream.java                                           *
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
import java.io.Reader;


/**
 *
 * 
 * @author Ayman based on JavaForum code
 */
public class ConsoleInputStream extends Reader
{
	private JConsole		console;
	private StringBuilder	stream;

	/**
	 * @param console
	 */
	public ConsoleInputStream(JConsole console)
	{
		this.console = console;
		stream = new StringBuilder();
	}

	/**
	 * @param text
	 */
	public void addText(String text)
	{
		synchronized (stream)
		{
			stream.append(text);
		}
	}

	@Override
	public synchronized void close() throws IOException
	{
		console = null;
		stream = null;
	}

	@Override
	public int read(char[] buf, int off, int len) throws IOException
	{
		int count = 0;
		boolean doneReading = false;
		for (int i = off; i < off + len && !doneReading; i++)
		{
			// determine if we have a character we can read
			// we need the lock for stream
			int length = 0;
			while (length == 0)
			{
				// sleep this thread until there is something to read
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				synchronized (stream)
				{
					length = stream.length();
				}
			}
			synchronized (stream)
			{
				// get the character
				buf[i] = stream.charAt(0);
				// delete it from the buffer
				stream.deleteCharAt(0);
				count++;
				if (buf[i] == '\n')
				{
					doneReading = true;
				}
			}
		}
		return count;
	}
}
