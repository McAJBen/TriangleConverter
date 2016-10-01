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
		
		long startTime = System.currentTimeMillis();
		
        blockThread = new btGrid(originalImg, newImg);
		blockThread.startConversion();
		
		
		
        blockThread = new btRandom(originalImg, newImg);
        blockThread.startConversion();
        
        System.out.println(System.currentTimeMillis() - startTime);
        
		FileHandler.putImageInFile(file, "New", newImg, G.getShortTitle());
		
		blockThread = null;
	}
	
	public void paint(Graphics g, Dimension size) {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawImage(newImg, 0, 0, size.width, size.height - 14, null);
        
		if (file != null && blockThread != null) {
			g2d.setColor(Color.GREEN);
			g2d.fillRect(0, size.height - 14, blockThread.getPercent(size.width), 14);
			g2d.setColor(Color.BLACK);
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
