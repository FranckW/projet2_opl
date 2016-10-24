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

public class BucketAnalyzer {

	public static List<File> trainingFiles = new ArrayList<File>();
	public static List<File> testingFiles = new ArrayList<File>();

	public static Map<File, String> trainingFilesContent = new HashMap<File, String>();
	public static Map<File, String> testingFilesContent = new HashMap<File, String>();

	public static void main(String[] args) {
		final File folderTesting = new File("nautilus-testing");
		final File folderTraining = new File("nautilus-training");
		getAllFiles(folderTraining, trainingFiles);
		getAllFiles(folderTesting, testingFiles);

		Map<String, CrashReport> stackTraceTraining = new HashMap<String, CrashReport>();
		Map<String, CrashReport> stackTraceTesting = new HashMap<String, CrashReport>();
		Map<String, String> result = new HashMap<String, String>();

		loadContentOfFiles(testingFiles, testingFilesContent);
		loadContentOfFiles(trainingFiles, trainingFilesContent);

		fillKeywordsMap(stackTraceTesting, testingFilesContent);
		fillKeywordsMap(stackTraceTraining, trainingFilesContent);

		int counter = 0;
		// for(File testingFile : testingFilesContent.keySet())
		// for(File trainingFile : trainingFilesContent.keySet()) {
		// Double maxValue = 0.0;
		//
		// Double distance =
		// StringSimilarity.similarity(testingFilesContent.get(testingFile),
		// trainingFilesContent.get(trainingFile));
		// if distance
		// }
		for (File testingFile : testingFilesContent.keySet()) {
			System.out.println("file number : " + counter++ + " titled : " + testingFile.getName());
			Map<String, Double> mapMatchValueBucketName = new HashMap<String, Double>();
			for (File trainingFile : trainingFilesContent.keySet()) {
				ArrayList<Double> lineMatchValues = new ArrayList<Double>();
				for (CrashLine crashLineTesting : stackTraceTesting.get(testingFilesContent.get(testingFile))
						.getCrashLines())
					if (stackTraceTraining.get(trainingFilesContent.get(trainingFile)) != null)
						for (CrashLine crashLineTraining : stackTraceTraining
								.get(trainingFilesContent.get(trainingFile)).getCrashLines())
							lineMatchValues.add(CrashLineComparator.compareLines(crashLineTesting, crashLineTraining));
				double sum = 0;
				for (Double distance : lineMatchValues)
					sum += distance;
				mapMatchValueBucketName.put(trainingFile.getParent(), sum);
			}
			String bucket = null;
			Double maxMatchValue = 1000000000.0;
			for (String bucketName : mapMatchValueBucketName.keySet()) {
				if (mapMatchValueBucketName.get(bucketName) < maxMatchValue) {
					maxMatchValue = mapMatchValueBucketName.get(bucketName);
					bucket = bucketName.substring(bucketName.length() - 10);
				}
			}
			result.put(testingFile.getName().substring(0, testingFile.getName().length() - 4), bucket);
		}

		counter = 0;
		SortedSet<String> sortedList = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return Integer.valueOf(a).compareTo(Integer.valueOf(b));
			}
		});
		sortedList.addAll(result.keySet());
		for (String testingFileName : sortedList) {
			System.out.println(testingFileName + " -> " + result.get(testingFileName));
			counter++;
		}
		System.out.println("" + counter);
	}

	private static void loadContentOfFiles(List<File> files, Map<File, String> filesContent) {
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

	private static void fillKeywordsMap(Map<String, CrashReport> stackTraceMap, Map<File, String> filesContent) {
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
					stackTraceMap.get(fileContent).addCrashLines(crashLine);
					crashLine = new CrashLine();
					Integer index = line.indexOf(" ");
					Integer crashLineNumber = Integer.parseInt(line.substring(1, index));
					crashLine.setLineNumber(crashLineNumber);
				}
				getAllKeywordsFromLine(line, crashLine);
			}
			stackTraceMap.get(fileContent).getCrashLines().add(crashLine);
			scanner.close();
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
