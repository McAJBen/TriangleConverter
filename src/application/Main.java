package application;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		Settings.load();
		
		if (G.display) {
			MainJPanel.main();
		}
		else {
			while (true) {
	        	File file = FileHandler.getFile();
	        	if (file != null) {
	        		int imagePixels = FileHandler.getPixels(file);
	        		for (int attempt = 1; attempt <= G.attempts; attempt++) {
	        			System.out.println("Found file: " + file);
			        	G.reset(imagePixels);
			        	System.out.println(G.getTitle(attempt));
			        	Conversion conversion = new Conversion(file, attempt);
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
