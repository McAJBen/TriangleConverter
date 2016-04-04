package application;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class btGrid extends BlockThreadHandler {

	private Dimension standardSize;
	private Dimension standardOffSet;
	private Dimension midStandardSize;
	private Dimension midStandardOffSet;
	private Dimension scaledStandardSize;
	private Dimension scaledStandardOffSet;
	private Point nextPos;

	public btGrid(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		standardSize = new Dimension(
			originalImg.getWidth() / G.blocksWide,
			originalImg.getHeight() / G.blocksWide);
		standardOffSet = new Dimension(
			originalImg.getWidth() - G.blocksWide * standardSize.width,
			originalImg.getHeight() - G.blocksWide * standardSize.height);
		
		midStandardSize = new Dimension(
			(int) (originalImg.getWidth() * G.scale / G.blocksWide),
			(int) (originalImg.getHeight() * G.scale / G.blocksWide));
		midStandardOffSet = new Dimension(
			(int) (originalImg.getWidth() * G.scale - G.blocksWide * midStandardSize.width),
			(int) (originalImg.getHeight() * G.scale - G.blocksWide * midStandardSize.height));
		
		scaledStandardSize = new Dimension(
			newImg.getWidth() / G.blocksWide,
			newImg.getHeight() / G.blocksWide);
		scaledStandardOffSet = new Dimension(
			newImg.getWidth() - G.blocksWide * scaledStandardSize.width,
			newImg.getHeight() - G.blocksWide * scaledStandardSize.height);
		nextPos = new Point(0, 0);
	}

	@Override
	public boolean isDone() {
		return nextPos.y >= G.blocksWide;
	}

	@Override
	public BlockLocation getNewBlockLocation() {
		
		Rectangle orig;
		Rectangle first;
		Rectangle second;
		
		do {
			Point position = getNextPos();
			if (position == null || position.y >= G.blocksWide) {
				return null;
			}
			orig = new Rectangle(
					getPoint(position.x, standardSize.width, standardOffSet.width),
					getPoint(position.y, standardSize.height, standardOffSet.height),
					getSize(position.x, standardSize.width, standardOffSet.width),
					getSize(position.y, standardSize.height, standardOffSet.height));
			first = new Rectangle(
					getPoint(position.x, midStandardSize.width, midStandardOffSet.width),
					getPoint(position.y, midStandardSize.height, midStandardOffSet.height),
					getSize(position.x, midStandardSize.width, midStandardOffSet.width),
					getSize(position.y, midStandardSize.height, midStandardOffSet.height));
			second = new Rectangle(
					getPoint(position.x, scaledStandardSize.width, scaledStandardOffSet.width),
					getPoint(position.y, scaledStandardSize.height, scaledStandardOffSet.height),
					getSize(position.x, scaledStandardSize.width, scaledStandardOffSet.width),
					getSize(position.y, scaledStandardSize.height, scaledStandardOffSet.height));
		
		} while (orig.width <= 0 || orig.height <= 0 ||
				 first.width <= 0 || first.height <= 0 ||
				 second.width <= 0 || second.height <= 0);
		return new BlockLocation(orig, first, second);
	}
	
	@Override
	public void removeBlockLocation(BlockLocation blockLocation) {}
	
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