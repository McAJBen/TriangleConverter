package application;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Block {
	private static final double MAX_STAGNANT_VAL = 100;
	
	private BufferedImage
			compareChunk, // Image this Block is trying to solve
			lastBestImgChunk; // Last best solved image from this block
	private TrianglesFile 
			bestTriFile; // Best set of triangles found so far
	private double 
			maxScore, // Max comparison score recorded by bestTriFile
			stagnantCount; // moves done since last improvement
	private TriangleMode 
			triangleMode = TriangleMode.RANDOM;
	
	
	/*Block(Rectangle rectangle, BufferedImage img) {
		this(rectangle, img, null, new ArrayList<Triangle>());
	}
	
	// position must be pixel position of top left of chunk
	// size = pixel size of chunk
	Block(Rectangle inR, Rectangle outR, BufferedImage img, BufferedImage baseImg, ArrayList<Triangle> trArray) {
		compareChunk = new BufferedImage(inR.width, inR.height, img.getType());
		compareChunk.getGraphics().drawImage(img, -inR.x, -inR.y, null);
		
		BufferedImage baseChunk = new BufferedImage(inR.width, inR.height, img.getType());
		if (baseImg != null) {
			baseChunk.getGraphics().drawImage(baseImg, -inR.x, -inR.y, null);
		}
		if (trArray.size() <= 0) {
			trArray.add(new Triangle());
		}
		bestTriFile = new TrianglesFile(trArray, inR.getSize(), baseChunk);
		maxScore = bestTriFile.compare(compareChunk);
		lastBestImgChunk = bestTriFile.getImage();
	}*/
	
	public Block(Rectangle original, Rectangle scaled, BufferedImage originalImg) {
		this(original, scaled, originalImg, new ArrayList<Triangle>(Arrays.asList(new Triangle())));
	}

	public Block(Rectangle original, Rectangle scaled, BufferedImage originalImg, ArrayList<Triangle> triangles) {
		
		BufferedImage b = originalImg.getSubimage(original.x, original.y, original.width, original.height);
		
		compareChunk = new BufferedImage(scaled.width, scaled.height, BufferedImage.TYPE_INT_ARGB);
		compareChunk.createGraphics().drawImage(b, 0, 0, scaled.width, scaled.height, null);
		
		// TODO breaks something
		bestTriFile = new TrianglesFile(triangles, scaled.getSize());
		
		maxScore = bestTriFile.compare(compareChunk);
		lastBestImgChunk = bestTriFile.getImage();
		
		
	}

	// checks triangleMode to modify bestTriFile and see if it improves
	void move() {
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
				while (bestTriFile.getSize() > G.maxTriangles) {
					bestTriFile.removeBackTriangle();
				}
				maxScore = bestTriFile.compare(compareChunk);
			}
		}
	}
	
	boolean isDone() {
		if (bestTriFile.hasAlpha()) {
			return false;
		}
		else if (maxScore > 0.99) {
			return true;
		}
		if (bestTriFile.getSize() == G.maxTriangles) {
			if (triangleMode == TriangleMode.REMOVE) {
				return true;
			}
		}
		return false;
	}
	
	BufferedImage getImage() {
		return lastBestImgChunk;
	}
	
	ArrayList<Triangle> getTriangles() {
		return bestTriFile.getTriangles();
	}
	
	double getMaxScore() {
		return maxScore;
	}

	BufferedImage getImage(Dimension newBlockPixelSize) {
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
