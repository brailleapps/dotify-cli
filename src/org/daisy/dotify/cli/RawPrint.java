/*
 * Braille Utils (C) 2010-2011 Daisy Consortium 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.dotify.cli;

import java.io.File;
import java.io.FileNotFoundException;

import javax.print.PrintException;

import org.daisy.braille.utils.pef.PrinterDevice;
import org.daisy.streamline.cli.ExitCode;

/**
 * Provides a command line UI for sending a file straight to a
 * printer.
 * @author Joel Håkansson
 */
public class RawPrint {
	
	/**
	 * Executes the application.
	 * @param args program arguments
	 * @throws FileNotFoundException if printing fails
	 * @throws PrintException if printing fails
	 */
	public static void main(String[] args) throws FileNotFoundException, PrintException {
		if (args.length != 2) {
			ExitCode.MISSING_ARGUMENT.exitSystem("Expected two arguments: device_name path_to_file");
		}
		PrinterDevice bd = new PrinterDevice(args[0], true);
		bd.transmit(new File(args[1]));
	}

}
