package de.uk.java.feader.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.ITokenizer;
import de.uk.java.feader.utils.Tokenizer;

public class SearchEngine implements ISearchEngine {

	private Tokenizer tokenizer;
	
	/**
	 * eine Map, die verwendet wird, um jeden vom RSS-Reader geladenen Eintrag zu speichern
	 */
	static Map<Integer,Entry> EntryMap = new HashMap<Integer,Entry>();
	
	
	/**
	 * eine HashMap, die als invertierter Index verwendet wird und die tokenisierten Wörter mit Zahlen kombiniert, die auf die Einträge verweisen, die sie enthalten
	 */
	static HashMap<String, HashSet<Integer>> invertedIndex = new HashMap<String, HashSet<Integer>>();
	
	
	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public List<Entry> search(String searchTerm) {
		
		List<Entry> found = new ArrayList<Entry>();
		
		String lowerCaseSearchTerm = searchTerm.toLowerCase(); // refaktorisierung des Suchbegriffs in Kleinschreibung
		
		if (lowerCaseSearchTerm.contains("*")) { // sollte der Suchbegriff eine Wildcard enthalten beginnt hier die Wildcarsuche
			
			found = SearchModes.wildcard(lowerCaseSearchTerm); // ruft die Methode wildcard auf und übergibt den kleingeschriebenen Suchbegriff
			return found;
			
		}
		else {
		
			found = SearchModes.basic(lowerCaseSearchTerm); // ruft die Methode basic auf und übergibt den kleingeschriebenen Suchbegriff wenn keine Wildcard im Suchbegriff enthalten ist
			return found;
			
		}
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public void createSearchIndex(List<Feed> feeds) {
		
		int counter = 0; // zählt die Menge der Einträge in der Liste der geladenen entries
		
		java.util.Iterator<Feed> feedIterator = feeds.iterator(); 
		while (feedIterator.hasNext()) { // iteriert über die Feeds
			Feed Feed = feedIterator.next();
			
			java.util.Iterator<Entry> entryIterator = Feed.getEntries().iterator();
			
			while (entryIterator.hasNext()) {	// iteriert über die in den Feeds enthaltenen Einträge 			
				
				Entry Entry = entryIterator.next();
				
				if (EntryMap.containsValue(Entry)) { // wenn ein Entry bereits in der Liste enthalten ist wird dieser übersprungen um duplikate zu verhindern
					continue;
				}
				
				EntryMap.put(counter, Entry); // fügt den Eintrag mit dem counter als ID der Map hinzu
				
				List<String> tokenized = tokenizer.tokenize(Entry.html()); // der gesamte Eintrag inklusive Überschrift wird tokenisiert
				
				java.util.Iterator<String> stringIterator = tokenized.iterator();
				
				while (stringIterator.hasNext()) { // iteriert über die einzelnen Worte jedes Eintrags (der der EntryMap hinzugefügt wurde)
					
					String word = stringIterator.next().toLowerCase(); // konvertiert die Worte in Kleinschreibung
					if (!invertedIndex.containsKey(word)) { // wenn ein Wort noch nicht im InvertedIndex steht wird dieses hinzugefügt und eine neues HashSet für die ID's angelegt
						invertedIndex.put(word, new HashSet<Integer>());
					}
					invertedIndex.get(word).add(counter); // die ID des aktuellen Eintrags wird dem jeweiligen Wort hinzugefügt
				}
				counter++; // nach jedem Eintrag wird der Counter um 1 erhöht um jedem Eintrag eine individuelle ID zuweisen zu können
			}
		}
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public void addToSearchIndex(Feed feed) {
		
		java.util.Iterator<Entry> entryIterator = feed.getEntries().iterator();
		
		while (entryIterator.hasNext()) { // iteriert über die Einträge des Feeds
			
			Entry Entry = entryIterator.next(); 
			
			int entryPoint = EntryMap.size(); // speichert die aktuell nächste, ungenutzte, ID in einer Variable
			
			if (EntryMap.containsValue(Entry)) { // wenn ein Entry bereits in der Liste enthalten ist wird dieser übersprungen um duplikate zu verhindern
				continue;
			}
			
			EntryMap.put(entryPoint, Entry); // fügt den Eintrag mit dem entryPoint als ID der Map hinzu
			
			List<String> tokenized = tokenizer.tokenize(Entry.html()); // der gesamte Eintrag inklusive Überschrift wird tokenisiert
			
			java.util.Iterator<String> stringIterator = tokenized.iterator();
			
			while (stringIterator.hasNext()) { // iteriert über die einzelnen Worte jedes Eintrags (der der EntryMap hinzugefügt wurde)
				
				String word = stringIterator.next().toLowerCase(); // konvertiert die Worte in Kleinschreibung
				if (!invertedIndex.containsKey(word)) { // wenn ein Wort noch nicht im InvertedIndex steht wird dieses hinzugefügt und eine neues HashSet für die ID's angelegt
					invertedIndex.put(word, new HashSet<Integer>());
				}
				invertedIndex.get(word).add(entryPoint); // die ID des aktuellen Eintrags wird dem jeweiligen Wort hinzugefügt

			}
		}
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public void setTokenizer(ITokenizer tokenizer) {
		
		this.tokenizer = (Tokenizer) tokenizer;
		
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @throws FileNotFoundException
	 */
	@Override
	public void saveSearchIndex(File indexFile) {
		
		String index = convertWithGuava(invertedIndex);
		boolean repeat = true; // repeat wird erst false, und stoppt damit die while-Schleife, wenn die Datei indexFile erfolgreich mit dem konvertierten Index beschrieben wurde
		while(repeat) {
			try {
				PrintWriter out = new PrintWriter(indexFile); // erstellt einen neuen PrintWriter, der den String index in die Datei indexFile schreibt
				out.print(index);
				out.close();
				repeat = false;
			} catch (FileNotFoundException e) {
				indexFile = new File("feader.index");	// sollte die Datei nicht gefunden werden wir eine leere Datei angelegt und die while-Schleife wiederholt		
			}
		}
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@Override
	public void loadSearchIndex(File indexFile) {
		
		try {
			FileInputStream fis = new FileInputStream(indexFile);// liest die Datei indexFile in die Objekt-Variable fis
			ObjectInputStream ois = new ObjectInputStream(fis); // konvertiert den FileInputStream zu einem ObjectInputStream; Macht den nächsten Schritt möglich
			String index = (String) ois.readObject(); // füllt den String index mit den Einträgen aus der Datei
			ois.close();
			convertBackWithGuava(index); //konvertiert den Index in eine HashMap
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();	
		}
		
	}
	
	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public boolean indexExists() {
		
		if (invertedIndex.isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Converts the Inverted Index to a String using Guava
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @param map
	 * @return String version of the invertedIndex
	 */
	public String convertWithGuava(HashMap<String, HashSet<Integer>> map) {
		return Joiner.on(";").withKeyValueSeparator("=").join(map); // fügt die Mapeinträge an den Semikolons zusammen
	}

	/**
	 * Converts the String back to an Inverted Index in form of a HashMap using Guava
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @param mapAsString
	 * @return Inverted Index in form of a HashMap
	 */
	public HashMap<String,HashSet<Integer>> convertBackWithGuava(String mapAsString) {
		
		Map<String, String> temp = Splitter.on(";").withKeyValueSeparator("=").split(mapAsString); // trennt jeden Mapeintrag am Gleichzeichen und separiert dessen Werte durch ein Semikolon und speichert dies in einer Map
		HashMap<String, HashSet<Integer>> newInvertedIndex = new HashMap<String, HashSet<Integer>>(); // erstellt eine leere HashMap welche im folenden mit dem invertedIndex gefüllt wird
		
		// im folgenden wird die Map von <String, String> zurück zu <String, HashSet<Integer>> konvertiert
		for (Map.Entry<String, String> entry : temp.entrySet()) {
			HashSet<Integer> temp2 = new HashSet<Integer>();
			temp2.add(Integer.parseInt(entry.getValue())); // die ID wird zurück in einen Integer geparsed und dem HashSet<Integer> hinzugefügt
	        newInvertedIndex.put(entry.getKey(), temp2); // der InvertedIndex wird als HashMap aus dem jeweiligen Wort des Eintrags und den dazugehörigen ID's zusammengesetzt
	    }
		
		return newInvertedIndex;
	}
	
}