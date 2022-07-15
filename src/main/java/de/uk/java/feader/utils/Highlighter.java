package de.uk.java.feader.utils;

public class Highlighter {
	
	/**
	 * Highlights all occurrences of a term (substring)
	 * in a text (string) in a case-insensitive way.
	 * Also processes <code>*</code> as wildcard character in <code>toHighlight</code>.
	 * @param text The text containing the string to highlight
	 * @param toHighlight The string to highlight
	 * @param highlightPrefix The prefix that gets prepended to the highlighted string
	 * @param highlightSuffix The suffix that gets appended to the highlighted string
	 * @return The resulting text with highlights
	 */
	public static String highlight(
			String text,
			String toHighlight,
			String highlightPrefix,
			String highlightSuffix) {
		
		//replace wildcard char (*) with regex
		toHighlight = toHighlight.replaceAll("\\*", "\\\\p{L}*?");
		//construct regex
		String regex = "(?i)\\b" + toHighlight + "\\b";
		//construct replacement
		String replacement = highlightPrefix + "$0" + highlightSuffix;
		
		//perform highlighting by replacement
		try {
			return text.replaceAll(regex, replacement);
		} catch (Exception e) {
			return text;
		}
	}

}
