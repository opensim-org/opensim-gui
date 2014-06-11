package org.opensim.console;

import java.io.IOException;
import java.io.Reader;


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
