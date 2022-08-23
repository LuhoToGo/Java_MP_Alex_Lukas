package de.uk.java.feader.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.uk.java.feader.data.Entry;

public class SearchModes extends SearchEngine{
	
	static List<Entry> foundEntries = new ArrayList<Entry>();
	
	public static List<Entry> wildcard(String lowerCaseSearchTerm) {
		
		foundEntries.clear();
		
		if (lowerCaseSearchTerm.endsWith("*")) {
			String newSearchTerm = lowerCaseSearchTerm.replace("*","");
			
			for (HashMap.Entry<String, HashSet<Integer>> entry : invertedIndex.entrySet()) {
			    String key = entry.getKey();
			    HashSet<Integer> value = entry.getValue();
			    
			    if (key.startsWith(newSearchTerm)) {
					
					java.util.Iterator<Integer> iterator = value.iterator();
					
					while (iterator.hasNext()) {
						int iterable = iterator.next();
						if (!foundEntries.contains(EntryMap.get(iterable))) {
							foundEntries.add(EntryMap.get(iterable));
						}
					}
			    }
			}
			return foundEntries;
		}
		else if (lowerCaseSearchTerm.startsWith("*")) {
			String newSearchTerm = lowerCaseSearchTerm.replace("*","");
			
			for (HashMap.Entry<String, HashSet<Integer>> entry : invertedIndex.entrySet()) {
			    String key = entry.getKey();
			    HashSet<Integer> value = entry.getValue();
			    
			    if (key.endsWith(newSearchTerm)) {
					
					java.util.Iterator<Integer> iterator = value.iterator();
					
					while (iterator.hasNext()) {
						int iterable = iterator.next();
						if (!foundEntries.contains(EntryMap.get(iterable))) {
							foundEntries.add(EntryMap.get(iterable));
						}
					}
			    }
			}
			return foundEntries;
		}
		else {
			String[] newSearchTerm = lowerCaseSearchTerm.split("\\*");
			for (HashMap.Entry<String, HashSet<Integer>> entry : invertedIndex.entrySet()) {
			    String key = entry.getKey();
			    HashSet<Integer> value = entry.getValue();
			    
			    if (key.startsWith(newSearchTerm[0]) && key.endsWith(newSearchTerm[1])) {
					
					java.util.Iterator<Integer> iterator = value.iterator();
					
					while (iterator.hasNext()) {
						int iterable = iterator.next();
						if (!foundEntries.contains(EntryMap.get(iterable))) {
							foundEntries.add(EntryMap.get(iterable));
						}
					}
			    }
			}
			return foundEntries;
		}
	}
	
	public static List<Entry> basic(String lowerCaseSearchTerm) {
		
		foundEntries.clear();
		
		if (invertedIndex.containsKey(lowerCaseSearchTerm)) {
			
			HashSet<Integer> x = invertedIndex.get(lowerCaseSearchTerm);
		
			java.util.Iterator<Integer> iterator = x.iterator();
		
			while (iterator.hasNext()) {
				int iterable = iterator.next();
				if (!foundEntries.contains(EntryMap.get(iterable))) {
					foundEntries.add(EntryMap.get(iterable));
				}
			}	
		}
		
		return foundEntries;
		
	}

}
