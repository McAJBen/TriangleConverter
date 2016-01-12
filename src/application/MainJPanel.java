package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainJPanel extends JPanel {

	private static final Dimension 
					SCREEN_SIZE = new Dimension(500, 500),
					SCREEN_OFFSET = new Dimension(7, 30);
	//private static final double
	//				FPS = 10,
	//				FPS_TIME = 1000000000 / FPS;
	private int blockSize;
	//private long lastFrameTime = System.nanoTime();
	
	private Dimension blockPixelSize;
	private File file;
	private BufferedImage originalImg, newImg;
	
	private Block block;
	
	public MainJPanel() {
		while (blockSize < 1 || blockSize > 500) {
			blockSize = Integer.parseInt(JOptionPane.showInputDialog("Input number of blocks wide and tall (1-50)"));
		}
		while (Block.getMaxTriangles() < 2 || Block.getMaxTriangles() > 10) {
			Block.setMaxTriangles(Integer.parseInt(JOptionPane.showInputDialog("Input number of Triangles per Block (2-10)")));
		}
		
		
		
	}

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
        	do {
	    		try {
	    			originalImg = ImageIO.read(file);
	    		} catch (IOException e) {
	    			JOptionPane.showMessageDialog(null, "ERROR: Could not read file" + file.getName());
	    		}
        	} while (originalImg == null);
    		
    		newImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), originalImg.getType());
    		
    		blockPixelSize = new Dimension(originalImg.getWidth()  / blockSize, originalImg.getHeight() / blockSize);
        	
            String triFile = "v1|";
            
            BlockThread.setup(originalImg, blockSize, blockPixelSize);
            
            BlockThread[] btArr = new BlockThread[2];
            for (int i = 0; i < btArr.length; i++) {
	           	btArr[i] = new BlockThread(i);
				btArr[i].run();
            }
            while (true) {
            	for (int i = 0; i < btArr.length; i++) {
	            	if (!btArr[i].isAlive()) {
	            		System.out.println("btArr " + i + " is done: ");
	            		btArr[i].add(newImg);
	            		
	            		btArr[i].cont();
	            		//btArr[i] = new BlockThread();
	            		btArr[i].run();
	            	}
	            	repaint();
            	}
            	if (BlockThread.isDone()) {
            		break;
            	}
            }
            for (BlockThread bt : btArr) {
            	try {
					bt.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            /*for (int i = 0; i < blockSize; i++) {
    			for (int j = 0; j < blockSize; j++) {
    				if (getFrame()) {
    					repaint();
    				}
    				BlockThread bt = new BlockThread(new Point(i, j), blockSize, blockPixelSize, originalImg);
    				
    				bt.run();
    				
    				try {
						bt.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    				
    				bt.add(newImg);
    				
    				//paintBlock(i, j);
    				//triFile = triFile.concat(block.getText((double)i / blockSize, (double)j / blockSize, 1.0 / blockSize));
    	    	}
            }*/
            
            
            
            
    		FileHandler.save(file, originalImg, newImg);
    		FileHandler.saveText(file, triFile);
    		System.out.println("completed: " + file.getAbsolutePath());
        }
	}

	/*private void paintBlock(int i, int j) {
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
	}*/
	
	/*private Dimension getPixels(int i, int j) {
		Dimension d = new Dimension(
				blockPixelSize.width + (i < (originalImg.getWidth() - blockSize * blockPixelSize.width) ? 1: 0),
				blockPixelSize.height + (j < (originalImg.getHeight() - blockSize * blockPixelSize.height) ? 1: 0));
		//System.out.println(i + " " + j + " " + d);
		return d;
	}
	
	private int getWidth(int i) {
		int offSet = originalImg.getWidth() - blockSize * blockPixelSize.width;
		return i * blockPixelSize.width + (i < offSet ? i : offSet);
		
	}
	private int getHeight(int j) {
		int offSet = originalImg.getHeight() - blockSize * blockPixelSize.height;
		return j * blockPixelSize.height + (j < offSet ? j : offSet);
	}*/
	
	
	public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
		g2d.drawImage(newImg, 0, 0, getSize().width, getSize().height - 14, null);
		g2d.drawRect(0, 0, getSize().width - 1, getSize().height - 14);
		if (file != null) {
			g2d.drawString(file.getName() + "", 2, getSize().height - 2);
			if (block != null) {
				block.paint(g2d, originalImg.getWidth(), originalImg.getHeight(), getSize());
			}
		}
		
		
    }
	
	/*private boolean getFrame() {
		if (System.nanoTime() - FPS_TIME > lastFrameTime) {
    		lastFrameTime += FPS_TIME;
    		return true;
    	}
    	return false;
	}*/
}