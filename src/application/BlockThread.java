package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class BlockThread extends Thread {

	private static BufferedImage 
			scaledUpImg,
			scaledImg;
	private static Dimension
			blockStandardSize, // Standard SIZE are the generic size of the block!
			newBlockStandardSize,
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
			blockSize,
			newBlockSize;
	private Point
			blockPosition,
			newBlockPosition;
	
	public BlockThread(String name) {
		super(name);
		position = getNextPosition();
	}

	public Point getNextBlockPosition() {
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
		if (getNextBlockPosition() == null) {
			System.out.println("POSITION IS NULL - BLOCK THREAD");
		}
		return new StringBuffer(getText(), getNextBlockPosition());
	}
	
	public static void setup(BufferedImage originalImg, BufferedImage scaledImg, BufferedImage newImg) {
		
		BlockThread.scaledImg = scaledImg;
		BlockThread.scaledUpImg = new BufferedImage(newImg.getWidth(), newImg.getHeight(), newImg.getType());// TODO
		scaledUpImg.getGraphics().drawImage(originalImg, 0, 0, scaledUpImg.getWidth(), scaledUpImg.getHeight(), null);
		
		BlockThread.blockStandardSize = new Dimension(scaledImg.getWidth()  / blocksWide, scaledImg.getHeight() / blocksWide);
		BlockThread.nextPos = new Point(0, 0);
		BlockThread.offSet = new Dimension(
				scaledImg.getWidth() - blocksWide * blockStandardSize.width,
				scaledImg.getHeight() - blocksWide * blockStandardSize.height);
		BlockThread.newBlockStandardSize = new Dimension(newImg.getWidth()  / blocksWide, newImg.getHeight() / blocksWide);
		BlockThread.newOffSet = new Dimension(
				newImg.getWidth() - blocksWide * newBlockStandardSize.width,
				newImg.getHeight() - blocksWide * newBlockStandardSize.height);
		
	}
	
	@Override
	public void run() {
		if (position == null) {
			return;
		}
		blockSize = new Dimension(
				getSize(position.x, blockStandardSize.width, offSet.width),
				getSize(position.y, blockStandardSize.height, offSet.height));
		blockPosition = new Point(
				getPoint(position.x, blockStandardSize.width, offSet.width),
				getPoint(position.y, blockStandardSize.height, offSet.height));
		newBlockSize = new Dimension(
				getSize(position.x, newBlockStandardSize.width, newOffSet.width),
				getSize(position.y, newBlockStandardSize.height, newOffSet.height));
		newBlockPosition = new Point(
				getPoint(position.x, newBlockStandardSize.width, newOffSet.width),
				getPoint(position.y, newBlockStandardSize.height, newOffSet.height));
		
		double bestScore = 0;
		Block bestBlock = null;
		while (currentSample < samples) {
			Block block = new Block(scaledImg, blockSize, blockPosition);
			while (!block.isDone()) {
				block.move();
				currentTestImage = block.getImage();
			}
			if (bestScore < block.getMaxScore()) {
				bestBlock = block;
				bestScore = block.getMaxScore();
			}
			currentSample++;
		}
		if (postProcessing) {
			
			
			Block block = new Block(scaledUpImg, newBlockSize, newBlockPosition, bestBlock.getTriangles());
			
			while (!block.isDone()) {
				block.move();
				currentTestImage = block.getImage();
			}
			bestBlock = block;
			
		}
		solvedText = bestBlock.getText(position.x, position.y, 1.0 / blocksWide);
		solvedImage = bestBlock.getImage(newBlockSize);
	}
	
	public void add(BufferedImage newImg) {
		if (position == null) {
			return;
		}
		Graphics2D g = newImg.createGraphics();
	    g.drawImage(solvedImage, newBlockPosition.x, newBlockPosition.y, newBlockSize.width, newBlockSize.height, null);
	    g.dispose();
	}
	
	public void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
		if (currentTestImage == null) {
			return;
		}
		g.drawImage(
				currentTestImage,
				newBlockPosition.x * windowSize.width / origW,
				newBlockPosition.y * windowSize.height / origH,
				newBlockSize.width * windowSize.width / origW,
				newBlockSize.height * windowSize.height / origH, null);
		g.setColor(Color.RED);
		g.drawString(currentSample + "",
				newBlockPosition.x * windowSize.width / origW, 
				newBlockPosition.y * windowSize.height / origH + 10);
	}
	
	public String getText() {
		return solvedText;
	}
	
	private int getSize(int i, int blockPixelSize, int offSet) {
		if (i < offSet) {
			return blockPixelSize + 1;
		}
		else {
			return blockPixelSize;
		}
	}
	
	private int getPoint(int i, int blockPixelSize, int offSet) {
		return i * blockPixelSize + Math.min(i, offSet);
	}
	
	public static boolean isDone() {
		return nextPos.y >= blocksWide;
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
		scaledImg = null;
		blockStandardSize = null;
		nextPos = null;
		offSet = null;
	}
}