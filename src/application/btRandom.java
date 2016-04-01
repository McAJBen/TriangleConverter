package application;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

public class btRandom extends BlockThreadHandler {

	private Random rand;
	private int randomPlacementsDone;
	private int minWidth;
	private int deltaWidth;
	private Dimension imageSize;
	
	public btRandom(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		rand = new Random();
		randomPlacementsDone = 0;
		
		imageSize = new Dimension(originalImg.getWidth(), originalImg.getHeight());
		
		int maxW = Math.max(imageSize.width, imageSize.height);
		maxW *= 1.1;
		maxW /= G.blocksWide;
		minWidth = Math.min(imageSize.width, imageSize.height);
		minWidth *= 0.9;
		minWidth /= G.blocksWide;
		deltaWidth = maxW - minWidth;
	}

	@Override
	public boolean isDone() {
		return randomPlacementsDone >= G.randomPlacements;
	}

	@Override
	public BlockLocation getNewBlockLocation() {
		Dimension blockSize = new Dimension(getSize(), getSize());
		Point blockPosition = new Point(rand.nextInt(imageSize.width - blockSize.width), rand.nextInt(imageSize.height - blockSize.height));
		
		Dimension scaledBlockSize = new Dimension((int)(blockSize.width * G.postScale), (int)(blockSize.height * G.postScale));
		Point scaledBlockPosition = new Point((int)(blockPosition.x * G.postScale), (int)(blockPosition.y * G.postScale));
		
		randomPlacementsDone++;
		return new BlockLocation(blockSize, blockPosition, scaledBlockSize, scaledBlockPosition);
	}
	
	private int getSize() {
		return rand.nextInt(deltaWidth) + minWidth;
	}
}
