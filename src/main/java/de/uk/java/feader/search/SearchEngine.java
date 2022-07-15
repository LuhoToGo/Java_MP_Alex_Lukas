package de.uk.java.feader.search;

import java.io.File;
import java.util.List;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.ITokenizer;

public class SearchEngine implements ISearchEngine {

	@Override
	public List<Entry> search(String searchTerm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createSearchIndex(List<Feed> feeds) {
		// TODO Auto-generated method stub
		
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
