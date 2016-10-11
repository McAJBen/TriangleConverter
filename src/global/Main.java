package global;

import java.io.File;

import application.Conversion;
import window.DisplayWindow;

public class Main {
	
	public static void main(String[] args) {
		Settings.load();
		
		if (G.getDisplay()) {
			DisplayWindow displayWindow = new DisplayWindow();
			displayWindow.start();
		}
		else {
			while (true) {
	        	File file = FileHandler.getFile();
	        	if (file != null) {
	        		for (int attempt = 1; attempt <= G.getMaxAttempts(); attempt++) {
	        			
			        	G.reset();
			        	System.out.println(file.getName() + "\t" + G.getTitle(attempt));
			        	Conversion conversion = new Conversion(file);
			        	long startTime = System.currentTimeMillis();
			        	conversion.startConversion();
			        	System.out.println("\t" + (System.currentTimeMillis() - startTime));
	        		}
	        	}
	        	else {
		    		try {
						Thread.sleep(10_000);
					} catch (InterruptedException e) { }
	        	}
	        }
		}
	}
}