package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import calculator.CrashLineComparator;
import keyword.FromKeyword;
import keyword.InKeyword;
import model.CrashLine;
import model.CrashReport;
import project_2_OPL.BucketAnalyzer;
import project_2_OPL.TestingFileAnalyzeThread;

public class CalculatorTest {

	@Test
	public void testingFile88ShouldBeInBucket000007600() {
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		loadFilesAndTheirContent(bucketAnalyzer);

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

	private void loadFilesAndTheirContent(BucketAnalyzer bucketAnalyzer) {
		final File testingFile = new File("nautilus-testing-test");
		final File trainingFile = new File("nautilus-training-test");
		bucketAnalyzer.getAllFiles(testingFile, bucketAnalyzer.testingFiles);
		bucketAnalyzer.getAllFiles(trainingFile, bucketAnalyzer.trainingFiles);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.testingFiles, bucketAnalyzer.testingFilesContent);
		bucketAnalyzer.loadContentOfFiles(bucketAnalyzer.trainingFiles, bucketAnalyzer.trainingFilesContent);
	}

	@Test
	public void testContentFileCorrectlyLoaded() {
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		loadFilesAndTheirContent(bucketAnalyzer);
		String real88FileContent = "#0  memset () at ../sysdeps/i386/i686/memset.S:85" + "No locals."
				+ "#1  0xb4f6e99c in ff_jpeg_fdct_islow (data=0x9c1f180)"
				+ "    at /build/buildd/ffmpeg-debian-0.svn20090204/libavcodec/jfdctint.c:250"
				+ "tmp0 = <value optimized out>" + "tmp1 = <value optimized out>" + "tmp2 = <value optimized out>"
				+ "tmp3 = <value optimized out>" + "tmp4 = <value optimized out>" + "tmp5 = <value optimized out>"
				+ "tmp6 = <value optimized out>" + "tmp7 = <value optimized out>" + "tmp10 = <value optimized out>"
				+ "tmp11 = <value optimized out>" + "tmp12 = <value optimized out>" + "tmp13 = <value optimized out>"
				+ "z1 = <value optimized out>" + "z2 = <value optimized out>" + "z3 = 1073590591"
				+ "z4 = <value optimized out>" + "z5 = <value optimized out>" + "dataptr = <value optimized out>"
				+ "#2  0xb740d170 in main_arena () from /lib/tls/i686/cmov/libc.so.6";
		real88FileContent.replaceAll("\t", "");
		real88FileContent.replaceAll("\n", "");
		for (File file : bucketAnalyzer.testingFiles)
			assertTrue(real88FileContent
					.equals(bucketAnalyzer.testingFilesContent.get(file).replaceAll("\t", "").replaceAll("\n", "")));
	}

	@Test
	public void testKeywordsWellExtractedFromLine() {
		String line = "#8  0xb7073bc2 in g_signal_emit () from /usr/lib/libgobject-2.0.so.0";
		CrashLine crashLine = new CrashLine();
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		bucketAnalyzer.getAllKeywordsFromLine(line, crashLine);
		assertTrue(crashLine.getKeywords().size() == 2);
		assertTrue(crashLine.getKeywords().get(0) instanceof InKeyword);
		assertTrue(crashLine.getKeywords().get(0).getValue().equals("g_signal_emit"));
		assertTrue(crashLine.getKeywords().get(1) instanceof FromKeyword);
		assertTrue(crashLine.getKeywords().get(1).getValue().equals("/usr/lib/libgobject-2.0.so.0"));
	}

	@Test
	public void testBinaryScoreCalculation() {
		String line1 = "#8  0xb7073bc2 in g_signal_emit () from /usr/lib/libgobject-2.0.so.0";
		String line2 = "#8  0xb7073bc2 in g_signal_emit () from /usr/lib/libgobject-2.0.so.0";
		String line3 = "#1  0xb4f6e99c in ff_jpeg_fdct_islow (data=0x9c1f180) at /build/buildd/ffmpeg-debian-0.svn20090204/libavcodec/jfdctint.c:250";
		CrashLine crashLine1 = new CrashLine();
		CrashLine crashLine2 = new CrashLine();
		CrashLine crashLine3 = new CrashLine();
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		bucketAnalyzer.getAllKeywordsFromLine(line1, crashLine1);
		bucketAnalyzer.getAllKeywordsFromLine(line2, crashLine2);
		bucketAnalyzer.getAllKeywordsFromLine(line3, crashLine3);
		crashLine1.setLineNumber(8);
		crashLine2.setLineNumber(8);
		crashLine3.setLineNumber(1);
		assertTrue(CrashLineComparator.compareLines(crashLine1, crashLine2, false) == 600.0);
		assertTrue(CrashLineComparator.compareLines(crashLine1, crashLine3, false) == 0.0);
	}
}
