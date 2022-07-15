package de.uk.java.feader.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import de.uk.java.feader.data.CustomListModel;
import de.uk.java.feader.data.Entry;
import de.uk.java.feader.data.Feed;
import de.uk.java.feader.io.FeedDownloader;
import de.uk.java.feader.io.IAppIO;
import de.uk.java.feader.search.ISearchEngine;
import de.uk.java.feader.utils.ConsoleBanner;
import de.uk.java.feader.utils.FeaderLogger;
import de.uk.java.feader.utils.FeaderUtils;
import de.uk.java.feader.utils.ITokenizer;


public class FeaderGUI extends JFrame implements ActionListener, PropertyChangeListener {
	
	// VERSION UID
	private static final long serialVersionUID = -988377916641865642L;
	
	// LOGGER
	private final static Logger LOGGER = FeaderLogger.getLogger();
	
	// CONSTANT APP VALUES
	private static final Dimension PREFERRED_WINDOW_SIZE = new Dimension(1024, 768); // preferred window size
	private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(800, 600); // preferred window size
	private static final int LAYOUT_GAP_H = 5; // horizontal GUI layout gap
	private static final int LAYOUT_GAP_V = 5; // vertical GUI layout gap
	
	private static final File CONFIG_FILE = new File("feader.config");
	private static final File FEEDS_FILE = new File("feader.feeds");
	private static final File SAMPLES_FILE = new File("feader.samples");
	private static final File INDEX_FILE = new File("feader.index");

	
	private List<Feed> feeds;
	private List<Entry> displayedEntries;
	
	private FeedDownloader downloader;
	private IAppIO io;
	private ISearchEngine search;
	
	private JList<Feed> feedsList;
	private CustomListModel<Feed> feedsListModel;
	private JTextPane entriesText;
	private JProgressBar progressBar;
	private JLabel feedInfo;
	private ToolBar toolBar;
	
	private UpdateFeedsTask updateFeedsTask;
	private SearchTask searchTask;
	
	private Properties config;
	
	
	/*
	 * Constructor, initializes the application
	 */
	public FeaderGUI(IAppIO io, ISearchEngine search, ITokenizer tokenizer) {
		ConsoleBanner.print(); // print console banner
		
		if (io == null) {
			LOGGER.severe("IFeaderIO param is null!");
			System.exit(-1);
		} else if (search == null) {
			LOGGER.severe("IFeaderSearchEngine param is null!");
			System.exit(-1);
		} else if (tokenizer == null) {
			LOGGER.severe("ITokenizer param is null!");
			System.exit(-1);
		}
		
		this.io = io;
		this.search = search;
		this.search.setTokenizer(tokenizer);
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	init(); // create new Feader instance
            }
        });
	}
	
	
	private void init() {
		config = io.loadConfig(CONFIG_FILE);
		displayedEntries = new ArrayList<>();
		downloader = new FeedDownloader();
		setupLookAndFeel(); // set GUI Look&Feel
		initGui(); // init GUI

		loadSubscribedFeeds(); //load saved data of subscribed feeds
		search.loadSearchIndex(INDEX_FILE);
		
		//update feeds list (is set in config!)
		if (config.getProperty(ConfigDialog.CONFIG_KEY_UPDATE_FEEDS, "true").equalsIgnoreCase("true")) {
			updateFeeds(); 
		}
	}
	
	
	/*
	 * Initializes the GUI
	 */
	private void initGui() {
		LOGGER.finest("Initializing GUI");
		
		// set window preferences
		LOGGER.finest("Setting up window preferences");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				io.saveSubscribedFeeds(feeds, FEEDS_FILE);
				io.saveConfig(config, CONFIG_FILE);
				search.saveSearchIndex(INDEX_FILE);
				System.exit(0);
			}
		});
		setIconImage(FeaderUtils.getIcon("logo", "").getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // set window close operation to "dispose"
		setTitle(config.getProperty(ConfigDialog.CONFIG_KEY_WINDOW_TITLE)); // set window title
		setPreferredSize(PREFERRED_WINDOW_SIZE); // set preferred window size
		setMinimumSize(MINIMUM_WINDOW_SIZE); // set preferred window size
		
		//set window content panel with border
		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		// create window ui elements
		setLayout(new BorderLayout(LAYOUT_GAP_H, LAYOUT_GAP_V));
		toolBar = new ToolBar(this, new ToolBar.SearchCallback() {
			@Override
			public void search(String searchTerm) {
				searchFeeds(searchTerm);
			}
		});
		add(toolBar, BorderLayout.PAGE_START);
		JSplitPane splitPane = new JSplitPane(
				config.getProperty(ConfigDialog.CONFIG_KEY_VERTICAL_LAYOUT).equalsIgnoreCase("true")
				? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(initFeedsList());
		splitPane.add(initEntriesList());
		add(splitPane, BorderLayout.CENTER);
		
		// init status / info bar at bottom of window
		add(initStatusBar(), BorderLayout.SOUTH);
		
		// display window
		LOGGER.finest("Preparing window for display");
		pack(); // "pack" window to minimal size needed/preferred
		setLocationRelativeTo(null); // position window in center of screen
		setVisible(true); // show window
	}
	
	
	/*
	 * Initializes the subscribed feeds list view
	 */
	private Component initFeedsList() {
		
		//add feeds list and label
		feedsListModel = new CustomListModel<>();
		feedsList = new JList<Feed>(feedsListModel); // init list
		feedsList.setCellRenderer(new FeedsListRenderer()); // custom cell renderer
		feedsList.setPreferredSize(new Dimension(300, 300)); // set init width of list
		// add selection listener
		feedsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && feedsList.getSelectedValue() != null) {
					showFeed(feedsList.getSelectedValue()); // show feed content
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(feedsList);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		return scrollPane;
	}


	/*
	 * initializes the entries list view
	 */
	private Component initEntriesList() {
		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet styleSheet = new StyleSheet();
		styleSheet.addRule("html {padding: 12px;}");
		styleSheet.addRule(".entry {line-height: 110%; font-family: sans-serif; border-top: 1px solid #aaaaaa; padding-bottom: 12px;}");
		styleSheet.addRule(".marked {background-color: yellow;}");
		styleSheet.addRule("h1 {font-size: 18px; font-weight: bold; padding-bottom: 12px;}");
		styleSheet.addRule("h2 {font-size: 16px; margin-top: 12px; margin-bottom: 4px;}");
		styleSheet.addRule(".entry-source {margin-bottom: 4px;}");
		styleSheet.addRule("p {margin-bottom: 4px;}");
		styleSheet.addRule("a {color: #0000aa; font-style: italic;}");
		htmlEditorKit.setStyleSheet(styleSheet);
		entriesText = new JTextPane();
		entriesText.setEditorKit(htmlEditorKit);
		entriesText.setEditable(false);
		entriesText.setContentType("text/html");
		// add hyperlink listener to open system default browser for links
		entriesText.addHyperlinkListener(new HyperlinkListener() {
	        @Override
	        public void hyperlinkUpdate(HyperlinkEvent hle) {
	            if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
	                try {
	                	Desktop.getDesktop().browse(hle.getURL().toURI());
	                } catch (Exception ex) {
	                	LOGGER.warning("Could not follow link click: " + hle.getURL());
	                }
	            }
	        }
	    });
		// wrap in scroll pane for scrolling long contents
		JScrollPane entriesListPane = new JScrollPane(entriesText);
		// disable horizontal scrolling 
		entriesListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return entriesListPane;
	}


	/*
	 * Initialized the bottom info / status bar
	 */
	private Component initStatusBar() {
		//init staus bar panel
		JPanel statusBar = new JPanel();
		statusBar.setPreferredSize(new Dimension(-1, 20));
		BoxLayout statusBarLayout = new BoxLayout(statusBar, BoxLayout.X_AXIS);
		statusBar.setLayout(statusBarLayout);
		//init and hide progress bar
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		statusBar.add(progressBar);
		//init and hide feed info label
		feedInfo = new JLabel();
		feedInfo.setVisible(false);
		statusBar.add(feedInfo);
		return statusBar;
	}
	
	
	/*
	 * Asks for a new feed to add/subscribe
	 */
	private void addFeed() {
		String feedUrl = (String) JOptionPane.showInputDialog(
			null,
			"Enter feed URL:", "Add new RSS/Atom feed",
			JOptionPane.OK_CANCEL_OPTION,
			FeaderUtils.getIcon("feed", ""),
			null, null);
		addFeed(feedUrl);
	}
	
	
	private void addFeed(String feedUrl) {
		if (feedUrl != null && !feeds.contains(new Feed(feedUrl))) {
			Feed feed = downloader.downloadFeed(feedUrl);
			if (feed == null) {
				JOptionPane.showMessageDialog(
					this,
					"Could not load feed: " + feedUrl + "\nMaybe the feed is not compatible with Feader?",
					"Error",
					JOptionPane.OK_OPTION,
					FeaderUtils.getIcon("error", "Error"));
				return;
			}
			feeds.add(0, feed);
			feedsListModel.add(0, feed);
			feedsList.clearSelection();
			feedsList.setSelectedValue(feed, true);
			search.addToSearchIndex(feed);
			LOGGER.info("Added feed '" + feedUrl + "' to feeds list.");
		} else {
			LOGGER.warning("Cannot add '" + feedUrl + "'. Maybe this feed is already subscribed?");
		}
	}
	
	
	/*
	 * Updates the list of subscribed feeds
	 */
	private void updateFeeds() {
		if (updateFeedsTask != null
			&& updateFeedsTask.getState() != StateValue.DONE)
				return;
		
		toolBar.updateFeedsEnabled(false);
		toolBar.searchEnabled(false);
		toolBar.resetSearch();
	    updateFeedsTask = new UpdateFeedsTask();
	    updateFeedsTask.addPropertyChangeListener(this);
	    updateFeedsTask.execute();
	}
	
	
	private void searchFeeds(String searchTerm) {
		if (searchTerm == null || searchTerm.length() == 0 ) {
			feedsList.setSelectedIndex(0);
			return;
		} else if (searchTask != null && searchTask.getState() != StateValue.DONE) {
			searchTask.cancel(true);
		}
		
		searchTask = new SearchTask(searchTerm);
		searchTask.addPropertyChangeListener(this);
		searchTask.execute();
	}


	private void loadSubscribedFeeds() {
		feeds = io.loadSubscribedFeeds(FEEDS_FILE);
		renderFeedsList();
	}
	
	
	private void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
		renderFeedsList();
	}
	
	
	private void renderFeedsList() {
		if (feeds == null) feeds = io.loadSubscribedFeeds(FEEDS_FILE);
		feedsListModel.clear();
		for (Feed feed : feeds) {
			feedsListModel.addElement(feed);
		}
	}
	
	
	/*
	 * Deleted the currently selected feed
	 */
	private void deleteFeed(Feed feed) {
		if (feed != null) {
			int confirm = JOptionPane.showConfirmDialog(
				this,
				"Do you really want to delete the following feed:\n\"" + feed.getTitle() + "\"?",
				"Delete feed",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.NO_OPTION,
				FeaderUtils.getIcon("deleteFeed", ""));
			if (confirm == 0) {
				feeds.remove(feed);
				feedsListModel.removeElement(feed);
				feedsList.clearSelection();
				entriesText.setText("");
				feedInfo.setText("");
				search.createSearchIndex(feeds);
			}
		} else {
			JOptionPane.showMessageDialog(
				this,
				"Cannot delete feed: None selected.",
				"Error deleting feed",
				JOptionPane.ERROR_MESSAGE,
				FeaderUtils.getIcon("error", ""));
			LOGGER.warning("Cannot delete feed. None selected.");
		}
	}
	
	
	/*
	 * Shows "About" dialog
	 */
	private void about() {
		JOptionPane.showMessageDialog(
			this,
			config.getProperty(ConfigDialog.CONFIG_KEY_ABOUT_TEXT, ConfigDialog.CONFIG_KEY_ABOUT_TEXT),
			config.getProperty(ConfigDialog.CONFIG_KEY_ABOUT_TITLE, ConfigDialog.DEFAULT_ABOUT_TITLE),
			JOptionPane.OK_OPTION,
			FeaderUtils.getIcon("about", ""));
	}
	
	
	/*
	 * Opens the configuration dialog
	 */
	private void openConfigDialog() {
		ConfigDialog.open(config, io, CONFIG_FILE);
	}
	
	
	/*
	 * Adds the default sample feeds
	 */
	private void addSampleFeeds() {
		int confirm = JOptionPane.showConfirmDialog(
			this,
			"Do you want to add all the sample feeds from\n" + SAMPLES_FILE.getAbsolutePath() + "?",
			"Add sample feeds",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.NO_OPTION,
			FeaderUtils.getIcon("addSampleFeeds", ""));
		if (confirm != 0) return;
		LOGGER.finest("Reading sample feeds from " + SAMPLES_FILE + " ...");
		try {
			List<String> sampleFeedUrls = Files.readAllLines(SAMPLES_FILE.toPath());
			for (String url : sampleFeedUrls) {
				addFeed(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Prepares GUI to show the selected feed's meta info and
	 * displays the selected feed's content in the feed entries view
	 */
	private void showFeed(Feed feed) {
		toolBar.resetSearch();
		if (feed == null) return;
		feedInfo.setText(feed.getShortFeedInfo());
		feedInfo.setVisible(true);
		displayedEntries.clear();
		displayedEntries.addAll(feed.getEntries());
		displayEntries(null);
	}
	
	/*
	 * Displays the selected feed's content in the feed entries view
	 */
	private void displayEntries(String searchTerm) {
		DisplayEntriesTask displayEntriesTask = new DisplayEntriesTask(searchTerm);
		displayEntriesTask.addPropertyChangeListener(this);
		displayEntriesTask.execute();
	}
	
	/*
	 * Shows a file chooser dialog and starts the export
	 * of the currenty displayed feed entries as HTML
	 */
	private void exportDisplayedEntries() {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setApproveButtonText("Export");
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.exists() && f.isDirectory() && f.canWrite();
			}
			@Override
			public String getDescription() {
				return "Directories";
			}
		});
		fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
		fc.setDialogTitle("Export entries as HTML: Choose directory");
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			if (f.exists() && f.isDirectory() && f.canWrite()) {
				File htmlFile = new File(f.getAbsolutePath() + File.separator 
						+ "Feader-Export-" + FeaderUtils.DATE_FORMAT_EXPORT.format(new Date()) + ".html");
				LOGGER.finest("Exporting entries to: " + htmlFile.getAbsolutePath());
				io.exportAsHtml(displayedEntries, htmlFile);
			} else {
				LOGGER.severe("User somehow managed to select invalid export directory.");
			}
		}
	}
	
	
	/*
	 * Sets up the GUI Look&Feel
	 */
	private void setupLookAndFeel() {
		if (!config.getProperty(ConfigDialog.CONFIG_KEY_LOOK_AND_FEEL).equalsIgnoreCase("true"))
			return;
		
		LOGGER.finest("Setting up system specific window look & feel");
		try {
			//try to get system L&F
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOGGER.warning("Could not use system specific window look & feel; using cross-platform L&F");
			try {
				//if it fails, use ugly cross-platform L&F
				UIManager.setLookAndFeel(
				        UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e1) {
				//if even that fails, tell me
				LOGGER.severe("Error setting up window look & feel");
			}
		}
	}


	/*
	 * Handle user actions
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "addFeed": addFeed(); break;
		case "deleteFeed": deleteFeed(feedsList.getSelectedValue()); break;
		case "updateFeeds": updateFeeds(); break;
		case "export": exportDisplayedEntries(); break;
		case "resetSearch": toolBar.resetSearch(); break;
		case "about": about(); break;
		case "showConfigDialog": openConfigDialog(); break;
		case "addSampleFeeds": addSampleFeeds(); break;
		default: break;
		}
	}

	
	/*
	 * Implementation of PropertyChangeListener 
	 * for changing state of progress bar
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("progress")) {
			progressBar.setValue((int) evt.getNewValue());
		}
	}
	
	
	/*
	 * SwingWorker extension for updating all feeds
	 */
	class UpdateFeedsTask extends SwingWorker<Void, Void> {
		
		@Override
		protected Void doInBackground() throws Exception {
			setProgress(0);
			feedInfo.setText("");
			feedInfo.setVisible(false);
			progressBar.setValue(0);
			progressBar.setString("Updating feeds...");
			progressBar.setVisible(true);
			feedsList.setEnabled(false);
			List<Feed> updatedFeeds = new ArrayList<>();
			
			for (int i = 0; i < feeds.size(); i++) {
				try {
					progressBar.setString("Updating: " + feeds.get(i).getTitle());
					Feed downloadedFeed = downloader.downloadFeed(feeds.get(i).getUrl());
					updatedFeeds.add(downloadedFeed != null ? downloadedFeed : feeds.get(i));
					setProgress((int)((i * 75.0d) / feeds.size()));
				} catch (Exception e) {
					continue;
				}
			}
			
			progressBar.setString("Rendering feeds list ...");
			setFeeds(updatedFeeds);
			setProgress(90);
			progressBar.setString("Updating search index ...");
			search.createSearchIndex(updatedFeeds);
			setProgress(100);
			return null;
		}

		@Override
		protected void done() {
			progressBar.setVisible(false);
			progressBar.setString("");
			toolBar.updateFeedsEnabled(true);
			toolBar.searchEnabled(true);
			feedsList.setEnabled(true);
			showFeed(feedsList.getSelectedValue());
		}

	}
	
	
	/*
	 * SwingWorker extension for searching in all feeds
	 */
	class SearchTask extends SwingWorker<List<Entry>, Entry> {
		
		private String searchTerm;
		
		public SearchTask(String searchTerm) {
			this.searchTerm = searchTerm;
		}
		
		@Override
		protected List<Entry> doInBackground() throws Exception {
			setProgress(0);
			feedInfo.setText("");
			feedInfo.setVisible(false);
			progressBar.setValue(0);
			progressBar.setString("Searching...");
			progressBar.setVisible(true);
			feedsList.clearSelection();
			displayedEntries.clear();
			setProgress(25);
			if (!search.indexExists()) search.createSearchIndex(feeds);
			List<Entry> results = search.search(searchTerm);
			displayedEntries.addAll(results);
			displayEntries(searchTerm);
			setProgress(100);
			return results;
		}

		@Override
		protected void done() {
			progressBar.setVisible(false);
			progressBar.setString("");
			feedInfo.setText("Displaying search results for: " + searchTerm + 
					" (" + displayedEntries.size() + " results)");
			feedInfo.setVisible(true);
		}

	}
	
	
	/*
	 * SwingWorker extension for displaying the entries of the selected feed
	 */
	class DisplayEntriesTask extends SwingWorker<Void, Void> {
		private String searchTerm;
		
		public DisplayEntriesTask(String searchTerm) {
			this.searchTerm = searchTerm;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			setProgress(0);
			feedInfo.setVisible(false);
			progressBar.setValue(0);
			progressBar.setString("Loading entries ...");
			progressBar.setVisible(true);
			
			boolean isSearch = searchTerm != null && searchTerm.length() > 0;
			StringBuilder sb = new StringBuilder();
			
			if (isSearch) {
				sb.append("<h1>" + displayedEntries.size() + 
						" search results for: \"" + searchTerm + "\"</h1>");
			} else if (feedsList.getSelectedValue() != null) {
				sb.append("<h1>" + feedsList.getSelectedValue().getTitle() + "</h1>");
			}
			
			for (int i = 0; i < displayedEntries.size(); i++) {
				if (isSearch) {
					sb.append(
						displayedEntries.get(i)
							.htmlHighlighted(
								searchTerm, "<span class=\"marked\">", "</span>")
					);
				} else {
					sb.append(displayedEntries.get(i).html());
				}
				setProgress((int)((i * 95.0d) / displayedEntries.size()));
			}
			
			entriesText.setText(sb.toString());
			setProgress(100);
			return null;
		}

		@Override
		protected void done() {
			entriesText.setCaretPosition(0);
			progressBar.setVisible(false);
			feedInfo.setVisible(true);
		}

	}
	

}
