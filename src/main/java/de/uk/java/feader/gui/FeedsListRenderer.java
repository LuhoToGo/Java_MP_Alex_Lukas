package de.uk.java.feader.gui;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import de.uk.java.feader.data.Feed;
import de.uk.java.feader.utils.FeaderUtils;


public class FeedsListRenderer implements ListCellRenderer<Feed> {
	
	@Override
	public Component getListCellRendererComponent(
			JList<? extends Feed> list,
			Feed value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
		
		JPanel feedPanel = new JPanel();
		feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
		feedPanel.setOpaque(list.isEnabled());
		feedPanel.setBackground(isSelected ? Color.DARK_GRAY : Color.WHITE);
		if (value == null) return feedPanel;
		
		feedPanel.add(Box.createVerticalStrut(5));
		feedPanel.setToolTipText(value.getDescription());
		
		JLabel title = new JLabel(value.getTitle());
		title.setFont(FeaderUtils.FONT_FEED_TITLE);
		title.setForeground(isSelected && list.isEnabled() ? Color.WHITE : Color.DARK_GRAY);
		title.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		feedPanel.add(title);
		
		JLabel info = new JLabel(
				value.getEntriesCount() + " entries" + 
				(value.getPublishedDateString() != null && value.getPublishedDateString().length() > 0
					? " (updated " + value.getPublishedDateString() + ")"
					: ""));
		info.setFont(FeaderUtils.FONT_DEFAULT);
		info.setForeground(isSelected && list.isEnabled() ? Color.WHITE : Color.GRAY);
		info.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		feedPanel.add(info);
		
		feedPanel.add(Box.createVerticalStrut(5));
		
		return feedPanel;
	}

}
