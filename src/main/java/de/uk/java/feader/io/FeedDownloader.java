package de.uk.java.feader.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.FeaderLogger;

public class FeedDownloader {
	
	// LOGGER
	private final static Logger LOGGER = FeaderLogger.getLogger();
	
	
	/**
	 * Reads RSS/Atom feeds from an array of URL strings
	 * and transforms it into a List<Feed> of Feed objects
	 * @param feedUrls List of URL strings to download from
	 * @return List of dowloaded feeds as Feed objects
	 */
	public List<Feed> downloadFeeds(List<String> feedUrls) {
		List<Feed> feeds = new ArrayList<Feed>();
		
		for (String feedUrl : feedUrls) {
			Feed feed = downloadFeed(feedUrl);
			
			if (feed == null) {
				LOGGER.warning("Could not load feed: " + feedUrl);
				continue;
			}
			feeds.add(feed);
		}
		return feeds;
	}
	
	
	/**
	 * Reads a RSS/Atom feed and parses it to a SyndFeed object
	 * @param feedUrl URL string to download feed from
	 * @return Feed object containing the downloaded data
	 */
	public Feed downloadFeed(String feedUrl) {
		try (CloseableHttpClient client = HttpClients.createMinimal()) {
			HttpUriRequest request = new HttpGet(feedUrl);
			try (CloseableHttpResponse response = client.execute(request);
					InputStream stream = response.getEntity().getContent()) {
				return readFeed(feedUrl, stream);
			} catch (Exception e) {
				//e.printStackTrace();
				return null;
			}
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Reads a feed data stream and creates a Feed object from it
	 * @param feedUrl The URL for this feed
	 * @param feedStream The data stream to read the data from
	 * @return
	 */
	public Feed readFeed(String feedUrl, InputStream feedStream) {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feedTemp = null;
		try {
			feedTemp = input.build(new XmlReader(feedStream));
		} catch (Exception e) {
			LOGGER.severe("Error reading stream for: " + feedUrl);
			return null;
		}
		if (feedTemp != null) {
			return new Feed(feedUrl, feedTemp);
		} else {
			return null;
		}
	}
	
	
}
