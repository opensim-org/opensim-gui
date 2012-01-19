package org.opensim.console;

import java.io.IOException;
import java.io.Writer;


/**
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
