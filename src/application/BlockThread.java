package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class BlockThread extends Thread {

	private static BufferedImage originalImg;
	private static Dimension
			blockPixelSize,
			newBlockPixelSize,
			offSet,
			newOffSet;
	private static int 
			samples,
			blocksWide;
	private static Point nextPos;
	private static boolean postProcessing;
	
	private int currentSample;
	private BufferedImage 
			currentTestImage, 
			solvedImage;
	private Point position;
	private String solvedText = "";
	private Dimension 
			blockSize, blockPosition,
			newBlockSize, newBlockPosition;
	
	public BlockThread(String name) {
		super(name);
		position = getNextPosition();
		
	}

	public Point getPosition() {
		return position;
	}
	
	private static Point getNextPosition() {
		if (isDone()) {
			return null;
		}
		Point p = (Point) nextPos.clone();
		nextPos.x++;
		if (nextPos.x >= blocksWide) {
			nextPos.y++;
			nextPos.x = 0;
		}
		
		return p;
	}

	public StringBuffer getStringBuffer() {
		if (getPosition() == null) {
			System.out.println("POSITION IS NULL - BLOCK THREAD");
		}
		return new StringBuffer(getText(), getPosition());
	}
	
	public static void setup(BufferedImage originalImg, BufferedImage newImg) {
		BlockThread.originalImg = originalImg;
		BlockThread.blockPixelSize = new Dimension(originalImg.getWidth()  / blocksWide, originalImg.getHeight() / blocksWide);
		BlockThread.nextPos = new Point(0, 0);
		BlockThread.offSet = new Dimension(
				originalImg.getWidth() - blocksWide * blockPixelSize.width,
				originalImg.getHeight() - blocksWide * blockPixelSize.height);
		BlockThread.newBlockPixelSize = new Dimension(newImg.getWidth()  / blocksWide, newImg.getHeight() / blocksWide);
		BlockThread.newOffSet = new Dimension(
				newImg.getWidth() - blocksWide * newBlockPixelSize.width,
				newImg.getHeight() - blocksWide * newBlockPixelSize.height);
	}
	
	@Override
	public void run() {
		if (position == null) {
			return;
		}
		blockSize = new Dimension(
				getPixelSize(position.x, blockPixelSize.width, offSet.width),
				getPixelSize(position.y, blockPixelSize.height, offSet.height));
		blockPosition = new Dimension(
				getPixelPoint(position.x, blockPixelSize.width, offSet.width),
				getPixelPoint(position.y, blockPixelSize.height, offSet.height));
		newBlockSize = new Dimension(
				getPixelSize(position.x, newBlockPixelSize.width, newOffSet.width),
				getPixelSize(position.y, newBlockPixelSize.height, newOffSet.height));
		newBlockPosition = new Dimension(
				getPixelPoint(position.x, newBlockPixelSize.width, newOffSet.width),
				getPixelPoint(position.y, newBlockPixelSize.height, newOffSet.height));
		
		double bestScore = 0;
		Block bestBlock = null;
		while (currentSample < samples) {
			Block block = new Block(originalImg, blockSize, blockPosition.width, blockPosition.height);
			while (!block.isDone()) {
				block.move(); // TODO stop slow down right here by transferring image
				currentTestImage = block.getImage();
			}
			if (bestScore < block.getMaxScore()) {
				bestBlock = block;
				bestScore = block.getMaxScore();
			}
			currentSample++;
			
		}
		if (postProcessing) {
			
			Block block = new Block(bestBlock, newBlockSize, newBlockPosition.width, newBlockPosition.height);
			
			while (!block.isDone()) {
				block.move(); // TODO stop slow down right here by transferring image
				currentTestImage = block.getImage();
			}
			bestBlock = block;
			
		}// TODO postProcessing
		solvedText = bestBlock.getText(position.x, position.y, 1.0 / blocksWide);
		solvedImage = bestBlock.getImage(newBlockPixelSize);
	}
	
	public void add(BufferedImage newImg) {
		if (position == null) {
			return;
		}
		Graphics2D g = newImg.createGraphics();
	    g.drawImage(solvedImage, newBlockPosition.width, newBlockPosition.height, newBlockSize.width, newBlockSize.height, null);
	    g.dispose();
	}
	
	public void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
		if (currentTestImage == null) {
			return;
		}
		g.drawImage(
				currentTestImage,
				newBlockPosition.width * windowSize.width / origW,
				newBlockPosition.height * windowSize.height / origH,
				newBlockSize.width * windowSize.width / origW,
				newBlockSize.height * windowSize.height / origH, null);
		g.setColor(Color.RED);
		g.drawString(currentSample + "",
				newBlockPosition.width * windowSize.width / origW, 
				newBlockPosition.height * windowSize.height / origH + 10);
	}
	
	public String getText() {
		return solvedText;
	}
	
	private int getPixelSize(int i, int blockPixelSize, int offSet) {
		return blockPixelSize + (i < offSet ? 1: 0);
	}
	
	private int getPixelPoint(int i, int blockPixelSize, int offSet) {
		return i * blockPixelSize + Math.min(i, offSet);
	}
	
	public static boolean isDone() {
		if (nextPos.y >= blocksWide) {
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
		blocksWide = blksize;
	}

	public static void setPostProcessing(boolean postProces) {
		postProcessing = postProces;
	}
	
	public static int getBlockSize() {
		return blocksWide;
	}

	public static void clear() {
		originalImg = null;
		blockPixelSize = null;
		nextPos = null;
		offSet = null;
	}


}

