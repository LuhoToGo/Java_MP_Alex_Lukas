package de.uk.java.feader.data;

import java.io.Serializable;

import de.uk.java.feader.utils.Highlighter;


public class Entry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String ENTRY_HTML_TEMPLATE =
			"<div class=\"entry\">"
			+ "	<h2>###TITLE###</h2>"
			+ "	<div class=\"entry-source\">"
			+ "		<b>###DATE###</b>, ###PARENTFEED###"
			+ "	</div>"
			+ "	<p>###CONTENT###</p>"
			+ "	<a href=\"###URL###\">###URL###</a>"
			+ "</div>";
	
	private static final String PATTERN_VALID_LINK = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9"
			+ "@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
	

	private String title;
	private String content;
	private String linkUrl;
	private String publishedDate;
	private String parentFeedTitle;
	
	
	public Entry(String title) {
		super();
		this.title = title;
		this.content = "";
		this.linkUrl = "";
		this.publishedDate = "";
		this.parentFeedTitle = "";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content != null ? content : "";
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl != null ? linkUrl : "";
	}

	public String getPublishedDateString() {
		return publishedDate;
	}

	public void setPublishedDateString(String publishedDateString) {
		this.publishedDate = publishedDateString != null ? publishedDateString : "";
	}
	
	public String getParentFeedTitle() {
		return parentFeedTitle;
	}

	public void setParentFeedTitle(String parentFeedTitle) {
		this.parentFeedTitle = parentFeedTitle != null ? parentFeedTitle : "";
	}

	/**
	 * Returns a complete HTML representation of the title and content of this entry.
	 * @return HTML as string
	 */
	public String html() {
		return ENTRY_HTML_TEMPLATE
			.replaceAll("###PARENTFEED###", getParentFeedTitle())
			.replace("###DATE###", getPublishedDateString() != null ? getPublishedDateString() : "")
			.replace("###TITLE###", getTitle())
			.replace("###CONTENT###", getContent())
			.replace("###URL###", getLinkUrl().matches(PATTERN_VALID_LINK) ? getLinkUrl() : "");
	}
	
	/**
	 * Returns a complete HTML representation of the title and content of this entry,
	 * including highlighting for the given term (preTerm and postTerm strings will be
	 * prepended and appended, respectively)
	 * @param highlightTerm The term to highlight
	 * @param preTerm Prefix of highlighting
	 * @param postTerm Suffix of highlighting
	 * @return The HTML representation
	 */
	public String htmlHighlighted(String highlightTerm, String preTerm, String postTerm) {
		return ENTRY_HTML_TEMPLATE
			.replaceAll("###PARENTFEED###", getParentFeedTitle())
			.replace("###DATE###", getPublishedDateString() != null ? getPublishedDateString() : "")
			.replace("###TITLE###", Highlighter.highlight(getTitle(), highlightTerm, preTerm, postTerm))
			.replace("###CONTENT###", Highlighter.highlight(getContent(), highlightTerm, preTerm, postTerm))
			.replace("###URL###", getLinkUrl().matches(PATTERN_VALID_LINK) ? getLinkUrl() : "");
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Entry)
			&& ((Entry)obj).html().equals(html());
	}
	
	@Override
	public int hashCode() {
		return html().hashCode();
	}
	
}
