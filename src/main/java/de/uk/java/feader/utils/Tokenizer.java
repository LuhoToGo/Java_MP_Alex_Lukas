package de.uk.java.feader.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;

/**
 * 
 * @author Alessandro Marini & Lukas Hoffmann
 * 
 * Die Klasse "Tokenizer" ist die Implementierung des Interfaces "ITokenzier".
 * Alle benötigten Methoden wurden innerhalb der Klasse definiert.
 *
 */
public class Tokenizer implements ITokenizer{
	
	@Override
	/**
	 * @param Vorgegebener Text der in Tokens unterteilt werden soll.
	 * 
	 * Der String "token" bewahrt das nächste Wort des Tokenizers auf.
	 * Dort kann das Wort mit Hilfe von Jsoup.parse() von HTML-Tags befreit werden.
	 * Daraufhin entfernt .replaceAll() alle uebrigen Sonderzeichen und Zahlen.
	 * Im letzten Schritt wird ueberprüft, ob der String leer ist.
	 * Ist dies nicht der Fall, wird das Wort in die Array-Liste hinzugefuegt.
	 * 
	 * @return Eine Array-Liste die den reinen Text in Form der einzelnen Worte beinhaltet.
	 * 
	 */
	public List<String> tokenize(String text) {
		
		List<String> tokens = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			
			token = Jsoup.parse(token).text();
			
			token = Jsoup.parse(token).text();
			
			token = token.replaceAll("[^öÖäÄüÜßa-zA-Z]","");
			
			if(token.isEmpty()) {
				continue;
			}
			else {
				tokens.add(token);
			}
		
		}
		return tokens;
	}

}