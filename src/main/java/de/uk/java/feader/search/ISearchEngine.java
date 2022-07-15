package de.uk.java.feader.search;

import java.io.File;
import java.util.List;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.ITokenizer;

public interface ISearchEngine {
	
	/**
	 * Searches the search index for <code>searchTerm</code> and
	 * returns a <code>List</code> of <code>Entry</code> objects
	 * whose title or content text contains <code>searchTerm</code>.
	 * If no results are found in the search process, this method
	 * should return an empty list. If, on the other hand, the search
	 * index is empty (e.g. at first run of application),
	 * this method should return <code>null</code>.
	 * @param searchTerm
	 * @return A list of search results (Entry instances), an empty list (if no results), or <code>null</code> (if no index).
	 */
	public List<Entry> search(String searchTerm);
	
	/**
	 * Creates an inverted index as a search structure using
	 * the data from the given <code>List</code> of <code>Feed</code>
	 * objects. The text data to index has to be taken from the
	 * <code>html()</code> method of the entries of the given feeds,
	 * as it returns title <b>and</b> content data for this entry.
	 * Beware that the <code>html()</code> method of  the <code>Entry</code>
	 * class returns text data containing HTML tags (which should NOT be indexed!).
	 * See https://en.wikipedia.org/wiki/Inverted_index
	 * or https://de.wikipedia.org/wiki/Invertierte_Datei
	 * for more information on inverted indices.
	 * @param feeds The <code>List</code> of <code>Feed</code> 
	 * objects to create the new inverted index for
	 */
	public void createSearchIndex(List<Feed> feeds);
	
	/**
	 * Adds a single <code>Feed</code> object to the inverted index, if an index exists.
	 * The text data to index has to be taken from the
	 * <code>html()</code> method of the entries of the given feed,
	 * as it returns title <b>and</b> content data for this entry.
	 * Beware that the <code>html()</code> method of  the <code>Entry</code>
	 * class returns text data containing HTML tags (which should NOT be indexed!).
	 * @param feed The <code>Feed</code> object to add the the inverted index.
	 */
	public void addToSearchIndex(Feed feed);
	
	/**
	 * Sets the ITokenizer instance used by this implementation of ISearchEngine
	 * @param tokenizer The ITokenizer instance to use
	 */
	public void setTokenizer(ITokenizer tokenizer);
	
	/**
	 * Saves the internal search index structure to the given file, if such an index structure exists.
	 * @param indexFile The file to write the data to.
	 */
	public void saveSearchIndex(File indexFile);
	
	/**
	 * Loads a previously saved search index structure from the given file
	 * <b>IF</b> the file exists. Otherwise, the index structure should remain
	 * uninitialized and calls to <code>search()</code> should return <code>null</code>.
	 * In this case, the application will call <code>createSearchIndex()</code> to
	 * initialize a new index.
	 * @param indexFile
	 */
	public void loadSearchIndex(File indexFile);
	
	/**
	 * Returns <code>true</code> if a search index exists for this ISearchEngine
	 * instance, <code>false</code> otherwise.
	 * @return
	 */
	public boolean indexExists();

}
