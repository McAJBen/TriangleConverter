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
	
	private BufferedImage newImg;
	private File file;
	private BlockThreadHandler blockThread;
	
	public Conversion(File f) {
		file = f;
	}

	public void startConversion() {
		BufferedImage originalImg = FileHandler.getImage(file);
		
		newImg = new BufferedImage(
				(int) (originalImg.getWidth() * G.getTotalScale()),
				(int) (originalImg.getHeight() * G.getTotalScale()),
				originalImg.getType());
		
        blockThread = new btGrid(originalImg, newImg);
		blockThread.startConversion();
		
        blockThread = new btRandom(originalImg, newImg);
        blockThread.startConversion();
        
		FileHandler.putImageInFile(file, "New", newImg, G.getShortTitle());
		
		blockThread = null;
	}
	
	public void paint(Graphics g, Dimension size) {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawImage(newImg, 0, 0, size.width, size.height - 14, null);
        
		if (file != null && blockThread != null) {
			g2d.drawString(file.getName() + " " + blockThread.getPercentDone(), 2, size.height - 2);
			if (G.getPreDraw()) {
				size.height -= 14;
				if (newImg != null) {
					blockThread.paint(g2d, size);
					g2d.setColor(Color.BLACK);
				}
			}
		}
    }

	public String getPercentDone() {
		return blockThread.getPercentDone();
	}
}
