package application;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class btRandom extends BlockThreadHandler {

	private int randomPlacementsDone;
	private final Dimension defaultSize;
	private Dimension imageSize;
	
	private ArrayList<Rectangle> alreadyTakenBlocks;
	
	btRandom(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		randomPlacementsDone = 0;
		
		imageSize = new Dimension(originalImg.getWidth(), originalImg.getHeight());
		
		defaultSize = new Dimension(imageSize.width / G.blocksWide, imageSize.height / G.blocksWide);
		
		alreadyTakenBlocks = new ArrayList<>();
	}

	@Override
	public boolean isDone() {
		return randomPlacementsDone >= G.randomBlocks;
	}

	@Override
	synchronized BlockLocation getNewBlockLocation() {
		randomPlacementsDone++;
		Rectangle orig, first, second;
		BlockLocation bl;
		do {
			Dimension size = getBlock();
			
			orig = new Rectangle(
					G.RANDOM.nextInt(imageSize.width - size.width),
					G.RANDOM.nextInt(imageSize.height - size.height),
					size.width,
					size.height);
			
			first = new Rectangle(
					(int)(orig.x * G.scale),
					(int)(orig.y * G.scale),
					(int)(orig.width * G.scale),
					(int)(orig.height * G.scale));
			
			second = new Rectangle(
					(int)(first.x * G.postScale),
					(int)(first.y * G.postScale),
					(int)(first.width * G.postScale),
					(int)(first.height * G.postScale));
			
			bl = new BlockLocation(orig, first, second);
		} while (collides(bl) ||
				orig.width <= 0 || orig.height <= 0 ||
				first.width <= 0 || first.height <= 0 ||
				second.width <= 0 || second.height <= 0);
		
		
		
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
		
		r.width *= G.RANDOM.nextDouble() + 0.5;
		r.height *= G.RANDOM.nextDouble() + 0.5;
		return r;
	}
	
	private synchronized boolean collides(BlockLocation bl) {
		for (int i = 0; i < alreadyTakenBlocks.size(); i++) {
			if (alreadyTakenBlocks.get(i).contains(bl.original)) {
				return true;
			}
		}
		return false;
	}

	
}