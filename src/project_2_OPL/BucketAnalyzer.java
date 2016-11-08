package project_2_OPL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import keyword.AtKeyword;
import keyword.FromKeyword;
import keyword.InKeyword;
import keyword.Keyword;
import model.CrashLine;
import model.CrashReport;

public class BucketAnalyzer {

	public List<File> trainingFiles = new ArrayList<File>();
	public List<File> testingFiles = new ArrayList<File>();

	public Map<File, String> trainingFilesContent = new HashMap<File, String>();
	public Map<File, String> testingFilesContent = new HashMap<File, String>();

	Map<String, CrashReport> stackTraceTraining = new HashMap<String, CrashReport>();
	Map<String, CrashReport> stackTraceTesting = new HashMap<String, CrashReport>();

	public static Map<String, String> result = new HashMap<String, String>();

	public void analyze(File folderTesting, File folderTraining) {
		extractData(folderTesting, folderTraining);

		List<Thread> threads = new ArrayList<Thread>();
		for (File testingFile : testingFilesContent.keySet()) {
			TestingFileAnalyzeThread testingFileAnalyzeThread = new TestingFileAnalyzeThread(stackTraceTraining,
					stackTraceTesting, trainingFilesContent, testingFilesContent, testingFile);
			Thread t = new Thread(testingFileAnalyzeThread);
			threads.add(t);
			t.start();
		}

		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		printResult();
	}

	public void analyzeBasic(File folderTesting, File folderTraining) {
		extractData(folderTesting, folderTraining);

		List<Thread> threads = new ArrayList<Thread>();
		for (File testingFile : testingFilesContent.keySet()) {
			BasicCalculationThread testBasicThread = new BasicCalculationThread(testingFilesContent.get(testingFile),
					trainingFilesContent, testingFile);
			Thread t = new Thread(testBasicThread);
			threads.add(t);
			t.start();
		}

		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		printResult();
	}

	private void printResult() {
		SortedSet<String> sortedList = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return Integer.valueOf(a).compareTo(Integer.valueOf(b));
			}
		});
		sortedList.addAll(result.keySet());
		for (String testingFileName : sortedList)
			System.out.println(testingFileName + " -> " + result.get(testingFileName));
	}

	private void extractData(File folderTesting, File folderTraining) {
		getAllFiles(folderTraining, trainingFiles);
		getAllFiles(folderTesting, testingFiles);

		// dummy version random
		// List<String> buckets = new ArrayList<String>();
		// for (File trainingFile : trainingFiles) {
		// String bucketname = trainingFile.getParentFile().getParent()
		// .substring(trainingFile.getParentFile().getParent().length() - 9);
		// if (!buckets.contains(bucketname))
		// buckets.add(bucketname);
		// }
		// for (File testingFile : testingFiles) {
		// int rand = (int) (Math.random() * buckets.size() - 1);
		// result.put(testingFile.getName().substring(0,
		// testingFile.getName().length() - 4), buckets.get(rand));
		// }

		loadContentOfFiles(testingFiles, testingFilesContent);
		loadContentOfFiles(trainingFiles, trainingFilesContent);

		fillKeywordsMap(stackTraceTesting, testingFilesContent);
		fillKeywordsMap(stackTraceTraining, trainingFilesContent);

		// for (File file : testingFilesContent.keySet()) {
		// System.out.println(file.getName());
		// for (CrashLine crashLine :
		// stackTraceTesting.get(testingFilesContent.get(file)).getCrashLines())
		// {
		// System.out.println("#" + crashLine.getLineNumber());
		// for (Keyword keyword : crashLine.getKeywords())
		// System.out.print(" " + keyword.getValue());
		// }
		// }
	}

	public void loadContentOfFiles(List<File> files, Map<File, String> filesContent) {
		FileReader fileReader = null;
		for (File file : files) {
			try {
				fileReader = new FileReader(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fileReader);
			try {
				String line = null;
				StringBuffer sb = new StringBuffer("");
				while ((line = br.readLine()) != null)
					sb.append(line + "\n");
				filesContent.put(file, sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void fillKeywordsMap(Map<String, CrashReport> stackTraceMap, Map<File, String> filesContent) {
		String fileContent = null;
		for (File file : filesContent.keySet()) {
			fileContent = filesContent.get(file);
			CrashReport crashReport = new CrashReport();
			if (!stackTraceMap.containsKey(fileContent))
				stackTraceMap.put(fileContent, crashReport);
			else
				return;
			Scanner scanner = new Scanner(fileContent);
			String line = null;
			CrashLine crashLine = new CrashLine();
			while (scanner.hasNext()) {
				line = scanner.nextLine();
				if (line.startsWith("#")) {
					if (crashLine.getLineNumber() != null)
						stackTraceMap.get(fileContent).addCrashLines(crashLine);
					crashLine = new CrashLine();
					Integer index = line.indexOf(" ");
					Integer crashLineNumber = Integer.parseInt(line.substring(1, index));
					crashLine.setLineNumber(crashLineNumber);
				}
				getAllKeywordsFromLine(line, crashLine);
			}
			if (crashLine.getLineNumber() != null)
				stackTraceMap.get(fileContent).addCrashLines(crashLine);
			scanner.close();
		}

	}

	public void getAllKeywordsFromLine(String line, CrashLine crashLine) {
		Scanner scanner = new Scanner(line);
		String word = null;
		Keyword keyword;
		while (scanner.hasNext()) {
			String keywordValue = scanner.next();
			switch (keywordValue) {
			case "in":
				word = scanner.next();
				if (word != null) {
					keyword = new InKeyword(word);
					crashLine.addKeyword(keyword);
				}
				break;
			case "at":
				word = scanner.next();
				if (word != null) {
					keyword = new AtKeyword(word);
					crashLine.addKeyword(keyword);
				}
				break;
			case "from":
				word = scanner.next();
				if (word != null) {
					keyword = new FromKeyword(word);
					crashLine.addKeyword(keyword);
				}
				break;
			default:
				break;
			}
		}
		scanner.close();

	}

	public void getAllFiles(File folder, List<File> fileList) {
		for (final File fileEntry : folder.listFiles())
			if (fileEntry.isDirectory())
				getAllFiles(fileEntry, fileList);
			else if (!fileList.contains(fileEntry))
				fileList.add(fileEntry);
	}

}
