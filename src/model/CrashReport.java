package model;

import java.util.ArrayList;
import java.util.List;

public class CrashReport {

	private List<CrashLine> crashLines = new ArrayList<CrashLine>();

	public CrashReport() {
	}

	public List<CrashLine> getCrashLines() {
		return crashLines;
	}

	public void addCrashLines(CrashLine crashLine) {
		crashLines.add(crashLine);
	}

}
