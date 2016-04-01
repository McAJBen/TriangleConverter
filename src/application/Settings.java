package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {
	
	private static final String
		// BOOLEANS
		PREDRAW_ID = "PREDRAW",
		POST_PROCESSING_ID = "POST_PROCESSING",
		DISPLAY_ID = "DISPLAY",
		SAMPLES_RANDOM_ID = "SAMPLES_RANDOM",
		// INTEGERS
		BLOCKS_WIDE_ID = "BLOCKS_WIDE",
		MAX_TRIANGLES_ID = "MAX_TRIANGLES",
		SAMPLES_ID = "SAMPLES",
		THREAD_COUNT_ID = "THREAD_COUNT",
		REPAINT_WAIT_ID = "REAPINT_WAIT_MS",
		ATTEMPTS_ID = "ATTEMPTS",
		// DOUBLES
		SCALE_ID = "SCALE",
		POST_SCALE_ID = "POST_SCALE",
		// FORMAT
		IDENTIFIER_SYMBOL = ":",
		COMMENT_SYMBOL = "#",
		RANDOM_ID = "RANDOM";
	
	@SuppressWarnings("resource")
	public static void load() {
		String settingsString = null;
		BufferedReader br = null;
		try {
			File settingsFile = new File(
					System.getProperty("user.dir") + "\\TriangleConverter.settings");
			// check if settings exist and read first line
			if (settingsFile.exists()) {
				br = new BufferedReader(new FileReader(settingsFile));
			}
			// if the settings file does not exist, go to catch
			else throw new IOException("Settings File does not exist");
			
		} catch (IOException e1) {
			System.out.println("Default settings have been set");
			createSettingsFile();
			return;
		}
		
		while (true) {
			// read a line
			try {
				settingsString = br.readLine();
				if (settingsString == null) {
					throw new IOException("No Line To Read");
				}
			} catch (IOException e) {
				// can't read another line, the file must be done
				break;
			}
			// if line == null continue to check if there are any more lines
			// if line begins with comment symbol, ignore the line
			if (settingsString != null && !settingsString.startsWith(COMMENT_SYMBOL)) {
				
				String[] split = settingsString.split(IDENTIFIER_SYMBOL);
				// if line does not have identifier ignore it
				if (split.length != 2) {
					continue;
				}
				switch (split[0]) {
					// BOOLEANS
					case PREDRAW_ID:
						G.preDraw = Boolean.parseBoolean(split[1]);
						break;
					case POST_PROCESSING_ID:
						G.postProcessing = Boolean.parseBoolean(split[1]);
						break;
					case DISPLAY_ID:
						G.display = Boolean.parseBoolean(split[1]);
						break;
					case SAMPLES_RANDOM_ID:
						G.samplesRandom = Boolean.parseBoolean(split[1]);
						break;
					// INTEGERS
					case BLOCKS_WIDE_ID:
						if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.blocksWideRandom = true;
						}
						else {
							G.blocksWide = Integer.parseInt(split[1]);
						}
						break;
					case MAX_TRIANGLES_ID:
						if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.maxTrianglesRandom = true;
						}
						else {
							G.maxTriangles = Integer.parseInt(split[1]);
						}
						break;
					case SAMPLES_ID:
						if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.samplesRandom = true;
						}
						else {
							G.samples = Integer.parseInt(split[1]);
						}
						break;
					case THREAD_COUNT_ID:
						if (split[1].equalsIgnoreCase("AUTO")) {
							G.threadCount = Runtime.getRuntime().availableProcessors();
						}
						else {
							G.threadCount = Integer.parseInt(split[1]);
						}
						break;
					case REPAINT_WAIT_ID:
						G.repaintWait = Integer.parseInt(split[1]);
						break;
					case ATTEMPTS_ID:
						G.attempts = Integer.parseInt(split[1]);
						break;
					// DOUBLES
					case SCALE_ID:
						G.scale = Double.parseDouble(split[1]);
						break;
					case POST_SCALE_ID:
						G.postScale = Double.parseDouble(split[1]);
						break;
					default: // any unknown ID is ignored
						break;
				}
			}
		}
	}

	private static void createSettingsFile() {
		// create default settings strings
		String settingsString = 
				COMMENT_SYMBOL + "All Comments must begin with " + COMMENT_SYMBOL 	+ "\n\n" +
		
				COMMENT_SYMBOL + "Integer variables\n" +
				COMMENT_SYMBOL + "These 3 can be set to 'RANDOM'\n" +
				BLOCKS_WIDE_ID		+ IDENTIFIER_SYMBOL + RANDOM_ID			+ "\n" +
				MAX_TRIANGLES_ID	+ IDENTIFIER_SYMBOL + RANDOM_ID			+ "\n" +
				SAMPLES_ID			+ IDENTIFIER_SYMBOL + RANDOM_ID			+ "\n" +
				COMMENT_SYMBOL + "Thread count can be set to 'AUTO' \n" +
				THREAD_COUNT_ID		+ IDENTIFIER_SYMBOL + "AUTO"			+ "\n" +
				REPAINT_WAIT_ID		+ IDENTIFIER_SYMBOL + G.repaintWait		+ "\n" +
				ATTEMPTS_ID			+ IDENTIFIER_SYMBOL + G.attempts		+ "\n\n" +
				
				COMMENT_SYMBOL + "Double variables\n" +
				SCALE_ID			+ IDENTIFIER_SYMBOL + G.scale			+ "\n" +
				POST_SCALE_ID		+ IDENTIFIER_SYMBOL + G.postScale		+ "\n\n" +
				
				COMMENT_SYMBOL + "Boolean variables\n" +
				PREDRAW_ID			+ IDENTIFIER_SYMBOL + G.preDraw			+ "\n" +
				POST_PROCESSING_ID	+ IDENTIFIER_SYMBOL + G.postProcessing	+ "\n" +
				DISPLAY_ID			+ IDENTIFIER_SYMBOL + G.display			+ "\n" +
				SAMPLES_RANDOM_ID 	+ IDENTIFIER_SYMBOL + G.samplesRandom;
		// write default settings to file
		try {
			File settingsFile = new File(
					System.getProperty("user.dir") + "\\TriangleConverter.settings");
			BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
			writer.write(settingsString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			// can't create the settings file?
		}
	}
}
