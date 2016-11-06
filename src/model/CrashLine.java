package model;

import java.util.ArrayList;
import java.util.List;

import keyword.Keyword;

public class CrashLine {

	private List<Keyword> keywords = new ArrayList<Keyword>();
	private Integer lineNumber;

	public CrashLine() {
	}

	public CrashLine(List<Keyword> keywords, Integer lineNumber) {
		super();
		this.keywords = keywords;
		this.lineNumber = lineNumber;
	}

	public List<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void addKeyword(Keyword keyword) {
		keywords.add(keyword);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("\n#" + lineNumber + " ");
		for (Keyword keyword : keywords)
			sb.append(keyword.toString() + " ");
		return sb.toString();
	}

}
