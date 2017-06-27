package blockStructure;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import global.G;

public class btRandom extends BlockThreadHandler {

	private int randomPlacementsDone;
	private final Dimension defaultSize;
	private final Dimension imageSize;
	private ArrayList<Rectangle> alreadyTakenBlocks;
	
	public btRandom(BufferedImage originalImg, BufferedImage newImg) {
		super(originalImg, newImg);
		randomPlacementsDone = 0;
		imageSize = new Dimension(originalImg.getWidth(), originalImg.getHeight());
		defaultSize = new Dimension(imageSize.width / G.getBlocksWide(), imageSize.height / G.getBlocksWide());
		alreadyTakenBlocks = new ArrayList<>(G.getThreadCount());
	}

	public boolean isDone() {
		return randomPlacementsDone >= G.getRandomBlocks();
	}
	
	public double getPercent() {
		return (double)randomPlacementsDone / G.getRandomBlocks();
	}

	BlockLocation getNewBlockLocation() {
		randomPlacementsDone++;
		Rectangle orig,
				scaled,
				post;
		BlockLocation bl;
		do {
			orig = getValidRect();
			scaled = toRectangle(orig, G.getScale());
			post = toRectangle(orig, G.getScale() * G.getPostScale());
			
			bl = new BlockLocation(orig, scaled, post);
			
		} while (
				scaled.width <= 0 || scaled.height <= 0 ||
				post.width <= 0 || post.height <= 0);
		
		return bl;
	}
	
	private Rectangle getValidRect() {
		while (true) {
			Dimension size = getBlock();
			for (int i = 0; i < 100; i++) {
				Rectangle orig = getRandomRect(size);
				if (orig.width > 0 && orig.height > 0 && !collides(orig)) {
					return orig;
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void removeBlockLocation(BlockLocation blockLocation) {
		synchronized(alreadyTakenBlocks) {
			alreadyTakenBlocks.remove(blockLocation.original);
		}
	}
	
	private static Rectangle toRectangle(Rectangle r, double scale) {
		return new Rectangle(
				(int)(r.x * scale),
				(int)(r.y * scale),
				(int)(r.width * scale),
				(int)(r.height * scale));
	}
	
	private Rectangle getRandomRect(Dimension size) {
		return new Rectangle(
				G.getRandInt(imageSize.width - size.width),
				G.getRandInt(imageSize.height - size.height),
				size.width,
				size.height);
	}

	private Dimension getBlock() {
		Dimension r;
		do {
			r = defaultSize.getSize();
			r.width *= G.getRandDouble() + 0.5;
			r.height *= G.getRandDouble() + 0.5;
		} while (r.width <= 0 || r.width >= defaultSize.width || r.height <= 0 || r.height >= defaultSize.height);
		
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
}