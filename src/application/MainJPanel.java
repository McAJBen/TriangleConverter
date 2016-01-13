package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainJPanel extends JPanel {

	private static final Dimension 
					SCREEN_SIZE = new Dimension(500, 500),
					SCREEN_OFFSET = new Dimension(7, 30);
	
	private int threadCount;
	private File file;
	private BufferedImage originalImg, newImg;
	
	
	public MainJPanel(int tc) {
		threadCount = tc;
	}

	public static void main(String[] args) {
		while (BlockThread.getBlockSize() < 1 || BlockThread.getBlockSize() > 1000) {
			BlockThread.setBlockSize(Integer.parseInt(JOptionPane.showInputDialog("Input number of blocks wide and tall (1-50-1000)\nmore blocks takes less time")));
		}
		while (Block.getMaxTriangles() < 2 || Block.getMaxTriangles() > 10) {
			Block.setMaxTriangles(Integer.parseInt(JOptionPane.showInputDialog("Input number of Triangles per Block (2-10)\nmore triangles takes more time")));
		}
		while (BlockThread.getSamples() < 1 || BlockThread.getSamples() > 10) {
			BlockThread.setSamples(Integer.parseInt(JOptionPane.showInputDialog("Input number of Samples per Block (1-10)\nmore samples takes more time")));
		}
		int threadCount = 0;
		while (threadCount < 1 || threadCount > 100) {
			threadCount = Integer.parseInt(JOptionPane.showInputDialog("Input number of Threads (2)\nmore threads make for faster work"));
		}
        JFrame frame = new JFrame("Triangle Converter" +
        		" W:" + BlockThread.getBlockSize() + 
        		" Tr:" + Block.getMaxTriangles() + 
        		" S:" + BlockThread.getSamples() + 
        		" Th:" + threadCount);
        MainJPanel imageEvolutionJPanel = new MainJPanel(threadCount);
        frame.add(imageEvolutionJPanel);
        frame.setSize(SCREEN_SIZE.width + SCREEN_OFFSET.width, SCREEN_SIZE.height + SCREEN_OFFSET.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        imageEvolutionJPanel.start();
    }
	
	private void start() {
        while (true) {
        	do {
        		file = FileHandler.getFile();
        	} while (file == null);
        	
        	System.out.println("Found file: " + file);
        	do {
	    		try {
	    			originalImg = ImageIO.read(file);
	    		} catch (IOException e) {
	    			JOptionPane.showMessageDialog(null, "ERROR: Could not read file" + file.getName());
	    		}
        	} while (originalImg == null);
    		
    		newImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), originalImg.getType());
    		
            ArrayList<StringBuffer> strings = new ArrayList<StringBuffer>();
            
            BlockThread.setup(originalImg);
            
            Thread t = new Thread() {
            	@Override
            	public void run() {
            		while (true) {
            			repaint();
            			try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
            		}
            	}
			};
			t.start();
			
			System.out.println("paint thread started");
			
			BlockThread[] btArr = new BlockThread[threadCount];
            for (int i = 0; i < btArr.length; i++) {
	           	btArr[i] = new BlockThread();
	           	
				btArr[i].start();
            }
            
            System.out.println("block threads started");
            
            while (true) {
            	for (int i = 0; i < btArr.length; i++) {
	            	if (!btArr[i].isAlive()) {
	            		btArr[i].add(newImg);
	            		strings.add(new StringBuffer(btArr[i].getText(), btArr[i].getPosition()));
	            		btArr[i] = new BlockThread();
	            		btArr[i].start();
	            	}
            	}
            	if (BlockThread.isDone()) {
            		break;
            	}
            }
            for (int i = 0; i < btArr.length; i++) {
            	try {
					btArr[i].join();
					btArr[i].add(newImg);
					strings.add(new StringBuffer(btArr[i].getText(), btArr[i].getPosition()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
    		FileHandler.save(file, originalImg, newImg);
    		
    		FileHandler.saveText(file, 
    				"b" + BlockThread.getBlockSize() + 
    				"t" + Block.getMaxTriangles() + 
    				"|" + StringBuffer.combineStrings(strings, BlockThread.getBlockSize()));
    		System.out.println("completed: " + file.getAbsolutePath());
        }
	}
	
	public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(newImg, 0, 0, getSize().width, getSize().height - 14, null);
		g2d.drawRect(0, 0, getSize().width - 1, getSize().height - 14);
		if (file != null) {
			g2d.drawString(file.getName() + "", 2, getSize().height - 2);
		}
    }
}