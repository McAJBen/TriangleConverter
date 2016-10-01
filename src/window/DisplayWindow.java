package window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import application.Conversion;
import global.FileHandler;
import global.G;

@SuppressWarnings("serial")
public class DisplayWindow extends JFrame {
	
	private static final Dimension SCREEN_SIZE = new Dimension(500, 500);
	private static final Dimension SCREEN_OFFSET = new Dimension(7, 30);
	private Window window;
	
	public DisplayWindow() {
		super();
		window = new Window();
    	add(window);
    	setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLocationRelativeTo(null);
    	setVisible(true);
	}
	
	public void start() {
		while (true) {
        	File file = FileHandler.getFile();
        	if (file != null) {
        		BufferedImage originalImg = FileHandler.getImage(file);
        		for (int attempt = 1; attempt <= G.getMaxAttempts(); attempt++) {
		        	global.G.reset();
		        	setTitle(G.getTitle(attempt));
		        	window.startConversion(file);
        		}
        		file.delete();
        		FileHandler.putImageInFile(file, "Original", originalImg, "");
        	}
        	else {
        		setTitle("Finding File ...");
	    		try {
					Thread.sleep(10_000);
				} catch (InterruptedException e) { }
        	}
        }
	}
	
	private static class Window extends JPanel {
		
		private Conversion conversion;
		
		private void startConversion(File file) {
			Thread repaintThread = getPaintThread();
			conversion = new Conversion(file);
        	repaintThread.start();
        	conversion.startConversion();
        	repaintThread.interrupt();
        	conversion = null;
		}
		
		public void paint(Graphics g) {
			super.paint(g);
			if (conversion != null) {
				try {
					Dimension size = getSize();
					g.setColor(Color.GREEN);
					g.fillRect(0, size.height - 14, conversion.getPercent(size.width), 14);
					g.setColor(Color.BLACK);
					g.drawString(conversion.getInfo(), 1, size.height - 3);
					
					size.height -= 14;
					conversion.paint(g, size);
					
				} catch (OutOfMemoryError e) {
					g.drawString("Out of memory, not able to display :(", 5, 15);
					g.drawString(conversion.getPercentDone(), 5, 30);
				}
			}
		}
		
		private Thread getPaintThread() {
			return new Thread("paintThread") {
				public void run() {
					while (!isInterrupted()) {
						repaint();
						try {
							sleep(G.getPaintWait());
						} catch (InterruptedException e) {}
					}
				}
			};
		}
	}
}