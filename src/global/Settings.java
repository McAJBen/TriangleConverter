package global;

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
		TRUE_COLOR_ID = "TRUE_COLOR",
		// INTEGERS
		BLOCKS_WIDE_ID = "BLOCKS_WIDE",
		MAX_TRIANGLES_ID = "MAX_TRIANGLES",
		SAMPLES_ID = "SAMPLES",
		THREAD_COUNT_ID = "THREAD_COUNT",
		REPAINT_WAIT_ID = "REAPINT_WAIT_MS",
		ATTEMPTS_ID = "ATTEMPTS",
		RANDOM_BLOCKS_ID = "RANDOM_BLOCKS",
		// DOUBLES
		SCALE_ID = "SCALE",
		POST_SCALE_ID = "POST_SCALE",
		FINAL_SCALE_ID = "FINAL_SCALE",
		// FORMAT
		ID_SYMB = ":",
		COMENT_SYMB = "#",
		RANDOM_ID = "RANDOM";
	
	@SuppressWarnings("resource")
	public static void load() {
		String settingsString = null;
		BufferedReader br = null;
		try {
			File settingsFile = new File(G.USER_DIR + G.BK_SLASH + G.SETTINGS_FILE);
			// check if settings exist and read first line
			if (settingsFile.exists()) {
				br = new BufferedReader(new FileReader(settingsFile));
			}
			// if the settings file does not exist, go to catch
			else throw new IOException(G.NO_SETTINGS_FILE);
			
		} catch (IOException e1) {
			createSettingsFile();
			return;
		}
		
		while (true) {
			// read a line
			try {
				settingsString = br.readLine();
				if (settingsString == null) {
					throw new IOException();
				}
			} catch (IOException e) {
				// can't read another line, the file must be done
				break;
			}
			// if line == null continue to check if there are any more lines
			// if line begins with comment symbol, ignore the line
			if (settingsString != null && !settingsString.startsWith(COMENT_SYMB)) {
				
				String[] split = settingsString.split(ID_SYMB);
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
						
						if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.postProcessingRandom = true;
						}
						else {
							G.postProcessing = Boolean.parseBoolean(split[1]);
						}
						break;
					case DISPLAY_ID:
						G.display = Boolean.parseBoolean(split[1]);
						break;
					case TRUE_COLOR_ID:
						G.trueColor = Boolean.parseBoolean(split[1]);
						break;
					// INTEGERS
					case BLOCKS_WIDE_ID:
						G.blocksWide = Integer.parseInt(split[1]);
						break;
					case MAX_TRIANGLES_ID:
						G.triangles = Integer.parseInt(split[1]);
						break;
					case SAMPLES_ID:
						G.samples = Integer.parseInt(split[1]);
						break;
					case THREAD_COUNT_ID:
						if (split[1].equalsIgnoreCase(G.AUTO)) {
							G.threadCount = Runtime.getRuntime().availableProcessors();
						}
						else {
							G.threadCount = Integer.parseInt(split[1]);
						}
						break;
					case RANDOM_BLOCKS_ID:
						if (split[1].startsWith(G.LOWER_X) || split[1].startsWith(G.UPPER_X)) {
							G.randomBlockMult = Integer.parseInt(split[1].substring(1));
						}
						else if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.randomBlocksRandom = true;
						}
						else {
							G.randomBlocks = Integer.parseInt(split[1]);
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
						if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.scaleRandom = true;
						}
						else {
							G.scale = Double.parseDouble(split[1]);
						}
						break;
					case POST_SCALE_ID:
						if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.postScaleRandom = true;
						}
						else {
							G.postScale = Double.parseDouble(split[1]);
						}
						break;
					case FINAL_SCALE_ID:
						if (split[1].equalsIgnoreCase(RANDOM_ID)) {
							G.finalScaleRandom = true;
						}
						else {
							G.finalScale = Double.parseDouble(split[1]);
						}
						break;
					default: // any unknown ID is ignored
						break;
				}
			}
		}
	}

	private static void createSettingsFile() {
		// create default settings strings
		final String settingsString = 
				COMENT_SYMB + "All Comments must begin with " + COMENT_SYMB 	+ "\n\n" +
		
				COMENT_SYMB + "Integer variables\n" +
				BLOCKS_WIDE_ID		+ ID_SYMB + G.blocksWide	+ "\n" +
				MAX_TRIANGLES_ID	+ ID_SYMB + G.triangles		+ "\n" +
				SAMPLES_ID			+ ID_SYMB + G.samples		+ "\n\n" +
				COMENT_SYMB + "Random can be set to 'x##' as a multiplier\n" +
				RANDOM_BLOCKS_ID	+ ID_SYMB + RANDOM_ID		+ "\n\n" +
				COMENT_SYMB + "Thread count can be set to 'AUTO'\n" +
				THREAD_COUNT_ID		+ ID_SYMB + "AUTO"			+ "\n" +
				REPAINT_WAIT_ID		+ ID_SYMB + G.repaintWait	+ "\n" +
				ATTEMPTS_ID			+ ID_SYMB + G.attempts		+ "\n\n" +
				COMENT_SYMB + "Double variables\n" +
				SCALE_ID			+ ID_SYMB + RANDOM_ID		+ "\n" +
				POST_SCALE_ID		+ ID_SYMB + RANDOM_ID		+ "\n" +
				FINAL_SCALE_ID		+ ID_SYMB + RANDOM_ID		+ "\n\n" +
				COMENT_SYMB + "Boolean variables\n" +
				POST_PROCESSING_ID	+ ID_SYMB + RANDOM_ID		+ "\n" +
				PREDRAW_ID			+ ID_SYMB + G.preDraw		+ "\n" +
				DISPLAY_ID			+ ID_SYMB + G.display		+ "\n" +
				TRUE_COLOR_ID		+ ID_SYMB + G.trueColor;
		// write default settings to file
		try {
			final File settingsFile = new File(G.USER_DIR + G.BK_SLASH + G.SETTINGS_FILE);
			final BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
			writer.write(settingsString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			// can't create the settings file?
		}
	}
}
