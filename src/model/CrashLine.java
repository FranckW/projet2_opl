package model;

import java.util.ArrayList;
import java.util.List;

import keyword.Keyword;

public class CrashLine {

	private List<Keyword> keywords = new ArrayList<Keyword>();
	private Integer lineNumber;

	public CrashLine() {
	}

	public List<Keyword> getKeywords() {
		return keywords;
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

}
