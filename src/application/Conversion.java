package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class Conversion {
	
	private BufferedImage newImg;
	private File file;
	private BlockThreadHandler blockThread;
	private int attemptNum;
	
	public Conversion(File f, int attemptNumber) {
		file = f;
		attemptNum = attemptNumber;
	}

	void startConversion() {
		long startTime = System.currentTimeMillis();
		BufferedImage originalImg = FileHandler.getImage(file);
		
		
		BufferedImage scaledImg = new BufferedImage((int)(originalImg.getWidth() * G.scale),  (int)(originalImg.getHeight() * G.scale), originalImg.getType());
		scaledImg.getGraphics().drawImage(originalImg, 0, 0, scaledImg.getWidth(), scaledImg.getHeight(), null);
		
		newImg = new BufferedImage((int) (scaledImg.getWidth() * G.postScale), (int) (scaledImg.getHeight() * G.postScale), originalImg.getType());
        
       
        blockThread = new btGrid(originalImg, newImg);
		blockThread.startConversion();
		System.out.println(System.currentTimeMillis() - startTime);
        if (G.samplesRandom) {
        	blockThread = new btRandom(originalImg, newImg);
        	blockThread.startConversion();
        }
        
		if (attemptNum >= G.attempts - 1) {
			file.delete();
			FileHandler.putImageInFile(file, "Original", originalImg, "");
		}
		FileHandler.putImageInFile(file, "New", newImg,
				"_" + (G.maxTriangles * G.blocksWide * G.blocksWide + G.randomBlocks) + "_" + attemptNum);
		
		blockThread = null;
		
		System.out.println(System.currentTimeMillis() - startTime);
	}
	
	public void paint(Graphics g, Dimension size) {
        Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(newImg, 0, 0, size.width, size.height - 14, null);
				
		if (file != null) {
			g2d.drawString(file.getName() + "", 2, size.height - 2);
			if (G.preDraw && blockThread != null) {
				Dimension windowSize = size;
				windowSize.height -= 14;
				if (newImg != null) {
					blockThread.paint(g2d, windowSize);
				}
			}
		}
    }
}
