package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;

import global.FileHandler;
import global.G;

@SuppressWarnings("serial")
public class DisplayWindow extends JFrame {
	
	private static final Dimension SCREEN_SIZE = new Dimension(500, 500);
	private static final Dimension SCREEN_OFFSET = new Dimension(7, 30);
	private final Window window;
	
	DisplayWindow() {
		super();
		window = new Window();
    	add(window);
    	setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(null);
    	setVisible(true);
	}
	
	void start() {
		while (true) {
			setTitle(G.FINDING_FILE);
	    	File file = FileHandler.getFile();
	    	if (file != null) {
	    		for (int attempt = 1; attempt <= G.getMaxAttempts(); attempt++) {
		        	global.G.reset();
		        	setTitle(G.getTitle(attempt));
		        	window.startConversion(file);
	    		}
	    		BufferedImage originalImg = FileHandler.getImage(file);
	    		file.delete();
	    		FileHandler.putImageInFile(file, G.ORIGINAL, originalImg, G.BLANK);
	    	}
		}
	}
	
	private static class Window extends JPanel {
		
		private Conversion conversion;
		
		public void paint(Graphics g) {
			super.paint(g);
			Dimension size = getSize();
			if (conversion != null) {
				try {
					g.setColor(Color.GREEN);
					g.fillRect(0, size.height - 14, conversion.getPercent(size.width), 14);
					g.setColor(Color.BLACK);
					g.drawString(conversion.getInfo(), 1, size.height - 3);
					
					size.height -= 14;
					conversion.paint(g, size);
					
				} catch (OutOfMemoryError e) {
					g.drawString(G.OUT_OF_MEMORY, 5, 15);
					g.drawString(conversion.getPercentDone(), 5, 30);
				}
			}
			else {
				g.setColor(Color.BLACK);
				g.drawString(G.FINDING_FILE, 1, size.height - 3);
			}
		}
		
		private void startConversion(File file) {
			Thread repaintThread = getPaintThread();
			conversion = new Conversion(file);
        	repaintThread.start();
        	conversion.startConversion();
        	repaintThread.interrupt();
        	conversion = null;
        	repaint();
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
	}
}