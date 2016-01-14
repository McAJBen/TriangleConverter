package application;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class Block {
	private static final int STAGNANT_POWER = 2;
	private static final double TOTAL_STAGNANT_POWER = Math.pow(10, STAGNANT_POWER);
	private static int maxTriangles;
	private BufferedImage originalImgChunk,
			lastBestImg;
	private TrianglesFile bestTriFile;
	private double maxScore,
			score,
			stagnantCount;
	private Point pos;
	
	private enum TriangleMode {
		RANDOM, COLOR_10, SHAPE_FULL,
		SHAPE_10, REMOVE {
			@Override
			public TriangleMode next() {
				return TriangleMode.RANDOM;
			};
		};
		public TriangleMode next() {
			return values()[ordinal() + 1];
		}
	}
	
	private TriangleMode triangleMode = TriangleMode.RANDOM;
	
	public Block(BufferedImage originalImg, Dimension size, int x, int y) {
		bestTriFile = new TrianglesFile(0, size);
		maxScore = 0;
		originalImgChunk = new BufferedImage(size.width, size.height, originalImg.getType());
	    Graphics2D g = originalImgChunk.createGraphics();
	    g.drawImage(originalImg, -x, -y, null);
	    g.dispose();
		maxScore = bestTriFile.compare(originalImgChunk);
		lastBestImg = bestTriFile.getImage();
		pos = new Point(x, y);
	}
	
	public void move() {
		TrianglesFile modifyTriFile = new TrianglesFile(bestTriFile);
		
		switch (triangleMode) {
			case RANDOM:
				modifyTriFile.modifyRandom();
				break;
			case COLOR_10:
				modifyTriFile.modifyColor();
				break;
			case SHAPE_FULL:
				modifyTriFile.modifyShape2();
				break;
			case SHAPE_10:
				modifyTriFile.modifyShape();
				break;
			case REMOVE:
				modifyTriFile.modifyRemove();
				break;
		}
		score = modifyTriFile.compare(originalImgChunk);
		
		if (score >= maxScore) {
			if (score > maxScore) {
				maxScore = score;
				stagnantCount = 0;
			}
			else {
				stagnantCount++;
			}
			bestTriFile = modifyTriFile;
			lastBestImg = bestTriFile.getImage();
		}
		else {
			stagnantCount++;
		}
		if (stagnantCount > TOTAL_STAGNANT_POWER) {
			triangleMode = triangleMode.next();
			stagnantCount = 0;
			if (triangleMode == TriangleMode.RANDOM) {
				
				bestTriFile.addTriangle();
				
				while (bestTriFile.getSize() > maxTriangles) {
					bestTriFile.removeBackTriangle();
				}
				maxScore = bestTriFile.compare(originalImgChunk);
			}
		}
	}
	
	public void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
		g.drawImage(
				lastBestImg, 
				pos.x * windowSize.width / origW,
				pos.y * (windowSize.height - 14) / origH,
				lastBestImg.getWidth() * windowSize.width / origW,
				lastBestImg.getHeight() * windowSize.height / origH, null);
	}
	
	public boolean isDone() {
		return ((bestTriFile.getSize() >= maxTriangles && triangleMode == TriangleMode.REMOVE) || maxScore > 0.99) && !bestTriFile.hasAlpha();
	}
	
	public BufferedImage getImage() {
		return lastBestImg;
	}
	public String getText(double x, double y, double size) {
		return bestTriFile.getText(x, y, size);
	}
	
	public static void setMaxTriangles(int numTriangles) {
		maxTriangles = numTriangles;
	}
	
	public static int getMaxTriangles() {
		return maxTriangles;
	}
	
	public double getMaxScore() {
		return maxScore;
	}
}
