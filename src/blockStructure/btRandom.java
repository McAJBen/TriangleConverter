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
			second = toRectangle(first, G.getPostScale());
			third = toRectangle(second, G.getFinalScale());
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
				Math.toIntExact(Math.round(r.x * scale)),
				Math.toIntExact(Math.round(r.y * scale)),
				Math.toIntExact(Math.round(r.width * scale)),
				Math.toIntExact(Math.round(r.height * scale)));
	}

	@Override
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
	
	@Override
	public double getPercent() {
		return (double)randomPlacementsDone / G.getRandomBlocks();
	}
	
}