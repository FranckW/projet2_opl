package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import model.CrashReport;
import project_2_OPL.BucketAnalyzer;
import project_2_OPL.TestingFileAnalyzeThread;

public class CalculatorTest {

	@Test
	public void testingFile88ShouldBeInBucket000007600() {
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		final File testingFile = new File("nautilus-testing-test");
		final File trainingFile = new File("nautilus-training-test");
		bucketAnalyzer.getAllFiles(testingFile, bucketAnalyzer.testingFiles);
		bucketAnalyzer.getAllFiles(trainingFile, bucketAnalyzer.trainingFiles);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.testingFiles, bucketAnalyzer.testingFilesContent);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.trainingFiles, bucketAnalyzer.trainingFilesContent);

		Map<String, CrashReport> stackTraceTraining = new HashMap<String, CrashReport>();
		Map<String, CrashReport> stackTraceTesting = new HashMap<String, CrashReport>();
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.testingFiles, bucketAnalyzer.testingFilesContent);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.trainingFiles, bucketAnalyzer.trainingFilesContent);

		bucketAnalyzer.fillKeywordsMap(stackTraceTesting, bucketAnalyzer.testingFilesContent);
		bucketAnalyzer.fillKeywordsMap(stackTraceTraining, bucketAnalyzer.trainingFilesContent);

		Thread t = null;
		for (File testFile : bucketAnalyzer.testingFilesContent.keySet()) {
			TestingFileAnalyzeThread testingFileAnalyzeThread = new TestingFileAnalyzeThread(stackTraceTraining,
					stackTraceTesting, bucketAnalyzer.trainingFilesContent, bucketAnalyzer.testingFilesContent,
					testFile);
			t = new Thread(testingFileAnalyzeThread);
			t.start();
		}
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (String testingFileName : bucketAnalyzer.result.keySet())
			if (testingFileName.equals("88"))
				assertTrue(bucketAnalyzer.result.get(testingFileName).equals("000007600"));
	}
}
