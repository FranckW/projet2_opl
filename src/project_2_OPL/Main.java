package project_2_OPL;

import java.io.File;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		final File folderTesting = new File("nautilus-testing");
		final File folderTraining = new File("nautilus-training");
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		Date startDate = new Date();
		System.out.println(startDate.toString());
		bucketAnalyzer.analyze(folderTesting, folderTraining);
		// bucketAnalyzer.analyzeBasic(folderTesting, folderTraining);
		Date endDate = new Date();
		System.out.println(endDate.toString());
	}

}
