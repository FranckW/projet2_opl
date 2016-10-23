package project_2_OPL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import keyword.AtKeyword;
import keyword.FromKeyword;
import keyword.InKeyword;
import keyword.Keyword;

public class BucketAnalyzer {

	public static List<File> trainingFiles = new ArrayList<File>();
	public static List<File> testingFiles = new ArrayList<File>();

	public static void main(String[] args) {
		final File folderTesting = new File("nautilus-testing");
		final File folderTraining = new File("nautilus-training");
		getAllFiles(folderTraining, trainingFiles);
		getAllFiles(folderTesting, testingFiles);

		Map<File, CrashReport> stackTraceTraining = new HashMap<File, CrashReport>();
		Map<File, CrashReport> stackTraceTesting = new HashMap<File, CrashReport>();

		fillKeywordsMap(stackTraceTesting, testingFiles);
		fillKeywordsMap(stackTraceTraining, trainingFiles);

		int counter = 0;
		// for (File trainingFile : stackTraceTraining.keySet())
		// System.out.println(stackTraceTraining.get(trainingFile).toString());
		//
		// for (File testingFile : stackTraceTesting.keySet()) {
		// System.out.println(stackTraceTesting.get(testingFile).toString());
		// }

		for (File testingFile : stackTraceTesting.keySet()) {
			System.out.println(testingFile.getName());
			for (File trainingFile : stackTraceTraining.keySet())
				if (stackTraceTesting.get(testingFile).toString()
						.equals(stackTraceTraining.get(trainingFile).toString())) {
					System.out.println(testingFile.getName().substring(0, testingFile.getName().length() - 4) + " -> "
							+ trainingFile.getParent().substring(trainingFile.getParent().length() - 10));
					counter++;
				}
		}
		System.out.println("" + counter);
	}

	private static void fillKeywordsMap(Map<File, CrashReport> stackTraceMap, List<File> files) {
		FileReader fileReader = null;
		for (File file : files) {
			CrashReport crashReport = new CrashReport();
			if (!stackTraceMap.containsKey(file))
				stackTraceMap.put(file, crashReport);
			else
				return;
			try {
				fileReader = new FileReader(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			BufferedReader br = new BufferedReader(fileReader);
			try {
				String line = null;
				CrashLine crashLine = new CrashLine();

				while ((line = br.readLine()) != null) {
					if (line.startsWith("#")) {
						stackTraceMap.get(file).addCrashLines(crashLine);
						crashLine = new CrashLine();
						Integer index = line.indexOf(" ");
						Integer crashLineNumber = Integer.parseInt(line.substring(1, index));
						crashLine.setLineNumber(crashLineNumber);
					}
					getAllKeywordsFromLine(line, crashLine);
				}
				stackTraceMap.get(file).getCrashLines().add(crashLine);
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

	private static void getAllKeywordsFromLine(String line, CrashLine crashLine) {
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

	public static void getAllFiles(File folder, List<File> fileList) {
		for (final File fileEntry : folder.listFiles())
			if (fileEntry.isDirectory()) {
				getAllFiles(fileEntry, fileList);
			} else {
				fileList.add(fileEntry);
			}
	}

}
