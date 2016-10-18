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

public class BucketAnalyzer {

	public static List<File> trainingFiles = new ArrayList<File>();
	public static List<File> testingFiles = new ArrayList<File>();

	public static void main(String[] args) {
		final File folderTesting = new File("C:\\Users\\franc\\Desktop\\Prog\\M2\\opl\\project 2\\nautilus-testing");
		final File folderTraining = new File("C:\\Users\\franc\\Desktop\\Prog\\M2\\opl\\project 2\\nautilus-training");
		getAllFiles(folderTraining, trainingFiles);
		getAllFiles(folderTesting, testingFiles);

		Map<File, List<String>> stackTraceTraining = new HashMap<File, List<String>>();
		Map<File, List<String>> stackTraceTesting = new HashMap<File, List<String>>();

		fillKeywordsMap(stackTraceTesting, testingFiles);
		fillKeywordsMap(stackTraceTraining, trainingFiles);

		int counter = 0;
		for (File trainingFile : stackTraceTraining.keySet())
			for (File testingFile : stackTraceTesting.keySet())
				if (stackTraceTesting.get(testingFile).equals(stackTraceTraining.get(trainingFile))) {
					System.out.println(testingFile.getName().substring(0, testingFile.getName().length() - 4) + " -> "
							+ trainingFile.getParent().substring(trainingFile.getParent().length() - 10));
					counter++;
				}
		System.out.println("" + counter);
	}

	private static void fillKeywordsMap(Map<File, List<String>> stackTraceMap, List<File> files) {
		FileReader fileReader = null;
		for (File file : files) {
			if (!stackTraceMap.containsKey(file))
				stackTraceMap.put(file, new ArrayList<String>());
			try {
				fileReader = new FileReader(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			BufferedReader br = new BufferedReader(fileReader);
			try {
				String line = null;

				while ((line = br.readLine()) != null) {
					analyzeFileLine(line, stackTraceMap, file);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.print(file.getName() + " keywords : ");
			for (String keyword : stackTraceMap.get(file))
				System.out.println(" --- " + keyword + " --- ");
		}
	}

	private static void analyzeFileLine(String line, Map<File, List<String>> keywordsMap, File file) {
		int inIndex = 0;
		int spaceIndex = 0;
		int atIndex;
		String keywordMethod = "";
		String keywordAt = "";
		String keyword = "";

		inIndex = line.indexOf(" in ");
		if (inIndex != -1) {
			spaceIndex = line.indexOf(" ", inIndex + 4);
			if (spaceIndex != -1) {
				keywordMethod = line.substring(inIndex + 4, spaceIndex);
				atIndex = line.indexOf(" at ");
				if (atIndex != -1) {
					spaceIndex = line.indexOf("\n", atIndex + 4);
					if (spaceIndex != -1) {
						keywordAt = line.substring(atIndex + 4, spaceIndex);
					}
				}
				keyword = keywordMethod + keywordAt;
				keywordsMap.get(file).add(keyword);
			}
		}
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
