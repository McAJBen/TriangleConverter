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
	private BufferedImage newImg;
	private int threadCount;
	private File file;
	
	public MainJPanel(int tc) {
		threadCount = tc;
	}

	public static void main(String[] args) {
		while (BlockThread.getBlockSize() < 1 || BlockThread.getBlockSize() > 1000) {
			BlockThread.setBlockSize(Integer.parseInt(JOptionPane.showInputDialog("Input number of blocks wide and tall (1-50-1000)\nmore blocks takes less time")));
		}
		while (Block.getMaxTriangles() < 2 || Block.getMaxTriangles() > 50) {
			Block.setMaxTriangles(Integer.parseInt(JOptionPane.showInputDialog("Input number of Triangles per Block (2-50)\nmore triangles takes more time")));
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
        Thread repaintThread = new Thread() {
			@Override
			public void run() {
				while (!isInterrupted()) {
    				repaint();
    				try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    			}
			}
		};
		repaintThread.start();
        while (true) {
        	file = FileHandler.getFile();
        	while (file == null) {
        		try {
					Thread.sleep(10000);
				} catch (InterruptedException e) { }
        		file = FileHandler.getFile();
        	}
        	System.out.println("Found file: " + file);
        	startConversion();
        }
	}
	
	private void startConversion() {
		BufferedImage originalImg = null;
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
        			
		ArrayList<BlockThread> btArr = new ArrayList<BlockThread>();
        for (int i = 0; i < threadCount; i++) {
           	btArr.add(new BlockThread());
        }
        
        for (BlockThread b: btArr) {
        	b.start();
        }
        
        while (!BlockThread.isDone()) {
        	for (int i = 0; i < btArr.size(); i++) {
            	if (!btArr.get(i).isAlive()) {
            		btArr.get(i).add(newImg);
            		strings.add(btArr.get(i).getStringBuffer());
            		btArr.set(i, new BlockThread());
            		btArr.get(i).start();
            		
            	}
        	}
        }
        while (btArr.size() > 0) {
            for (int i = 0; i < btArr.size(); i++) {
            	if (!btArr.get(i).isAlive()) {
					btArr.get(i).add(newImg);
					strings.add(btArr.get(i).getStringBuffer());
					btArr.remove(i);
					break;
				}
            }
        }
        
		FileHandler.save(file, originalImg, newImg);
		
		FileHandler.saveText(file, 
				"b" + BlockThread.getBlockSize() + "t" + Block.getMaxTriangles() + "|",
				strings, BlockThread.getBlockSize());
		
		file.delete();
		repaint();
		
		System.out.println("completed: " + file.getAbsolutePath());
		
		newImg = null;
		BlockThread.clear();
		
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