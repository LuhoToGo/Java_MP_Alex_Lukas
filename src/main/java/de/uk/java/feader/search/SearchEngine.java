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
	static Map<Integer,Entry> EntryMap = new HashMap<Integer,Entry>();
	static HashMap<String, HashSet<Integer>> invertedIndex = new HashMap<String, HashSet<Integer>>();
	
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

	@Override
	public void createSearchIndex(List<Feed> feeds) {
		
		int counter = 0;
		
		java.util.Iterator<Feed> iterator = feeds.iterator();
		while (iterator.hasNext()) {
			Feed Feed = iterator.next();
			
			java.util.Iterator<Entry> iteratorTwo = Feed.getEntries().iterator();
			
			while (iteratorTwo.hasNext()) {				
				
				Entry Entry = iteratorTwo.next();
				
				if (EntryMap.containsValue(Entry)) {
					continue;
				}
				
				EntryMap.put(counter, Entry);
				
				List<String> tokenized = tokenizer.tokenize(Entry.html());
				
				java.util.Iterator<String> iteratorThree = tokenized.iterator();
				
				while (iteratorThree.hasNext()) {
					
					String word = iteratorThree.next().toLowerCase();
					if (!invertedIndex.containsKey(word)) {
						invertedIndex.put(word, new HashSet<Integer>());
					}
					invertedIndex.get(word).add(counter);
				}
				counter++;
			}
		}
	}

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

	@Override
	public void setTokenizer(ITokenizer tokenizer) {
		
		this.tokenizer = (Tokenizer) tokenizer;
		
	}

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
	
	@Override
	public boolean indexExists() {
		
		if (invertedIndex.isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}
	
	public String convertWithGuava(HashMap<String, HashSet<Integer>> map) {
		return Joiner.on(",").withKeyValueSeparator("=").join(map);
	}

	public HashMap<String,HashSet<Integer>> convertBackWithGuava(String mapAsString) {
		
		Map<String, String> test = Splitter.on(",").withKeyValueSeparator("=").split(mapAsString);
		HashMap<String, HashSet<Integer>> newII = new HashMap<String, HashSet<Integer>>();
		
		for (Map.Entry<String, String> entry : test.entrySet()) {
			HashSet<Integer> z = new HashSet<Integer>();
			z.add(Integer.parseInt(entry.getValue()));
	        newII.put(entry.getKey(), z);
	    }
		
		return newII;
	}
	
}