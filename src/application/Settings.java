package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {
	
	private static final boolean
		DEFAULT_PRE_DRAW = true;
	private static final int
		DEFAULT_BLOCK_SIZE = 10,
		DEFAULT_MAX_TRIANGLES = 2,
		DEFAULT_SAMPLES = 1,
		DEFAULT_THREAD_COUNT = 1,
		DEFAULT_REPAINT_WAIT = 500;
	private static final double
		DEFAULT_SCALE_DOWN = 1.0;
	private static final String
		BLOCK_SIZE_ID = "BLOCK_SIZE",
		MAX_TRIANGLES_ID = "MAX_TRIANGLES",
		SAMPLES_ID = "SAMPLES",
		THREAD_COUNT_ID = "THREAD_COUNT",
		SCALE_DOWN_ID = "SCALE_DOWN",
		REPAINT_WAIT_ID = "REAPINT_WAIT_MS",
		PREDRAW_ID = "PREDRAW",
		IDENTIFIER_SYMBOL = ":",
		COMMENT_SYMBOL = "#";
	
	private boolean
		predraw;
	private int 
		blockSize,
		maxTriangles,
		samples,
		threadCount,
		repaintWait;
	private double 
		scaleDown;
	private boolean hasSettings;
	
	public Settings() {
		hasSettings = false;
	}
	
	@SuppressWarnings("resource")
	private void getSettings() {
		if (hasSettings) {
			return;
		}
		String settingsString = null;
		BufferedReader br = null;
		try {
			File settingsFile = new File(
					System.getProperty("user.dir") + "\\TriangleConverter.settings");
			br = new BufferedReader(new FileReader(settingsFile));
			settingsString = br.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			setDefaultSettings();
			return;
		}
		
		
		do {
			if (!settingsString.startsWith(COMMENT_SYMBOL)) {
				switch (settingsString.substring(0, settingsString.indexOf(IDENTIFIER_SYMBOL))) {
					case BLOCK_SIZE_ID:
						blockSize = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER_SYMBOL) + 1));
						break;
					case MAX_TRIANGLES_ID:
						maxTriangles = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER_SYMBOL) + 1));
						break;
					case SAMPLES_ID:
						samples = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER_SYMBOL) + 1));
						break;
					case THREAD_COUNT_ID:
						threadCount = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER_SYMBOL) + 1));
						break;
					case SCALE_DOWN_ID:
						scaleDown = Double.parseDouble(settingsString.substring(settingsString.indexOf(IDENTIFIER_SYMBOL) + 1));
						break;
					case REPAINT_WAIT_ID:
						repaintWait = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER_SYMBOL) + 1));
						break;
					case PREDRAW_ID:
						predraw = Boolean.parseBoolean(settingsString.substring(settingsString.indexOf(IDENTIFIER_SYMBOL) + 1));
						break;
					case "": // comment out
						break;
					default:
						System.out.println("Unknown settings being loaded");
						break;
				}
			}
			try {
				settingsString = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (settingsString != null);
		hasSettings = true;
	}

	private void setDefaultSettings() {
		hasSettings = true;
		blockSize = DEFAULT_BLOCK_SIZE;
		maxTriangles = DEFAULT_MAX_TRIANGLES;
		samples = DEFAULT_SAMPLES;
		threadCount = DEFAULT_THREAD_COUNT;
		scaleDown = DEFAULT_SCALE_DOWN;
		repaintWait = DEFAULT_REPAINT_WAIT;
		predraw = DEFAULT_PRE_DRAW;
		System.out.println("Default settings have been set");
		createSettingsFile();
	}

	private void createSettingsFile() {
		String settingsString = 
				COMMENT_SYMBOL + "This is the Settings File\n" +
				COMMENT_SYMBOL + "All Comments must begin with " + COMMENT_SYMBOL + "\n" +
				COMMENT_SYMBOL + "All variables must be written just like the ones following\n" +
				BLOCK_SIZE_ID + 	IDENTIFIER_SYMBOL + blockSize + 	"\n" +
				MAX_TRIANGLES_ID + 	IDENTIFIER_SYMBOL + maxTriangles + 	"\n" +
				SAMPLES_ID + 		IDENTIFIER_SYMBOL + samples + 		"\n" +		
				THREAD_COUNT_ID + 	IDENTIFIER_SYMBOL + threadCount + 	"\n" + 
				SCALE_DOWN_ID + 	IDENTIFIER_SYMBOL + scaleDown + 	"\n" +
				REPAINT_WAIT_ID + 	IDENTIFIER_SYMBOL + repaintWait + 	"\n" + 
				PREDRAW_ID + 		IDENTIFIER_SYMBOL + predraw + 		"\n";
		try {
			File settingsFile = new File(
					System.getProperty("user.dir") + "\\TriangleConverter.settings");
			BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
			writer.write(settingsString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Cannot create Settings File");
		}
		
	}

	public int getBlockSize() {
		getSettings();
		if (blockSize <= 0) {
			System.out.println(BLOCK_SIZE_ID + "ERROR");
			return DEFAULT_BLOCK_SIZE;
		}
		return blockSize;
	}

	public int getMaxTriangles() {
		getSettings();
		if (maxTriangles <= 0) {
			System.out.println(MAX_TRIANGLES_ID + "ERROR");
			return DEFAULT_MAX_TRIANGLES;
		}
		return maxTriangles;
	}

	public int getSamples() {
		getSettings();
		if (samples <= 0) {
			System.out.println(SAMPLES_ID + "ERROR");
			return DEFAULT_SAMPLES;
		}
		return samples;
	}

	public int getThreadCount() {
		getSettings();
		if (threadCount <= 0) {
			System.out.println(THREAD_COUNT_ID + "ERROR");
			return DEFAULT_THREAD_COUNT;
		}
		return threadCount;
	}
	
	public double getScaleDown() {
		getSettings();
		if (scaleDown <= 0) {
			System.out.println(SCALE_DOWN_ID + "ERROR");
			return DEFAULT_SCALE_DOWN;
		}
		return scaleDown;
	}
	
	public int getRepaintWait() {
		getSettings();
		if (repaintWait < 0) {
			System.out.println(REPAINT_WAIT_ID + "ERROR");
			return DEFAULT_REPAINT_WAIT;
		}
		return repaintWait;
	}
	
	public boolean getPreDraw() {
		getSettings();
		return predraw;
	}
}
