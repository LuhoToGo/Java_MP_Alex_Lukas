package de.uk.java.feader.utils;
import java.awt.Font;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.uk.java.feader.gui.FeaderGUI;

public class FeaderUtils {
	
	public static final Font FONT_DEFAULT = new JLabel().getFont();
	public static final Font FONT_FEED_TITLE = FONT_DEFAULT.deriveFont(24).deriveFont(Font.BOLD);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd, HH:mm:SS");
	public static final SimpleDateFormat DATE_FORMAT_EXPORT = new SimpleDateFormat("yyyy.MM.dd-HH.mm.SS");

	
	/**
	 * Loads a ImageIcon from a .png file
	 * @param iconName The name of the file <b>without</b> the .png extension
	 * @param altText Alt text for the image
	 * @return ImageIcon built from the loaded image
	 */
	public static ImageIcon getIcon(String iconName, String altText) {
		URL imageUrl = FeaderGUI.class.getClassLoader().getResource(iconName + ".png");
		if (imageUrl == null) return null;
		return new ImageIcon(imageUrl, altText);
	}

}
