package application;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Block {
	private static final double MAX_STAGNANT_VAL = 100;
	
	private BufferedImage
			imgChunk, // Image this Block is trying to solve
			lastBestImgChunk; // Last best solved image from this block
	private TrianglesFile 
			bestTriFile; // Best set of triangles found so far
	private double 
			maxScore, // Max comparison score recorded by bestTriFile
			stagnantCount; // moves done since last improvement
	private Point 
			pos; // position on greater image where this chunk is
	private TriangleMode 
			triangleMode = TriangleMode.RANDOM;
	
	// the current modify the block should make to a triangle
	private static enum TriangleMode {
		RANDOM, COLOR_10, SHAPE_FULL, SHAPE_10, 
		REMOVE {
			@Override // sets the last iterator back to the beginning
			public TriangleMode next() {
				return TriangleMode.RANDOM;
			};
		};
		// increases to the next type of TriangleMode
		public TriangleMode next() {
			return values()[ordinal() + 1];
		}
	}
	
	// position must be pixel position of top left of chunk
	// size = pixel size of chunk
	public Block(Point position, Dimension size, BufferedImage img, ArrayList<Triangle> trArray) {
		pos = position;
		imgChunk = new BufferedImage(size.width, size.height, img.getType());
		imgChunk.getGraphics().drawImage(img, -position.x, -position.y, null);
		bestTriFile = new TrianglesFile(trArray, size);
		maxScore = bestTriFile.compare(imgChunk);
		lastBestImgChunk = bestTriFile.getImage();
	}

	// checks triangleMode to modify bestTriFile and see if it improves
	public void move(int maxTriangles) {
		TrianglesFile modifyTriFile = new TrianglesFile(bestTriFile);
		// changes modifyTri based on triangleMode
		switch (triangleMode) {
			case RANDOM:
				modifyTriFile.modifyRandom();
				break;
			case COLOR_10:
				modifyTriFile.modifyColor10();
				break;
			case SHAPE_FULL:
				modifyTriFile.modifyShapeFull();
				break;
			case SHAPE_10:
				modifyTriFile.modifyShape10();
				break;
			case REMOVE:
				modifyTriFile.modifyRemove();
				break;
		}
		double modifyScore = modifyTriFile.compare(imgChunk);
		
		// checks if the modify improved
		if (modifyScore >= maxScore) {
			if (modifyScore > maxScore) {
				maxScore = modifyScore;
				stagnantCount = 0;
			}
			else {
				stagnantCount++;
			}
			bestTriFile = modifyTriFile;
			lastBestImgChunk = bestTriFile.getImage();
		}
		else {
			stagnantCount++;
		}
		// if modifying at the current triangleMode isn't doing enough
		if (stagnantCount > MAX_STAGNANT_VAL) {
			triangleMode = triangleMode.next();
			stagnantCount = 0;
			// if triangleMode is at the end try adding another triangle
			if (triangleMode == TriangleMode.RANDOM) {
				bestTriFile.addTriangle();
				while (bestTriFile.getSize() > maxTriangles) {
					bestTriFile.removeBackTriangle();
				}
				maxScore = bestTriFile.compare(imgChunk);
			}
		}
	}
	
	// paints the last best compare to screen
	public void paint(Graphics2D g, int origW, int origH, Dimension windowSize) {
		g.drawImage(
				lastBestImgChunk,
				pos.x * windowSize.width / origW,
				pos.y * windowSize.height / origH,
				lastBestImgChunk.getWidth() * windowSize.width / origW,
				lastBestImgChunk.getHeight() * windowSize.height / origH, null);
	}
	
	public boolean isDone(int maxTriangles) {
		if (bestTriFile.hasAlpha()) {
			return false;
		}
		else if (maxScore > 0.99) {
			return true;
		}
		if (bestTriFile.getSize() == maxTriangles) {
			if (triangleMode == TriangleMode.REMOVE) {
				return true;
			}
		}
		return false;
	}
	
	public BufferedImage getImage() {
		return lastBestImgChunk;
	}
	
	// gets .trifi information
	public String getText(double x, double y, double size) {
		return bestTriFile.getText(x, y, size);
	}
	
	public ArrayList<Triangle> getTriangles() {
		return bestTriFile.getTriangles();
	}
	
	public double getMaxScore() {
		return maxScore;
	}

	public BufferedImage getImage(Dimension newBlockPixelSize) {
		return bestTriFile.getImage(newBlockPixelSize);
	}
}
