package blockStructure;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import global.G;

public class btRandom extends BlockThreadHandler {

	private int randomPlacementsDone;
	private final Dimension defaultSize;
	private Dimension imageSize;
	private ArrayList<Rectangle> alreadyTakenBlocks;
	
	public btRandom(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		allowAlpha = true;
		randomPlacementsDone = 0;
		imageSize = new Dimension(originalImg.getWidth(), originalImg.getHeight());
		defaultSize = new Dimension(imageSize.width / G.getBlocksWide(), imageSize.height / G.getBlocksWide());
		alreadyTakenBlocks = new ArrayList<>();
	}

	@Override
	public boolean isDone() {
		return randomPlacementsDone >= G.getRandomBlocks();
	}

	@Override
	synchronized BlockLocation getNewBlockLocation() {
		randomPlacementsDone++;
		Rectangle orig,
				first,
				second,
				third;
		BlockLocation bl;
		do {
			Dimension size = getBlock();
			
			do {
				orig = new Rectangle(
						G.getRandInt(imageSize.width - size.width),
						G.getRandInt(imageSize.height - size.height),
						size.width,
						size.height);
			} while (collides(orig) || orig.width <= 0 || orig.height <= 0);
			
			first = toRectangle(orig, G.getScale());
			second = toRectangle(first, G.getPostScale());
			third = toRectangle(second, G.getFinalScale());
			bl = new BlockLocation(orig, first, second, third);
			
		} while (
				first.width <= 0 || first.height <= 0 ||
				second.width <= 0 || second.height <= 0 ||
				third.width <= 0 || third.height <= 0);
		
		alreadyTakenBlocks.add(bl.original);
		
		return bl;
	}
	
	private static Rectangle toRectangle(Rectangle r, double scale) {
		return new Rectangle(
				(int)(r.x * scale),
				(int)(r.y * scale),
				(int)(r.width * scale),
				(int)(r.height * scale));
	}

	@Override
	void removeBlockLocation(BlockLocation blockLocation) {
		synchronized(this) {
			alreadyTakenBlocks.remove(blockLocation.original);
		}
	}
	
	private Dimension getBlock() {
		Dimension r = defaultSize.getSize();
		
		r.width *= G.getRandDouble() + 0.5;
		r.height *= G.getRandDouble() + 0.5;
		return r;
	}
	
	private synchronized boolean collides(Rectangle rect) {
		for (int i = 0; i < alreadyTakenBlocks.size(); i++) {
			if (alreadyTakenBlocks.get(i).intersects(rect)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public double getPercent() {
		return (double)randomPlacementsDone / G.getRandomBlocks();
	}
	
}