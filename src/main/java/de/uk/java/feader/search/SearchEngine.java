package de.uk.java.feader.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.ITokenizer;
import de.uk.java.feader.utils.Tokenizer;

public class SearchEngine implements ISearchEngine {

	@Override
	public List<Entry> search(String searchTerm) {
		
		List<Entry> found = new ArrayList();
		// TODO
		return found;
	}

	@SuppressWarnings("null")
	@Override
	public void createSearchIndex(List<Feed> feeds) {
		
		
		
		java.util.Iterator<Feed> iterator = feeds.iterator();
		while (iterator.hasNext()) {
			Feed Feed = iterator.next();
			java.util.Iterator<Entry> iteratorTwo = Feed.getEntries().iterator();
			while (iteratorTwo.hasNext()) {
				Entry Entry = iteratorTwo.next();
				Tokenizer test = new Tokenizer();
				test.tokenize(Entry.html());
				
				
			}
			
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
