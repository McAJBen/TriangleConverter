package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainJPanel extends JPanel {

	private static final Dimension 
					SCREEN_SIZE = new Dimension(500, 500),
					SCREEN_OFFSET = new Dimension(7, 30);
	
	private Conversion conversion;

	static void main() {
		MainJPanel mainJPanel = new MainJPanel();
		JFrame frame = new JFrame();
    	frame.add(mainJPanel);
    	frame.setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	frame.setVisible(true);
        while (true) {
        	File file = FileHandler.getFile();
        	if (file != null) {
        		
        		for (int attempt = 1; attempt <= G.attempts; attempt++) {
		        	G.reset(attempt);
		        	
		        	Thread repaintThread = null;
		        	frame.setTitle(G.getTitle(attempt));
		        	repaintThread = mainJPanel.getPaintThread();
		        	repaintThread.start();
		        	mainJPanel.startConversion(file, attempt);
		        	repaintThread.interrupt();
        		}
        	}
        	else {
        		frame.setTitle("Finding File ...");
	    		try {
					Thread.sleep(10_000);
				} catch (InterruptedException e) { }
        	}
        }
    }
	
	public void paint(Graphics g) {
		super.paint(g);
		if (conversion != null) {
			conversion.paint(g, getSize());
		}
	}
	
	private void startConversion(File f, int i) {
		conversion = new Conversion(f, i);
    	conversion.startConversion();
    	conversion = null;
	}

	private Thread getPaintThread() {
		return new Thread("repaintThread") {
			public void run() {
				while (!isInterrupted()) {
					repaint();
					try {
						sleep(G.repaintWait);
					} catch (InterruptedException e) {}
				}
			}
		};
	}
}