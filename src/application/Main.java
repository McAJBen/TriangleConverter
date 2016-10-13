package application;

import java.awt.image.BufferedImage;
import java.io.File;

import global.FileHandler;
import global.G;
import global.Settings;

public class Main {
	
	public static void main(String[] args) {
		Settings.load();
		
		if (G.getDisplay()) {
			startDisplay();
		}
		else {
			startText();
		}
	}
	
	private static void startDisplay() {
		final DisplayWindow displayWindow = new DisplayWindow();
		displayWindow.start();
	}
	
	private static void startText() {
		while (true) {
        	final File file = FileHandler.getFile();
        	if (file != null) {
        		for (int attempt = 1; attempt <= G.getMaxAttempts(); attempt++) {
		        	G.reset();
		        	System.out.println(file.getName() + G.TAB + G.getTitle(attempt));
		        	final Conversion conversion = new Conversion(file);
		        	final long startTime = System.currentTimeMillis();
		        	conversion.startConversion();
		        	System.out.println(G.TAB + (System.currentTimeMillis() - startTime));
        		}
        		BufferedImage originalImg = FileHandler.getImage(file);
        		file.delete();
        		FileHandler.putImageInFile(file, G.ORIGINAL, originalImg, G.BLANK);
        	}
		}
	}
}