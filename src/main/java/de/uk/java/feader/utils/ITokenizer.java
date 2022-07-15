package de.uk.java.feader.utils;

import java.util.List;

public interface ITokenizer {
	
	/**
	 * Tokenizes the input text into a <code>List</code>
	 * of <code>String</code> objects representing the
	 * single tokens of the input text.
	 * A token is supposed to be a single word of the input text, not
	 * containing anything but language letter characters, so that
	 * <code>"Feader is called Feader, right?"</code> becomes
	 * a <code>List</code> like <code>[Feader, is, called, Feader, right]</code>.
	 * <b>Also, the tokenizer is supposed to ignore HTML tags and only process text contents,
	 * so that "&lt;h1&gt;This is a heading!!!&lt;/h1&gt;" is tokenized to
	 * "[This, is, a, heading]" !</b>
	 * @param text The text to tokenize
	 * @return The <code>List</code> containing the tokens as strings
	 */
	public List<String> tokenize(String text);

}

