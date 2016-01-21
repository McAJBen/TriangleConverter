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
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainJPanel extends JPanel {

	private static final Dimension 
					SCREEN_SIZE = new Dimension(500, 500),
					SCREEN_OFFSET = new Dimension(7, 30);
	private BufferedImage newImg;
	private boolean preDraw;
	private int 
			threadCount,
			repaintWait;
	private double 
			scale,
			postScale;
	private File file;
	private ArrayList<BlockThread> blockThreadArray;
	
	public MainJPanel(int threadCount, double scale, int repaintWait, boolean preDraw, double postScale) {
		this.threadCount = threadCount;
		this.scale = scale;
		this.repaintWait = repaintWait;
		this.preDraw = preDraw;
		this.postScale = postScale / scale;
	}

	public static void main(String[] args) {
		
		Settings settings = new Settings();
		
		BlockThread.setBlockSize(settings.getBlockSize());
		Block.setMaxTriangles(settings.getMaxTriangles());
		BlockThread.setSamples(settings.getSamples());
		BlockThread.setPostProcessing(settings.getPostProcessing());
		
        JFrame frame = new JFrame("Triangle Converter" +
        		" Wi:" + BlockThread.getBlockSize() + 
        		" Tr:" + Block.getMaxTriangles() + 
        		" Sa:" + BlockThread.getSamples() + 
        		" Th:" + settings.getThreadCount() + 
        		" Sc:" + settings.getScaleDown() + 
        		" Ps:" + settings.getPostScale());
        
        MainJPanel imageEvolutionJPanel = new MainJPanel(
        		settings.getThreadCount(),
        		settings.getScaleDown(),
        		settings.getRepaintWait(),
        		settings.getPreDraw(),
        		settings.getPostScale());
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
						sleep(repaintWait);
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
		
		BufferedImage scaledImg = new BufferedImage((int)(originalImg.getWidth() * scale),  (int)(originalImg.getHeight() * scale), originalImg.getType());
		scaledImg.getGraphics().drawImage(originalImg, 0, 0, scaledImg.getWidth(), scaledImg.getHeight(), null);
		
		newImg = new BufferedImage((int) (scaledImg.getWidth() * postScale), (int) (scaledImg.getHeight() * postScale), originalImg.getType());
		
        ArrayList<StringBuffer> strings = new ArrayList<StringBuffer>();
        
        BlockThread.setup(scaledImg, newImg);
        			
		blockThreadArray = new ArrayList<BlockThread>();
        for (int i = 0; i < threadCount; i++) {
           	blockThreadArray.add(new BlockThread("BlockThread" + i));
        }
        
        for (BlockThread b: blockThreadArray) {
        	b.start();
        }
        
        while (!BlockThread.isDone()) {
        	for (int i = 0; i < blockThreadArray.size(); i++) {
            	if (!blockThreadArray.get(i).isAlive()) {
            		blockThreadArray.get(i).add(newImg);
            		strings.add(blockThreadArray.get(i).getStringBuffer());
            		blockThreadArray.set(i, new BlockThread("BlockThread" + i));
            		blockThreadArray.get(i).start();
            		
            	}
        	}
        }
        while (blockThreadArray.size() > 0) {
            for (int i = 0; i < blockThreadArray.size(); i++) {
            	if (!blockThreadArray.get(i).isAlive()) {
					blockThreadArray.get(i).add(newImg);
					strings.add(blockThreadArray.get(i).getStringBuffer());
					blockThreadArray.remove(i);
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
			if (preDraw && blockThreadArray != null) { // TODO allow user to change if drawing or not
				Dimension windowSize = getSize();
				windowSize.height -= 14;
				for (BlockThread bt: blockThreadArray) {
					bt.paint(g2d, newImg.getWidth(), newImg.getHeight(), windowSize);
				}
			}
		}
    }
}