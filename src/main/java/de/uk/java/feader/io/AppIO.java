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
			FileInputStream fis = new FileInputStream(feedsFile); // liest die Datei feedsFile in die Objekt-Variable fis
			ObjectInputStream ois = new ObjectInputStream(fis); // konvertiert den FileInputStream zu einem ObjectInputStream; Macht den nächsten Schritt möglich
			@SuppressWarnings("unchecked")
			List<Feed> feeds = (List<Feed>) ois.readObject(); // füllt die Liste feeds mit den abonnierten Feeds aus der Datei
			ois.close(); 
			return feeds;
			
		} catch (ClassNotFoundException | IOException e) {
			List<Feed> feeds = new ArrayList<Feed>(); // erstellt Leere Liste sollte eine Exception auftreten
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
			FileOutputStream fos = new FileOutputStream(feedsFile); // liest die Datei feedsFile in den OutputStream
			ObjectOutputStream oos = new ObjectOutputStream(fos); // konvertiert den OutputStream von File zu Object, um ein Object hinzufügen zu können
			oos.writeObject(feeds); // schreibt die Liste feeds in die Datei feedsFile
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
		try (InputStream input = new FileInputStream(configFile)){ // versucht die configFile zu laden und im nächsten Schritt die Variable config zu befüllen
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
		
		try (OutputStream output = new FileOutputStream(configFile)){ // versucht die configFile zu laden und mit dem Inhalt der config Variable zu füllen
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
			FileUtils.writeStringToFile(htmlFile, "<!DOCTYPE html><html><head><title>Feader Export</title></head><body>", "UTF-8", true); // ergänzt das strukturell notwendige HTML-Gerüst
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for (Entry entry : entries) { // konvertiert jeden Eintrag der Liste in HTML und fügt sie der Datei htmlFile hinzu
			try {
				FileUtils.writeStringToFile(htmlFile, entry.html(), "UTF-8", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileUtils.writeStringToFile(htmlFile, "</body></html>", "UTF-8", true); // ergänzt die abschließenden HTML-Tags 
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
