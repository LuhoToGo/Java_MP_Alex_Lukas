package de.uk.java.feader.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;

public class AppIO implements IAppIO{

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
	public List<Feed> loadSubscribedFeeds(File feedsFile) throws FileNotFoundException {
		
		List<Feed> feedList = new ArrayList<Feed>();
		
		Scanner s = new Scanner(feedsFile);
		
		while (s.hasNextLine()) {
			Feed x = new Feed(s.nextLine());
			feedList.add(x);
		}
		s.close();		
		
		return feedList;
	}

	@Override
	public void saveSubscribedFeeds(List<Feed> feeds, File feedsFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Properties loadConfig(File configFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveConfig(Properties config, File configFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportAsHtml(List<Entry> entries, File htmlFile) {
		// TODO Auto-generated method stub
		
	}

}
