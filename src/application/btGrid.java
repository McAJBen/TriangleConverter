package application;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class btGrid extends BlockThreadHandler {

	private Dimension blockStandardSize;
	private Dimension standardOffSet;
	private Dimension scaledBlockStandardSize;
	private Dimension scaledStandardOffSet;
	private Point nextPos;

	public btGrid(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		blockStandardSize = new Dimension(
			originalImg.getWidth() / G.blocksWide,
			originalImg.getHeight() / G.blocksWide);
		standardOffSet = new Dimension(
			originalImg.getWidth() - G.blocksWide * blockStandardSize.width,
			originalImg.getHeight() - G.blocksWide * blockStandardSize.height);
		scaledBlockStandardSize = new Dimension(
			newImg.getWidth() / G.blocksWide,
			newImg.getHeight() / G.blocksWide);
		scaledStandardOffSet = new Dimension(
			newImg.getWidth() - G.blocksWide * scaledBlockStandardSize.width,
			newImg.getHeight() - G.blocksWide * scaledBlockStandardSize.height);
		nextPos = new Point(0, 0);
	}

	@Override
	public boolean isDone() {
		return nextPos.y >= G.blocksWide;
	}

	@Override
	public BlockLocation getNewBlockLocation() {
		
		Dimension blockSize;
		Point blockPosition;
		Dimension scaledBlockSize;
		Point scaledBlockPosition;
		
		do {
			Point position = getNextPos();
			if (position == null || position.y >= G.blocksWide) {
				return null;
			}
			blockSize = new Dimension(
					getSize(position.x, blockStandardSize.width, standardOffSet.width),
					getSize(position.y, blockStandardSize.height, standardOffSet.height));
			blockPosition = new Point(
					getPoint(position.x, blockStandardSize.width, standardOffSet.width),
					getPoint(position.y, blockStandardSize.height, standardOffSet.height));
			scaledBlockSize = new Dimension(
					getSize(position.x, scaledBlockStandardSize.width, scaledStandardOffSet.width),
					getSize(position.y, scaledBlockStandardSize.height, scaledStandardOffSet.height));
			scaledBlockPosition = new Point(
					getPoint(position.x, scaledBlockStandardSize.width, scaledStandardOffSet.width),
					getPoint(position.y, scaledBlockStandardSize.height, scaledStandardOffSet.height));
		
		} while (blockSize.width <= 0 || blockSize.height <= 0 ||
				scaledBlockSize.width <= 0 || scaledBlockSize.height <= 0);
		return new BlockLocation(blockSize, blockPosition, scaledBlockSize, scaledBlockPosition);
	}
	
	private static int getSize(int i, int blockPixelSize, int offSet) {
		if (i < offSet) {
			return blockPixelSize + 1;
		}
		else {
			return blockPixelSize;
		}
	}
	
	private static int getPoint(int i, int blockPixelSize, int offSet) {
		return i * blockPixelSize + Math.min(i, offSet);
	}

	private synchronized Point getNextPos() {
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
}