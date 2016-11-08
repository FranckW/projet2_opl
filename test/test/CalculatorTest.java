package test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import calculator.CrashLineComparator;
import calculator.HammingDistance;
import calculator.LevenshteinDistance;
import calculator.StringSimilarity;
import keyword.FromKeyword;
import keyword.InKeyword;
import model.CrashLine;
import project_2_OPL.BucketAnalyzer;

public class CalculatorTest {

	@SuppressWarnings("static-access")
	@Test
	public void testAnalyzeFile88ShouldBeInBucket000007600() {
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		final File testingFile = new File("nautilus-testing-test");
		final File trainingFile = new File("nautilus-training-test");
		bucketAnalyzer.analyze(testingFile, trainingFile);

		for (String testingFileName : bucketAnalyzer.result.keySet())
			if (testingFileName.equals("88"))
				assertTrue(bucketAnalyzer.result.get(testingFileName).equals("000007600"));
	}

	@SuppressWarnings("static-access")
	@Test
	public void testBasicCalculator() {
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		final File testingFile = new File("nautilus-testing-test");
		final File trainingFile = new File("nautilus-training-test");
		bucketAnalyzer.analyzeBasic(testingFile, trainingFile);

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
		CrashLine crashLine = setupCrashLine1();
		assertTrue(crashLine.getKeywords().size() == 2);
		assertTrue(crashLine.getKeywords().get(0) instanceof InKeyword);
		assertTrue(crashLine.getKeywords().get(0).getValue().equals("g_signal_emit"));
		assertTrue(crashLine.getKeywords().get(1) instanceof FromKeyword);
		assertTrue(crashLine.getKeywords().get(1).getValue().equals("/usr/lib/libgobject-2.0.so.0"));
	}

	private CrashLine setupCrashLine1() {
		String line = "#8  0xb7073bc2 in g_signal_emit () from /usr/lib/libgobject-2.0.so.0";
		CrashLine crashLine = new CrashLine();
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		bucketAnalyzer.getAllKeywordsFromLine(line, crashLine);
		crashLine.setLineNumber(8);
		return crashLine;
	}

	private CrashLine setupCrashLine2() {
		String line = "#9  0xb7073bc2 in g_signal_emit () from /usr/lip/libgobject-2.0.so.0";
		CrashLine crashLine = new CrashLine();
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		bucketAnalyzer.getAllKeywordsFromLine(line, crashLine);
		crashLine.setLineNumber(9);
		return crashLine;
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
		assertTrue(CrashLineComparator.compareLines(crashLine1, crashLine2) == 600.0);
		assertTrue(CrashLineComparator.compareLines(crashLine1, crashLine3) == 0.0);
	}

	@Test
	public void testHammingCalculation() {
		CrashLine crashLine1 = setupCrashLine1();
		CrashLine crashLine2 = setupCrashLine2();
		assertTrue(CrashLineComparator.hammingCalculation(crashLine1, crashLine2) == 2);
		assertTrue(CrashLineComparator.hammingCalculation(crashLine1, crashLine1) == 0);
	}

	@Test
	public void testLevenshteinCalculation() {
		CrashLine crashLine1 = setupCrashLine1();
		CrashLine crashLine2 = setupCrashLine2();
		assertTrue(CrashLineComparator.levenshteinCalculation(crashLine1, crashLine2) == 2);
		assertTrue(CrashLineComparator.levenshteinCalculation(crashLine1, crashLine1) == 0);
	}

	@Test
	public void testStringSimilarity() {
		String line1 = "#9  0xb7073bc2 in g_signal_emit () from /usr/lib/libgobject-2.0.so.0";
		String line2 = "#8  0xb7073bc2 in g_signal_emit () from /usr/lib/libgobject-2.0.so.0";
		assertTrue(CrashLineComparator.stringSimilarityCalculation(line1, line1) == 1);
		assertTrue(CrashLineComparator.stringSimilarityCalculation(line1, line2) < 1);
	}

	@Test
	public void testTooLongFileIsPutInDefaultBucket() {
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		final File testTooLongFile = new File("testTooLongFile");
		final File trainingFile = new File("nautilus-training");
		bucketAnalyzer.analyzeBasic(testTooLongFile, trainingFile);
		for (String testingFileName : bucketAnalyzer.result.keySet())
			if (testingFileName.equals("98"))
				assertTrue(bucketAnalyzer.result.get(testingFileName).equals("000000000"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHammingNullArgsRaiseException() {
		HammingDistance hammingDistance = new HammingDistance();
		hammingDistance.apply(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLevenshteinNullArgsRaiseException() {
		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		levenshteinDistance.apply(null, null);
	}

	@Test
	public void testHammingReturnsIntegerMaxValueWhenArgsNotSameSize() {
		HammingDistance hammingDistance = new HammingDistance();
		assertTrue(hammingDistance.apply("foo", "foofoo").equals(Integer.MAX_VALUE));
	}

	@Test
	public void testSimilarityReturnsOneWhenEmptyStrings() {
		assertTrue(StringSimilarity.similarity("", "") == 1.0);
	}

	@Test
	public void testLevenshteinReturnsOtherStringsSizeWhenEmptyString() {
		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		assertTrue(levenshteinDistance.apply("", "foo") == 3);
		assertTrue(levenshteinDistance.apply("foo", "") == 3);
	}

	@Test
	public void testLevenshteinSwapStringsWhenFirstIsShorter() {
		LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		assertTrue(levenshteinDistance.apply("fooo", "fo") == levenshteinDistance.apply("fo", "fooo"));
	}

	@Test
	public void testCompareLinesReturnsZeroWhenACrashLineIsNull() {
		assertTrue(CrashLineComparator.compareLines(null, setupCrashLine1()) == 0.0);
		assertTrue(CrashLineComparator.compareLines(setupCrashLine1(), null) == 0.0);
	}

	@Test
	public void testCompareLinesReturnsZeroWhenLineNumberMissing() {
		String line = "#8  0xb7073bc2 in g_signal_emit () from /usr/lib/libgobject-2.0.so.0";
		CrashLine crashLine = new CrashLine();
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();
		bucketAnalyzer.getAllKeywordsFromLine(line, crashLine);
		assertTrue(CrashLineComparator.compareLines(crashLine, setupCrashLine1()) == 0.0);
		assertTrue(CrashLineComparator.compareLines(setupCrashLine1(), crashLine) == 0.0);
	}

	@Test
	public void testUnknownKeywordAreSkipped() {
		BucketAnalyzer bucketAnalyzer = new BucketAnalyzer();

		String line1 = "#8  0xb7073bc2 in ?? ()";
		CrashLine crashLine1 = new CrashLine();
		bucketAnalyzer.getAllKeywordsFromLine(line1, crashLine1);

		String line2 = "#8  0xb7073bc2 in ?? ()";
		CrashLine crashLine2 = new CrashLine();
		bucketAnalyzer.getAllKeywordsFromLine(line2, crashLine2);

		crashLine1.setLineNumber(8);
		crashLine2.setLineNumber(8);

		assertTrue(CrashLineComparator.compareLines(crashLine1, crashLine2) == 200.0);
		assertTrue(CrashLineComparator.hammingCalculation(crashLine1, crashLine2) == 0);
		assertTrue(CrashLineComparator.levenshteinCalculation(crashLine1, crashLine2) == 0);

	}
}
