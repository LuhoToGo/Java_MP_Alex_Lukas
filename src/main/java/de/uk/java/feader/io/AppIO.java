package de.uk.java.feader.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;

public class AppIO implements IAppIO {

	@Override
	/**
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 */
	public List<Feed> loadSubscribedFeeds(File feedsFile) {
		
		try {
			FileInputStream fis = new FileInputStream(feedsFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			List<Feed> feeds = (List<Feed>) ois.readObject();
			ois.close();
			return feeds;
			
		} catch (ClassNotFoundException | IOException e) {
			List<Feed> feeds = new ArrayList<Feed>();
			e.printStackTrace();
			return feeds;
			
		}
		
	}

	@Override
	public void saveSubscribedFeeds(List<Feed> feeds, File feedsFile) {
		
		try {
			FileOutputStream fos = new FileOutputStream(feedsFile); //eventuell FileNotFoundException -> create File
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(feeds);
			oos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Properties loadConfig(File configFile) {
		
		Properties config = new Properties();
		try (InputStream input = new FileInputStream(configFile)){
			config.load(input);
			return config;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
		
	}

	@Override
	public void saveConfig(Properties config, File configFile) {
		
		try (OutputStream output = new FileOutputStream(configFile)){ //eventuell FileNotFoundException -> create File
			config.store(output, null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void exportAsHtml(List<Entry> entries, File htmlFile) {
		
		try {
			FileUtils.writeStringToFile(htmlFile, entries.toString(), "UTF-8");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
