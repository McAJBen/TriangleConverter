package global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Settings {
	
	private static final String
		ID_SYMB = ":",
		COMENT_SYMB = "#";
	
	private static ArrayList<Integer> blocksWide, maxTriangles, samples, randomBlocks;
	private static ArrayList<Double> scales, postScales, finalScales;
	
	@SuppressWarnings("resource")
	public static void load() {
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
		
		blocksWide = new ArrayList<>();
		maxTriangles = new ArrayList<>();
		samples = new ArrayList<>();
		randomBlocks = new ArrayList<>();
		scales = new ArrayList<>();
		postScales = new ArrayList<>();
		finalScales = new ArrayList<>();
		
		while (true) {
			// read a line
			try {
				String settingsString = br.readLine();
				if (settingsString == null) {
					throw new IOException();
				}
				setVar(settingsString);
			} catch (IOException e) {
				// can't read another line, the file must be done
				break;
			}
		}
		
		blocksWide.trimToSize();
		maxTriangles.trimToSize();
		samples.trimToSize();
		randomBlocks.trimToSize();
		scales.trimToSize();
		postScales.trimToSize();
		finalScales.trimToSize();
		if (G.sequential) {
			G.attempts = seqSize();
		}
	}
	
	static int seqSize() {
		return blocksWide.size() * maxTriangles.size() * samples.size() *
			randomBlocks.size() * scales.size() * postScales.size() * finalScales.size();
	}

	// return true if correctly dealt with line
	// return false if sequential is starting
	private static void setVar(String line) {
		if (!line.startsWith(COMENT_SYMB)) {
			
			String[] split = line.split(ID_SYMB);
			// if line does not have identifier ignore it
			if (split.length == 2) {
				switch (Setting.valueOf(split[0])) {
				// BOOLEANS
				case PREDRAW:
					G.preDraw = Boolean.parseBoolean(split[1]);
					break;
				case POST_PROCESSING:
					G.postProcessing = Boolean.parseBoolean(split[1]);
					break;
				case DISPLAY:
					G.display = Boolean.parseBoolean(split[1]);
					break;
				case TRUE_COLOR:
					G.trueColor = Boolean.parseBoolean(split[1]);
					break;
				case SEQUENTIAL:
					G.sequential = Boolean.parseBoolean(split[1]);
					break;
				// INTEGERS
				case BLOCKS_WIDE:
					G.blocksWide = Integer.parseInt(split[1]);
					blocksWide.add(G.blocksWide);
					break;
				case MAX_TRIANGLES:
					G.triangles = Integer.parseInt(split[1]);
					maxTriangles.add(G.triangles);
					break;
				case SAMPLES:
					G.samples = Integer.parseInt(split[1]);
					samples.add(G.samples);
					break;
				case THREAD_COUNT:
					if (split[1].equalsIgnoreCase(G.AUTO)) {
						G.threadCount = Runtime.getRuntime().availableProcessors();
					}
					else {
						G.threadCount = Integer.parseInt(split[1]);
					}
					break;
				case RANDOM_BLOCKS:
					G.randomBlockMult = Integer.parseInt(split[1]);
					randomBlocks.add(G.randomBlockMult);
					break;
				case REPAINT_WAIT_MS:
					G.repaintWait = Integer.parseInt(split[1]);
					break;
				case ATTEMPTS:
					G.attempts = Integer.parseInt(split[1]);
					break;
				// DOUBLES
				case SCALE:
					G.scale = Double.parseDouble(split[1]);
					scales.add(G.scale);
					break;
				case POST_SCALE:
					G.postScale = Double.parseDouble(split[1]);
					postScales.add(G.postScale);
					break;
				case FINAL_SCALE:
					G.finalScale = Double.parseDouble(split[1]);
					finalScales.add(G.finalScale);
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
		
				COMENT_SYMB + "Thread count can be set to 'AUTO'\n" +
				Setting.THREAD_COUNT	+ ID_SYMB + "AUTO"			+ "\n\n" +
				
				Setting.REPAINT_WAIT_MS	+ ID_SYMB + G.repaintWait	+ "\n" +
				Setting.ATTEMPTS		+ ID_SYMB + G.attempts		+ "\n\n" +
				
				COMENT_SYMB + "Boolean variables\n" +
				Setting.POST_PROCESSING	+ ID_SYMB + G.postProcessing+ "\n" +
				Setting.PREDRAW			+ ID_SYMB + G.preDraw		+ "\n" +
				Setting.DISPLAY			+ ID_SYMB + G.display		+ "\n" +
				Setting.TRUE_COLOR		+ ID_SYMB + G.trueColor		+ "\n" +
				Setting.SEQUENTIAL		+ ID_SYMB + G.sequential	+ "\n\n" +
				
				COMENT_SYMB + "Start of sequential operations...\n\n" +
				
				Setting.BLOCKS_WIDE		+ ID_SYMB + G.blocksWide	+ "\n" +
				Setting.MAX_TRIANGLES	+ ID_SYMB + G.triangles		+ "\n" +
				Setting.SAMPLES			+ ID_SYMB + G.samples		+ "\n\n" +
				
				Setting.RANDOM_BLOCKS	+ ID_SYMB + G.randomBlockMult+ "\n\n" +
				
				Setting.SCALE			+ ID_SYMB + G.scale			+ "\n" +
				Setting.POST_SCALE		+ ID_SYMB + G.postScale		+ "\n" +
				Setting.FINAL_SCALE		+ ID_SYMB + G.finalScale;
				
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

	public static void reset(int i) {
		
		G.blocksWide = blocksWide.get(i % blocksWide.size());
		i /= blocksWide.size();
		G.triangles = maxTriangles.get(i % maxTriangles.size());
		i /= maxTriangles.size();
		G.samples = samples.get(i % samples.size());
		i /= samples.size();
		G.randomBlockMult = randomBlocks.get(i % randomBlocks.size());
		i /= randomBlocks.size();
		G.scale = scales.get(i % scales.size());
		i /= scales.size();
		G.postScale = postScales.get(i % postScales.size());
		i /= postScales.size();
		G.finalScale = finalScales.get(i % finalScales.size());
		//System.out.println(G.getShortTitle());
		
	}
}
