package de.uk.java.feader.utils;

public class ConsoleBanner {
	
	private static final String CONSOLE_BANNER = 
			"    ___       ___       ___       ___       ___       ___   \n" + 
			"   /\\  \\     /\\  \\     /\\  \\     /\\  \\     /\\  \\     /\\  \\  \n" + 
			"  /::\\  \\   /::\\  \\   /::\\  \\   /::\\  \\   /::\\  \\   /::\\  \\ \n" + 
			" /::\\:\\__\\ /::\\:\\__\\ /::\\:\\__\\ /:/\\:\\__\\ /::\\:\\__\\ /::\\:\\__\\\n" + 
			" \\/\\:\\/__/ \\:\\:\\/  / \\/\\::/  / \\:\\/:/  / \\:\\:\\/  / \\;:::/  /\n" + 
			"    \\/__/   \\:\\/  /    /:/  /   \\::/  /   \\:\\/  /   |:\\/__/ \n" + 
			"             \\/__/     \\/__/     \\/__/     \\/__/     \\|__|  \n";
	
	/**
	 * Prints the Feader console banner
	 */
	public static final void print() {
		System.out.println(CONSOLE_BANNER);
		System.out.println(">> A Reader for RSS/Atom Feeds, built by the "
				+ "\"Softwaretechnologie II: Java\" class");
		System.out.println(">> Starting log output ...\n");
	}

}
