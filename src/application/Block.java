package application;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Block {
	private static final int PAINT_IMAGE_SIZE = 250, 
			STAGNANT_POWER = 2;
	private static final double TOTAL_STAGNANT_POWER = Math.pow(10, STAGNANT_POWER);
	private static final int MAX_TRAINGLES = 2;
	private BufferedImage originalImg,
			lastBestImg;
	private TrianglesFile bestTriFile;
	private double maxScore,
			score,
			stagnantCount;
	
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
	
	public Block(BufferedImage origImg, Dimension size, int x, int y) {
		bestTriFile = new TrianglesFile(0, size);
		maxScore = 0;
		originalImg = new BufferedImage(size.width, size.height, origImg.getType());
	    Graphics2D g = originalImg.createGraphics();
	    g.drawImage(origImg, -x, -y, null);
	    g.dispose();
		maxScore = bestTriFile.compare(originalImg);
		lastBestImg = bestTriFile.getImage();
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
		score = modifyTriFile.compare(originalImg);
		
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
				
				while (bestTriFile.getSize() > MAX_TRAINGLES) {
					bestTriFile.removeBackTriangle();
				}
				maxScore = bestTriFile.compare(originalImg);
			}
		}
	}
	
	public void paint(Graphics2D g) {
		g.drawImage(originalImg, PAINT_IMAGE_SIZE, 0, PAINT_IMAGE_SIZE, PAINT_IMAGE_SIZE, null);
		g.drawImage(lastBestImg, PAINT_IMAGE_SIZE * 2, 0, PAINT_IMAGE_SIZE, PAINT_IMAGE_SIZE, null);
	}
	
	public boolean isDone() {
		return ((bestTriFile.getSize() >= MAX_TRAINGLES && triangleMode == TriangleMode.REMOVE) || maxScore > 0.99) && !bestTriFile.hasAlpha();
	}
	
	public Image getImage() {
		return lastBestImg;
	}
	
	@Override
	public String toString() {
		return bestTriFile.toString();
	}
}
