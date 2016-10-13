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
		BufferedImage originalImg = to4Byte(FileHandler.getImage(file));
		
		newImg = getNew4Byte(
				(int) (originalImg.getWidth() * G.getTotalScale()),
				(int) (originalImg.getHeight() * G.getTotalScale()));
		
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
	
	private static BufferedImage getNew4Byte(int w, int h) {
		BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		
		int[] blank = new int[w * h * 4];
		
		b.getRaster().setPixels(0, 0, b.getWidth(), b.getHeight(), blank);
		
		return b;
	}
	
	private static BufferedImage to4Byte(BufferedImage b) {
		if (b.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			return b;
		}
		else {
			BufferedImage b2 = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			b2.getGraphics().drawImage(b, 0, 0, null);
	    	return b2;
		}
	}
}
