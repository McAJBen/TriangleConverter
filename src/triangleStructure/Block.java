package triangleStructure;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import global.G;

public class Block {
	private static final double MAX_STAGNANT_VAL = 100;
	
	private BufferedImage
			compareChunk, // Image this Block is trying to solve
			lastImgChunk; // last tested image
	private TrianglesFile 
			bestTriFile; // Best set of triangles found so far
	private double 
			maxScore, // Max comparison score recorded by bestTriFile
			stagnantCount; // moves done since last improvement
	private TriangleMode 
			triangleMode = TriangleMode.RANDOM;
	
	public Block(BufferedImage compareImg, BufferedImage baseImg, Dimension size) {
		this(compareImg, baseImg, size, new ArrayList<Triangle>(Arrays.asList(new Triangle())));
	}

	public Block(BufferedImage compareImg, BufferedImage baseImg, Dimension size, ArrayList<Triangle> triangles) {
		
		compareChunk = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		compareChunk.createGraphics().drawImage(compareImg, 0, 0, size.width, size.height, null);
		
		bestTriFile = new TrianglesFile(triangles, size, baseImg);
		
		maxScore = bestTriFile.compare(compareChunk);
		lastImgChunk = bestTriFile.getImage();
	}

	// checks triangleMode to modify bestTriFile and see if it improves
	public void move() {
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
		double modifyScore = modifyTriFile.compare(compareChunk);
		
		if (G.getPreDrawShowBest()) {
			lastImgChunk = bestTriFile.getImage();
		}
		else {
			lastImgChunk = modifyTriFile.getImage();
		}
		
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
				while (bestTriFile.getSize() > G.getTriangles()) {
					bestTriFile.removeBackTriangle();
				}
				maxScore = bestTriFile.compare(compareChunk);
			}
		}
	}
	
	public boolean isDone(boolean ignoreAlpha) {
		if (ignoreAlpha || !bestTriFile.hasAlpha()) {
			if (maxScore > 0.99) {
				return true;
			}
			if (triangleMode == TriangleMode.REMOVE && bestTriFile.getSize() == G.getTriangles()) {
				return true;
			}
		}
		return false;
	}
	
	public BufferedImage getImage() {
		return lastImgChunk;
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
	// the current modify the block should make to a triangle
	private static enum TriangleMode {
		RANDOM, COLOR_10, SHAPE_FULL, SHAPE_10, 
		REMOVE;
		// increases to the next type of TriangleMode
		private TriangleMode next() {
			return values()[(ordinal() + 1) % 5];
		}
	}
}
