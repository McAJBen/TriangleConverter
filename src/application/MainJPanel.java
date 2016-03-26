package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainJPanel extends JPanel {

	private static final Dimension 
					SCREEN_SIZE = new Dimension(500, 500),
					SCREEN_OFFSET = new Dimension(7, 30);
	private BufferedImage newImg;
	private File file;
	private ArrayList<BlockThread> blockThreadArray;

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
        Thread repaintThread = new Thread("repaintThread") {
			@Override
			public void run() {
				while (!isInterrupted()) {
    				repaint();
    				try {
						sleep(G.repaintWait);
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
    			System.out.println("ERROR: Could not read file" + file.getName());
    		}
    	} while (originalImg == null);
		
		BufferedImage scaledImg = new BufferedImage((int)(originalImg.getWidth() * G.scale),  (int)(originalImg.getHeight() * G.scale), originalImg.getType());
		scaledImg.getGraphics().drawImage(originalImg, 0, 0, scaledImg.getWidth(), scaledImg.getHeight(), null);
		
		newImg = new BufferedImage((int) (scaledImg.getWidth() * G.postScale), (int) (scaledImg.getHeight() * G.postScale), originalImg.getType());
		
        ArrayList<StringBuffer> strings = new ArrayList<StringBuffer>();
        
        BlockThread.setup(originalImg, scaledImg, newImg);
        
		blockThreadArray = new ArrayList<BlockThread>();
        for (int i = 0; i < G.threadCount; i++) {
           	blockThreadArray.add(new BlockThread("" + i));
        }
        
        for (BlockThread b: blockThreadArray) {
        	b.start();
        }
        
        while (!BlockThread.isDone()) {
        	for (int i = 0; i < blockThreadArray.size(); i++) {
            	if (!blockThreadArray.get(i).isAlive()) {
            		strings.add(blockThreadArray.get(i).getStringBuffer());
            		blockThreadArray.set(i, new BlockThread("" + i));
            		blockThreadArray.get(i).start();
            		
            	}
        	}
        }
        while (blockThreadArray.size() > 0) {
            for (int i = 0; i < blockThreadArray.size(); i++) {
            	if (!blockThreadArray.get(i).isAlive()) {
					strings.add(blockThreadArray.get(i).getStringBuffer());
					blockThreadArray.remove(i);
					break;
				}
            }
        }
        
		FileHandler.save(file, originalImg, newImg);
		
		FileHandler.saveText(file, strings);
		
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
				
		if (file != null) {
			g2d.drawString(file.getName() + "", 2, getSize().height - 2);
			if (G.preDraw && blockThreadArray != null) {
				Dimension windowSize = getSize();
				windowSize.height -= 14;
				try {
					for (BlockThread bt: blockThreadArray) {
						bt.paint(g2d, newImg.getWidth(), newImg.getHeight(), windowSize);
					}
				}
				catch (ConcurrentModificationException ce) {
					
				}
			}
		}
    }
}