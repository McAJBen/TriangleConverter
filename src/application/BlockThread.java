package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlockThread extends Thread {

	private static BufferedImage 
			scaledUpImg,
			scaledImg;
	private static Dimension
			blockStandardSize, // Standard SIZE are the generic size of the block
			newBlockStandardSize,
			offSet,
			newOffSet;
	private static Point nextPos;
	private static Graphics2D newImgGraphics;
	
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
		if (nextPos.x >= G.blocksWide) {
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
	
	public static void setup(BufferedImage originalImg, BufferedImage scaleImg, BufferedImage newImg) {
		scaledImg = scaleImg;
		scaledUpImg = new BufferedImage(newImg.getWidth(), newImg.getHeight(), newImg.getType());
		scaledUpImg.getGraphics().drawImage(originalImg, 0, 0, scaledUpImg.getWidth(), scaledUpImg.getHeight(), null);
		
		blockStandardSize = new Dimension(scaledImg.getWidth() / G.blocksWide, scaledImg.getHeight() / G.blocksWide);
		nextPos = new Point(0, 0);
		offSet = new Dimension(
				scaledImg.getWidth() - G.blocksWide * blockStandardSize.width,
				scaledImg.getHeight() - G.blocksWide * blockStandardSize.height);
		newBlockStandardSize = new Dimension(newImg.getWidth() / G.blocksWide, newImg.getHeight() / G.blocksWide);
		newOffSet = new Dimension(
				newImg.getWidth() - G.blocksWide * newBlockStandardSize.width,
				newImg.getHeight() - G.blocksWide * newBlockStandardSize.height);
		newImgGraphics = newImg.createGraphics();
		
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
		while (currentSample < G.samples) {
			Block block = new Block(blockPosition, blockSize, scaledImg, new ArrayList<Triangle>());
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
		if (G.postProcessing) {
			Block block = new Block(newBlockPosition, newBlockSize, scaledUpImg, bestBlock.getTriangles());
			while (!block.isDone()) {
				block.move();
				currentTestImage = block.getImage();
			}
			bestBlock = block;
		}
		solvedText = bestBlock.getText(position.x, position.y, 1.0 / G.blocksWide);
		solvedImage = bestBlock.getImage(newBlockSize);
		
	    newImgGraphics.drawImage(solvedImage, newBlockPosition.x, newBlockPosition.y, newBlockSize.width, newBlockSize.height, null);
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
		
		g.setColor(Color.YELLOW);
		
		g.drawString(getName() + "",
				newBlockPosition.x * windowSize.width / origW + 1, 
				newBlockPosition.y * windowSize.height / origH + 11);
		g.drawRect(
				newBlockPosition.x * windowSize.width / origW,
				newBlockPosition.y * windowSize.height / origH,
				newBlockSize.width * windowSize.width / origW,
				newBlockSize.height * windowSize.height / origH);
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
		return nextPos.y >= G.blocksWide;
	}

	public static void clear() {
		scaledImg = null;
		blockStandardSize = null;
		nextPos = null;
		offSet = null;
	}
}