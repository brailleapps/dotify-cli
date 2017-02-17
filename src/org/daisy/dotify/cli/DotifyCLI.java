package org.daisy.dotify.cli;

import org.daisy.dotify.SystemProperties;

public class DotifyCLI extends BasicUI {

	public DotifyCLI(String[] args) {
		super(args);
		putCommand("convert", "Converts a document into braille with Dotify", Main.class);
		putCommand("translate", "Translates text on system in to braille on system out", TranslateCLI.class);
	}
	
	@Override
	public String getName() {
		return "dotify";
	}
	
	@Override
	public String getDescription() {
		return "Provides translation and formatting of documents into braille as well as tools for managing PEF-files.";
	}

	@Override
	public String getVersion() {
		return SystemProperties.SYSTEM_RELEASE;
	}
	
	@Override
	public String getBuildIdentifier() {
		return SystemProperties.SYSTEM_BUILD;
	}
	
	public static void main(String[] args) throws Exception {
		DotifyCLI ui = new DotifyCLI(args);
		ui.run();
	}

}
