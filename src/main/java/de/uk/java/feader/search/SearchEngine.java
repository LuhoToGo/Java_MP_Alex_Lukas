package de.uk.java.feader.search;

import java.io.File;
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
				
				Tokenizer test = new Tokenizer();
				List<String> tokenized = test.tokenize(Entry.html());
				
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTokenizer(ITokenizer tokenizer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveSearchIndex(File indexFile) {
		// TODO Auto-generated method stub
		
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
