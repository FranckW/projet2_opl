package project_2_OPL;

import java.util.ArrayList;
import java.util.List;

import keyword.Keyword;

public class CrashLineComparator {

	static double compareLines(CrashLine crashLine1, CrashLine crashLine2) {
		ArrayList<Double> distances = new ArrayList<Double>();

		if (crashLine1 == null || crashLine2 == null || crashLine1.getLineNumber() == null
				|| crashLine2.getLineNumber() == null)
			return Double.MAX_VALUE;
		else {
			// levenshteinCalculation(crashLine1, crashLine2, distances);
			hammingCalculation(crashLine1, crashLine2, distances);
			// stringSimilarityCalculation(crashLine1, crashLine2, distances);

			double sum = 0;
			for (Double distance : distances)
				sum += distance;
			return sum / distances.size();
		}
	}

	private static void stringSimilarityCalculation(CrashLine crashLine1, CrashLine crashLine2,
			ArrayList<Double> distances) {
		distances.add(StringSimilarity.similarity(crashLine1.getLineNumber().toString(),
				crashLine2.getLineNumber().toString()));

		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords())
				distances.add(StringSimilarity.similarity(keyword1.getValue(), keyword2.getValue()));
	}

	private static void levenshteinCalculation(CrashLine crashLine1, CrashLine crashLine2, List<Double> distances) {
		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		int value = levenshteinDistance.apply(crashLine1.getLineNumber().toString(),
				crashLine2.getLineNumber().toString());
		distances.add(new Double(value));

		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords()) {
				value = levenshteinDistance.apply(keyword1.getValue(), keyword2.getValue());
				distances.add(new Double(value));
			}
	}

	private static void hammingCalculation(CrashLine crashLine1, CrashLine crashLine2, List<Double> distances) {
		HammingDistance hammingDistance = new HammingDistance();
		int value = hammingDistance.apply(crashLine1.getLineNumber().toString(), crashLine2.getLineNumber().toString());
		distances.add(new Double(value));

		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords()) {
				value = hammingDistance.apply(keyword1.getValue(), keyword2.getValue());
				distances.add(new Double(value));
			}
	}

}
