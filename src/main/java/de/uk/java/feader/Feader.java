package de.uk.java.feader;

import de.uk.java.feader.gui.FeaderGUI;
import de.uk.java.feader.io.IAppIO;
import de.uk.java.feader.search.ISearchEngine;
import de.uk.java.feader.utils.ITokenizer;
import de.uk.java.feader.utils.Tokenizer;


public class Feader {
	
	
	/*
	 * main method starts the application
	 */
	public static void main(String[] args) {
		IAppIO io = getAppIO();
		ISearchEngine search = getSearchEngine();
		ITokenizer tokenizer = getTokenizer();
		new FeaderGUI(io, search, tokenizer);
	}
	
	/**
	 * Returns a new IAppIO instance
	 * @return the new IAppIO instance
	 */
	public static IAppIO getAppIO() {
		// HIER INSTANZ DER EIGENEN IMPLEMENTATION ZURÜCKGEBEN !!!
		// return new AppIO();
		return null;
	}
	
	/**
	 * Returns a new ISearchEngine instance
	 * @return the new ISearchEngine instance
	 */
	public static ISearchEngine getSearchEngine() {
		// HIER INSTANZ DER EIGENEN IMPLEMENTATION ZURÜCKGEBEN !!!
		// return new SearchEngine();
		return null;
	}
	
	/**
	 * Returns a new ITokenizer instance
	 * @return the new ITokenizer instance
	 */
	public static ITokenizer getTokenizer() {
		// HIER INSTANZ DER EIGENEN IMPLEMENTATION ZURÜCKGEBEN !!!
		// return new Tokenizer();
		
		return new Tokenizer();
	}

}
