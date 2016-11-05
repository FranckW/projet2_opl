package project_2_OPL;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestingFileAnalyzeThread implements Runnable {

	private Map<String, CrashReport> stackTraceTraining;
	private Map<String, CrashReport> stackTraceTesting;
	private Map<File, String> trainingFilesContent;
	private Map<File, String> testingFilesContent;
	private File testingFile;

	public TestingFileAnalyzeThread(Map<String, CrashReport> stackTraceTraining,
			Map<String, CrashReport> stackTraceTesting, Map<File, String> trainingFilesContent,
			Map<File, String> testingFilesContent, File testingFile) {
		super();
		this.stackTraceTraining = stackTraceTraining;
		this.stackTraceTesting = stackTraceTesting;
		this.trainingFilesContent = trainingFilesContent;
		this.testingFilesContent = testingFilesContent;
		this.testingFile = testingFile;
	}

	@Override
	public void run() {
		System.out.println("file titled : " + testingFile.getName());
		Map<String, Double> mapMatchValueBucketName = new HashMap<String, Double>();
		for (File trainingFile : trainingFilesContent.keySet()) {
			ArrayList<Double> lineMatchValues = new ArrayList<Double>();
			for (CrashLine crashLineTesting : stackTraceTesting.get(testingFilesContent.get(testingFile))
					.getCrashLines())
				if (stackTraceTraining.get(trainingFilesContent.get(trainingFile)) != null)
					for (CrashLine crashLineTraining : stackTraceTraining.get(trainingFilesContent.get(trainingFile))
							.getCrashLines()) {
						boolean isInMatchingSequence = false;
						double matchScore = CrashLineComparator.compareLines(crashLineTesting, crashLineTraining,
								isInMatchingSequence);
						if (matchScore < 100) {
							isInMatchingSequence = false;
						} else {
							isInMatchingSequence = true;
						}
						lineMatchValues.add(matchScore);
					}
			double sum = 0;
			for (Double distance : lineMatchValues)
				sum += distance;
			if (mapMatchValueBucketName.containsKey(trainingFile.getParentFile().getParent())) {
				if (mapMatchValueBucketName.get(trainingFile.getParentFile().getParent()) > sum)
					mapMatchValueBucketName.put(trainingFile.getParentFile().getParent(), sum);
			} else {
				mapMatchValueBucketName.put(trainingFile.getParentFile().getParent(), sum);
			}
			String bucket = null;
			Double maxMatchValue = 0.0;
			for (String bucketName : mapMatchValueBucketName.keySet()) {
				if (mapMatchValueBucketName.get(bucketName) > maxMatchValue) {
					maxMatchValue = mapMatchValueBucketName.get(bucketName);
					bucket = bucketName.substring(bucketName.length() - 9);
				}
			}
			BucketAnalyzer.result.put(testingFile.getName().substring(0, testingFile.getName().length() - 4), bucket);
		}
	}

}
