package application;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class BlockThread extends Thread {

	private static BufferedImage originalImg;
	private static Dimension
			blockPixelSize,
			offSet;
	private static int 
			samples,
			blockSize;
	private static Point nextPos;
	
	private BufferedImage solvedImage;
	private Point position;
	private String solvedText = "";
	
	public BlockThread() {
		position = getPosition();
	}
	
	private static Point getPosition() {
		if (isDone()) {
			return null;
		}
		Point p = (Point) nextPos.clone();
		nextPos.x++;
		if (nextPos.x >= blockSize) {
			nextPos.y++;
			nextPos.x = 0;
		}
		
		return p;
	}
	
	public static void setup(BufferedImage originalImg) {
		BlockThread.originalImg = originalImg;
		BlockThread.blockPixelSize = new Dimension(originalImg.getWidth()  / blockSize, originalImg.getHeight() / blockSize);
		BlockThread.nextPos = new Point(0, 0);
		BlockThread.offSet = new Dimension(
				originalImg.getWidth() - blockSize * blockPixelSize.width,
				originalImg.getHeight() - blockSize * blockPixelSize.height);
	}
	
	@Override
	public void run() {
		if (position == null) {
			return;
		}
		double bestScore = 0;
		for (int i = 0; i < samples; i++) {
			Block block = new Block(originalImg, getPixels(position.x, position.y), getWidth(position.x), getHeight(position.y));
			while (!block.isDone()) {
				block.move();
			}
			if (bestScore < block.getMaxScore()) {
				solvedImage = block.getImage();
				solvedText = block.getText(position.x, position.y, 1.0 / blockSize);
				bestScore = block.getMaxScore();
			}
		}
	}
	
	public void add(BufferedImage newImg) {
		if (position == null) {
			return;
		}
		Graphics2D g = newImg.createGraphics();
	    g.drawImage(solvedImage, getWidth(position.x), getHeight(position.y), null);
	    g.dispose();
	}
	
	public String getText() {
		return solvedText;
	}
	
	private Dimension getPixels(int i, int j) {
		return new Dimension(
				blockPixelSize.width + (i < (originalImg.getWidth() - blockSize * blockPixelSize.width) ? 1: 0),
				blockPixelSize.height + (j < (originalImg.getHeight() - blockSize * blockPixelSize.height) ? 1: 0));
	}
	
	private int getWidth(int i) {
		return i * blockPixelSize.width + (i < offSet.width ? i : offSet.width);
	}
	
	private int getHeight(int j) {
		return j * blockPixelSize.height + (j < offSet.height ? j : offSet.height);
	}
	
	public static boolean isDone() {
		if (nextPos.y >= blockSize) {
			return true;
		}
		return false;
	}
	
	public static void setSamples(int samp) {
		samples = samp;
	}
	
	public static int getSamples() {
		return samples;
	}
	
	public static void setBlockSize(int blksize) {
		blockSize = blksize;
	}
	
	public static int getBlockSize() {
		return blockSize;
	}
}

