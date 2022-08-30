package de.uk.java.feader.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.uk.java.feader.data.Entry;


/**
 * Feeds the SearchEngine the different SearchModes.
 * Basic Search is a regular full term search and wildcard allows the use of the "*" symbol as a wildcard
 * 
 * @author Alessandro Marini & Lukas Hoffmann
 *
 */
public class SearchModes extends SearchEngine{
	
	private static List<Entry> foundEntries = new ArrayList<Entry>();
	
	/**
	 * Allows the use of the "*" symbol as a wildcard. This is possible in three different ways:
	 * The search term can end with a wildcard, start with it or contain it in between regular search terms.
	 * The Method checks which case applies and then returns the entries in question.
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @param lowerCaseSearchTerm
	 * @return List of found Entries
	 */
	public static List<Entry> wildcard(String lowerCaseSearchTerm) {
		
		foundEntries.clear(); // leert die Liste bevor sie neu gefüllt werden kann
		
		if (lowerCaseSearchTerm.endsWith("*")) { // code der ausgeführt wird wenn sich die Wildcard am Ende des Suchbegriffs befindet
			String newSearchTerm = lowerCaseSearchTerm.replace("*",""); //entfernt die Wildcard aus dem Suchbegriff um eine Suche im InvertedIndex möglich zu machen
			
			for (HashMap.Entry<String, HashSet<Integer>> entry : invertedIndex.entrySet()) { // iteriert über die Einträge im InvertedIndex
			    String key = entry.getKey(); // speichert das Wort in einem String
			    HashSet<Integer> value = entry.getValue(); // speichert die zugehörigen ID's in einem HashSet<Integer>
			    
			    if (key.startsWith(newSearchTerm)) { // code der ausgeführt wird wenn das Wort mit dem Suchbegriff beginnt
					
					java.util.Iterator<Integer> iterator = value.iterator();
					
					while (iterator.hasNext()) { // iteriert über das HashSet
						int iterable = iterator.next();
						if (!foundEntries.contains(EntryMap.get(iterable))) { // wenn die Ausgabeliste den aktuellen Eintrag noch nicht enthält wird dieser hinzugefügt
							foundEntries.add(EntryMap.get(iterable));
						}
					}
			    }
			}
			return foundEntries;
		}
		else if (lowerCaseSearchTerm.startsWith("*")) { // code der ausgeführt wird wenn sich die Wildcard am Anfang des Suchbegriffs befindet
			String newSearchTerm = lowerCaseSearchTerm.replace("*",""); //entfernt die Wildcard aus dem Suchbegriff um eine Suche im InvertedIndex möglich zu machen
			
			for (HashMap.Entry<String, HashSet<Integer>> entry : invertedIndex.entrySet()) { // iteriert über die Einträge im InvertedIndex
			    String key = entry.getKey(); // speichert das Wort in einem String
			    HashSet<Integer> value = entry.getValue(); // speichert die zugehörigen ID's in einem HashSet<Integer>
			    
			    if (key.endsWith(newSearchTerm)) { // code der ausgeführt wird wenn das Wort mit dem Suchbegriff endet
					
					java.util.Iterator<Integer> iterator = value.iterator();
					
					while (iterator.hasNext()) { // iteriert über das HashSet
						int iterable = iterator.next();
						if (!foundEntries.contains(EntryMap.get(iterable))) { // wenn die Ausgabeliste den aktuellen Eintrag noch nicht enthält wird dieser hinzugefügt
							foundEntries.add(EntryMap.get(iterable));
						}
					}
			    }
			}
			return foundEntries;
		}
		else { // code der ausgeführt wird wenn sich die Wildcard in der Mitte des Suchbegriffs befindet
			String[] newSearchTerm = lowerCaseSearchTerm.split("\\*"); // teilt den Suchbegriff an der Wildcard in zwei Teile (dabei wird die Wildcard entfernt) und speichert diese in einem Array
			for (HashMap.Entry<String, HashSet<Integer>> entry : invertedIndex.entrySet()) { // iteriert über die Einträge im InvertedIndex
			    String key = entry.getKey(); // speichert das Wort in einem String
			    HashSet<Integer> value = entry.getValue(); // speichert die zugehörigen ID's in einem HashSet<Integer>
			    
			    if (key.startsWith(newSearchTerm[0]) && key.endsWith(newSearchTerm[1])) { // code der ausgeführt wird wenn das Wort mit den Teilen des im Array enthaltenen Begriffs anfängt und endet
					
					java.util.Iterator<Integer> iterator = value.iterator();
					
					while (iterator.hasNext()) { // iteriert über das HashSet
						int iterable = iterator.next();
						if (!foundEntries.contains(EntryMap.get(iterable))) { // wenn die Ausgabeliste den aktuellen Eintrag noch nicht enthält wird dieser hinzugefügt
							foundEntries.add(EntryMap.get(iterable));
						}
					}
			    }
			}
			return foundEntries;
		}
	}
	
	/**
	 * Checks if the SearchTerm is contained in the invertedIndex and returns the entries in question.
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @param lowerCaseSearchTerm
	 * @return List of found entries
	 */
	public static List<Entry> basic(String lowerCaseSearchTerm) {
		
		foundEntries.clear(); // leert die Liste bevor sie neu gefüllt werden kann
		
		if (invertedIndex.containsKey(lowerCaseSearchTerm)) { // code der ausgeführt wird wenn der Suchbegriff im InvertedIndex enthalten ist
			
			HashSet<Integer> x = invertedIndex.get(lowerCaseSearchTerm); // speichert die ID's des gefundenen Begriffs in einem HashSet
		
			java.util.Iterator<Integer> iterator = x.iterator();
		
			while (iterator.hasNext()) { // iteriert über das HashSet
				int iterable = iterator.next();
				if (!foundEntries.contains(EntryMap.get(iterable))) { // wenn die Ausgabeliste den aktuellen Eintrag noch nicht enthält wird dieser hinzugefügt
					foundEntries.add(EntryMap.get(iterable));
				}
			}	
		}
		
		return foundEntries;
		
	}

}
