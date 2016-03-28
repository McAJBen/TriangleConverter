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
		
        JFrame frame = new JFrame();
        
        MainJPanel imageEvolutionJPanel = new MainJPanel();
        frame.add(imageEvolutionJPanel);
        frame.setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        while (true) {
        	File file = FileHandler.getFile();
        	if (file != null) {
        		for (int i = 0; i < G.attempts; i++) {
        			System.out.println("Found file: " + file);
		        	G.reset();
		        	frame.setTitle(getTitle(i));
		        	
		        	Thread repaintThread = imageEvolutionJPanel.getPaintThread();
		        	repaintThread.start();
		        	
		        	imageEvolutionJPanel.startConversion(file, i);
	    	
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
        		" At:" + attempt;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if (conversion != null) {
			conversion.paint(g, getSize());
		}
	}
}