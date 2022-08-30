package de.uk.java.feader;

import de.uk.java.feader.gui.FeaderGUI;
import de.uk.java.feader.io.AppIO;
import de.uk.java.feader.io.IAppIO;
import de.uk.java.feader.search.ISearchEngine;
import de.uk.java.feader.search.SearchEngine;
import de.uk.java.feader.utils.ITokenizer;
import de.uk.java.feader.utils.Tokenizer;

/*
 * Das gesamte Projekt wurde von beiden Gruppenmitgliedern (Lukas Hoffmann und Alessandro Marini) angefertigt.
 * Wir haben ausschließlich in Anwesenheit des anderen an der Modulabschlussprüfung gearbeitet. 
 * Dazu haben wir uns regelmäßig Online und mit CodeTogether zusammengesetzt.
 * Daher ist es uns nicht möglich einzelne Methoden einer einzelnen Person zuzuordnen.
 * Keine Methode wurde ohne die Mitarbeit beider Gruppenmitglieder erstellt oder bearbeitet.
 * Die Kommentare wurden jeweils individuell erstellt.
 * 
 * @author Alessandro Marini & Lukas Hoffmann
 */
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
		return new AppIO();
	}
	
	/**
	 * Returns a new ISearchEngine instance
	 * @return the new ISearchEngine instance
	 */
	public static ISearchEngine getSearchEngine() {
		return new SearchEngine();
	}
	
	/**
	 * Returns a new ITokenizer instance
	 * @return the new ITokenizer instance
	 */
	public static ITokenizer getTokenizer() {
		return new Tokenizer();
	}

}
