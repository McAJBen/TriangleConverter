package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {
	
	private static final int
		DEFAULT_BLOCK_SIZE = 10,
		DEFAULT_MAX_TRIANGLES = 2,
		DEFAULT_SAMPLES = 1,
		DEFAULT_THREAD_COUNT = 1;
	private static final String
		BLOCK_SIZE_ID = "BLOCK_SIZE",
		MAX_TRIANGLES_ID = "MAX_TRIANGLES",
		SAMPLES_ID = "SAMPLES",
		THREAD_COUNT_ID = "THREAD_COUNT",
		IDENTIFIER = ":";
	
	
	private int 
		blockSize,
		maxTriangles,
		samples,
		threadCount;
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
			switch (settingsString.substring(0, settingsString.indexOf(IDENTIFIER))) {
				case BLOCK_SIZE_ID:
					blockSize = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER) + 1));
					break;
				case MAX_TRIANGLES_ID:
					maxTriangles = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER) + 1));
					break;
				case SAMPLES_ID:
					samples = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER) + 1));
					break;
				case THREAD_COUNT_ID:
					threadCount = Integer.parseInt(settingsString.substring(settingsString.indexOf(IDENTIFIER) + 1));
					break;
				case "": // comment out
					break;
				default:
					System.out.println("Unknown settings being loaded");
					break;
			}
			try {
				settingsString = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (settingsString != null);
		
		// TODO obtain settings
		hasSettings = true;
	}

	private void setDefaultSettings() {
		hasSettings = true;
		blockSize = DEFAULT_BLOCK_SIZE;
		maxTriangles = DEFAULT_MAX_TRIANGLES;
		samples = DEFAULT_SAMPLES;
		threadCount = DEFAULT_THREAD_COUNT;
		System.out.println("Default settings have been set");
		createSettingsFile();
	}

	private void createSettingsFile() {
		String settingsString = 
				IDENTIFIER + "This is the Settings File" +
				IDENTIFIER + "All Comments must begin with " + IDENTIFIER +
				IDENTIFIER + "All variables must be written just like the ones following" +
				BLOCK_SIZE_ID + 	IDENTIFIER + blockSize + 	"\n" +
				MAX_TRIANGLES_ID + 	IDENTIFIER + maxTriangles + "\n" +
				SAMPLES_ID + 		IDENTIFIER + samples + 		"\n" +		
				THREAD_COUNT_ID + 	IDENTIFIER + threadCount + 	"\n";
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
		return blockSize;
	}

	public int getMaxTriangles() {
		getSettings();
		return maxTriangles;
	}

	public int getSamples() {
		getSettings();
		return samples;
	}

	public int getThreadCount() {
		getSettings();
		return threadCount;
	}
}
