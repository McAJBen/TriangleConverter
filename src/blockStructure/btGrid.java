package blockStructure;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import global.G;

public class btGrid extends BlockThreadHandler {

	private D2D standardSize;
	private D2D midStandardSize;
	private D2D scaledStandardSize;
	private D2D finalStandardSize;
	
	private Point nextPos;

	public btGrid(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		
		standardSize = new D2D();
		midStandardSize = new D2D();
		scaledStandardSize = new D2D();
		finalStandardSize = new D2D();
		
		standardSize.setSize(
			(double)originalImg.getWidth() / G.getBlocksWide(),
			(double)originalImg.getHeight() / G.getBlocksWide());
		
		midStandardSize.setSize(
			standardSize.getWidth() * G.getScale(),
			standardSize.getHeight() * G.getScale());
		
		scaledStandardSize.setSize(
			midStandardSize.getWidth() * G.getPostScale(),
			midStandardSize.getHeight() * G.getPostScale());
		
		finalStandardSize.setSize(
				scaledStandardSize.getWidth() * G.getFinalScale(),
				scaledStandardSize.getHeight() * G.getFinalScale());
		
		nextPos = new Point(0, 0);
	}

	@Override
	public boolean isDone() {
		return nextPos.y >= G.getBlocksWide();
	}

	@Override
	public BlockLocation getNewBlockLocation() {
		
		Rectangle orig;
		Rectangle first;
		Rectangle second;
		Rectangle third;
		
		do {
			Point position = getNextPos();
			if (position == null || position.y >= G.getBlocksWide()) {
				return null;
			}
			orig = toRectangle(position, standardSize);
			first = toRectangle(position, midStandardSize);
			second = toRectangle(position, scaledStandardSize);
			third = toRectangle(position, finalStandardSize);
			
		} while (orig.width <= 0 || orig.height <= 0 ||
				 first.width <= 0 || first.height <= 0 ||
				 second.width <= 0 || second.height <= 0 ||
				 third.width <= 0 || third.height <= 0);
		return new BlockLocation(orig, first, second, third);
	}
	
	private static Rectangle toRectangle(Point pos, D2D size) {
		Rectangle r = new Rectangle();
		
		r.setLocation(
				(int)(pos.x * size.getWidth()),
				(int)(pos.y * size.getHeight()));
		r.setSize(
				(int)((pos.x + 1) * size.getWidth()) - r.x,
				(int)((pos.y + 1) * size.getHeight()) - r.y);
		
		return r;
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

	private Point getNextPos() {
		synchronized (nextPos) {
			Point p = (Point) nextPos.clone();
			nextPos.x++;
			if (nextPos.x >= G.getBlocksWide()) {
				nextPos.y++;
				nextPos.x = 0;
			}
			return p;
		}
	}
	
	@Override
	public double getPercent() {
		return (nextPos.getX() / G.getBlocksWide() + nextPos.y) / G.getBlocksWide();
	}
}