package de.uk.java.feader.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import de.uk.java.feader.utils.FeaderUtils;

public class Feed implements Serializable, Comparable<Feed> {
	
	private static final long serialVersionUID = 1L;
	
	private String url;
	private String title;
	private String description;
	private String publishedDateString;
	private List<Entry> entries;
	
	
	public Feed(String url) {
		super();
		this.url = url;
		this.entries = new ArrayList<Entry>();
		this.title = "";
		this.description = "";
		this.publishedDateString = "";
	}
	
	/**
	 * Creates an instance of a Feed and transfers the feed
	 * data form a SyndFeed object to the new instance.
	 * @param url The URL string of this feed
	 * @param sourceFeed The SyndFeed object holding the data for this feed instance
	 */
	public Feed(String url, SyndFeed sourceFeed) {
		this(url);
		setTitle(sourceFeed.getTitle());
		setDescription(sourceFeed.getDescription());
		
		if (sourceFeed.getPublishedDate() != null)
			setPublishedDateString(FeaderUtils.DATE_FORMAT.format(sourceFeed.getPublishedDate()));

		
		for (SyndEntry entryTemp : sourceFeed.getEntries()) {
			Entry entry = new Entry(entryTemp.getTitle());
			entry.setContent(entryTemp.getDescription().getValue());
			entry.setLinkUrl(entryTemp.getLink());
			entry.setParentFeedTitle(getTitle());
			if (entryTemp.getPublishedDate() != null) {
				entry.setPublishedDateString(FeaderUtils.DATE_FORMAT.format(entryTemp.getPublishedDate()));
			}
			addEntry(entry);
		}
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setTitle(String title) {
		this.title = title != null ? title : "";
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String description) {
		this.description = description != null ? description : "";
	}

	public String getDescription() {
		return description;
	}

	public void setPublishedDateString(String publishedDateString) {
		this.publishedDateString = publishedDateString != null ? publishedDateString : "";
	}

	public String getPublishedDateString() {
		return publishedDateString;
	}
	
	/**
	 * Returns a short string containing a combination of meta data for this feed
	 * @return info string
	 */
	public String getShortFeedInfo() {
		return getTitle() + " [" +
				getEntriesCount() + " entries]: " + 
				getDescription() +
				(getPublishedDateString() != null && getPublishedDateString().length() > 0
					? " (updated " + getPublishedDateString() + ")"
					: "");
	}
	
	public void addEntry(Entry entry) {
		if (entry != null) entries.add(entry);
	}

	public List<Entry> getEntries() {
		return entries;
	}
	
	public int getEntriesCount() {
		return entries.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Feed)
			&& ((Feed)obj).getUrl().equals(url);
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}
	
	@Override
	public String toString() {
		return getTitle();
	}

	@Override
	public int compareTo(Feed o) {
		return getPublishedDateString().compareTo(o.getPublishedDateString());
	}
	
}
