package application;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class btRandom extends BlockThreadHandler {

	private int randomPlacementsDone;
	private int minWidth;
	private int deltaWidth;
	private Dimension imageSize;
	
	private ArrayList<Rectangle> alreadyTakenBlocks;
	
	btRandom(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		randomPlacementsDone = 0;
		
		imageSize = new Dimension(originalImg.getWidth(), originalImg.getHeight());
		
		int maxW = Math.max(imageSize.width, imageSize.height);
		maxW *= 1.1;
		maxW /= G.blocksWide;
		minWidth = Math.min(imageSize.width, imageSize.height);
		minWidth *= 0.9;
		minWidth /= G.blocksWide;
		deltaWidth = maxW - minWidth;
		alreadyTakenBlocks = new ArrayList<>();
	}

	@Override
	public boolean isDone() {
		return randomPlacementsDone >= G.randomBlocks;
	}

	@Override
	synchronized BlockLocation getNewBlockLocation() {
		randomPlacementsDone++;
		BlockLocation bl;
		do {
			int w = getSize();
			int h = getSize();
			Rectangle blockSize = new Rectangle(
					G.RANDOM.nextInt(imageSize.width - w), G.RANDOM.nextInt(imageSize.height - h),
					w, h);
			
			Rectangle scaledBlockSize = new Rectangle(
					(int)(blockSize.x * G.scale), (int)(blockSize.y * G.scale),
					(int)(blockSize.width * G.scale), (int)(blockSize.height * G.scale));
			
			Rectangle scaled2BlockSize = new Rectangle(
					(int)(blockSize.x * G.postScale * G.scale), (int)(blockSize.y * G.postScale * G.scale),
					(int)(blockSize.width * G.postScale * G.scale), (int)(blockSize.height * G.postScale * G.scale));
			
			bl = new BlockLocation(blockSize, scaledBlockSize, scaled2BlockSize);
		} while (collides(bl));
		
		alreadyTakenBlocks.add(bl.original);
		
		return bl;
	}
	
	private boolean collides(BlockLocation bl) {
		for (int i = 0; i < alreadyTakenBlocks.size(); i++) {
			if (alreadyTakenBlocks.get(i).contains(bl.original)) {
				return true;
			}
		}
		return false;
	}

	@Override
	void removeBlockLocation(BlockLocation blockLocation) {
		alreadyTakenBlocks.remove(blockLocation.original);
	}
	
	private int getSize() {
		return G.RANDOM.nextInt(deltaWidth) + minWidth;
	}
}