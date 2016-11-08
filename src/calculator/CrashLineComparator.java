package calculator;

import keyword.Keyword;
import model.CrashLine;

public class CrashLineComparator {

	public static double compareLines(CrashLine crashLine1, CrashLine crashLine2, boolean isInMatchSequence) {
		if (crashLine1 == null || crashLine2 == null || crashLine1.getLineNumber() == null
				|| crashLine2.getLineNumber() == null)
			return 0.0;
		else {
			// return levenshteinCalculation(crashLine1, crashLine2);
			// return hammingCalculation(crashLine1, crashLine2);
			// return stringSimilarityCalculation(crashLine1, crashLine2);
			return binaryStringSimilarityCalculation(crashLine1, crashLine2, isInMatchSequence);
		}
	}

	public static double stringSimilarityCalculation(String fileContent1, String fileContent2) {
		return StringSimilarity.similarity(fileContent1, fileContent2);
	}

	private static double levenshteinCalculation(CrashLine crashLine1, CrashLine crashLine2) {
		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		Double score = 0.0;
		int value = levenshteinDistance.apply(crashLine1.getLineNumber().toString(),
				crashLine2.getLineNumber().toString());
		score += value;

		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords())
				if (!keyword1.getValue().equals("??") && !keyword2.getValue().equals("??")) {
					value = levenshteinDistance.apply(keyword1.getValue(), keyword2.getValue());
					score += value;
				}
		return score;
	}

	private static double hammingCalculation(CrashLine crashLine1, CrashLine crashLine2) {
		HammingDistance hammingDistance = new HammingDistance();
		Double score = 0.0;
		int value = hammingDistance.apply(crashLine1.getLineNumber().toString(), crashLine2.getLineNumber().toString());
		score += value;

		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords())
				if (!keyword1.getValue().equals("??") && !keyword2.getValue().equals("??")) {
					value = hammingDistance.apply(keyword1.getValue(), keyword2.getValue());
					score += value;
				}
		return score;
	}

	private static double binaryStringSimilarityCalculation(CrashLine crashLine1, CrashLine crashLine2,
			boolean isInMatchSequence) {
		Double score = 0.0;
		if (crashLine1.getLineNumber().toString().equals(crashLine2.getLineNumber().toString()))
			score += 1;
		for (Keyword keyword1 : crashLine1.getKeywords())
			for (Keyword keyword2 : crashLine2.getKeywords())
				if (!keyword1.getValue().equals("??") && !keyword2.getValue().equals("??"))
					if (keyword1.getClass().getName().equals(keyword2.getClass().getName())
							&& keyword1.getValue().equals(keyword2.getValue()))
						// if (isInMatchSequence)
						// score += 2000;
						// else
						score += 200;
		return score;
	}

}
