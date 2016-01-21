package reader;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import application.Triangle;

public class Block {
	private TrianglesFile bestTriFile;
	private double 
			maxScore,
			score;
	
	
	public Block(Dimension size, ArrayList<Triangle> triangles) {
		bestTriFile = new TrianglesFile(triangles, size);
		maxScore = bestTriFile.compare();
	}
	public void move() {
		TrianglesFile modifyTriFile = new TrianglesFile(bestTriFile);
		
		modifyTriFile.modifyShape();
		score = modifyTriFile.compare();
		if (score >= maxScore) {
			if (score > maxScore) {
				maxScore = score;
			}
			bestTriFile = modifyTriFile;
		}
	}

	public BufferedImage getImage() {
		return bestTriFile.getImage();
	}
	
	public TrianglesFile getTriangleFile() {
		return bestTriFile;
	}
	
	public boolean isDone() {
		return !bestTriFile.hasAlpha();
	}
	
	public String getText(double x, double y, double size) {
		return bestTriFile.getText(x, y, size);
	}
	public double getMaxScore() {
		return maxScore;
	}
}