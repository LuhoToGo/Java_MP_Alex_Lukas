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
	 * A Map used to store every entry loaded by the RSS Reader
	 */
	static Map<Integer,Entry> EntryMap = new HashMap<Integer,Entry>();
	
	
	/**
	 * A HashMap used as an Inverted Index, combining tokenized words with Numbers pointing to the entries containing them
	 */
	static HashMap<String, HashSet<Integer>> invertedIndex = new HashMap<String, HashSet<Integer>>();
	
	
	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public List<Entry> search(String searchTerm) {
		
		List<Entry> found = new ArrayList<Entry>();
		
		String lowerCaseSearchTerm = searchTerm.toLowerCase();
		
		if (lowerCaseSearchTerm.contains("*")) {
			
			found = SearchModes.wildcard(lowerCaseSearchTerm);
			return found;
			
		}
		else {
		
			found = SearchModes.basic(lowerCaseSearchTerm);
			return found;
			
		}
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public void createSearchIndex(List<Feed> feeds) {
		
		int counter = 0;
		
		java.util.Iterator<Feed> feedIterator = feeds.iterator();
		while (feedIterator.hasNext()) {
			Feed Feed = feedIterator.next();
			
			java.util.Iterator<Entry> entryIterator = Feed.getEntries().iterator();
			
			while (entryIterator.hasNext()) {				
				
				Entry Entry = entryIterator.next();
				
				if (EntryMap.containsValue(Entry)) {
					continue;
				}
				
				EntryMap.put(counter, Entry);
				
				List<String> tokenized = tokenizer.tokenize(Entry.html());
				
				java.util.Iterator<String> stringIterator = tokenized.iterator();
				
				while (stringIterator.hasNext()) {
					
					String word = stringIterator.next().toLowerCase();
					if (!invertedIndex.containsKey(word)) {
						invertedIndex.put(word, new HashSet<Integer>());
					}
					invertedIndex.get(word).add(counter);
				}
				counter++;
			}
		}
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 */
	@Override
	public void addToSearchIndex(Feed feed) {
		
		java.util.Iterator<Entry> entryIterator = feed.getEntries().iterator();
		
		while (entryIterator.hasNext()) {
			
			Entry Entry = entryIterator.next();
			
			int entryPoint = EntryMap.size();
			
			if (EntryMap.containsValue(Entry)) {
				continue;
			}
			
			EntryMap.put(entryPoint, Entry);
			
			List<String> tokenized = tokenizer.tokenize(Entry.html());
			
			java.util.Iterator<String> stringIterator = tokenized.iterator();
			
			while (stringIterator.hasNext()) {
				
				String word = stringIterator.next().toLowerCase();
				if (!invertedIndex.containsKey(word)) {
					invertedIndex.put(word, new HashSet<Integer>());
				}
				invertedIndex.get(word).add(entryPoint);
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
		boolean repeat = true;
		while(repeat) {
			try {
				PrintWriter out = new PrintWriter(indexFile);
				out.print(index);
				out.close();
				repeat = false;
			} catch (FileNotFoundException e) {
				indexFile = new File("feader.index");			
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
			FileInputStream fis = new FileInputStream(indexFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			String index = (String) ois.readObject();
			ois.close();
			convertBackWithGuava(index);
			
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
	 * Converts the Inverted Index to a String
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @param map
	 * @return String version of the invertedIndex
	 */
	public String convertWithGuava(HashMap<String, HashSet<Integer>> map) {
		return Joiner.on(",").withKeyValueSeparator("=").join(map);
	}

	/**
	 * Converts the String back to an Inverted Index in form of a HashMap
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @param mapAsString
	 * @return Inverted Index in form of a HashMap
	 */
	public HashMap<String,HashSet<Integer>> convertBackWithGuava(String mapAsString) {
		
		Map<String, String> temp = Splitter.on(",").withKeyValueSeparator("=").split(mapAsString);
		HashMap<String, HashSet<Integer>> newInvertedIndex = new HashMap<String, HashSet<Integer>>();
		
		for (Map.Entry<String, String> entry : temp.entrySet()) {
			HashSet<Integer> temp2 = new HashSet<Integer>();
			temp2.add(Integer.parseInt(entry.getValue()));
	        newInvertedIndex.put(entry.getKey(), temp2);
	    }
		
		return newInvertedIndex;
	}
	
}