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
	
	private static Conversion conversion;

	public static void main(String[] args) {
		
		Settings.load();
		
        JFrame frame = new JFrame("Triangle Converter" +
        		" Wi:" + G.blocksWide + 
        		" Tr:" + G.maxTriangles + 
        		" Sa:" + G.samples + 
        		" Th:" + G.threadCount + 
        		" Sc:" + G.scale + 
        		" Ps:" + G.postScale);
        
        MainJPanel imageEvolutionJPanel = new MainJPanel();
        frame.add(imageEvolutionJPanel);
        frame.setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        imageEvolutionJPanel.start();
    }
	
	private void start() {
        while (true) {
        	File file = FileHandler.getFile();
        	while (file == null) {
        		try {
					Thread.sleep(10_000);
				} catch (InterruptedException e) { }
        		file = FileHandler.getFile();
        	}
        	System.out.println("Found file: " + file);
        	Thread repaintThread = new RepaintThread();
        	repaintThread.start();
        	conversion = new Conversion(file);
        	conversion.startConversion();
        	conversion = null;
        	repaintThread.interrupt();
        	Runtime.getRuntime().gc();
        	
        }
	}
	
	private class RepaintThread extends Thread {
		public RepaintThread() {
			super ("repaintThread");
		}
		@Override
		public void run() {
			while (!isInterrupted()) {
				repaint();
				try {
					sleep(G.repaintWait);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if (conversion != null) {
			conversion.paint(g, getSize());
		}
	}
}