package de.uk.java.feader.gui;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.uk.java.feader.utils.FeaderUtils;


@SuppressWarnings("serial")
public class ToolBar extends JToolBar {
	
	private static final Dimension BUTTON_DIM_DEFAULT = new Dimension(48, 48);
	private static final Dimension BUTTON_DIM_SMALL = new Dimension(32, 32);
	
	private JTextField searchField;
	private JButton updateFeedsButton;
	
	
	/**
	 * Creates a Feader GUI toolbar instance and initializes the toolbar's elements.
	 * @param buttonActionListener The ActionListener to register for the toolbar buttons
	 * @param searchCallback The callback object for the live search feature
	 */
	public ToolBar (ActionListener buttonActionListener, SearchCallback searchCallback) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOrientation(JToolBar.HORIZONTAL);
		setFloatable(false); // set to not floatable
		add(new JLabel(FeaderUtils.getIcon("logo", "")));
		add(Box.createHorizontalStrut(30));
		
		//add buttons to toolbar
		add(createToolBarButton("addFeed", "addFeed",
				"Subscribe to a new Atom feed", "Add", false, buttonActionListener));
		add(createToolBarButton("deleteFeed", "deleteFeed",
				"Delete the selected feed", "Delete", false, buttonActionListener));
		updateFeedsButton = createToolBarButton("updateFeeds", "updateFeeds",
				"Update all feeds", "Update", false, buttonActionListener);
		add(updateFeedsButton);
		
		add(Box.createHorizontalStrut(30));
		
		add(createToolBarButton("export", "export",
				"Export displayed feed entries as HTML", "Export", false, buttonActionListener));
		add(createToolBarButton("config", "showConfigDialog",
				"Open Feader's configuration dialog", "Configuration", false, buttonActionListener));
		add(createToolBarButton("addSampleFeeds", "addSampleFeeds",
				"Add sample feeds", "Add samples", false, buttonActionListener));
		add(createToolBarButton("about", "about",
				"About Feader", "About", false, buttonActionListener));
		
		//add expanding spacer aka. "glue"
		add(Box.createHorizontalGlue());
		
		//add search field and label
		JPanel searchPanel = new JPanel();
		BoxLayout searchLayout = new BoxLayout(searchPanel, BoxLayout.X_AXIS);
		searchPanel.setLayout(searchLayout);
		searchPanel.add(new JLabel(FeaderUtils.getIcon("search", "Search feeds: ")));
		searchPanel.add(Box.createHorizontalStrut(10));
		searchField = new JTextField(20);
		searchField.setMaximumSize(searchField.getPreferredSize());
		searchField.setName("searchField");
		
		//add document listener to search field, call search callback
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) { searchCallback.search(searchField.getText()); }
			@Override
			public void insertUpdate(DocumentEvent e) { searchCallback.search(searchField.getText()); }
			@Override
			public void changedUpdate(DocumentEvent e) { searchCallback.search(searchField.getText()); }
		});
		searchPanel.add(searchField);
		//add search panel to toolbar
		add(searchPanel);
		//add reset search button
		add(createToolBarButton("resetSearch", "resetSearch",
				"Reset search", "X", true, buttonActionListener));
	}
	
	
	/**
	 * Resets the text of the search field
	 */
	public void resetSearch() {
		searchField.setText("");
	}
	
	/**
	 * Sets the search field to enabled/disabled
	 * @param enabled
	 */
	public void searchEnabled(boolean enabled) {
		searchField.setEnabled(enabled);
	}
	
	/**
	 * Sets the "update feeds" button to enabled/disabled
	 * @param enabled
	 */
	public void updateFeedsEnabled(boolean enabled) {
		updateFeedsButton.setEnabled(enabled);
	}
	
	
	/*
	 * Creates a toolbar button
	 */
	private JButton createToolBarButton(
			String imageName,
			String actionCommand,
			String toolTipText,
			String altText,
			boolean small,
			ActionListener actionListener) {

		//create button
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(actionListener);
		
		ImageIcon icon = FeaderUtils.getIcon(imageName, altText);

		if (icon != null) { // image found
			button.setIcon(icon);
		} else { // no image found
			button.setText(altText);
		}

		button.setPreferredSize(small ? BUTTON_DIM_SMALL : BUTTON_DIM_DEFAULT);
		button.setMaximumSize(small ? BUTTON_DIM_SMALL : BUTTON_DIM_DEFAULT);
		button.setFocusable(false);
		//button.setMaximumSize(new Dimension(button.getMinimumSize().width, Integer.MAX_VALUE));
		return button;
	}
	
	/**
	 * Defines the search callback
	 * @author bkis
	 */
	public interface SearchCallback {
		public void search(String searchTerm);
	}

}
