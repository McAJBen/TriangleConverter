package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import blockStructure.BlockThreadHandler;
import blockStructure.btGrid;
import blockStructure.btRandom;
import global.FileHandler;
import global.G;

public class Conversion {
	
	private final File file;
	private BufferedImage newImg;
	private BlockThreadHandler blockThread;
	
	Conversion(File f) {
		file = f;
	}

	void startConversion() {
		BufferedImage originalImg = FileHandler.getImage(file);
		if (originalImg.getType() == BufferedImage.TYPE_4BYTE_ABGR || originalImg.getType() == BufferedImage.TYPE_INT_ARGB) {
			int[] pix3 = new int[originalImg.getWidth() * originalImg.getHeight() * 3];
			int[] pix4 = new int[originalImg.getWidth() * originalImg.getHeight() * 4];
			originalImg.getRaster().getPixels(0, 0, originalImg.getWidth(), originalImg.getHeight(), pix4);
			
			for (int i = 0; i < originalImg.getWidth() * originalImg.getHeight(); i++) {
				pix3[i * 3    ] = pix4[i * 4];
				pix3[i * 3 + 1] = pix4[i * 4 + 1];
				pix3[i * 3 + 2] = pix4[i * 4 + 2];
	    	}
	    	originalImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	    	originalImg.getRaster().setPixels(0, 0, originalImg.getWidth(), originalImg.getHeight(), pix3);
		}
		
		newImg = new BufferedImage(
				(int) (originalImg.getWidth() * G.getTotalScale()),
				(int) (originalImg.getHeight() * G.getTotalScale()),
				BufferedImage.TYPE_3BYTE_BGR);
		
        blockThread = new btGrid(originalImg, newImg);
        blockThread.start();
		
        blockThread = new btRandom(originalImg, newImg);
        blockThread.start();
        
		FileHandler.putImageInFile(file, G.NEW, newImg, G.getShortTitle());
		
		blockThread = null;
	}
	
	void paint(Graphics g, Dimension size) {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawImage(newImg, 0, 0, size.width, size.height, null);
        
		try {
			if (G.getPreDraw()) {
				blockThread.paint(g2d, size);
				g2d.setColor(Color.BLACK);
			}
		} catch (NullPointerException e) {
			// blockThread not created
		}
    }
	
	String getInfo() {
		try {
			return file.getName() + G.SPACE + 
					blockThread.getPercentDone() +
					G.RUN_TIME + blockThread.getRunTime() +
					G.END + blockThread.getEstimatedEndTime();
			
		} catch (NullPointerException e) {
			return file.getName();
		}
	}
	
	File getFile() {
		return file;
	}

	String getPercentDone() {
		return blockThread.getPercentDone();
	}

	int getPercent(int width) {
		try {
			return (int) (blockThread.getPercent() * width);
		} catch (NullPointerException e) {
			return 0;
		}
	}
}
