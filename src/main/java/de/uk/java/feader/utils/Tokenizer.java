package de.uk.java.feader.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;


public class Tokenizer implements ITokenizer{
	
	
	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public List<String> tokenize(String text) {
		
		List<String> tokens = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(text); //initialisieren eines StringTokenizers mit dem �bergebenen Text; Teilt den Text in Worte/Tokens auf
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			
			token = Jsoup.parse(token).text(); //Jsoup erkennt und entfernt HTML-Tags
			
			token = token.replaceAll("[^öÖäÄüÜßa-zA-Z]",""); //Entfernen von allen Zahlen und Symbolen au�er A-Z und Umlauten
			
			if(token.isEmpty()) { //sollte ein HTML-Tag oder sonstiges komplett gel�scht worden sein, wird mit dem n�chsten Token weitergemacht
				continue;
			}
			else {
				tokens.add(token); //Hinzuf�gen des einzelnen Token zur StringList aller Tokens
			}
		
		}
		return tokens;
	}

}