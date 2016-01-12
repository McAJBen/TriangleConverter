package application;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class BlockThread extends Thread {

	private static BufferedImage originalImg;
	private static Dimension blockPixelSize;
	private static int blockSize;
	private static Point nextPos;
	
	private BufferedImage newImage;
	private Dimension size;
	private Point position;
	
	
	public BlockThread(int i) {
		position = new Point(i, 0);
	}
	
	
	public void cont() {
		position.x+= 2;
		if (position.x >= blockSize) {
			position.x -= blockSize;
			position.y++;
			
		}
		
		if (position.x % 2 == 0) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public BlockThread() {
		//position = getPosition();
		
		
	}
	
	private static Point getPosition() {
		Point p = (Point) nextPos.clone();
		
		nextPos.x++;
		if (nextPos.x == blockSize) {
			nextPos.y++;
			nextPos.x = 0;
			if (nextPos.y == blockSize) {
				// TODO out of points
				System.out.println("OUT OF POINTS TO GIVE");
				nextPos.y = 0;
			}
		}
		
		return p;
	}
	
	public static void setup(BufferedImage originalImg, int blockSize, Dimension blockPixelSize) {
		BlockThread.originalImg = originalImg;
		BlockThread.blockSize = blockSize;
		BlockThread.blockPixelSize = blockPixelSize;
		BlockThread.nextPos = new Point(0, 0);
	}
	
	
	@Override
	public void run() {
		
		paintBlock();
		
		super.run();
		
		
		
		
	}
	
	
	
	private void paintBlock() {
		
		size = new Dimension(getWidth(position.x), getHeight(position.y));
		
		Block block = new Block(originalImg, getPixels(position.x, position.y), size.width, size.height);
		
		while (!block.isDone()) {
			block.move();
			/*if (getFrame()) {
				repaint();
			}*/ // TODO figure out if I need this
		}
		newImage = block.getImage();
	}
	
	
	public void add(BufferedImage newImg) {
		Graphics2D g = newImg.createGraphics();
	    g.drawImage(newImage, size.width, size.height, null);
	    g.dispose();
	}
	
	private Dimension getPixels(int i, int j) {
		Dimension d = new Dimension(
				blockPixelSize.width + (i < (originalImg.getWidth() - blockSize * blockPixelSize.width) ? 1: 0),
				blockPixelSize.height + (j < (originalImg.getHeight() - blockSize * blockPixelSize.height) ? 1: 0));
		return d;
	}
	
	private int getWidth(int i) {
		int offSet = originalImg.getWidth() - blockSize * blockPixelSize.width;
		return i * blockPixelSize.width + (i < offSet ? i : offSet);
		
	}
	private int getHeight(int j) {
		int offSet = originalImg.getHeight() - blockSize * blockPixelSize.height;
		return j * blockPixelSize.height + (j < offSet ? j : offSet);
	}
	
	public static boolean isDone() {
		if (nextPos.y == blockSize) {
			return true;
		}
		return false;
	}
}

