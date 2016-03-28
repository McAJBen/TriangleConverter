package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;

import javax.imageio.ImageIO;

public class Conversion {
	
	private BufferedImage newImg;
	private File file;
	private BlockThread[] blockThreadArray;
	private int attemptNum;
	
	public Conversion(File f, int attemptNumber) {
		file = f;
		attemptNum = attemptNumber;
	}

	void startConversion() {
		BufferedImage originalImg = null;
		do {
    		try {
    			originalImg = ImageIO.read(file);
    		} catch (IOException e) {
    			System.out.println("ERROR: Could not read file" + file.getName());
    		}
    	} while (originalImg == null);
		
		BufferedImage scaledImg = new BufferedImage((int)(originalImg.getWidth() * G.scale),  (int)(originalImg.getHeight() * G.scale), originalImg.getType());
		scaledImg.getGraphics().drawImage(originalImg, 0, 0, scaledImg.getWidth(), scaledImg.getHeight(), null);
		
		newImg = new BufferedImage((int) (scaledImg.getWidth() * G.postScale), (int) (scaledImg.getHeight() * G.postScale), originalImg.getType());
        
        BlockThread.setup(originalImg, scaledImg, newImg, Thread.currentThread());
        
		blockThreadArray = new BlockThread[G.threadCount];
        for (int i = 0; i < blockThreadArray.length; i++) {
           	blockThreadArray[i] = new BlockThread("" + i);
        }
        
        for (BlockThread b: blockThreadArray) {
        	b.start();
        }
        
        while (!BlockThread.isDone()) {
        	try {
				Thread.sleep(10_000);
			} catch (InterruptedException e) {}
        }
        for (int i = 0; i < blockThreadArray.length; i++) {
        	while (blockThreadArray[i].isAlive()) {
        		try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
        	}
        }
        
		if (attemptNum >= G.attempts - 1) {
			file.delete();
			FileHandler.putImageInFile(file, "Original", originalImg, "");
		}
		FileHandler.putImageInFile(file, "New", newImg,
				"_" + (G.maxTriangles * G.blocksWide * G.blocksWide) + "_" + attemptNum);
		
		newImg = null;
		blockThreadArray = null;
		BlockThread.clear();
	}
	
	public void paint(Graphics g, Dimension size) {
        Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(newImg, 0, 0, size.width, size.height - 14, null);
				
		if (file != null) {
			g2d.drawString(file.getName() + "", 2, size.height - 2);
			if (G.preDraw && blockThreadArray != null) {
				Dimension windowSize = size;
				windowSize.height -= 14;
				if (newImg != null) {
					try {
						for (BlockThread bt: blockThreadArray) {
							bt.paint(g2d, newImg.getWidth(), newImg.getHeight(), windowSize);
						}
					}
					catch (ConcurrentModificationException ce) {
						System.out.println("failed to paint");
					}
				}
			}
		}
    }
}
