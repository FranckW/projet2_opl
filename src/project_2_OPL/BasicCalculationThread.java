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
		if (testingFile.length() > 100000) {
			System.out.println("size too high");
			BucketAnalyzer.result.put(testingFile.getName().substring(0, testingFile.getName().length() - 4),
					"000000000");
			return;
		}
		System.out.println("file titled : " + testingFile.getName());
		Map<String, Double> mapMatchValueBucketName = new HashMap<String, Double>();
		for (File trainingFile : trainingFilesContent.keySet()) {
			if (!mapMatchValueBucketName.containsKey(trainingFile.getParentFile().getParent()))
				mapMatchValueBucketName.put(trainingFile.getParentFile().getParent(), 0.0);
			double matchValue = CrashLineComparator.stringSimilarityCalculation(testingFileContent,
					trainingFilesContent.get(trainingFile));
			if (mapMatchValueBucketName.get(trainingFile.getParentFile().getParent()) < matchValue)
				mapMatchValueBucketName.put(trainingFile.getParentFile().getParent(), matchValue);
		}
		String bucket = null;
		Double maxMatchValue = 0.0;
		for (String bucketName : mapMatchValueBucketName.keySet())
			if (mapMatchValueBucketName.get(bucketName) > maxMatchValue) {
				maxMatchValue = mapMatchValueBucketName.get(bucketName);
				bucket = bucketName.substring(bucketName.length() - 9);
			}
		BucketAnalyzer.result.put(testingFile.getName().substring(0, testingFile.getName().length() - 4), bucket);
	}

}
