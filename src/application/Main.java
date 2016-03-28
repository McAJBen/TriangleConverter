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
	        		
	        		for (int i = 0; i < G.attempts; i++) {
	        			System.out.println("Found file: " + file);
			        	G.reset(imagePixels);
			        	System.out.println(MainJPanel.getTitle(i));
			        	
			        	Conversion conversion = new Conversion(file, i);
			        	conversion.startConversion();
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
