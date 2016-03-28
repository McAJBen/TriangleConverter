package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlockThread extends Thread {

	private static Thread mainThread;
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
	private static boolean calledInterrupt;
	
	private int currentSample;
	private BufferedImage 
			currentTestImage;
	private Point position;
	private Dimension 
			blockSize,
			newBlockSize;
	private Point
			blockPosition,
			newBlockPosition;
	
	public BlockThread(String name) {
		super(name);
		reset();
	}

	public Point getNextBlockPosition() {
		return position;
	}
	
	private static synchronized Point getNextPosition() {
		if (isDone()) {
			callInterrupt();
			return null;
		}
		Point p = (Point) nextPos.clone();
		nextPos.x++;
		if (nextPos.x >= G.blocksWide) {
			nextPos.y++;
			nextPos.x = 0;
			if (!G.display) {
				System.out.printf("%.2f%%%n",100.0 * nextPos.y / G.blocksWide);
			}
		}
		return p;
	}
	
	private static synchronized void callInterrupt() {
		if (!calledInterrupt) {
			mainThread.interrupt();
			calledInterrupt = true;
		}
	}

	public static void setup(BufferedImage originalImg, BufferedImage scaleImg, BufferedImage newImg, Thread mainThread) {
		calledInterrupt = false;
		BlockThread.mainThread = mainThread;
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
		while (position != null) {
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
			
			if (blockSize.width <= 0 || blockSize.height <= 0 ||
				newBlockSize.width <= 0 || newBlockSize.height <= 0) {
				
				reset();
				continue;
			}
			
			
			double bestScore = 0;
			Block bestBlock = null;
			while (currentSample < G.samples) {
				Block block = new Block(blockPosition, blockSize, scaledImg, new ArrayList<Triangle>());
				while (!block.isDone()) {
					block.move();
					if (G.preDraw) {
						currentTestImage = block.getImage();
					}
				}
				currentTestImage = block.getImage();
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
					if (G.preDraw) {
						currentTestImage = block.getImage();
					}
				}
				currentTestImage = block.getImage();
				bestBlock = block;
			}
			
			paintTo(bestBlock.getImage(newBlockSize), newBlockPosition, newBlockSize);
		    
		    reset();
		}
	}
	
	private static synchronized void paintTo(BufferedImage b, Point p, Dimension size) {
		newImgGraphics.drawImage(b, p.x, p.y, size.width, size.height, null);
	}
	
	private void reset() {
		position = getNextPosition();
		currentSample = 0;
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