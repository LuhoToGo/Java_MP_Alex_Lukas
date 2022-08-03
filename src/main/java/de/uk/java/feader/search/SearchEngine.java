package de.uk.java.feader.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.ITokenizer;
import de.uk.java.feader.utils.Tokenizer;

public class SearchEngine implements ISearchEngine {

	private Tokenizer tokenizer;
	Map<Integer,Entry> EntryMap = new HashMap<Integer,Entry>();
	HashMap<String, HashSet<Integer>> invertedIndex = new HashMap<String, HashSet<Integer>>();
	
	@Override
	public List<Entry> search(String searchTerm) {
		
		String[] words = searchTerm.split("\\W+");
		HashSet<Integer> x = new HashSet<Integer>(invertedIndex.get(words[0].toLowerCase()));
		
		for (String word: words) {
			x.retainAll(invertedIndex.get(word));
		}
		
		List<Entry> found = new ArrayList<Entry>();
		for (int num: x) {
			found.add(EntryMap.get(num));
		}	
		return found;
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
				
				EntryMap.put(counter, Entry);
				
				List<String> tokenized = tokenizer.tokenize(Entry.html());
				
				java.util.Iterator<String> iteratorThree = tokenized.iterator();
				
				while (iteratorThree.hasNext()) {
					
					String word = iteratorThree.next();
					if (!invertedIndex.containsKey(word)) {
						invertedIndex.put(word, new HashSet<Integer>());
					}
					invertedIndex.get(word).add(counter);
				}
			}
			counter++;
		}		
	}

	@Override
	public void addToSearchIndex(Feed feed) {
		
		java.util.Iterator<Entry> entryIterator = feed.getEntries().iterator();
		
		while (entryIterator.hasNext()) {
			
			Entry Entry = entryIterator.next();
			
			int entryPoint = EntryMap.size(); //vielleicht .size()+1
			EntryMap.put(entryPoint, Entry);
			
			List<String> tokenized = tokenizer.tokenize(Entry.html());
			
			java.util.Iterator<String> stringIterator = tokenized.iterator();
			
			while (stringIterator.hasNext()) {
				
				String word = stringIterator.next();
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
		
		
		
	}

	@Override
	public void loadSearchIndex(File indexFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean indexExists() {
		// TODO Auto-generated method stub
		return false;
	}

}
