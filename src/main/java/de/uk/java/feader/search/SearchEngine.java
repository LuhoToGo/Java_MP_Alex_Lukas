package de.uk.java.feader.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.ITokenizer;
import de.uk.java.feader.utils.Tokenizer;

public class SearchEngine implements ISearchEngine {

	private Tokenizer tokenizer;
	 List<String> stopwords = Arrays.asList("a", "able", "about",
	            "across", "after", "all", "almost", "also", "am", "among", "an",
	            "and", "any", "are", "as", "at", "be", "because", "been", "but",
	            "by", "can", "cannot", "could", "dear", "did", "do", "does",
	            "either", "else", "ever", "every", "for", "from", "get", "got",
	            "had", "has", "have", "he", "her", "hers", "him", "his", "how",
	            "however", "i", "if", "in", "into", "is", "it", "its", "just",
	            "least", "let", "like", "likely", "may", "me", "might", "most",
	            "must", "my", "neither", "no", "nor", "not", "of", "off", "often",
	            "on", "only", "or", "other", "our", "own", "rather", "said", "say",
	            "says", "she", "should", "since", "so", "some", "than", "that",
	            "the", "their", "them", "then", "there", "these", "they", "this",
	            "tis", "to", "too", "twas", "us", "wants", "was", "we", "were",
	            "what", "when", "where", "which", "while", "who", "whom", "why",
	            "will", "with", "would", "yet", "you", "your");
	 
	    Map<String, List<Tuple>> index = new HashMap<String, List<Tuple>>();
	    List<String> files = new ArrayList<String>();
	
	@Override
	public List<Entry> search(String searchTerm) {
		 Set<String> answer = new HashSet<String>();
         String word = searchTerm.toLowerCase();
         List<Tuple> idx = index.get(word);
         if (idx != null) {
             for (Tuple t : idx) {
                 answer.add(files.get(t.fileno));
             }
         }
         System.out.print(word);
         for (String f : answer) {
             System.out.print(" " + f);
         }
         System.out.println("");
		
		return null;
	}

	@Override
	public void createSearchIndex(List<Feed> feeds) {
		int fileno = files.indexOf(feeds);
        if (fileno == -1) {
            files.add(feeds);
            fileno = files.size() - 1;
        }
 
        int pos = 0;
        for (String line = reader.readLine(); line != null; line = reader
                .readLine()) {
            for (String _word : line.split("\\W+")) {
                String word = _word.toLowerCase();
                pos++;
                if (stopwords.contains(word))
                    continue;
                List<Tuple> idx = index.get(word);
                if (idx == null) {
                    idx = new LinkedList<Tuple>();
                    index.put(word, idx);
                }
                idx.add(new Tuple(fileno, pos));
            }
        }
        System.out.println("indexed " + file.getPath() + " " + pos + " words");
		
	}

	@Override
	public void addToSearchIndex(Feed feed) {
		
		
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
		
		
	}

	
	@Override
	public boolean indexExists() {
		
		if (invertedIndex.isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}
	
	 private class Tuple {
	        private int fileno;
	        private int position;
	 
	        public Tuple(int fileno, int position) {
	            this.fileno = fileno;
	            this.position = position;
	        }
	    }

}
