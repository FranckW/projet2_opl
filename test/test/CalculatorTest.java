package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import calculator.CrashLineComparator;
import model.CrashLine;
import model.CrashReport;
import project_2_OPL.BucketAnalyzer;
import project_2_OPL.TestingFileAnalyzeThread;

public class CalculatorTest {

	// @Test
	// public void testLoadContentOfFile() {
	// BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
	// final File testingFile = new File("nautilus-testing\\24.txt");
	// bucketAnalyzer.testingFiles.add(testingFile);
	// bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.testingFiles,
	// bucketAnalyzer.testingFilesContent);
	// System.out.println(bucketAnalyzer.testingFilesContent.get(testingFile));
	// assertTrue(bucketAnalyzer.testingFilesContent.get(testingFile)
	// .equals("#0 0x40814a26 in strcmp () from /lib/libc.so.6 \n" + "No symbol
	// table info available.\n"
	// + "#1 0x4032c7f0 in ?? () from /usr/lib/libgtk-x11-2.0.so.0 \n"
	// + "No symbol table info available. \n"
	// + "#2 0x4032c7f0 in ?? () from /usr/lib/libgtk-x11-2.0.so.0 \n"
	// + "No symbol table info available. \n"
	// + "Backtrace stopped: previous frame identical to this frame (corrupt
	// stack?)"));
	// }

	@Test
	public void testingFile88ShouldBeInBucket000007600() {
		// 88.txt devrait être dans
		// 000007600\\00003331427\\Stacktrace.txt
		// et non dans
		// 000015900\\0000553389\\StackTrace.txt
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		final File testingFile = new File("nautilus-testing-test");
		final File trainingFile = new File("nautilus-training-test");
		bucketAnalyzer.getAllFiles(testingFile, bucketAnalyzer.testingFiles);
		bucketAnalyzer.getAllFiles(trainingFile, bucketAnalyzer.trainingFiles);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.testingFiles, bucketAnalyzer.testingFilesContent);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.trainingFiles, bucketAnalyzer.trainingFilesContent);
		// for (File file : bucketAnalyzer.trainingFilesContent.keySet())
		// System.out.println(bucketAnalyzer.trainingFilesContent.get(file));

		Map<String, CrashReport> stackTraceTraining = new HashMap<String, CrashReport>();
		Map<String, CrashReport> stackTraceTesting = new HashMap<String, CrashReport>();
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.testingFiles, bucketAnalyzer.testingFilesContent);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.trainingFiles, bucketAnalyzer.trainingFilesContent);

		bucketAnalyzer.fillKeywordsMap(stackTraceTesting, bucketAnalyzer.testingFilesContent);
		bucketAnalyzer.fillKeywordsMap(stackTraceTraining, bucketAnalyzer.trainingFilesContent);

		Map<String, Double> mapMatchValueBucketName = new HashMap<String, Double>();
		for (File testFile : bucketAnalyzer.testingFilesContent.keySet())
			for (File trainFile : bucketAnalyzer.trainingFilesContent.keySet()) {
				ArrayList<Double> lineMatchValues = new ArrayList<Double>();
				for (CrashLine crashLineTesting : stackTraceTesting
						.get(bucketAnalyzer.testingFilesContent.get(testFile)).getCrashLines())
					if (stackTraceTraining.get(bucketAnalyzer.trainingFilesContent.get(trainFile)) != null)
						for (CrashLine crashLineTraining : stackTraceTraining
								.get(bucketAnalyzer.trainingFilesContent.get(trainFile)).getCrashLines()) {
							double matchScore = CrashLineComparator.compareLines(crashLineTesting, crashLineTraining);
							// if (matchScore > 0)
							// System.out.println("bucket : " +
							// trainFile.getParent() + " score : " +
							// matchScore);
						}
			}

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
			System.out.println(testingFileName + " -> " + bucketAnalyzer.result.get(testingFileName));

	}
}
