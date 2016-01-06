package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainJPanel extends JPanel {

	private static final Dimension 
					SCREEN_SIZE = new Dimension(500, 500),
					SCREEN_OFFSET = new Dimension(7, 30);
	private static final double 
					FPS = 10,
					FPS_TIME = 1000000000 / FPS;
	private static final int
					BLOCK_SIZE = 50;
	
	private Block block;
	private Dimension blockPixelSize;
	private long lastFrameTime = System.nanoTime();
	private BufferedImage originalImg, newImg;
	private File file;

	public static void main(String[] args) {
        JFrame frame = new JFrame("Triangle Converter");
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
        	do {
        		file = FileHandler.getFile();
        	} while (file == null);
        	
        	System.out.println("Found file: " + file);
        	
    		try {
    			originalImg = ImageIO.read(file);
    		} catch (IOException e) {
    			JOptionPane.showMessageDialog(null, "ERROR: Could not read file");
    			System.exit(-1);
    		}
    		
    		newImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), originalImg.getType());
    		
    		blockPixelSize = new Dimension(originalImg.getWidth()  / BLOCK_SIZE, originalImg.getHeight() / BLOCK_SIZE);
        	
            
            for (int i = 0; i < BLOCK_SIZE; i++) {
    			for (int j = 0; j < BLOCK_SIZE; j++) {
    				paintBlock(i, j);
    	    	}
            }
    		FileHandler.save(file, originalImg, newImg);
        }
	}

	private void paintBlock(int i, int j) {
		block = new Block(originalImg, getPixels(i, j), getWidth(i), getHeight(j));
		
		while (!block.isDone()) {
			block.move();
			if (getFrame()) {
				repaint();
			}
		}
		Graphics2D g = newImg.createGraphics();
	    g.drawImage(block.getImage(), getWidth(i), getHeight(j), null);
	    g.dispose();
	}
	
	private Dimension getPixels(int i, int j) {
		Dimension d = new Dimension(
				blockPixelSize.width + (i < (originalImg.getWidth() - BLOCK_SIZE * blockPixelSize.width) ? 1: 0),
				blockPixelSize.height + (j < (originalImg.getHeight() - BLOCK_SIZE * blockPixelSize.height) ? 1: 0));
		//System.out.println(i + " " + j + " " + d);
		return d;
	}
	
	private int getWidth(int i) {
		int offSet = originalImg.getWidth() - BLOCK_SIZE * blockPixelSize.width;
		return i * blockPixelSize.width + (i < offSet ? i : offSet);
		
	}
	private int getHeight(int j) {
		int offSet = originalImg.getHeight() - BLOCK_SIZE * blockPixelSize.height;
		return j * blockPixelSize.height + (j < offSet ? j : offSet);
	}
	
	
	public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
		g2d.drawImage(newImg, 0, 0, getSize().width, getSize().height - 14, null);
		g2d.drawRect(0, 0, getSize().width - 1, getSize().height - 14);

		g2d.drawString(file.getName() + "", 2, getSize().height - 2);
    }
	
	private boolean getFrame() {
		if (System.nanoTime() - FPS_TIME > lastFrameTime) {
    		lastFrameTime += FPS_TIME;
    		return true;
    	}
    	return false;
	}
}