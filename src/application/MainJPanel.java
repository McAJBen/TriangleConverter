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

	public static void main(String[] args) {
		
		Settings.load();
		
		MainJPanel imageEvolutionJPanel = new MainJPanel();
		JFrame frame = null;
		if (G.display) {
			frame = new JFrame();
        	frame.add(imageEvolutionJPanel);
        	frame.setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
        	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	frame.setLocationRelativeTo(null);
        	frame.setVisible(true);
		}
        while (true) {
        	File file = FileHandler.getFile();
        	if (file != null) {
        		
        		int imagePixels = FileHandler.getPixels(file);
        		
        		for (int i = 0; i < G.attempts; i++) {
        			System.out.println("Found file: " + file);
		        	G.reset(imagePixels);
		        	
		        	Thread repaintThread = null;
		        	if (G.display) {
		        		frame.setTitle(getTitle(i));
		        		repaintThread = imageEvolutionJPanel.getPaintThread();
		        		repaintThread.start();
		        	}
		        	else {
		        		System.out.println(getTitle(i));
		        	}
		        	
		        	imageEvolutionJPanel.startConversion(file, i);
		        	
		        	if (G.display) {
		        		repaintThread.interrupt();
		        	}
        		}
        	}
        	else {
        		if (G.display) {
        			frame.setTitle("Finding File ...");
        		}
	    		try {
					Thread.sleep(10_000);
				} catch (InterruptedException e) { }
        	}
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

	private static String getTitle(int attempt) {
		return "Triangle Converter" +
        		" Wi:" + G.blocksWide + 
        		" Tr:" + G.maxTriangles + 
        		" Sa:" + G.samples + 
        		" Th:" + G.threadCount + 
        		" Sc:" + G.scale + 
        		" Ps:" + G.postScale + 
        		" At:" + attempt + "/" + G.attempts;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if (conversion != null) {
			conversion.paint(g, getSize());
		}
	}
}