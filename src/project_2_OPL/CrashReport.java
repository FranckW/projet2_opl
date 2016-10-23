package project_2_OPL;

import java.util.ArrayList;
import java.util.List;

public class CrashReport {

	private List<CrashLine> crashLines = new ArrayList<CrashLine>();

	public CrashReport() {
	}

	public CrashReport(List<CrashLine> crashLines) {
		super();
		this.crashLines = crashLines;
	}

	public List<CrashLine> getCrashLines() {
		return crashLines;
	}

	public void setCrashLines(List<CrashLine> crashLines) {
		this.crashLines = crashLines;
	}

	public void addCrashLines(CrashLine crashLine) {
		crashLines.add(crashLine);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		for (CrashLine crashLine : crashLines)
			sb.append(crashLine.toString());
		return sb.toString();
	}

}
