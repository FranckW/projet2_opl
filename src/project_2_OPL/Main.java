package project_2_OPL;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		final File folderTesting = new File("nautilus-testing");
		final File folderTraining = new File("nautilus-training");
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		// bucketAnalyzer.analyze(folderTesting, folderTraining);
		bucketAnalyzer.analyzeBasic(folderTesting, folderTraining);
	}

}
