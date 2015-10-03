/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.threejs;

import java.io.StringWriter;

/**
 *
 * Based on code.google.com/p/json-simple/issues
 */
public class JSONWriter extends StringWriter
{
	final static String indentstring = "  "; //define as you wish
	final static String spaceaftercolon = " "; //use "" if you don't want space after colon

	private int indentlevel = 0;

	@Override
	public void write(int c)
	{
		char ch = (char) c;
		if (ch == '[' || ch == '{')
		{
			super.write(c);
			super.write('\n');
			indentlevel++;
			writeIndentation();
		} else if (ch == ',')
		{
			super.write(c);
			super.write('\n');
			writeIndentation();
		} else if (ch == ']' || ch == '}')
		{
			super.write('\n');
			indentlevel--;
			writeIndentation();
			super.write(c);
		} else if (ch == ':')
		{
			super.write(c);
			super.write(spaceaftercolon);
		} else
		{
			super.write(c);
		}

	}

	private void writeIndentation()
	{
		for (int i = 0; i < indentlevel; i++)
		{
			super.write(indentstring);
		}
	}
}
