package project_2_OPL;

import java.util.ArrayList;
import java.util.List;

import keyword.Keyword;

public class CrashLineComparator {

	static double compareLines(CrashLine crashLine1, CrashLine crashLine2, boolean isInMatchingSequence) {
		ArrayList<Double> distances = new ArrayList<Double>();

		if (crashLine1 == null || crashLine2 == null || crashLine1.getLineNumber() == null
				|| crashLine2.getLineNumber() == null)
			return 0;
		else {
			// levenshteinCalculation(crashLine1, crashLine2, distances);
			// hammingCalculation(crashLine1, crashLine2, distances);
			// stringSimilarityCalculation(crashLine1, crashLine2, distances);
			binaryStringSimilarityCalculation(crashLine1, crashLine2, distances, isInMatchingSequence);

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
				if (!keyword1.getValue().equals("??") && !keyword2.getValue().equals("??")) {
					distances.add(StringSimilarity.similarity(keyword1.getValue(), keyword2.getValue()));
				} else
					distances.add(0.0);
	}

	private static void levenshteinCalculation(CrashLine crashLine1, CrashLine crashLine2, List<Double> distances) {
		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		int value = levenshteinDistance.apply(crashLine1.getLineNumber().toString(),
				crashLine2.getLineNumber().toString());
		distances.add(new Double(value));

		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords()) {
				if (!keyword1.getValue().equals("??") && !keyword2.getValue().equals("??")) {
					value = levenshteinDistance.apply(keyword1.getValue(), keyword2.getValue());
					distances.add(new Double(value));
				} else
					distances.add(0.0);
			}
	}

	private static void hammingCalculation(CrashLine crashLine1, CrashLine crashLine2, List<Double> distances) {
		HammingDistance hammingDistance = new HammingDistance();
		int value = hammingDistance.apply(crashLine1.getLineNumber().toString(), crashLine2.getLineNumber().toString());
		distances.add(new Double(value));

		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords()) {
				if (!keyword1.getValue().equals("??") && !keyword2.getValue().equals("??")) {
					value = hammingDistance.apply(keyword1.getValue(), keyword2.getValue());
					distances.add(new Double(value));
				} else
					distances.add(0.0);
			}
	}

	private static void binaryStringSimilarityCalculation(CrashLine crashLine1, CrashLine crashLine2,
			ArrayList<Double> distances, boolean isInMatchingSequence) {
		Double score = 0.0;
		if (crashLine1.getLineNumber().toString().equals(crashLine2.getLineNumber().toString()))
			score += 1;
		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords())
				if (!keyword1.getValue().equals("??") && !keyword2.getValue().equals("??"))
					if (keyword1.getClass().getName().equals(keyword2.getClass().getName())
							&& keyword1.getValue().equals(keyword2.getValue()))
						if (isInMatchingSequence)
							score += 2000;
						else
							score += 200;
		distances.add(score);
	}

}
