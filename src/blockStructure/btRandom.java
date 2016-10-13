package blockStructure;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import global.G;

public class btRandom extends BlockThreadHandler {

	private int randomPlacementsDone;
	private final Dimension defaultSize;
	private final Dimension imageSize;
	private ArrayList<Rectangle> alreadyTakenBlocks;
	
	public btRandom(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		allowAlpha = true;
		randomPlacementsDone = 0;
		imageSize = new Dimension(originalImg.getWidth(), originalImg.getHeight());
		defaultSize = new Dimension(imageSize.width / G.getBlocksWide(), imageSize.height / G.getBlocksWide());
		alreadyTakenBlocks = new ArrayList<>(G.getThreadCount());
	}

	public boolean isDone() {
		return randomPlacementsDone >= G.getRandomBlocks();
	}

	BlockLocation getNewBlockLocation() {
		randomPlacementsDone++;
		Rectangle orig,
				first,
				second,
				third;
		BlockLocation bl;
		do {
			Dimension size = getBlock();
			orig = getRandomRect(size);
			while (collides(orig) || orig.width <= 0 || orig.height <= 0) {
				orig = getRandomRect(size);
			}
			
			first = toRectangle(orig, G.getScale());
			second = toRectangle(orig, G.getScale() * G.getPostScale());
			third = toRectangle(orig, G.getScale() * G.getPostScale() * G.getFinalScale());
			
			
			bl = new BlockLocation(orig, first, second, third);
			
		} while (
				first.width <= 0 || first.height <= 0 ||
				second.width <= 0 || second.height <= 0 ||
				third.width <= 0 || third.height <= 0);
		
		return bl;
	}
	
	private Rectangle getRandomRect(Dimension size) {
		return new Rectangle(
				G.getRandInt(imageSize.width - size.width),
				G.getRandInt(imageSize.height - size.height),
				size.width,
				size.height);
	}
	
	private static Rectangle toRectangle(Rectangle r, double scale) {
		return new Rectangle(
				(int)(r.x * scale),
				(int)(r.y * scale),
				(int)(r.width * scale),
				(int)(r.height * scale));
	}

	void removeBlockLocation(BlockLocation blockLocation) {
		synchronized(alreadyTakenBlocks) {
			alreadyTakenBlocks.remove(blockLocation.original);
		}
	}
	
	private Dimension getBlock() {
		Dimension r = defaultSize.getSize();
		
		r.width *= G.getRandDouble() + 0.5;
		r.height *= G.getRandDouble() + 0.5;
		return r;
	}
	
	private boolean collides(Rectangle rect) {
		synchronized (alreadyTakenBlocks) {
			for (Rectangle bl: alreadyTakenBlocks) {
				if (bl.intersects(rect)) {
					return true;
				}
			}
			alreadyTakenBlocks.add(rect);
			return false;
		}
	}
	
	public double getPercent() {
		return (double)randomPlacementsDone / G.getRandomBlocks();
	}
	
	int getTotalTriangles() {
		return G.getTriangles() * G.getRandomBlocks();
	}
	
}