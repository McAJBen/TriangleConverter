package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JPanel;

import blockStructure.BlockThreadHandler;
import blockStructure.btGrid;
import blockStructure.btRandom;
import global.FileHandler;
import global.G;

@SuppressWarnings("serial")
public class Conversion extends JPanel {
	
	private File file;
	private BufferedImage newImg;
	private BlockThreadHandler blockThread;
	private LoadingBar loadingBar;
	
	public Conversion() {
		loadingBar = new LoadingBar(this);
	}
	
	void startConversion(File file) {
		Thread repaintThread = getPaintThread();
		Thread loadThread = getLoadThread();
		this.file = file;
    	repaintThread.start();
    	loadThread.start();
    	
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
		this.file = null;
		
    	repaintThread.interrupt();
    	loadThread.interrupt();
    	
    	repaint();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		if (blockThread != null) {
			try {
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
				
			} catch (OutOfMemoryError e) {
				g.drawString(G.OUT_OF_MEMORY, 5, 15);
				g.drawString(blockThread.getPercentDone(), 5, 30);
			}
		}
		else {
			g.setColor(Color.BLACK);
			g.drawString(G.FINDING_FILE, 1, size.height - 3);
		}
	}
	
	String getInfo() {
		try {
			return blockThread.getPercentDone() +
					G.RUN_TIME + blockThread.getRunTime() +
					G.END + blockThread.getEstimatedEndTime() +
					G.SPACE + file.getName();
			
		} catch (NullPointerException e) {
			return "No Info";
		}
	}
	
	private Thread getPaintThread() {
		return new Thread(G.PAINT_THREAD) {
			public void run() {
				while (!isInterrupted()) {
					repaint();
					try {
						sleep(G.getPaintWait());
					} catch (InterruptedException e) {break;}
				}
			}
		};
	}
	
	private Thread getLoadThread() {
		return new Thread(G.LOAD_THREAD) {
			public void run() {
				while (!isInterrupted()) {
					loadingBar.repaint();
					try {
						sleep(15);
					} catch (InterruptedException e) {break;}
				}
			}
		};
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

	public LoadingBar getLoadingBar() {
		return loadingBar;
	}
}
