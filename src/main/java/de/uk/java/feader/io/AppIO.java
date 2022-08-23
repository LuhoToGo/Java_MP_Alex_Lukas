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

	
	/**
	 * 
	 * 
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @throws ClassNotFoundException 
	 * @throws IOException
	 */
	@Override
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

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @throws IOException
	 */
	@Override
	public void saveSubscribedFeeds(List<Feed> feeds, File feedsFile) {
		
		try {
			FileOutputStream fos = new FileOutputStream(feedsFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(feeds);
			oos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @throws IOException
	 */
	@Override
	public Properties loadConfig(File configFile) {
		
		Properties config = new Properties();
		try (InputStream input = new FileInputStream(configFile)){
			config.load(input);
			return config;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
		
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @throws IOException
	 */
	@Override
	public void saveConfig(Properties config, File configFile) {
		
		try (OutputStream output = new FileOutputStream(configFile)){ //eventuell FileNotFoundException -> create File
			config.store(output, null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @author Alessandro Marini & Lukas Hoffmann
	 * @throws IOException
	 */
	@Override
	public void exportAsHtml(List<Entry> entries, File htmlFile) {
		
		try {
			FileUtils.writeStringToFile(htmlFile, "<!DOCTYPE html><html><head><title>Feader Export</title></head><body>", "UTF-8", true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for (Entry entry : entries) {
			try {
				FileUtils.writeStringToFile(htmlFile, entry.html(), "UTF-8", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileUtils.writeStringToFile(htmlFile, "</body></html>", "UTF-8", true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
