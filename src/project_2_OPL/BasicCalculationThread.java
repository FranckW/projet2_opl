package project_2_OPL;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import calculator.CrashLineComparator;

public class BasicCalculationThread implements Runnable {

	private File testingFile;
	private String testingFileContent;
	private Map<File, String> trainingFilesContent;

	public BasicCalculationThread(String testingFileContent, Map<File, String> trainingFilesContent, File testingFile) {
		super();
		this.testingFileContent = testingFileContent;
		this.trainingFilesContent = trainingFilesContent;
		this.testingFile = testingFile;
	}

	@Override
	public void run() {
		System.out.println("file titled : " + testingFile.getName());
		Map<String, Double> mapMatchValueBucketName = new HashMap<String, Double>();
		for (File trainingFile : trainingFilesContent.keySet())
			mapMatchValueBucketName.put(trainingFile.getParentFile().getParent(), CrashLineComparator
					.stringSimilarityCalculation(testingFileContent, trainingFilesContent.get(trainingFile)));
		String bucket = null;
		Double maxMatchValue = 0.0;
		for (String bucketName : mapMatchValueBucketName.keySet()) {
			if (mapMatchValueBucketName.get(bucketName) > maxMatchValue) {
				maxMatchValue = mapMatchValueBucketName.get(bucketName);
				bucket = bucketName.substring(bucketName.length() - 9);
			}
		}
		System.out.println("file titled : " + testingFile.getName() + " analyzed");
		BucketAnalyzer.result.put(testingFile.getName().substring(0, testingFile.getName().length() - 4), bucket);

	}

}
