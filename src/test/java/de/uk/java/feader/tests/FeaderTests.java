package de.uk.java.feader.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.tidy.Tidy;

import de.uk.java.feader.Feader;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.io.FeedDownloader;
import de.uk.java.feader.io.IAppIO;
import de.uk.java.feader.search.ISearchEngine;
import de.uk.java.feader.utils.ITokenizer;


public class FeaderTests {
	
	private static final String TEST_FEED_URL = "https://feader.test.de/feed.rss";
	private static final String TEST_FEED_TITLE = "Feader Test Feed";
	private static final String TEST_FEED_DESC = "This is a test feed for the Feader project";
	
	private static final File CONFIG_FILE = new File("feader.test.config");
	private static final File HTML_FILE = new File("feader.test.html");
	private static final File FEEDS_FILE = new File("feader.test.feeds");
	private static final File INDEX_FILE = new File("feader.test.index");
	
	private static final IAppIO IO = Feader.getAppIO();
	private static final ISearchEngine SEARCH = Feader.getSearchEngine();
	private static final ITokenizer TOKENIZER = Feader.getTokenizer();
	private static final FeedDownloader DOWNLOADER = new FeedDownloader();
	private static final List<Feed> FEEDS = new ArrayList<Feed>();
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("[TEST PREP] Deleting test files ...");
		deleteTestFiles();
		System.out.println("[TEST PREP] Preparing test data...");
		FEEDS.add(DOWNLOADER.readFeed(TEST_FEED_URL, ClassLoader.getSystemResource("test.rss").openStream()));
		System.out.println("[TEST PREP] Prepared test feeds: " + FEEDS);
		SEARCH.setTokenizer(TOKENIZER);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("[TEST CLEANUP] Deleting test files ...");
		deleteTestFiles();
	}
	
	private static void deleteTestFiles() {
		if (FEEDS_FILE.exists()) FEEDS_FILE.delete();
		if (CONFIG_FILE.exists()) CONFIG_FILE.delete();
		if (HTML_FILE.exists()) HTML_FILE.delete();
		if (INDEX_FILE.exists()) INDEX_FILE.delete();
	}
	
	@Test
	public void testIoSaveSubscribedFeeds() {
		System.out.println("[TEST] IFeaderIO.saveSubscribedFeeds()");
		IO.saveSubscribedFeeds(FEEDS, FEEDS_FILE);
		
		if (!FEEDS_FILE.exists()) {
			System.err.println("The test save-file '" + FEEDS_FILE.getName() + "' was not created!");
			fail("feeds save-file was not created!");
		}
		else if (!(FEEDS_FILE.length() > 0)) {
			System.err.println("The test save-file '" + FEEDS_FILE.getName() + "' has no content!");
			fail("feeds save-file is empty!");
		}
	}
	
	@Test
	public void testIoLoadSubscribedFeeds() {
		System.out.println("[TEST] IFeaderIO.loadSubscribedFeeds()");
		List<Feed> feedsTemp = IO.loadSubscribedFeeds(FEEDS_FILE);
		
		if (!(feedsTemp.size() > 0)) {
			System.err.println("No feeds were read from test save-file '" + FEEDS_FILE.getName() + "'!");
			fail("no feeds read from " + FEEDS_FILE.getAbsolutePath());
		}
		
		if (!feedsTemp.get(0).getUrl().equals(TEST_FEED_URL)
				|| !feedsTemp.get(0).getTitle().equals(TEST_FEED_TITLE)
				|| !feedsTemp.get(0).getDescription().equals(TEST_FEED_DESC)) {
			System.err.println("The data read from save-file '" + FEEDS_FILE.getName() + "' doesn't match the test input data!");
			fail("feed sample data (" + FEEDS_FILE.getAbsolutePath() + ") doesn't match read object!");
		}
	}
	
	@Test
	public void testIoLoadAndSaveConfig() {
		System.out.println("[TEST] IFeaderIO.loadConfig() AND IFeaderIO.saveConfig()");
		Properties config = new Properties();
		config.setProperty("testKey", "testValue");
		IO.saveConfig(config, CONFIG_FILE);
		config = IO.loadConfig(CONFIG_FILE);
		
		if (!CONFIG_FILE.exists()) {
			System.err.println("The test config-file '" + CONFIG_FILE.getName() + "' was not created!");
			fail("config file was not created!");
		} else if (!(CONFIG_FILE.length() > 0)) {
			System.err.println("The test config-file '" + CONFIG_FILE.getName() + "' is empty!");
			fail("config file is empty!");
		}
		
		if (!config.getProperty("testKey").equals("testValue"))
			fail("sample config data doesn't match read config data!");
	}
	
	@Test
	public void testIoHtmlExport() {
		System.out.println("[TEST] IFeaderIO.exportAsHtml()");
		IO.exportAsHtml(FEEDS.get(0).getEntries(), HTML_FILE);
		
		Tidy tidy = new Tidy();
		tidy.setShowWarnings(true);
		
		try {
			tidy.parse(new FileInputStream(HTML_FILE), new OutputStream() {
				@Override public void write(int b) throws IOException {}
			});
		} catch (FileNotFoundException e) {}
		
		if (!HTML_FILE.exists()) {
			System.err.println("The test export HTML-file '" + HTML_FILE.getName() + "' was not created!");
			fail("HTML file was not created!");
		} else if (!(HTML_FILE.length() > 0)) {
			System.err.println("The test export HTML-file '" + HTML_FILE.getName() + "' is empty!");
			fail("HTML file is empty!");
		} else if (tidy.getParseErrors() > 0 || tidy.getParseWarnings() > 0) {
			System.err.println("HTML of export is not valid HTML! See console output for errors and warnings!");
			fail("HTML of export is not valid HTML! See console output for details!");
		}
	}
	
	@Test
	public void testSearchIndexSaveAndLoad() {
		System.out.println("[TEST] IFeaderSearchEngine index saving and loading");
		
		long indexFileTime = INDEX_FILE.lastModified();
		
		SEARCH.createSearchIndex(FEEDS);
		SEARCH.saveSearchIndex(INDEX_FILE);
		
		try { Thread.sleep(200); }
		catch (InterruptedException e) {}
		
		if (indexFileTime == INDEX_FILE.lastModified()) {
			System.err.println("saveSearchIndex() doesn't seem to save an index file!");
			fail("No index file created!");
		}
	}
	
	@Test
	public void testSearch() {
		System.out.println("[TEST] IFeaderSearchEngine search implementation");
		SEARCH.createSearchIndex(FEEDS);
		
		if (SEARCH.search("Feader") == null) {
			System.err.println("Search returns null - no index created?");
			fail("Search returns null!");
		}
		
		if (SEARCH.search("Feader").size() != 2) {
			System.err.println("The search gives the wrong number of results! Maybe there are duplicates?");
			fail("wrong number of search results!");
		}
		
		if (SEARCH.search("feader").size() != 2) {
			System.err.println("The search doesn't seem to be case insensitive! Maybe the index was built with a non-normalized vocabulary?");
			fail("search not case-insensitive!");
		}
		
		if (!SEARCH.search("Feader").get(0).getTitle().matches("Test Title \\d")) {
			System.err.println("The returned entry data doesn't match test sample data!");
			fail("returned entry data doesn't match test sample data!");
		}
		
		SEARCH.addToSearchIndex(FEEDS.get(0));
		
		if (SEARCH.search("Feader").size() != 2) {
			System.err.println("Search returns duplicate results if identical feeds are added!");
			fail("search returns duplicate results if identical feeds are added!");
		}
	}
	
	@Test
	public void testTokenizer() {
		System.out.println("[TEST] ITokenizer tokenization");
		String input = "<h1>This is a heading!!!</h1>";
		String expected = "[This, is, a, heading]";

		if (!TOKENIZER.tokenize(input).toString().equals(expected)) {
			System.err.println("Tokenizer doesn't produce the expected results. Input \"" + input + 
					"\" is expected to produce a List " + expected + ", but instead priduces: " + TOKENIZER.tokenize(input).toString());
			fail("Tokenizer doesn't produce the expected results.");
		}
	}

}
