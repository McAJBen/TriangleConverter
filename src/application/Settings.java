package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {
	
	private static final boolean // DEFAULT values if none are found in settings file
		DEFAULT_PRE_DRAW = true,
		DEFAULT_POST_PROCESSING = true;
	private static final int
		DEFAULT_BLOCK_SIZE = 10,
		DEFAULT_MAX_TRIANGLES = 2,
		DEFAULT_SAMPLES = 1,
		DEFAULT_THREAD_COUNT = 1,
		DEFAULT_REPAINT_WAIT = 500;
	private static final double
		DEFAULT_SCALE = 1.0,
		DEFAULT_POST_SCALE = 1.0;
	
	private static final String
		// BOOLEANS
		PREDRAW_ID = "PREDRAW",
		POST_PROCESSING_ID = "POST_PROCESSING",
		// INTEGERS
		BLOCK_SIZE_ID = "BLOCK_SIZE",
		MAX_TRIANGLES_ID = "MAX_TRIANGLES",
		SAMPLES_ID = "SAMPLES",
		THREAD_COUNT_ID = "THREAD_COUNT",
		REPAINT_WAIT_ID = "REAPINT_WAIT_MS",
		// DOUBLES
		SCALE_ID = "SCALE",
		POST_SCALE_ID = "POST_SCALE",
		// FORMAT
		IDENTIFIER_SYMBOL = ":",
		COMMENT_SYMBOL = "#",
		DEFAULT_HEADER = 
				COMMENT_SYMBOL + "All Comments must begin with " + COMMENT_SYMBOL;
	
	private boolean
		predraw,
		postProcessing;
	private int 
		blockSize,
		maxTriangles,
		samples,
		threadCount,
		repaintWait;
	private double
		scale,
		postScale;
	private boolean hasSettings;
	
	public Settings() {
		hasSettings = false;
	}
	
	@SuppressWarnings("resource")
	private void getSettings() {
		// if this class already has loaded the settings, dont load again
		if (hasSettings) {
			return;
		}
		// sets all settings to a known valid
		setDefaultSettings();
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
				int indexOfIdentifier = settingsString.indexOf(IDENTIFIER_SYMBOL);
				// if line does not have identifier ignore it
				if (indexOfIdentifier == -1) {
					continue;
				}
				String stringAfterIDSymbol = settingsString.substring(indexOfIdentifier + 1);
				switch (settingsString.substring(0, indexOfIdentifier)) {
					// BOOLEANS
					case PREDRAW_ID:
						predraw = Boolean.parseBoolean(stringAfterIDSymbol);
						break;
					case POST_PROCESSING_ID:
						postProcessing = Boolean.parseBoolean(stringAfterIDSymbol);
						break;
					// INTEGERS
					case BLOCK_SIZE_ID:
						blockSize = Integer.parseInt(stringAfterIDSymbol);
						break;
					case MAX_TRIANGLES_ID:
						maxTriangles = Integer.parseInt(stringAfterIDSymbol);
						break;
					case SAMPLES_ID:
						samples = Integer.parseInt(stringAfterIDSymbol);
						break;
					case THREAD_COUNT_ID:
						threadCount = Integer.parseInt(stringAfterIDSymbol);
						break;
					case REPAINT_WAIT_ID:
						repaintWait = Integer.parseInt(stringAfterIDSymbol);
						break;
					// DOUBLES
					case SCALE_ID:
						scale = Double.parseDouble(stringAfterIDSymbol);
						break;
					case POST_SCALE_ID:
						postScale = Double.parseDouble(stringAfterIDSymbol);
						break;
					default: // any unknown ID is ignored
						break;
				}
			}
		}
		// Tell file it has read the settings file and changed values
		hasSettings = true;
	}

	// Loads default settings to current class
	//  used to ensure values are set to a normal if none are in settings file
	private void setDefaultSettings() {
		hasSettings = true;
		blockSize = DEFAULT_BLOCK_SIZE;
		maxTriangles = DEFAULT_MAX_TRIANGLES;
		samples = DEFAULT_SAMPLES;
		threadCount = DEFAULT_THREAD_COUNT;
		scale = DEFAULT_SCALE;
		repaintWait = DEFAULT_REPAINT_WAIT;
		predraw = DEFAULT_PRE_DRAW;
		postScale = DEFAULT_POST_SCALE;
		postProcessing = DEFAULT_POST_PROCESSING;
	}

	private void createSettingsFile() {
		// create default settings strings
		String settingsString = 
				DEFAULT_HEADER												+ "\n" +
				BLOCK_SIZE_ID		+ IDENTIFIER_SYMBOL + blockSize			+ "\n" +
				MAX_TRIANGLES_ID	+ IDENTIFIER_SYMBOL + maxTriangles		+ "\n" +
				SAMPLES_ID			+ IDENTIFIER_SYMBOL + samples			+ "\n" +
				THREAD_COUNT_ID		+ IDENTIFIER_SYMBOL + threadCount		+ "\n" +
				SCALE_ID			+ IDENTIFIER_SYMBOL + scale				+ "\n" +
				REPAINT_WAIT_ID		+ IDENTIFIER_SYMBOL + repaintWait		+ "\n" +
				PREDRAW_ID			+ IDENTIFIER_SYMBOL + predraw			+ "\n" +
				POST_SCALE_ID		+ IDENTIFIER_SYMBOL + postScale			+ "\n" +
				POST_PROCESSING_ID	+ IDENTIFIER_SYMBOL + postProcessing	+ "\n";
		// write default settings to file
		try {
			File settingsFile = new File(
					System.getProperty("user.dir") + "\\TriangleConverter.settings");
			BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
			writer.write(settingsString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			// can't create the settings file
		}
	}
	// methods that return values read from settings file (or default)
	public int getBlockSize() {
		getSettings();
		if (blockSize < 1) {
			invalidVar(BLOCK_SIZE_ID);
			return DEFAULT_BLOCK_SIZE;
		}
		return blockSize;
	}

	public int getMaxTriangles() {
		getSettings();
		if (maxTriangles < 2) {
			invalidVar(MAX_TRIANGLES_ID);
			return DEFAULT_MAX_TRIANGLES;
		}
		return maxTriangles;
	}

	public int getSamples() {
		getSettings();
		if (samples <= 0) {
			invalidVar(SAMPLES_ID);
			return DEFAULT_SAMPLES;
		}
		return samples;
	}

	public int getThreadCount() {
		getSettings();
		if (threadCount <= 0) {
			invalidVar(THREAD_COUNT_ID);
			return DEFAULT_THREAD_COUNT;
		}
		return threadCount;
	}
	
	public double getScaleDown() {
		getSettings();
		if (scale <= 0) {
			invalidVar(SCALE_ID);
			return DEFAULT_SCALE;
		}
		return scale;
	}
	
	public int getRepaintWait() {
		getSettings();
		if (repaintWait < 0) {
			invalidVar(REPAINT_WAIT_ID);
			return DEFAULT_REPAINT_WAIT;
		}
		return repaintWait;
	}
	
	public boolean getPreDraw() {
		getSettings();
		return predraw;
	}
	
	public double getPostScale() {
		getSettings();
		if (postScale <= 0) {
			invalidVar(POST_SCALE_ID);
			return DEFAULT_POST_SCALE;
		}
		return postScale;
	}
	
	public boolean getPostProcessing() {
		getSettings();
		return postProcessing;
	}
	// outputs to console if the variable in settings file is invalid
	private void invalidVar(String varID) {
		System.out.println(varID + " ERROR - INVALID VAR");
	}
}
