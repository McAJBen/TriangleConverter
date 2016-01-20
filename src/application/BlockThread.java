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
			offSet;
	private static int 
			samples,
			blockSize;
	private static Point nextPos;
	
	private int currentSample;
	private BufferedImage 
			currentTestImage, 
			solvedImage;
	private Point position;
	private String solvedText = "";
	
	public BlockThread() {
		position = getNextPosition();
	}
	
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
		if (nextPos.x >= blockSize) {
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
		while (currentSample < samples) {
			Block block = new Block(originalImg, getPixels(position.x, position.y), getWidth(position.x), getHeight(position.y));
			while (!block.isDone()) {
				block.move(); // TODO stop slowdown right here by transfering image
				currentTestImage = block.getImage();
			}
			if (bestScore < block.getMaxScore()) {
				solvedImage = block.getImage();
				solvedText = block.getText(position.x, position.y, 1.0 / blockSize);
				bestScore = block.getMaxScore();
			}
			currentSample++;
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
	
	public void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
		if (currentTestImage == null) {
			return;
		}
		g.drawImage(
				currentTestImage,
				getWidth(position.x) * windowSize.width / origW,
				getHeight(position.y) * (windowSize.height - 14) / origH,
				currentTestImage.getWidth() * windowSize.width / origW,
				currentTestImage.getHeight() * windowSize.height / origH, null);
		g.setColor(Color.RED);
		g.drawString(currentSample + "",
				getWidth(position.x + 1) * windowSize.width / origW - 8, 
				getHeight(position.y + 1) * (windowSize.height - 14) / origH);
	}
	
	public String getText() {
		return solvedText;
	}
	
	private Dimension getPixels(int i, int j) {
		return new Dimension(
				getPixelsX(i),
				getPixelsY(j));
	}
	
	private int getPixelsX(int x) {
		return blockPixelSize.width + (x < (originalImg.getWidth() - blockSize * blockPixelSize.width) ? 1: 0);
	}
	
	private int getPixelsY(int y) {
		return blockPixelSize.height + (y < (originalImg.getHeight() - blockSize * blockPixelSize.height) ? 1: 0);
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

	public BufferedImage getImage() {
		return solvedImage;
	}

	public Point getWH() {
		return new Point(getWidth(position.x), getHeight(position.y));
	}

	public static void clear() {
		originalImg = null;
		blockPixelSize = null;
		nextPos = null;
		offSet = null;
	}

}

