package de.uk.java.feader.io;

import java.io.File;
import java.util.List;
import java.util.Properties;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;

public interface IAppIO {
	
	/**
	 * Loads the previously subscribed/added feeds data
	 * from <code>feedsFile</code> and returns it as a
	 * <code>List</code> of <code>Feed</code> objects.
	 * @param feedsFile The file to load the feeds' data from
	 * @return The <code>List</code> of loaded feeds
	 */
	public List<Feed> loadSubscribedFeeds(File feedsFile);
	
	/**
	 * Saves the currently subscribes/added feeds to
	 * <code>feedsFile</code>
	 * @param feeds A <code>List</code> of the subscribed feeds
	 * @param feedsFile The file to save the feeds' data to
	 */
	public void saveSubscribedFeeds(List<Feed> feeds, File feedsFile);
	
	/**
	 * Loads the properties-formatted config file <code>configFile</code>
	 * to a <code>Properties</code> object
	 * @param configFile The file to load the config data from
	 * @return A <code>Properties</code> object holding the loaded config data
	 */
	public Properties loadConfig(File configFile);
	
	/**
	 * Saves the data from a <code>Properties</code> object to
	 * <code>configFile</code>
	 * @param config The <code>Properties</code> object holding the config data
	 * @param configFile The file to write the data to
	 */
	public void saveConfig(Properties config, File configFile);
	
	/**
	 * Exports (writes) a valid HTML document to <code>htmlFile</code>, containing
	 * readable HTML representations of the given <code>Entry</code> objects
	 * form the <code>List</code> of entries.
	 * @param entries The <code>List</code> of <code>Entry</code> objects to export
	 * @param htmlFile The file to write the data to
	 */
	public void exportAsHtml(List<Entry> entries, File htmlFile);

}
