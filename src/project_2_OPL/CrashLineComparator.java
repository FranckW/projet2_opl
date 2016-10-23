package project_2_OPL;

import java.util.ArrayList;

import keyword.Keyword;

public class CrashLineComparator {

	static double compareLines(CrashLine crashLine1, CrashLine crashLine2) {
		ArrayList<Double> distances = new ArrayList<Double>();

		if (crashLine1 == null || crashLine2 == null || crashLine1.getLineNumber() == null
				|| crashLine2.getLineNumber() == null)
			return -1;
		else {
			distances.add(StringSimilarity.similarity(crashLine1.getLineNumber().toString(),
					crashLine2.getLineNumber().toString()));

			for (Keyword keyword1 : crashLine1.getKeywords())
				for (Keyword keyword2 : crashLine2.getKeywords())
					distances.add(StringSimilarity.similarity(keyword1.getValue(), keyword2.getValue()));

			double sum = 0;
			for (Double distance : distances)
				sum += distance;
			return sum / distances.size();
		}
	}

}
