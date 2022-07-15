package de.uk.java.feader.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.uk.java.feader.io.IAppIO;
import de.uk.java.feader.utils.FeaderUtils;

public class ConfigDialog {
	
	public static final String DEFAULT_WINDOW_TITLE = "Feader - A Reader for RSS/Atom Feeds";
	public static final String CONFIG_KEY_WINDOW_TITLE = "windowTitle";
	
	public static final String DEFAULT_ABOUT_TITLE = "About Feader";
	public static final String CONFIG_KEY_ABOUT_TITLE = "aboutTitle";
	
	public static final String DEFAULT_ABOUT_TEXT = "Feader is a feed reader for RSS and Atom feeds.";
	public static final String CONFIG_KEY_ABOUT_TEXT = "aboutText";
	
	public static final String CONFIG_KEY_UPDATE_FEEDS = "updateFeedsOnStartup";
	public static final String CONFIG_KEY_VERTICAL_LAYOUT = "layoutVertical";
	public static final String CONFIG_KEY_LOOK_AND_FEEL = "useSystemLookAndFeel";
	
	/**
	 * Prepares and displays the config dialog
	 * @param config The Properties object containing the current config data
	 * @param io The IAppIO instance to use for saving the config changes
	 * @param configFile The file to save the config to
	 */
	public static void open(Properties config, IAppIO io, File configFile) {
		//window setup
		JFrame sd = new JFrame();
		sd.setTitle(config.getProperty("configDialogTitle", "Feader Configuration"));
		sd.setIconImage(FeaderUtils.getIcon("config", "").getImage());
		sd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		sd.setResizable(false);
		sd.setAlwaysOnTop(true);
		
		//config panel
		JPanel configPanel = new JPanel();
		BoxLayout layout = new BoxLayout(configPanel, BoxLayout.Y_AXIS);
		configPanel.setLayout(layout);
		configPanel.setBorder(new EmptyBorder(30, 30, 10, 30));
		
		//CHECKBOX: use vertical layout
		JCheckBox updateFeedsCheckBox = new JCheckBox("Update all feeds when application starts");
		updateFeedsCheckBox.setName(CONFIG_KEY_UPDATE_FEEDS);
		updateFeedsCheckBox.setSelected(config.getProperty(updateFeedsCheckBox.getName()).equalsIgnoreCase("true"));
		configPanel.add(updateFeedsCheckBox);
		configPanel.add(Box.createVerticalStrut(10));
		
		//CHECKBOX: use vertical layout
		JCheckBox vertLayoutCheckBox = new JCheckBox("Use vertical window layout");
		vertLayoutCheckBox.setName(CONFIG_KEY_VERTICAL_LAYOUT);
		vertLayoutCheckBox.setSelected(config.getProperty(vertLayoutCheckBox.getName()).equalsIgnoreCase("true"));
		configPanel.add(vertLayoutCheckBox);
		configPanel.add(Box.createVerticalStrut(10));
		
		//CHECKBOX: look & feel
		JCheckBox lookAndFeelCheckBox = new JCheckBox("Use system Look&Feel (if possible)");
		lookAndFeelCheckBox.setName(CONFIG_KEY_LOOK_AND_FEEL);
		lookAndFeelCheckBox.setSelected(config.getProperty(lookAndFeelCheckBox.getName()).equalsIgnoreCase("true"));
		configPanel.add(lookAndFeelCheckBox);
		configPanel.add(Box.createVerticalStrut(30));
		
		//restart hint
		JLabel restartHint = new JLabel("  You have to restart Feader for the settings to take effect!");
		restartHint.setIcon(FeaderUtils.getIcon("config", ""));
		restartHint.setFont(restartHint.getFont().deriveFont(Font.ITALIC));
		configPanel.add(restartHint);
		configPanel.add(Box.createVerticalStrut(10));
		
		// SAVE / CANCEL
		FlowLayout saveCancelLayout = new FlowLayout(FlowLayout.RIGHT);
		JPanel saveCancelPanel = new JPanel(saveCancelLayout);
		//canel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//close configurations window
				sd.dispose();
			}
		});
		cancelButton.setMargin(new Insets(5,5,5,5));
		saveCancelPanel.add(cancelButton);
		//save button
		JButton saveButton = new JButton("Save Settings");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				config.setProperty(vertLayoutCheckBox.getName(), vertLayoutCheckBox.isSelected() ? "true" : "false");
				config.setProperty(updateFeedsCheckBox.getName(), updateFeedsCheckBox.isSelected() ? "true" : "false");
				config.setProperty(lookAndFeelCheckBox.getName(), lookAndFeelCheckBox.isSelected() ? "true" : "false");
				//save config
				io.saveConfig(config, configFile);
				//close configurations window
				sd.dispose();
			}
		});
		saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
		saveButton.setMargin(new Insets(5,5,5,5));
		saveCancelPanel.add(saveButton);
		//add to panel
		configPanel.add(saveCancelPanel);
		
		//align everything left
		for (Component c : configPanel.getComponents())
			((JComponent)c).setAlignmentX(JComponent.LEFT_ALIGNMENT);

		//prepare for window display
		sd.add(configPanel);
		sd.pack();
		sd.setVisible(true);
		sd.setLocationRelativeTo(null);
	}

}
