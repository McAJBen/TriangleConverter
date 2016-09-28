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
		Rectangle orig = new Rectangle(),
				first = new Rectangle(),
				second = new Rectangle(),
				third = new Rectangle();
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
			
			first = new Rectangle(
					(int)(orig.x * G.getScale()),
					(int)(orig.y * G.getScale()),
					(int)(orig.width * G.getScale()),
					(int)(orig.height * G.getScale()));
			
			second = new Rectangle(
					(int)(first.x * G.getPostScale()),
					(int)(first.y * G.getPostScale()),
					(int)(first.width * G.getPostScale()),
					(int)(first.height * G.getPostScale()));
			
			third = new Rectangle(
					(int)(second.x * G.getFinalScale()),
					(int)(second.y * G.getFinalScale()),
					(int)(second.width * G.getFinalScale()),
					(int)(second.height * G.getFinalScale()));
			
			bl = new BlockLocation(orig, first, second, third);
			
		} while (
				first.width <= 0 || first.height <= 0 ||
				second.width <= 0 || second.height <= 0 ||
				third.width <= 0 || third.height <= 0);
		
		alreadyTakenBlocks.add(bl.original);
		
		return bl;
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
	boolean usePreviousImage() {
		return true;
	}
	
	@Override
	public String getPercentDone() {
		double perc = (double)randomPlacementsDone / G.getRandomBlocks() * 100;
		return String.format("%s %2.0f %%", getClass().getSimpleName(), perc);
	}
	
}