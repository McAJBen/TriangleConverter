package application;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

public class btGrid extends BlockThreadHandler {

	private D2D standardSize;
	private D2D midStandardSize;
	private D2D scaledStandardSize;
	
	private Point nextPos;

	public btGrid(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		
		standardSize = new D2D();
		midStandardSize = new D2D();
		scaledStandardSize = new D2D();
		
		standardSize.setSize(
			(double)originalImg.getWidth() / G.blocksWide,
			(double)originalImg.getHeight() / G.blocksWide);
		
		midStandardSize.setSize(
			standardSize.getWidth() * G.scale,
			standardSize.getHeight() * G.scale);
		
		scaledStandardSize.setSize(
			midStandardSize.getWidth() * G.postScale,
			midStandardSize.getHeight() * G.postScale);
		
		nextPos = new Point(0, 0);
	}

	@Override
	public boolean isDone() {
		return nextPos.y >= G.blocksWide;
	}

	@Override
	public BlockLocation getNewBlockLocation() {
		
		Rectangle orig = new Rectangle();
		Rectangle first = new Rectangle();
		Rectangle second = new Rectangle();
		
		do {
			Point position = getNextPos();
			if (position == null || position.y >= G.blocksWide) {
				return null;
			}
			orig.setLocation(
					(int)(position.x * standardSize.getWidth()),
					(int)(position.y * standardSize.getHeight()));
			orig.setSize(
					(int)((position.x + 1) * standardSize.getWidth()) - orig.x,
					(int)((position.y + 1) * standardSize.getHeight()) - orig.y);
			first.setLocation(
					(int)(position.x * midStandardSize.getWidth()),
					(int)(position.y * midStandardSize.getHeight()));
			first.setSize(
					(int)((position.x + 1) * midStandardSize.getWidth()) - first.x,
					(int)((position.y + 1) * midStandardSize.getHeight()) - first.y);
			second.setLocation(
					(int)(position.x * scaledStandardSize.getWidth()),
					(int)(position.y * scaledStandardSize.getHeight()));
			second.setSize(
					(int)((position.x + 1) * scaledStandardSize.getWidth()) - second.x,
					(int)((position.y + 1) * scaledStandardSize.getHeight()) - second.y);
		
		} while (orig.width <= 0 || orig.height <= 0 ||
				 first.width <= 0 || first.height <= 0 ||
				 second.width <= 0 || second.height <= 0);
		return new BlockLocation(orig, first, second);
	}
	
	@Override
	public void removeBlockLocation(BlockLocation blockLocation) {}
	
	private class D2D extends Dimension2D {
		private double width;
		private double height;
		@Override
		public double getHeight() {
			return height;
		}

		@Override
		public double getWidth() {
			return width;
		}

		@Override
		public void setSize(double width, double height) {
			this.width = width;
			this.height = height;
		}
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