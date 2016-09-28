package application;

import java.io.File;

public class Main {
	
	public static void main(String[] args) {
		Settings.load();
		
		if (G.display) {
			DisplayWindow displayWindow = new DisplayWindow();
			displayWindow.start();
		}
		else {
			while (true) {
	        	File file = FileHandler.getFile();
	        	if (file != null) {
	        		for (int attempt = 1; attempt <= G.attempts; attempt++) {
	        			System.out.println("Found file: " + file);
			        	G.reset();
			        	System.out.println(G.getTitle(attempt));
			        	Conversion conversion = new Conversion(file);
			        	long startTime = System.currentTimeMillis();
			        	conversion.startConversion();
			        	System.out.println(System.currentTimeMillis() - startTime);
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